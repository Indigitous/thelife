package com.p2c.thelife.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;
import android.util.Log;

import com.p2c.thelife.BitmapCacheHandler;
import com.p2c.thelife.Server;
import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.Utilities;
import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.UserModel;

/**
 * Integration test between Android client and Server. 
 * This integration test covers both server calls and data store refreshes.
 * 
 * Testing as follows:
 * 		owner: register and login
 * 		owner: query profile
 * 		owner: update profile
 * 		owner: update image
 *  
 * @author clarence
 *
 */
public class UserIntegrationTest extends AndroidTestCase implements ServerListener, DSRefreshedListener {
	
	private static final String TAG = "UserIntegrationTest";
	
	// Owner test values
	private static final String OWNER_EMAIL = "itest----email5@ballistiq.com";
	private static final String OWNER_PASSWORD = "123456";
	private static final String OWNER_FIRST_NAME = "ITFIRST5";
	private static final String OWNER_LAST_NAME = "ITLAST5";
	private static final String OWNER_LOCALE = "en";
	private static final String OWNER_MOBILE = "555-5555";
	private static final String CHANGE_SUFFIX = "-1";
	
	// authentication token for the owner and user2
	private String m_ownerToken = null;

	// test objects
	private UserModel m_owner = null;
	
	
	public UserIntegrationTest() {
		super();
	}
	
	
	@Override
	public void setUp() {
		try { super.setUp(); } catch (Exception e) { Log.e(TAG, "setUp()", e); }

		m_ownerToken = null;
	}
	
	
	@Override
	public void tearDown() {
		try { super.setUp(); } catch (Exception e) { Log.e(TAG, "tearDown()", e); }

		// delete test user
		if (m_ownerToken != null) {
			Server server = null;
			server = new Server(getContext(), m_ownerToken);
			server.deleteUser(this, "deleteUser");
			waitForServerResponse();			
		}
	}
	
	/**
	 * the test
	 */
	synchronized public void test1() {
		
		Log.i(TAG, "Beginning USER Integration test");		
		
		Server server = null;
		
		// test register the owner
		server = new Server(getContext());
		server.register(OWNER_EMAIL, OWNER_PASSWORD, OWNER_FIRST_NAME, OWNER_LAST_NAME, OWNER_LOCALE, this, "register1");
		waitForServerResponse();
		server = null;
		
		// test login
		server = new Server(getContext());
		server.login(OWNER_EMAIL, OWNER_PASSWORD, this, "login1");
		waitForServerResponse();
		server = null;
				
		// test queryUserProfile
		server = new Server(getContext(), m_ownerToken);
		server.queryUserProfile(m_owner.id, this, "queryUserProfile1");
		waitForServerResponse();
		server = null;
		
		// test updateUserProfile
		server = new Server(getContext(), m_ownerToken);
		server.updateUserProfile(m_owner.id, OWNER_FIRST_NAME + CHANGE_SUFFIX, OWNER_LAST_NAME + CHANGE_SUFFIX, OWNER_EMAIL + CHANGE_SUFFIX, OWNER_MOBILE, this, "updateUserProfile1");
		waitForServerResponse();
		server = null;		
		
		// test queryUserProfile
		server = new Server(getContext(), m_ownerToken);
		server.queryUserProfile(m_owner.id, this, "queryUserProfile2");
		waitForServerResponse();
		server = null;
		
		// test updateImage
		Bitmap bitmap = Bitmap.createBitmap(160, 160, Bitmap.Config.ARGB_8888);
		BitmapCacheHandler.saveBitmapToCache("users", m_owner.id, "image", bitmap);
		server = new Server(getContext(), m_ownerToken);
		server.updateImage("users", m_owner.id, this, "updateImage");
		waitForServerResponse();
		server = null;		
		
		Log.i(TAG, "Finished USER Integration test");				
	}
	

	/**
	 * Test the server callback values.
	 */
	@Override
	synchronized public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		
		System.out.println("NOTIFY SERVER RESPONSE " + indicator);		
		try {
			if (indicator.equals("register1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_owner = UserModel.fromJSON(jsonObject, false);
				assertEquals(OWNER_EMAIL, m_owner.email);
				assertEquals(OWNER_FIRST_NAME, m_owner.firstName);
				assertEquals(OWNER_LAST_NAME, m_owner.lastName);
				assertEquals(null, m_owner.mobile);
				
				m_ownerToken = jsonObject.getString("authentication_token");
				assertNotNull(m_ownerToken);
				
			} else if (indicator.equals("login1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				UserModel user = UserModel.fromJSON(jsonObject, false);
				assertEquals(OWNER_EMAIL, user.email);
				assertEquals(OWNER_FIRST_NAME, user.firstName);
				assertEquals(OWNER_LAST_NAME, user.lastName);
				assertEquals(null, user.mobile);
				assertEquals(m_owner.id, user.id);
				String token = jsonObject.getString("authentication_token");
				assertEquals(token, m_ownerToken);	
								
			} else if (indicator.equals("queryUserProfile1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_owner.setFromPartialJSON(jsonObject);
				assertEquals(OWNER_EMAIL, m_owner.email);
				assertEquals(OWNER_FIRST_NAME, m_owner.firstName);
				assertEquals(OWNER_LAST_NAME, m_owner.lastName);
				assertEquals(null, m_owner.mobile);
				
			} else if (indicator.equals("updateUserProfile1")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204		
				
			} else if (indicator.equals("queryUserProfile2")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				assertEquals(OWNER_EMAIL + CHANGE_SUFFIX, jsonObject.getString("email"));
				assertEquals(OWNER_FIRST_NAME + CHANGE_SUFFIX, jsonObject.getString("first_name"));
				assertEquals(OWNER_LAST_NAME + CHANGE_SUFFIX, jsonObject.getString("last_name"));
				assertEquals(OWNER_MOBILE, jsonObject.getString("mobile"));				
								
			}  else if (indicator.equals("updateImage")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204
				
			}  else if (indicator.equals("deleteUser")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204				
								
			} else {
				assertTrue("Don't know server response indicator " + indicator, false);
			}
		} catch (JSONException e) {
			assertTrue("AssertServerSuccess " + indicator + " JSONException: " + jsonObject + e, false);
		}
		
		notify();
	}
	
	
	private void assertServerSuccess(String indicator, int httpCode, String errorString) {
		assertTrue("AssertServerSuccess " + indicator + ": " + httpCode + " " + errorString, Utilities.isSuccessfulHttpCode(httpCode));
	}
	
	
	synchronized private void waitForServerResponse() {
		try { 
			wait(30000);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	
	/**
	 * Test the values provided by the data store refresh callback.
	 */
	@Override
	synchronized public void notifyDSRefreshed(String indicator) {
		
		System.out.println("NOTIFY DS REFRESHED " + indicator);
		
		assertTrue("Don't know data store refresh indicator " + indicator, false);
		
		notify();
	}

}
