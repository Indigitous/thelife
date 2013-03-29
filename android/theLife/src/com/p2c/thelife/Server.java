package com.p2c.thelife;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;


/**
 * Call the Server using REST calls.
 * @author clarence
 *
 */
public class Server {
	
	private static final String TAG = "Server";
	
	/**
	 * To receive the server's response, implement this callback.
	 *
	 */
	public interface ServerListener {
		public void notifyResponseAvailable(String indicator, JSONObject jsonObject);
	}
	
	/**
	 * Log into theLife. This means the user already has an account.
	 */
	public void login(String username, String password, ServerListener listener, String indicator) {
		
		// API end point
		// returns HTTP 201 on a success, HTTP 401 on a fail
		String urlString = TheLifeConfiguration.SERVER_URL + "/v1/authenticate";
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("email", username));
			pairs.add(new BasicNameValuePair("password", password));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);		
						
			new ServerCall("POST", formEntity, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "login()", e);
		}
	}
	
	/**
	 * Register for an account in theLife. The email must not already be taken.
	 */
	public void register(String username, String password, String firstName, String lastName, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 422 on a already taken (or missing) email, HTTP 201 on a success
		String urlString = TheLifeConfiguration.SERVER_URL + "/v1/register";
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("email", username));
			pairs.add(new BasicNameValuePair("password", password));
			pairs.add(new BasicNameValuePair("first_name", firstName));
			pairs.add(new BasicNameValuePair("last_name", lastName));			
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);		
						
			new ServerCall("POST", formEntity, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "register()", e);
		}
	}
	
	
	/**
	 * Create a new friend for the current user.
	 */
	public void createFriend(String firstName, String lastName, int thresholdIndex, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 422 on an incorrect form (such as a bad threshold), HTTP 201 on a success
		String urlString = TheLifeConfiguration.SERVER_URL + "/v1/friends";
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("authentication_token", TheLifeConfiguration.getToken()));
			pairs.add(new BasicNameValuePair("first_name", firstName));
			pairs.add(new BasicNameValuePair("last_name", lastName));
			pairs.add(new BasicNameValuePair("threshold_id", String.valueOf(thresholdIndex + 1))); // TODO: need a better server API here			
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);		
						
			new ServerCall("POST", formEntity, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "createFriend()", e);
		}
	}
	
	
	/**
	 * Create a new friend for the current user.
	 */
	public void deleteFriend(int friendId, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 404 on an unknown friend, HTTP 201 on a success TODO check this
		String urlString = TheLifeConfiguration.SERVER_URL + "/v1/friends?token=" + TheLifeConfiguration.getToken() + "&friend_id=" + String.valueOf(friendId);
		
		try {					
			new ServerCall("DELETE", null, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "deleteFriend()", e);
		}
	}	
	
	
	/**
	 * Create a new group for the current user.
	 */
	public void createGroup(String name, String description, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
		String urlString = TheLifeConfiguration.SERVER_URL + "/v1/groups";
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("authentication_token", TheLifeConfiguration.getToken()));
			pairs.add(new BasicNameValuePair("name", name));
			pairs.add(new BasicNameValuePair("full_description", description));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);		
						
			new ServerCall("POST", formEntity, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "createGroup()", e);
		}
	}		
		
	
	
	
	/********************************* Background thread Server access task *************************************/
	
	private class ServerCall extends AsyncTask<String, Void, JSONObject> {
		
		private String m_httpMethod = null;
		private HttpEntity m_entity = null;
		private ServerListener m_listener = null;
		private String m_indicator = null;
		
		public ServerCall(String httpMethod, HttpEntity entity, ServerListener listener, String indicator) {
			m_httpMethod = httpMethod;
			m_entity = entity;
			m_listener = listener;
			m_indicator = indicator;
		}
		
		// background thread		
		@Override
		protected JSONObject doInBackground(String... urls) {
			
			JSONObject jsonObject = null;
			
			// [Clarence] I tried using HttpURLConnection (preferred by Android) to do the POST, 
			// but when the Rails server read the form data it was always garbled
			// (there was a leading mystery character, carriage return and line feed).
			// Instead, this is implemented using Apache's HttpClient. 
			// See their doc at http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html.
				
			AndroidHttpClient httpClient = null;
			try {			
				Log.d(TAG, "STARTING ServerCall with " + urls[0]);	
								
				httpClient = AndroidHttpClient.newInstance(m_indicator);
				httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TheLifeConfiguration.HTTP_CONNECTION_TIMEOUT);
				httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, TheLifeConfiguration.HTTP_READ_TIMEOUT);
				
				HttpUriRequest httpRequest = createHttpRequest(urls[0]);

				HttpResponse httpResponse = httpClient.execute(httpRequest);
				int responseCode = httpResponse.getStatusLine().getStatusCode();
				
				System.out.println("HERE IS THE STATUS CODE " + responseCode);
				
				String jsonString = null;
				if (responseCode >= 200 && responseCode <= 299) {			
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					HttpEntity httpEntity = httpResponse.getEntity();					
					httpEntity.writeTo(outStream);		
					jsonString = outStream.toString("UTF-8");
//					jsonString = Utilities.readBufferedStream(new InputStreamReader(httpEntity.getContent()));
					Log.d(TAG, "GOT THE MODELS CONNECTION RESPONSE STRING " + jsonString);					
				}
				
				if (jsonString != null) {
					jsonObject = new JSONObject(jsonString);
				}
			} catch (JSONException e) {
				Log.wtf(TAG, "ServerCall.doInBackground()", e);				
			} catch (MalformedURLException e) {
				Log.wtf(TAG, "ServerCall().doInBackground", e);
			} catch (IOException e) {
				Log.e(TAG, "ServerCall().doInBackground", e);				
			} finally {
				if (httpClient != null) {
					httpClient.close();
				}
			}	
			
			return jsonObject;
		}
		
		private HttpUriRequest createHttpRequest(String urlString) {
			if (m_httpMethod.equals("POST")) {
				HttpPost httpRequest = new HttpPost(urlString);
				httpRequest.setEntity(m_entity);	
				return httpRequest;
			} else if (m_httpMethod.equals("DELETE")) {
				HttpDelete httpRequest = new HttpDelete(urlString);
				return httpRequest;
			} else {
				Log.e(TAG, "Bad HTTP method in createHttpRequest " + m_httpMethod);
				return null;
			}
		}
		
		// UI thread		
		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			
			Log.d(TAG, "HERE IN ON POST EXECUTE");
			
			m_listener.notifyResponseAvailable(m_indicator, jsonObject);
		}
	}

}
