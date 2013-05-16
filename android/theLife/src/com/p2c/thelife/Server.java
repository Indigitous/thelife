package com.p2c.thelife;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.p2c.thelife.model.FriendModel;


/**
 * Call the Server using REST calls.
 * Uses a downloaded version of Apache HttpClient, httpcomponents-client-4.2.5, 
 * because standard Android HttpClient does not support multipart forms.
 * The downloaded JARs are:
 * 		commons-codec-1.6.jar
 * 		commons-logging-1.1.1.jar
 * 		fluent-hc-4.2.5.jar
 * 		httpclient-4.2.5.jar
 * 		httpclient-cache-4.2.5.jar
 * 		httpcore-4.2.4.jar
 * 		httpmime-4.2.5.jar
 * @author clarence
 *
 */
public class Server {
	
	private static final String TAG = "Server";
	
	private Context m_context = null;
	
	/**
	 * To receive the server's response, implement this callback.
	 *
	 */
	public interface ServerListener {
		/**
		 * @param indicator		returned to the listener
		 * @param httpCode 		HTTP code return value, example 200
		 * @param jsonObject	the result of the server call, can be null
		 * @param errorString	error message, can be null
		 */
		public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString);
	}
	
	
	
	public Server(Context context) {
		m_context = context;
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
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
			
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
	public void register(String username, String password, String firstName, String lastName, String locale, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 422 on a already taken (or missing) email, HTTP 201 on a success
		String urlString = Utilities.makeServerUrlStringNoToken("register");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("email", username));
			pairs.add(new BasicNameValuePair("password", password));
			pairs.add(new BasicNameValuePair("first_name", firstName));
			pairs.add(new BasicNameValuePair("last_name", lastName));
			pairs.add(new BasicNameValuePair("mobile", null));
			pairs.add(new BasicNameValuePair("locale", locale));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
			
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
	public void createFriend(String firstName, String lastName, FriendModel.Threshold threshold, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 422 on an incorrect form (such as a bad threshold), HTTP 201 on a success
		String urlString = Utilities.makeServerUrlString("friends");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("first_name", firstName));
			pairs.add(new BasicNameValuePair("last_name", lastName));
			pairs.add(new BasicNameValuePair("threshold_id", String.valueOf(threshold.serverId)));		
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);			
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "createFriend()", e);
		}
	}
	
	
	/**
	 * Delete an existing friend for the current user.
	 */
	public void deleteFriend(int friendId, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 404 on an unknown friend, HTTP 201 on a success
		String urlString = Utilities.makeServerUrlString("friends/" + String.valueOf(friendId));
		
		try {
			HttpDelete httpRequest = new HttpDelete(urlString);
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "deleteFriend()", e);
		}
	}	
	
	
	public void updateFriend(int friendId, String firstName, String lastName, ServerListener listener, String indicator) {
		// API endpoint
		// returns HTTP 404 on an unknown friend, HTTP 204 on a success
		
		try {
			String urlString = Utilities.makeServerUrlString("friends/" + String.valueOf(friendId));
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("first_name", firstName));
			pairs.add(new BasicNameValuePair("last_name", lastName));					
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
			
			HttpPut httpRequest = new HttpPut(urlString);
			httpRequest.setEntity(formEntity);			
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);				
		} catch (Exception e) {
			Log.e(TAG, "updateFriend()", e);
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
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);				
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "createGroup()", e);
		}
	}
	
	
	/**
	 * Create a new event for the current user.
	 * @param newThreshold	can be null; if not null then this is the new threshold level for the event
	 */
	public void createEvent(int deedId, int friendId, boolean withPrayerSupport, FriendModel.Threshold newThreshold, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
		String urlString = Utilities.makeServerUrlString("events");
		
		try {
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("activity_id", String.valueOf(deedId)));
			pairs.add(new BasicNameValuePair("friend_id", String.valueOf(friendId)));
			pairs.add(new BasicNameValuePair("prayer_requested", withPrayerSupport ? "true" : "false"));
			if (newThreshold != null) {
				pairs.add(new BasicNameValuePair("threshold_id", String.valueOf(newThreshold.serverId)));
			}
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
	 * @param queryString	string to search in group names and descriptions. If null, then return all groups.
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
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
			
			HttpPost httpRequest = new HttpPost(urlString);
			httpRequest.setEntity(formEntity);				
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "createRequest()", e);
		}
	}	
	
	
	/**
	 * Delete an existing request.
	 */
	public void deleteRequest(int requestId, ServerListener listener, String indicator) {
		
		// API endpoint
		// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
		String urlString = Utilities.makeServerUrlString("requests/" + String.valueOf(requestId));
		
		try {
			HttpDelete httpRequest = new HttpDelete(urlString);
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "deleteRequest()", e);
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
			pairs.add(new BasicNameValuePair("accept", String.valueOf(hasAccepted)));			
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
	 * Get the user's account record.
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
	
	
	/**
	 * Update the user's profile. Users can only update their own profile.
	 */
	public void updateUserProfile(int userId, String firstName, String lastName, String email, String mobile, ServerListener listener, String indicator) {
		try {
			// API endpoint
			// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
			String urlString = Utilities.makeServerUrlString("users/" + String.valueOf(userId));
			
			HttpPut httpRequest = new HttpPut(urlString);	
			
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("first_name", String.valueOf(firstName)));			
			pairs.add(new BasicNameValuePair("last_name", String.valueOf(lastName)));
			pairs.add(new BasicNameValuePair("email", String.valueOf(email)));
			pairs.add(new BasicNameValuePair("mobile", String.valueOf(mobile)));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
			
			httpRequest.setEntity(entity);
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "updateUserProfile()", e);
		}		
	}
	
	
	/**
	 * Delete the given group.
	 */
	public void deleteGroup(int groupId, ServerListener listener, String indicator) {

		try {
			// API endpoint
			// returns HTTP 404 on an unknown group, HTTP 201 on a success
			String urlString = Utilities.makeServerUrlString("groups/" + String.valueOf(groupId));		
			
			HttpDelete httpRequest = new HttpDelete(urlString);
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "deleteGroup()", e);
		}
	}	
	
	
	/**
	 * Delete the given user from the given group.
	 */
	public void deleteUserFromGroup(int groupId, int userId, ServerListener listener, String indicator) {

		try {
			// API endpoint
			// returns HTTP 404 on an unknown group, HTTP 201 on a success
			String urlString = Utilities.makeServerUrlString("groups/" + String.valueOf(groupId) + "/users/" + String.valueOf(userId));		
			
			HttpDelete httpRequest = new HttpDelete(urlString);
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "deleteUserFromGroup()", e);
		}
	}
	
	
	/**
	 * Pledge to pray for the given event.
	 */
	public void pledgeToPray(int eventId, ServerListener listener, String indicator) {
		
		try {
			// API endpoint
			// returns HTTP 404 on an unknown group, HTTP 201 on a success
			// returns "errors" error string if already pledged to pray for this event
			String urlString = Utilities.makeServerUrlString("events/" + String.valueOf(eventId) + "/pledge");		
			
			HttpPost httpRequest = new HttpPost(urlString);
			new ServerCall(httpRequest, listener, indicator).execute(urlString);					
		} catch (Exception e) {
			Log.e(TAG, "pledgeToPray()", e);
		}
		
	}
	
	
	/**
	 * Update the image at <urlPrefix>/<id>. The image must already be in the local file cache.
	 * @param urlPrefix		either "users", "friends" or "activities"
	 * @param id
	 * @param bitmap
	 * @param listener
	 * @param indicator
	 */
	public void updateImage(String urlPrefix, int id, ServerListener listener, String indicator) {
		try {
			// API endpoint
			// returns HTTP 422 on an incorrect form (such as a missing name), HTTP 201 on a success
			String urlString = Utilities.makeServerUrlString(urlPrefix + "/" + String.valueOf(id));

			HttpPut httpRequest = new HttpPut(urlString);
			
			// only update the image, and the server will also update its thumbnail
			File file = new File(BitmapCacheHandler.generateFullCacheFileName(urlPrefix, id, "image"));
			
			// multipart entity not supported in standard Android HttpClient library, so this uses a downloaded version of apache HttpClient instead.
			org.apache.http.entity.mime.MultipartEntity entity = 
				new org.apache.http.entity.mime.MultipartEntity(org.apache.http.entity.mime.HttpMultipartMode.STRICT);
			org.apache.http.entity.mime.content.FileBody fileBody = 
				new org.apache.http.entity.mime.content.FileBody(file, "image/jpeg");
			entity.addPart("image", fileBody);
					
			httpRequest.setEntity(entity);
						
			new ServerCall(httpRequest, listener, indicator).execute(urlString);			
		} catch (Exception e) {
			Log.e(TAG, "updateImage()", e);
		}		
	}
	
	
	
	/********************************* Background thread Server access task *************************************/
	
	private class ServerCall extends AsyncTask<String, Void, JSONObject> {
		
		private HttpUriRequest m_httpRequest = null;
		private ServerListener m_listener = null;
		private String m_indicator = null;
		private int m_httpCode = -1;
		private boolean m_connectionTimeout = false;
		private String m_errorString = null;
		private boolean m_isBadConnection = false;
		
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
			HttpEntity httpEntity = null;
			ByteArrayOutputStream outStream = null;
			
			// try multiple retries for connection timeouts
			m_connectionTimeout = true;
			for (int i = 0; i < 4 && m_connectionTimeout; i++) {
				m_connectionTimeout = false;
				try {			
					Log.d(TAG, i + " STARTING ServerCall " + m_httpRequest.getMethod() + " " + urls[0]);
									
					httpClient = AndroidHttpClient.newInstance(m_indicator);
					httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TheLifeConfiguration.HTTP_SERVER_CONNECTION_TIMEOUT);
					httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, TheLifeConfiguration.HTTP_READ_TIMEOUT);
					
					HttpResponse httpResponse = httpClient.execute(m_httpRequest);
					m_httpCode = httpResponse.getStatusLine().getStatusCode();
										
					String jsonString = null;
					httpEntity = httpResponse.getEntity();
					
					// make sure there is a response to read
					if (httpEntity != null) {
						outStream = new ByteArrayOutputStream();						
						httpEntity.writeTo(outStream);		
						jsonString = outStream.toString("UTF-8");
					}
					
					Log.d(TAG, "GOT THE SERVER CALL RESPONSE " + m_httpCode + " " + jsonString);					
					
					if (jsonString != null) {
						jsonString = jsonString.trim();
						
						// look for the JSON reply value
						if (jsonString.length() > 0) {
							// if the result is a JSONArray, wrap it inside a JSONObject called "a"
							if (jsonString.charAt(0) == '[') {
								JSONArray jsonArray = new JSONArray(jsonString);
								jsonObject = new JSONObject();
								jsonObject.put("a",  jsonArray);
							} else {
								jsonObject = new JSONObject(jsonString);
								
								// look for an error
								JSONObject errorObject = jsonObject.optJSONObject("errors");
								if (errorObject != null) {
									// there is an error, so build a string from the JSON
									JSONArray names = errorObject.names();
									if (names.length() > 0) {
										String errorKey = (String)names.get(0);
										JSONArray jsonArrayErrors = errorObject.optJSONArray(errorKey);
										jsonArrayErrors.get(0);
										if (jsonArrayErrors.length() > 0) {
											m_errorString = jsonArrayErrors.getString(0);
										}
									}
								}
							}
						}
						
	
					}
				} catch (JSONException e) {
					Log.wtf(TAG, "ServerCall.doInBackground()1", e);				
				} catch (MalformedURLException e) {
					Log.wtf(TAG, "ServerCall.doInBackground()2", e);
				} catch (org.apache.http.conn.ConnectTimeoutException e) {
					m_connectionTimeout = true;
					Log.e(TAG, "ServerCall.doInBackground()3", e);
				} catch (java.net.SocketTimeoutException e) {
					m_connectionTimeout = true;
					Log.e(TAG, "ServerCall.doInBackground()4 " + e.getMessage());
				} catch (org.apache.http.conn.HttpHostConnectException e) {
					m_isBadConnection = true;
					Log.e(TAG, "ServerCall.doInBackground()5" + e.getMessage());
				} catch (java.net.UnknownHostException e) {
					m_isBadConnection = true;
					Log.e(TAG, "ServerCall.doInBackground()5" + e.getMessage());					
				} catch (IOException e) {
					Log.e(TAG, "ServerCall.doInBackground()6", e);
				} catch (Exception e) {
					Log.e(TAG, "ServerCall.doInBackground()7", e);
				} finally {
					if (outStream != null) {
						try { outStream.close(); } catch (Exception e) { }
						outStream = null;
					}
					if (httpEntity != null) {
						try { httpEntity.consumeContent(); } catch (Exception e) { }
						httpEntity = null;
					}
					if (httpClient != null) {
						
						// from the Apache doc; may help connection timeout problems?
						httpClient.getConnectionManager().closeExpiredConnections();
						
						httpClient.close();
						httpClient = null;
					}				
				}
			}
			
			return jsonObject;
		}
		
		// UI thread		
		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			if (m_connectionTimeout) {
				Utilities.showConnectionErrorToast(m_context, "SERVER CONN TIMEOUT " + m_indicator, Toast.LENGTH_SHORT);
				m_connectionTimeout = false;
			}
			if (m_errorString != null) {
				Utilities.showErrorToast(m_context, m_context.getResources().getString(R.string.error_prefix) + " " + m_errorString, Toast.LENGTH_SHORT);
			} else if (m_isBadConnection) {
				Utilities.showErrorToast(m_context, 
										 m_context.getResources().getString(R.string.error_prefix) + " " + 
										 m_context.getResources().getString(R.string.error_bad_connection), Toast.LENGTH_SHORT);
				m_isBadConnection = false;				
			}

			m_context = null;
			
			Log.d(TAG, "HERE IN ON POST EXECUTE with " + m_indicator);
			
			m_listener.notifyServerResponseAvailable(m_indicator, m_httpCode, jsonObject, m_errorString);
		}
	}

}
