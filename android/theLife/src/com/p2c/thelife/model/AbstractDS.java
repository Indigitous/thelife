package com.p2c.thelife.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.p2c.thelife.TheLifeConfiguration;
import com.p2c.thelife.Utilities;



/**
 * Superclass for data stores of model records. 
 * Holds the information, provides access to the data, and refreshes the data from the server.
 * @author clarence
 *
 */
public abstract class AbstractDS<T extends AbstractModel> {

	private static final int NUM_RETRIES = 2;
	
	
	/**
	 * Listener interface for DS data changed event.
	 *
	 */
	public interface DSChangedListener {
		public void notifyDSChanged(ArrayList<Integer> oldModelIds, ArrayList<Integer> newModelIds);
	}
	
	
	/**
	 * Listener interface for DS data refresh completed event. 
	 * This is called whether or not there was any new data from the refresh.
	 *
	 */
	public interface DSRefreshedListener {
		public void notifyDSRefreshed(String indicator);
	}
	
	protected ArrayList<T> m_data = new ArrayList<T>(); 	// in memory list of model objects
	protected ArrayList<Integer> m_oldModelIds = new ArrayList<Integer>(); // model ids before a refresh
	protected ArrayList<Integer> m_newModelIds = new ArrayList<Integer>(); // model ids after a refresh
	
	protected Context m_context = null;
	protected String m_token = null;
	protected String TAG = null;							// for logging
	protected String m_cacheFileName = null; 				// must be set in subclass
	protected boolean m_isRefreshing = false; 				// lock to prevent more than one thread refresh at any time
	protected String m_refreshIndicator = null;
	protected String m_refreshSettingTimestampKey = null;
	protected String m_refreshURLPath = null; 
	protected long m_refreshDelta = 0;						// in seconds
	protected int m_numRetries = 0;
	
	private boolean m_connectionTimeout = false;
	
	// what to do with new data that has just been read, compared to the existing data
	protected int MODE_REPLACE = 0;
	protected int MODE_PREPEND = 1;
	protected int MODE_APPEND = 2;
	protected int m_newDataMode = MODE_REPLACE;

	
	/**
	 * Create a generic data store.
	 * @param context
	 * @param token							authentication token for testing and debugging
	 * @param tag							for logging
	 * @param cache_file_name
	 * @param refreshSettingTimestampKey 	system preferences key
	 * @param refreshURL
	 * @param 
	 */
	public AbstractDS(Context context, String token, String tag, String cacheFileName, 
					  String refreshSettingTimestampKey, String refreshURLPath, String refreshSettingDeltaKey, long refreshDeltaDefault) {
		
		// initialize instance vars
		m_context = context;
		m_token = token;
		TAG = tag;
		m_cacheFileName = TheLifeConfiguration.getCacheDirectory() + cacheFileName;
		m_refreshSettingTimestampKey = refreshSettingTimestampKey;
		m_refreshURLPath = refreshURLPath;
		m_refreshDelta = TheLifeConfiguration.getSystemSettings().getLong(refreshSettingDeltaKey, refreshDeltaDefault);
		
		// load model objects from the JSON cache file on this device.
		if (m_token == null && TheLifeConfiguration.getOwnerDS().isValidOwner()) {
			
			FileReader fileReader = null;
			try {
				File cacheFile = new File(m_cacheFileName);
				if (cacheFile.exists())
				{
					Log.d(TAG, "THE MODELS CACHE FILE EXISTS");
					
					fileReader = new FileReader(cacheFile);
					String jsonString = Utilities.readBufferedStream(fileReader);
					if (jsonString != null) {
						JSONArray jsonArray = new JSONArray(jsonString);					
						addModels(jsonArray, false, m_data);
					}
				} else {
					Log.d(TAG, "THE MODELS CACHE FILE DOES NOT EXIST");
				}
			} catch (FileNotFoundException e) {
				Log.e(TAG, "constructor", e);
			} catch (JSONException e) {
				Log.wtf(TAG, "constructor", e);			
			} finally {
				if (fileReader != null) {
					try { fileReader.close(); } catch (Exception e) {}
				}
			}
		}
		
		// set the old model ids to be whatever was read from the cache
		copyModelIds();
	}
	
	/**
	 * @param id
	 * @return the model object with the given id
	 */
	public T findById(int id) {
		for (T m:m_data) {
			if (m.id == id) {
				return m;
			}		
		}
		
		return null;
	}
	
	
	/**
	 * @return all model objects in the datastore.
	 */
	public ArrayList<T> findAll() {
		return m_data;
	}
	
	/**
	 * Add a model which is already in the server.
	 * This does not update the cache or notify listeners.
	 */
	public void add(T model) {
		m_data.add(0, model); // add to the start of the list
	}
	
