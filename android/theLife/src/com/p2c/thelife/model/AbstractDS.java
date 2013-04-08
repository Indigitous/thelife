package com.p2c.thelife.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.p2c.thelife.TheLifeConfiguration;
import com.p2c.thelife.Utilities;



public abstract class AbstractDS<T extends AbstractModel> {
	
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
	protected String TAG = null;							// for logging
	protected SharedPreferences m_systemSettings = null;	
	protected String m_cacheFileName = null; 				// must be set in subclass
	protected boolean m_isRefreshing = false; 				// lock to prevent more than one thread refresh at any time
	protected String m_refreshIndicator = null;
	protected String m_refreshSettingTimestampKey = null;
	protected String m_refreshURL = null; 
	protected long m_refreshDelta = 0;						// in seconds

	
	/**
	 * Create a generic data store.
	 * @param context
	 * @param tag				for logging
	 * @param cache_file_name
	 * @param refreshSettingTimestampKey system preferences key
	 * @param refreshURL
	 * @param 
	 */
	public AbstractDS(Context context, String tag, String cacheFileName, 
					  String refreshSettingTimestampKey, String refreshURLPath, String refreshSettingDeltaKey, long refreshDeltaDefault) {
		
		// initialize instance vars
		m_context = context;
		TAG = tag;
		m_cacheFileName = TheLifeConfiguration.getCacheDirectory() + cacheFileName;
		m_systemSettings = context.getSharedPreferences(TheLifeConfiguration.SYSTEM_PREFERENCES_FILE, Context.MODE_PRIVATE);
		m_refreshSettingTimestampKey = refreshSettingTimestampKey;
		m_refreshURL = Utilities.makeServerUrlString(refreshURLPath); // TODO correct
		m_refreshURL = Utilities.makeServerUrlStringDebug("http://thelife.ballistiq.com/api/v11/", refreshURLPath); // TODO debug 
		m_refreshDelta = m_systemSettings.getLong(refreshSettingDeltaKey, refreshDeltaDefault);
		
		// load model objects from the JSON cache file on this device.
		// TODO: is this too slow for the main thread?
		if (TheLifeConfiguration.isValidUser()) {
			try {
				File cacheFile = new File(m_cacheFileName);
				if (cacheFile.exists())
				{
					Log.d(TAG, "THE MODELS CACHE FILE EXISTS");
					
					String jsonString = Utilities.readBufferedStream(new FileReader(cacheFile));
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
		m_data.add(model);
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
	 * Force a refresh.
	 * @param refreshIndicator
	 */
	public void forceRefresh(String refreshIndicator) {
		
		if (!m_isRefreshing) {
			
			// force a refresh by erasing the timestamp indicating when the last time a refresh was done
			SharedPreferences.Editor system_settings_editor = m_systemSettings.edit();
			system_settings_editor.putLong(m_refreshSettingTimestampKey, System.currentTimeMillis());
			system_settings_editor.commit();			
			
			refresh(refreshIndicator);
		}
	}
	
	
	/**
	 * Refresh the model objects cache.
	 */
	public void refresh(String refreshIndicator) {
		
		// find when the model objects were most recently refreshed
		long lastRefresh = m_systemSettings.getLong(m_refreshSettingTimestampKey, 0);
		Log.d(TAG, "the last refresh was " + lastRefresh);
		
		// TODO: for debugging
		lastRefresh = 0;
		
		// if the model objects were not refreshed recently
		if (System.currentTimeMillis() - lastRefresh > m_refreshDelta) {
				
			// if the model objects are not currently being refreshed
			if (!m_isRefreshing) {  // this variable is only accessed in the UI (main) thread
				try {
					m_isRefreshing = true;
					m_refreshIndicator = refreshIndicator;
					Log.d(TAG, "WILL NOW RUN BACKGROUND MODELS REFRESH");
					new readFromServer().execute(new URL(m_refreshURL));
				} catch (MalformedURLException e) {
					Log.e(TAG, "refresh()", e);
				} finally {
					m_isRefreshing = false;
				}
			}
			
		}
	}	
	
	
	/**
	 * Write the string of JSON objects to the cache file.
	 * @param jsonString
	 * @return true if the write succeeded, false if it failed
	 */
	public boolean writeJSONCache(String jsonString) {
		
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
	
	
	/********************************** helper routines ********************************/
	
	/**
	 * Create a model object from JSON.
	 * @param context
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	protected abstract T createFromJSON(JSONObject json, boolean useServer) throws JSONException;
	
	
	/**
	 * Can be called from the UI thread or a background thread.
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
			try {			
				Log.d(TAG, "AM NOW RUNNING READFROMSERVER with " + urls[0]);	
				URL modelsEP = urls[0];
				modelsConnection = (HttpURLConnection)modelsEP.openConnection();
				modelsConnection.setConnectTimeout(TheLifeConfiguration.HTTP_CONNECTION_TIMEOUT);
				modelsConnection.setReadTimeout(TheLifeConfiguration.HTTP_READ_TIMEOUT);
				
				Log.d(TAG, "GOT THE MODELS CONNECTION RESPONSE CODE " + modelsConnection.getResponseCode());

				String jsonString = null;
				if (modelsConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {							
					jsonString = Utilities.readBufferedStream(new InputStreamReader(modelsConnection.getInputStream()));
				}
				
				if (jsonString != null) {
					JSONArray jsonArray = new JSONArray(jsonString);					
				
					// add the new data models to a separate list in case of an error
					data2 = new ArrayList<T>();
					addModels(jsonArray, true, data2);
					
					// write the new data models to disk
					if (writeJSONCache(jsonString)) {
						
						// remember the timestamp of this successful refresh
						
						SharedPreferences.Editor system_settings_editor = m_systemSettings.edit();
						system_settings_editor.putLong(m_refreshSettingTimestampKey, System.currentTimeMillis());
						system_settings_editor.commit();
					}					
				}
			} catch (JSONException e) {
				Log.wtf(TAG, "readFromServer()", e);				
			} catch (MalformedURLException e) {
				Log.wtf(TAG, "readFromServer()", e);
			} catch (IOException e) {
				Log.e(TAG, "readFromServer()", e);				
			} finally {
				if (modelsConnection != null) {
					modelsConnection.disconnect();
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
					
			if (data2 != null) {
				// no error, so use the new data
				Log.d(TAG, "HERE IN ON POST EXECUTE with data2 size " + data2.size());				
				m_data = data2;
				
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
			listener.notifyDSChanged(m_oldModelIds, m_newModelIds);
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

}
