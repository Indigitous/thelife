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

import org.apache.http.util.CharArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.p2c.thelife.TheLifeConfiguration;



public abstract class AbstractDS<T extends AbstractModel> {
	
	protected ArrayList<T> m_data = new ArrayList<T>(); 	// in memory list of model objects
	
	protected Context m_context = null;
	protected String TAG = null;							// for logging
	protected SharedPreferences m_systemSettings = null;	
	protected String m_cacheFileName = null; 				// must be set in subclass
	protected boolean m_isRefreshing = false; 				// lock to prevent more than one thread refresh at any time
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
					  String refreshSettingTimestampKey, String refreshURL, String refreshSettingDeltaKey, long refreshDeltaDefault) {
		
		// initialize instance vars
		m_context = context;
		TAG = tag;
		m_cacheFileName = TheLifeConfiguration.cacheDirectory + cacheFileName;
		m_systemSettings = context.getSharedPreferences(TheLifeConfiguration.SYSTEM_PREFERENCES_FILE, Context.MODE_PRIVATE);
		m_refreshSettingTimestampKey = refreshSettingTimestampKey;
		m_refreshURL = refreshURL;
		m_refreshDelta = m_systemSettings.getLong(refreshSettingDeltaKey, refreshDeltaDefault);
		
		// load model objects from the JSON cache file on this device.
		// TODO: is this too slow for the main thread?
		try {
			File cacheFile = new File(m_cacheFileName);
			if (cacheFile.exists())
			{
				Log.d(TAG, "THE MODELS CACHE FILE EXISTS");
				
				String jsonString = readJSONStream(new FileReader(cacheFile));
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
	 * Refresh the model objects cache.
	 */
	public void refresh() {
		
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
	 * Read the JSON objects from the given stream.
	 * Note that InputStreamReader is buffered.
	 * @param is
	 * @return the string containing JSON, or null if an error
	 */
	protected String readJSONStream(InputStreamReader is) {

		String jsonString = null;
		
		try {
			char buffer[] = new char[1024];
			CharArrayBuffer jsonBuffer = new CharArrayBuffer(1024);			

			int numBytesRead = is.read(buffer);
			while (numBytesRead != -1) {
				jsonBuffer.append(buffer, 0, numBytesRead);
				numBytesRead = is.read(buffer);
			}
			
			jsonString = new String(jsonBuffer.buffer());
		} catch (IOException e) {
			Log.e(TAG, "readJSONStream()", e);
		}
		
		return jsonString;
	}	
	
	protected void addModels(JSONArray jsonArray, boolean useServer, ArrayList<T> list) throws JSONException {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			Log.d(TAG, "ADD ANOTHER JSON OBJECT " + json);
			
			// create the model object
			T model = createFromJSON(json, useServer);
			list.add(model);
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
				Log.d(TAG, "AM NOW RUNNING READFROMSERVER with" + urls[0]);	
				URL modelsEP = urls[0];
				modelsConnection = (HttpURLConnection)modelsEP.openConnection();
				modelsConnection.setConnectTimeout(TheLifeConfiguration.HTTP_CONNECTION_TIMEOUT);
				modelsConnection.setConnectTimeout(TheLifeConfiguration.HTTP_READ_TIMEOUT);
				
				Log.d(TAG, "GOT THE MODELS CONNECTION RESPONSE CODE" + modelsConnection.getResponseCode());

				String jsonString = null;
				if (modelsConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {							
					jsonString = readJSONStream(new InputStreamReader(modelsConnection.getInputStream()));
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
		
		// UI thread		
		@Override
		protected void onPostExecute(ArrayList<T> data2) {
			
			Log.d(TAG, "HERE IN ON POST EXECUTE");
			
			if (data2 != null) {
				// no error, so use the new data
				m_data = data2;
				notifyDataStoreListeners(); // tell listeners that the data has changed, on the UI thread
			}
		
			// release lock
			m_isRefreshing = false;	
		}
		
	}		
	
	
	
	
	/************************************* DataStoreListener *****************************************/
	
	// protected ArrayList<DataStoreListener> m_listeners = new ArrayList<DataStoreListener>();
	protected DataStoreListener m_listener = null;
	
	public void addDataStoreListener(DataStoreListener theListener) {
		//m_listeners.add(theListener);
		m_listener = theListener;
	}
	
	public void notifyDataStoreListeners() {
//		for (DataStoreListener listener:m_listeners) {
//			listener.notifyDataChanged();
//		}
		if (m_listener != null) {
			m_listener.notifyDataChanged();
		}
	}
	
	public void removeDataStoreListener(DataStoreListener theListener) {
		//m_listeners.remove(theListener);
		m_listener = null;
	}
	
	public void clearAllListeners() {
		//m_listeners.clear();
		m_listener = null;
	}

}