	/**
	 * Delete a model which has already been deleted in the server.
	 * This does not update the cache or notify listeners.
	 */
	public void delete(int id) {
		
		int index = 0;
		for (T m:m_data) {
			if (m.id == id) {
				m_data.remove(index);
				return;
			}
			index++;
		}
	}	
	
	/** 
	 * Update a model which has already been updated in the server.
	 * This does not update the cache or notify listeners.
	 */
	public void update(T model) {
		delete(model.id);
		add(model);
	}
	
	
	public int count() {
		return m_data.size();
	}
	
	
	/**
	 * Force a refresh, reading in all the records.
	 * @param refreshIndicator
	 */
	public void forceRefresh(String refreshIndicator) {
		forceRefresh(refreshIndicator, 0);
	}
	
	
	/**
	 * Force a refresh, reading in up to the maximum number of most recent records.
	 * @param refreshIndicator
	 * @param max
	 */
	public void forceRefresh(String refreshIndicator, int max) {			
		refresh(refreshIndicator, true, NUM_RETRIES, max, null, MODE_REPLACE);
	}
		
	
	/**
	 * Refresh the model objects cache, reading in unlimited number of records.
	 * @param refreshIndicator
	 */
	public void refresh(String refreshIndicator) {
		refresh(refreshIndicator, false, 0, 0, null, MODE_REPLACE);
	}
	
	
	/**
	 * Refresh the model objects cache, reading in up to the maximum number of most recent records.
	 * @param refreshIndicator
	 * @param max
	 */
	public void refresh(String refreshIndicator, int max) {
		refresh(refreshIndicator, false, 0, max, null, MODE_REPLACE);
	}
	
	
	/**
	 * Refresh the model objects cache, reading in up to the maximum number of most recent records according to the given parameter.
	 * @param	refreshIndicator	passed back to the caller
	 * @param 	force				whether or not to force a refresh
	 * @param 	numberRetries		number of times to try again if encountering a connection timeout 
	 * @param	max					the maximum number of records to read; 0 means read all 
	 * @param 	params				in URL encoded form, example: "&after=12345"
	 * @param	newDataMode 		either MODE_REPLACE, MODE_PREPEND or MODE_APPEND, what to do with the data that is read in, compared to any existing data
	 * This method looks to see the last time a refresh occurred.
	 */
	protected void refresh(String refreshIndicator, boolean force, int numRetries, int max, String params, int newDataMode) {
		
		if (force) {
			
			// force a refresh by erasing the timestamp indicating previous refresh time
			SharedPreferences.Editor system_settings_editor = TheLifeConfiguration.getSystemSettings().edit();
			system_settings_editor.putLong(m_refreshSettingTimestampKey, 0L);
			system_settings_editor.commit();			
		}				
		
		// find when the model objects were most recently refreshed
		long lastRefresh = TheLifeConfiguration.getSystemSettings().getLong(m_refreshSettingTimestampKey, 0);
		
		// TODO: for debugging
		//lastRefresh = 0;
		
		// if the model objects were not refreshed recently
		if (System.currentTimeMillis() - lastRefresh > m_refreshDelta) {
				
			// if the model objects are not currently being refreshed
			if (!m_isRefreshing) {  // this variable is only accessed in the UI (main) thread
				try {
					m_isRefreshing = true;
					m_refreshIndicator = refreshIndicator;
					m_numRetries = numRetries;
					String refreshURL = Utilities.makeServerUrlString(m_refreshURLPath, m_token);
					
					// add on optional parameters
					if (max != 0) {
						refreshURL += "&max=" + String.valueOf(max);
					}
					if (params != null) {
						refreshURL += params;
					}
					
					m_newDataMode = newDataMode;
					
					// run in background thread
					Log.d(TAG, "WILL NOW RUN BACKGROUND MODELS REFRESH");					
					new readFromServer().execute(new URL(refreshURL));
				} catch (MalformedURLException e) {
					Log.e(TAG, "refresh()", e);
				} finally {
					m_isRefreshing = false;
				}
			}
			
		}
	}	

	
	/********************************** helper routines ********************************/
	
	
	
	/**
	 * Write the string of JSON objects to the cache file.
	 * @param jsonString
	 * @return true if the write succeeded, false if it failed
	 */
	protected boolean replaceJSONCache(String jsonString) {
		
		boolean success = true;
		
		FileWriter fileWriter = null;
		try {
			File cacheFile = new File(m_cacheFileName);
			fileWriter = new FileWriter(cacheFile); // buffered
			fileWriter.write(jsonString);
		} catch (IOException e) {
			Log.e(TAG, "writeJSONCache()", e);
			success = false;
		} finally {
			if (fileWriter != null) {
				try { fileWriter.close(); } catch (IOException e) { }
			}
		}
		
		return success;
	}
	
