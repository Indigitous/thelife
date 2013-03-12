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
import java.util.Collection;

import org.apache.http.util.CharArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeApplication;


/**
 * Deed Model datastore
 * @author clarence
 *
 */
public class DeedsDS extends AbstractDS {
	
	private static final String TAG = "DeedsDS"; 
	
	private ArrayList<DeedModel> m_data = new ArrayList<DeedModel>(); // in memory list of deeds
	
	private Drawable m_genericIcon = null;
	private boolean m_is_refreshing = false; // lock to prevent more than one thread refresh at any time
	private SharedPreferences m_system_settings = null;
	private String m_cache_file_name = null;

	public DeedsDS(Context context) {
		
		// initialize instance vars
		m_genericIcon = context.getResources().getDrawable(R.drawable.pray);
		m_system_settings = context.getSharedPreferences(TheLifeApplication.SYSTEM_PREFERENCES_FILE, Context.MODE_PRIVATE);
		m_cache_file_name = context.getCacheDir().getAbsolutePath() + "/deeds.json"; // data/data/com.p2c.thelife/cache/deeds.json
		
		// load deeds from cache file on device
		// TODO: is this too slow for the main thread?
		try {
			File deedsFile = new File(m_cache_file_name);
			if (deedsFile.exists())
			{
				Log.d(TAG, "THE DEEDS CACHE FILE EXISTS");
				
				String jsonString = readJSONStream(new FileReader(deedsFile));
				if (jsonString != null) {
					JSONArray jsonArray = new JSONArray(jsonString);					
					addDeeds(jsonArray, m_data);
				}
			} else {
				Log.d(TAG, "THE DEEDS CACHE FILE DOES NOT EXIST");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();			
		}
	}
	
	/**
	 * Refresh the list of deeds.
	 * UI thread only
	 */
	public void refresh() {
		
		// find when the deeds were most recently loaded
		long last_deed_load = m_system_settings.getLong("last_deed_load", 0);
		Log.d(TAG, "the last deed load was " + last_deed_load);
		
		// TODO: for debugging
		last_deed_load = 0;
		
		// if the deeds were not refreshed recently
		if (System.currentTimeMillis() - last_deed_load > TheLifeApplication.RELOAD_DEEDS_DELTA) {
				
			// if the deeds are not currently being refreshed
			if (!m_is_refreshing) {  // this variable is only accessed in the UI (main) thread
				try {
					m_is_refreshing = true;
					Log.d(TAG, "WILL NOW RUN BACKGROUND DEEDS");
					new readFromServer().execute(new URL("http://thelife.ballistiq.com/deeds.json"));
				} catch (MalformedURLException e) {
					e.printStackTrace();		
				} finally {
					m_is_refreshing = false;
				}
			}
			
		}
	}
	
	private class readFromServer extends AsyncTask<URL, Void, String> {
		
		// background thread		
		@Override
		protected String doInBackground(URL... urls) {
			String jsonString = null;
				
			HttpURLConnection deedsConnection = null;
			try {
				Thread.sleep(5000); // TODO: testing
				
				Log.d(TAG, "AM NOW RUNNING READFROMSERVER with" + urls[0]);	
				URL deedsEP = urls[0];
				deedsConnection = (HttpURLConnection)deedsEP.openConnection();
				deedsConnection.setConnectTimeout(TheLifeApplication.HTTP_CONNECTION_TIMEOUT);
				deedsConnection.setConnectTimeout(TheLifeApplication.HTTP_READ_TIMEOUT);
				
				Log.d(TAG, "GOT THE DEEDS CONNECTION RESPONSE CODE" + deedsConnection.getResponseCode());

				if (deedsConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {							
					jsonString = readJSONStream(new InputStreamReader(deedsConnection.getInputStream()));
				}
			} catch (InterruptedException e) {
				;			
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();				
			} finally {
				if (deedsConnection != null) {
					deedsConnection.disconnect();
				}
			}	
			
			return jsonString;
		}
		
		// UI thread		
		@Override
		protected void onPostExecute(String jsonString) {
			
			Log.d(TAG, "HERE IN ON POST EXECUTE");
			
			try {
				if (jsonString != null) {
					JSONArray jsonArray = new JSONArray(jsonString);					
				
					// use a separate list in case of an error
					ArrayList<DeedModel> data2 = new ArrayList<DeedModel>();
					addDeeds(jsonArray, data2);
					
					// no error, so use the new data
					m_data = data2;
					notifyDataStoreListeners(); // tell listeners that the data has changed
					
					if (writeJSONCache(jsonString)) {
						
						// remember the timestamp of this successful refresh
						
						SharedPreferences.Editor system_settings_editor = m_system_settings.edit();
						system_settings_editor.putLong("last_deed_load", System.currentTimeMillis());
						system_settings_editor.commit();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// release lock
			m_is_refreshing = false;	
		}
		
	}	
	
	/**
	 * Read the JSON objects from the given stream.
	 * Note that InputStreamReader is buffered.
	 * @param is
	 * @return the string containing JSON, or null if an error
	 */
	private String readJSONStream(InputStreamReader is) {

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
			e.printStackTrace();
		}
		
		return jsonString;
	}
	
	private void addDeeds(JSONArray jsonArray, ArrayList<DeedModel> list) throws JSONException {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			Log.d("JSON", "ADD ANOTHER JSON OBJECT WITH TITLE " + json.optString("title", ""));
			
			// create the deed
			DeedModel deed = DeedModel.fromJSON(json, m_genericIcon);
			list.add(deed);
		}
	}
	
	/**
	 *  Write the string of JSON objects to the cache file.
	 * @param jsonString
	 * @return true if the write succeeded, false if it failed
	 */
	public boolean writeJSONCache(String jsonString) {
		
		boolean success = true;
		
		FileWriter fileWriter = null;
		try {
			File deedsFile = new File(m_cache_file_name);
			fileWriter = new FileWriter(deedsFile); // buffered
			fileWriter.write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		} finally {
			if (fileWriter != null) {
				try { fileWriter.close(); } catch (IOException e) { }
			}
		}
		
		return success;
	}
	
	
	/**
	 * @return all deed model objects in the datastore.
	 */
	public Collection<DeedModel> findAll() {
		return m_data;
	}

	/**
	 * @param deed_id
	 * @return the deed model object with the given id
	 */
	public DeedModel findById(int deed_id) {
		
		for (DeedModel m:m_data) {
			if (m.deed_id == deed_id) {
				return m;
			}		
		}
		
		return null;
	}
	
	/**
	 * @param threshold
	 * @return all deed model objects applicable to the given threshold
	 */
	public Collection<DeedModel> findByThreshold(FriendModel.Threshold threshold) {
		ArrayList<DeedModel> deeds = new ArrayList<DeedModel>();
		
		for (DeedModel m:m_data) {
			if (m.is_applicable(threshold)) {
				deeds.add(m);
			}		
		}
		
		return deeds;
	}

}
