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


public class DeedsDS extends AbstractDS {
	
	private static final String TAG = "DeedsDS"; 
	
	private ArrayList<DeedModel> m_data = new ArrayList<DeedModel>();
	
	private Drawable m_genericIcon = null;
	private volatile boolean m_is_refreshing = false;
	private SharedPreferences m_system_settings = null;
	private String m_cache_file_name = null;

	public DeedsDS(Context context) {
		
		// initialize instance vars
		m_genericIcon = context.getResources().getDrawable(R.drawable.pray);
		m_system_settings = context.getSharedPreferences(TheLifeApplication.SYSTEM_PREFERENCES_FILE, Context.MODE_PRIVATE);
		m_cache_file_name = context.getCacheDir().getAbsolutePath() + "/deeds.json"; // data/data/com.p2c.thelife/cache/deeds.json
		
		// load deed list from cache
		try {
			File deedsFile = new File(m_cache_file_name);
			if (deedsFile.exists())
			{
				Log.d(TAG, "THE DEEDS CACHE FILE EXISTS");
				
				String jsonString = readJSONStream(new FileReader(deedsFile));
				JSONArray jsonArray = new JSONArray(jsonString);					
				addDeeds(jsonArray, m_data);
			} else {
				Log.d(TAG, "THE DEEDS CACHE FILE DOES NOT EXIST");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();			
		}
	}
	
	// UI thread only
	public void refresh() {
		
		// find when the deeds were most recently loaded
		long last_deed_load = m_system_settings.getLong("last_deed_load", 0);
		Log.d(TAG, "the last deed load was " + last_deed_load);
		
		// TODO: for debugging
		last_deed_load = 0;
		
		// if the deeds were not refreshed recently
		if (System.currentTimeMillis() - last_deed_load > TheLifeApplication.RELOAD_DEEDS_DELTA) {
				
			// if the deeds are not currently being refreshed
			if (!m_is_refreshing) {  // okay to test, since this is only in the UI (main) thread
				try {
					m_is_refreshing = true;
					Log.d(TAG, "WILL NOW RUN BACKGROUND DEEDS");
					new readJSON().execute(new URL("http://thelife.ballistiq.com/deeds.json"));
				} catch (MalformedURLException e) {
					e.printStackTrace();		
				} finally {
					m_is_refreshing = false;
				}
			}
			
		}
	}
	
	private class readJSON extends AsyncTask<URL, Void, String> {
		
		// background thread		
		@Override
		protected String doInBackground(URL... urls) {
			String jsonString = null;
				
			HttpURLConnection deedsConnection = null;
			try {
				Thread.sleep(5000); // TODO: testing
				
				Log.d(TAG, "AM NOW RUNNING READJSON with" + urls[0]);	
				URL deedsEP = urls[0];
				deedsConnection = (HttpURLConnection)deedsEP.openConnection();
				deedsConnection.setConnectTimeout(TheLifeApplication.HTTP_CONNECTION_TIMEOUT);
				deedsConnection.setConnectTimeout(TheLifeApplication.HTTP_READ_TIMEOUT);
				
				Log.d(TAG, "GOT THE RESPONSE CODE" + deedsConnection.getResponseCode());

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
					
					writeJSONCache(jsonString);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// release lock
			m_is_refreshing = false;
			
			// remember the timestamp of this successful load
			SharedPreferences.Editor system_settings_editor = m_system_settings.edit();
			system_settings_editor.putLong("last_deed_load", System.currentTimeMillis());
			system_settings_editor.commit();
		}
		
	}	
	
	
	// InputStreamReader is buffered
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
	
	private void addDeeds(JSONArray jsonArray, ArrayList<DeedModel> list) {
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				Log.d("JSON", "ADD ANOTHER JSON OBJECT WITH TITLE " + json.optString("title", ""));
				
				// create the deed
				DeedModel deed = DeedModel.fromJSON(json, m_genericIcon);
				list.add(deed);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void writeJSONCache(String jsonString) {
		
		FileWriter fileWriter = null;
		try {
			File deedsFile = new File(m_cache_file_name);
			fileWriter = new FileWriter(deedsFile); // buffered
			fileWriter.write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileWriter != null) {
				try { fileWriter.close(); } catch (IOException e) { }
			}
		}
	}
	
	
	/**
	 * @return all deed model objects in the database.
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