	/**
	 * Prepend the new JSON, in newDataJSONString, to the cache file.
	 * @param newDataJSONString
	 * @param oldData
	 * @param newData
	 * @return
	 */
	protected boolean prependJSONCache(String newDataJSONString, int oldDataSize, int newDataSize) {
		
		boolean success = true;
		
		if (newDataSize > 0) {
			if (oldDataSize == 0) {
				// if there isn't any old data, just write the file
				replaceJSONCache(newDataJSONString);
			} else {
				
				// make sure the cache file is really there
				File cacheFile = new File(m_cacheFileName);
				if (!cacheFile.exists()) {
					Log.wtf(TAG, "prependJSONCache() missing cache file " + m_cacheFileName);
					success = false;
				} else {				
					
					FileReader fileReader = null;
					FileWriter fileWriter = null;					
					try {
					
						// read in the old JSON data
						fileReader = new FileReader(cacheFile);
						String oldDataJSONString = Utilities.readBufferedStream(fileReader);
						fileReader.close();
						fileReader = null;
						
						// write the new JSON data at the start of the file, 
						//      adding a comma after the final '}' and skipping the final ']'
						fileWriter = new FileWriter(cacheFile);
						int finalBraceIndex = newDataJSONString.lastIndexOf('}');					
						fileWriter.write(newDataJSONString, 0, finalBraceIndex + 1);
						fileWriter.write(',');
						
						// write the old JSON next, starting with the first '{' and thus skipping the first '['
						int firstBraceIndex = oldDataJSONString.indexOf('{');
						fileWriter.write(oldDataJSONString, firstBraceIndex, oldDataJSONString.length() - firstBraceIndex);
						fileWriter.close();
						fileWriter = null;
						
					} catch (Exception e) {
						Log.e(TAG, "prependJSONCache()", e);
						success = false;
					} finally {
						if (fileReader != null) {
							try { fileReader.close(); } catch (Exception e) { }
						}
						if (fileWriter != null) {
							try { fileWriter.close(); } catch (Exception e) { }
						}						
					}
				}
			}
		}
			
		return success;
	}
	
	
	protected void replaceModels(ArrayList <T> newModels) {
		m_data = newModels;
	}
	
	
	protected void prependModels(ArrayList <T> newModels) {
		m_data.addAll(0, newModels);
	}
	
		
	/**
	 * Create a model object from JSON.
	 * @param context
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	protected abstract T createFromJSON(JSONObject json, boolean useServer) throws JSONException;
	
	
	/**
	 * Can be called from the UI thread (from constructor) or a background thread (normal case).
	 */
	protected void addModels(JSONArray jsonArray, boolean useServer, ArrayList<T> list) throws JSONException {

		m_newModelIds.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			Log.d(TAG, "ADD ANOTHER JSON OBJECT " + json);
			
			// create the model object 
			T model = createFromJSON(json, useServer);
			
			// add the new model to the list and remember it
			list.add(model);
			m_newModelIds.add(model.id);
		}
	}	
	
	
	/**
	 * Copy over the new model ids to the old model ids array.
	 */
	private void copyModelIds() {
		if (m_newModelIds != null) {
			m_oldModelIds.clear();
			for (Integer id:m_newModelIds) {
				m_oldModelIds.add(id);
			}
		}
	}
	
	
	/********************************* Background thread refresh task *************************************/
	
	private class readFromServer extends AsyncTask<URL, Void, ArrayList<T>> {
		
		// background thread		
		@Override
		protected ArrayList<T> doInBackground(URL... urls) {
			ArrayList<T> data2 = null;
				
			HttpURLConnection modelsConnection = null;
			InputStreamReader isr = null;
			
			// try multiple retries for connection timeouts
			m_connectionTimeout = true;
			for (int i = 0; i < (m_numRetries + 1) && m_connectionTimeout; i++) {
				m_connectionTimeout = false;
				
				// make sure the user hasn't logged out
				if (TheLifeConfiguration.getOwnerDS().isValidOwner()) {
					
					try {			
						Log.d(TAG, i + " DS READFROMSERVER with " + urls[0]);	
						URL modelsEP = urls[0];
						modelsConnection = (HttpURLConnection)modelsEP.openConnection();
						modelsConnection.setConnectTimeout(TheLifeConfiguration.HTTP_SERVER_CONNECTION_TIMEOUT);
						modelsConnection.setReadTimeout(TheLifeConfiguration.HTTP_READ_TIMEOUT);
						
						Log.d(TAG, i + " DS HTTP RESPONSE CODE " + modelsConnection.getResponseCode());
		
						String jsonString = null;
						if (modelsConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
							isr = new InputStreamReader(modelsConnection.getInputStream());
							jsonString = Utilities.readBufferedStream(isr);
						}
						
						if (jsonString != null) {
							JSONArray jsonArray = new JSONArray(jsonString);					
						
							// add the new data models to a separate list in case of an error
							data2 = new ArrayList<T>();
							addModels(jsonArray, true, data2);
							
							// make sure the user hasn't logged out							
							if (TheLifeConfiguration.getOwnerDS().isValidOwner()) {
								
								// write the new data models to disk								
								boolean success = (m_newDataMode == MODE_REPLACE) ? replaceJSONCache(jsonString) : prependJSONCache(jsonString, m_data.size(), data2.size());
								if (success) {
									// remember the timestamp of this successful refresh
									
									SharedPreferences.Editor system_settings_editor = TheLifeConfiguration.getSystemSettings().edit();
									system_settings_editor.putLong(m_refreshSettingTimestampKey, System.currentTimeMillis());
									system_settings_editor.commit();
								}
							}
						}
					} catch (JSONException e) {
						Log.wtf(TAG, "readFromServer()", e);				
					} catch (MalformedURLException e) {
						Log.wtf(TAG, "readFromServer()", e);
					} catch (java.net.SocketTimeoutException e) {
						m_connectionTimeout = true;
						Log.e(TAG, "readFromServer() CONNECTION TIMEOUT " + e.getMessage());
					} catch (IOException e) {
						Log.e(TAG, "readFromServer()", e);				
					} finally {
						if (isr != null) {
							try { isr.close(); } catch (Exception e) { }
							isr = null;
						}
						if (modelsConnection != null) {
							modelsConnection.disconnect();
							modelsConnection = null;
						}
					}
				}
			}
			
			return data2;
		}
		
		/**
		 * on UI thread
		 * Note that the server data only becomes "usable" to the clients on the UI thread
		 */
		@Override
		protected void onPostExecute(ArrayList<T> data2) {
			
			if (m_connectionTimeout && m_numRetries > 0) {
				Utilities.showErrorToast(m_context, TAG + " CONN TIMEOUT ", Toast.LENGTH_SHORT);	
				m_connectionTimeout = false;
			}
					
			if (data2 != null) {
				// no error, so use the new data
				Log.d(TAG, "DS ON POST EXECUTE with data2 size " + data2.size());
				
				if (m_newDataMode == MODE_REPLACE) {
					replaceModels(data2);
				} else {
					prependModels(data2);
				}
				
				// tell listeners that the data has changed, on the UI thread
				notifyDSChangedListeners(); 
				
				// remember the model ids for next refresh
				copyModelIds();
			}
			
			// tell listeners that the refresh has completed
			notifyDSRefreshedListeners(m_refreshIndicator);			
		
			// release lock
			m_isRefreshing = false;	
		}
		
	}		
	
	
	
	
	/************************************* DSListeners *****************************************/
	
	/**
	 * DSChanged listener
	 */
	protected ArrayList<DSChangedListener> m_changedListeners = new ArrayList<DSChangedListener>();
	
	public void addDSChangedListener(DSChangedListener theListener) {
		m_changedListeners.add(theListener);
	}
	
	public void notifyDSChangedListeners() {
		for (DSChangedListener listener:m_changedListeners) {
			if (listener != null) {
				listener.notifyDSChanged(m_oldModelIds, m_newModelIds);
			}
		}
	}
	
	public void removeDSChangedListener(DSChangedListener theListener) {
		m_changedListeners.remove(theListener);
	}
	
	public void clearAllDSChangedListeners() {
		m_changedListeners.clear();
	}
	
	
	/**
	 * DSRefreshed listener
	 */
	protected DSRefreshedListener m_refreshedListener = null;
	
	public void addDSRefreshedListener(DSRefreshedListener theListener) {
		m_refreshedListener = theListener;
	}
	
	public void notifyDSRefreshedListeners(String refreshIndicator) {
		if (m_refreshedListener != null) {
			m_refreshedListener.notifyDSRefreshed(refreshIndicator);
		}
	}
	
	public void removeDSRefreshedListener(DSRefreshedListener theListener) {
		m_refreshedListener = null;
	}
	
	public void clearAllDSRefreshedListeners() {
		m_refreshedListener = null;
	}	
	

	
	/****************************************** Cleanup **********************************/
	


	/**
	 * Remove all the JSON files in the cache.
	 */
	public static void removeAllJSONFiles() {
		File cacheDirectory = new File(TheLifeConfiguration.getCacheDirectory());
		
		File cacheFiles[] = cacheDirectory.listFiles( new FilenameFilter() { 
			
			@Override
			public boolean accept(File dir, String filename) {
				// JSON files
				return filename.endsWith(".json") || filename.endsWith(".jpeg_");
			}
			
		});
		
		for (File f:cacheFiles) {
			Log.i("removeAllJSONFiles()", "Deleting file " + f.getName());
			boolean wasDeleted = f.delete();
			if (!wasDeleted) {
				Log.e("removeAllJSONFiles()", "Unable to delete cache file " + f.getName());
			}
		}

	}

}