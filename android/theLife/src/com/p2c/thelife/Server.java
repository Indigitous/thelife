package com.p2c.thelife;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
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
		/**
		 * @param indicator		returned to the listener
		 * @param jsonObject	the result of the server call, null if there was a failure
		 */
		public void notifyServerResponseAvailable(String indicator, JSONObject jsonObject);
	}
	
	/**
	 * Log into theLife. This means the user already has an account.
	 */
	public void login(String username, String password, ServerListener listener, String indicator) {
		
		// API end point
		// returns HTTP 201 on a success, HTTP 401 on a fail
		String urlString = Utilities.makeServerUrlStringNoToken("authenticate");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("email", username));
			pairs.add(new BasicNameValuePair("password", password));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
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
		String urlString = Utilities.makeServerUrlStringNoToken("register");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("email", username));
			pairs.add(new BasicNameValuePair("password", password));
			pairs.add(new BasicNameValuePair("first_name", firstName));
			pairs.add(new BasicNameValuePair("last_name", lastName));			
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);			
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
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
		String urlString = Utilities.makeServerUrlString("friends");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("first_name", firstName));
			pairs.add(new BasicNameValuePair("last_name", lastName));
			pairs.add(new BasicNameValuePair("threshold_id", String.valueOf(thresholdIndex + 1))); // TODO: need a better server API here			
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);			
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
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
		String urlString = Utilities.makeServerUrlString("friends") + "&friend_id=" + String.valueOf(friendId);
		
		try {
			HttpDelete httpRequest = new HttpDelete(urlString);
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
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
		String urlString = Utilities.makeServerUrlString("groups");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("name", name));
			pairs.add(new BasicNameValuePair("description", description));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);				
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "createGroup()", e);
		}
	}
	
	
	/**
	 * Create a new event for the current user.
	 */
	public void createEvent(int deedId, int friendId, boolean withPrayerSupport, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
		String urlString = Utilities.makeServerUrlString("events");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("activity_id", String.valueOf(deedId)));
			pairs.add(new BasicNameValuePair("friend_id", String.valueOf(friendId)));
			pairs.add(new BasicNameValuePair("prayer_requested", withPrayerSupport ? "true" : "false"));			
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);		
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);				
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "createEvent()", e);
		}
	}			
	
	
	/**
	 * Query against all the groups in the system.
	 * The return value will be a JSONObject containing a single JSONArray of the groups. 
	 */
	public void queryGroups(String queryString, ServerListener listener, String indicator) {
	
		try {
			// API end point
			// returns HTTP 201 on a success, HTTP 401 on a fail
			String urlString = Utilities.makeServerUrlString("groups") + "&query=" + URLEncoder.encode(queryString, "UTF-8");	
			
			HttpGet httpRequest = new HttpGet(urlString);
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "login()", e);
		}
	}	
	
	
	/**
	 * Create a request asking to join a group (kind=REQUEST_MEMBERSHIP) or asking a person to join my group (kind=INVITE).
	 */
	public void createRequest(int groupId, String kind, String email, String sms, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
		String urlString = Utilities.makeServerUrlString("requests");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("group_id", String.valueOf(groupId)));
			pairs.add(new BasicNameValuePair("type", kind));
			if (email != null) {
				pairs.add(new BasicNameValuePair("email", email));
			}
			if (sms != null) {
				pairs.add(new BasicNameValuePair("sms", sms));
			}			
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);				
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "createRequest()", e);
		}
	}	
	
	/**
	 * Process a request to add a user to a group.
	 */
	public void processGroupMembershipRequest(int requestId, boolean hasAccepted, int userId, int groupId, ServerListener listener, String indicator ) {
		
		// API endpoint
		// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
		String urlString = Utilities.makeServerUrlString("requests/" + String.valueOf(requestId) + "/process");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("accepted", String.valueOf(hasAccepted)));			
			pairs.add(new BasicNameValuePair("user_id", String.valueOf(userId)));
			pairs.add(new BasicNameValuePair("group_id", String.valueOf(groupId)));					
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);				
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "processGroupMembershipRequest()", e);
		}
	}
	
	
	/**
	 * Process a request to add a user to a group.
	 */
	public void queryUserProfile(int userId, ServerListener listener, String indicator) {
		
		try {
			// API endpoint
			// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
			String urlString = Utilities.makeServerUrlString("my_users/" + String.valueOf(userId));
			
			HttpGet httpRequest = new HttpGet(urlString);
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "queryUserProfile()", e);
		}
	}			
	
	
	public void updateUserProfile(int userId, String firstName, String lastName, String email, String phone, ServerListener listener, String indicator) {
		try {
			// API endpoint
			// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
			String urlString = Utilities.makeServerUrlString("my_users/" + String.valueOf(userId));
			
			HttpPut httpRequest = new HttpPut(urlString);
			
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("first_name", String.valueOf(firstName)));			
			pairs.add(new BasicNameValuePair("last_name", String.valueOf(lastName)));
			pairs.add(new BasicNameValuePair("email", String.valueOf(email)));
			pairs.add(new BasicNameValuePair("phone", String.valueOf(phone)));								
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);
			httpRequest.setEntity(formEntity);
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "updateUserProfile()", e);
		}		
	}
	
	
	/********************************* Background thread Server access task *************************************/
	
	private class ServerCall extends AsyncTask<String, Void, JSONObject> {
		
		private HttpUriRequest m_httpRequest = null;
		private ServerListener m_listener = null;
		private String m_indicator = null;
		
		public ServerCall(HttpUriRequest httpRequest, ServerListener listener, String indicator) {
			m_httpRequest = httpRequest;
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
				
				HttpResponse httpResponse = httpClient.execute(m_httpRequest);
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
					jsonString = jsonString.trim();
					
					if (jsonString.length() > 0) {
						// if the result is a JSONArray, wrap it inside a JSONObject
						if (jsonString.charAt(0) == '[') {
							JSONArray jsonArray = new JSONArray(jsonString);
							jsonObject = new JSONObject();
							jsonObject.put("a",  jsonArray);
						} else {
							jsonObject = new JSONObject(jsonString);
						}
					}
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
		
		// UI thread		
		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			
			Log.d(TAG, "HERE IN ON POST EXECUTE");
			
			m_listener.notifyServerResponseAvailable(m_indicator, jsonObject);
		}
	}

}
