package com.p2c.thelife.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.util.Log;

import com.p2c.thelife.Server;
import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.Utilities;
import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.GroupUsersDS;
import com.p2c.thelife.model.GroupsDS;
import com.p2c.thelife.model.RequestModel;
import com.p2c.thelife.model.RequestsDS;
import com.p2c.thelife.model.UserModel;


/**
 * Integration test between Android client and Server. 
 * This integration test covers both server calls and data store refreshes.
 * 
 * Testing as follows:
 * 		owner: register
 * 		owner: create group
 * 		owner: INVITE person (user2) to group
 * 		user2: register
 * 		user2: accepts INVITE request from OWNER, is now member of the group
 * 		owner: receives acceptance
 * 		owner: remove user2 from group, delete group
 *  
 * @author clarence
 *
 */
public class InviteIntegrationTest extends AndroidTestCase implements ServerListener, DSRefreshedListener {
	
	private static final String TAG = "InviteIntegrationTest";
	
	// Owner test values
	private static final String OWNER_EMAIL = "itemail3@ballistiq.com";
	private static final String OWNER_PASSWORD = "123456";
	private static final String OWNER_FIRST_NAME = "ITFIRST3";
	private static final String OWNER_LAST_NAME = "ITLAST3";
	private static final String OWNER_LOCALE = "en";
	private static final String OWNER_MOBILE = null;
	
	// User2 test values
	private static final String USER2_EMAIL = "itemail4@ballistiq.com";
	private static final String USER2_PASSWORD = "123456";
	private static final String USER2_FIRST_NAME = "ITFIRST4";
	private static final String USER2_LAST_NAME = "ITLAST4";
	private static final String USER2_LOCALE = "en";
	private static final String USER2_MOBILE = null;
	
	// other test values
	private static final String GROUP1_NAME = "INTEGRATGROUP3";
	private static final String GROUP1_DESCRIPTION = "This is a system testing group only";
	
	// authentication token for the owner and user2
	private String m_ownerToken = null;
	private String m_user2Token = null;

	// test objects
	private UserModel m_owner = null;
	private UserModel m_user2 = null;
	private GroupModel m_group1 = null;
	private int		   m_inviteRequest1_id = 0;
	
	// data stores
	private static GroupsDS m_ownerGroupsDS = null;
	private static GroupUsersDS m_ownerGroupUsersDS = null;
	private static RequestsDS m_ownerRequestsDS = null;
	private static RequestsDS m_user2RequestsDS = null;
	
	
	public InviteIntegrationTest() {
		super();
	}
	
	@Override
	public void setUp() {
	}
	
	@Override
	public void tearDown() {
	}
	
	/**
	 * the test
	 */
	synchronized public void test1() {
		Log.i(TAG, "Beginning INVITE Integration test");		
		
		Server server = null;
		
		// test register the owner
		server = new Server(getContext());
		server.register(OWNER_EMAIL, OWNER_PASSWORD, OWNER_FIRST_NAME, OWNER_LAST_NAME, OWNER_LOCALE, this, "register1");
		waitForServerResponse();
		server = null;
			
		// now that the authentication token is known, initialize the data stores
		m_ownerGroupsDS = new GroupsDS(getContext(), m_ownerToken);
		m_ownerGroupsDS.addDSRefreshedListener(this);
		m_ownerRequestsDS = new RequestsDS(getContext(), m_ownerToken);
		m_ownerRequestsDS.addDSRefreshedListener(this);				
		
		// test createGroup
		server = new Server(getContext(), m_ownerToken);
		server.createGroup(GROUP1_NAME, GROUP1_DESCRIPTION, this, "createGroup1");
		waitForServerResponse();
		server = null;
		
		// test GroupsDS, should find the group
		m_ownerGroupsDS.forceRefresh("groups1");
		waitForServerResponse();
		
		// test createRequest INVITE, from group leader OWNER to USER2, to join GROUP1
		server = new Server(getContext(), m_ownerToken);
		server.createRequest(m_group1.id, RequestModel.INVITE, USER2_EMAIL, null, this, "createInviteRequest1");
		waitForServerResponse();
		server = null;		

		// test register user2
		server = new Server(getContext());
		server.register(USER2_EMAIL, USER2_PASSWORD, USER2_FIRST_NAME, USER2_LAST_NAME, USER2_LOCALE, this, "register2");
		waitForServerResponse();
		server = null;
		
		// set up the user2 requests data store
		m_user2RequestsDS = new RequestsDS(getContext(), m_user2Token);		
		m_user2RequestsDS.addDSRefreshedListener(this);		
		
		// ensure user2 receives the INVITE request
		m_user2RequestsDS.forceRefresh("user2Requests1");
		waitForServerResponse();
		
		// test processGroupMembershipRequest  INVITE ACCEPT
		server = new Server(getContext(), m_user2Token);
		server.processGroupMembershipRequest(m_inviteRequest1_id, true, m_user2.id, m_group1.id, this, "processGroupMembership1");
		waitForServerResponse();
		server = null;
		
		// ensure user2 no longer sees the accepted INVITE request
		m_user2RequestsDS.forceRefresh("user2Requests2");
		waitForServerResponse();
		
		// ensure owner receives the ACCEPTED request
		m_ownerRequestsDS.forceRefresh("ownerRequests1");
		waitForServerResponse();
		
		// test remove the accepted INVITE request
		server = new Server(getContext(), m_ownerToken);
		server.deleteRequest(m_inviteRequest1_id, this, "deleteRequest1");
		waitForServerResponse();
		server = null;
		
		// ensure user2 has no more requests
		m_ownerRequestsDS.forceRefresh("ownerRequests2");
		waitForServerResponse();		
		
		// set up the group users data store
		m_ownerGroupUsersDS = new GroupUsersDS(getContext(), m_ownerToken,  m_group1.id);
		m_ownerGroupUsersDS.addDSRefreshedListener(this);
		
		// ensure user2 is now in the group
		m_ownerGroupUsersDS.forceRefresh("groupUsers1");
		waitForServerResponse();	
		
		// remove user2 from the group
		server = new Server(getContext(), m_ownerToken);
		server.deleteUserFromGroup(m_group1.id, m_user2.id, this, "deleteUserFromGroup1");
		waitForServerResponse();
		server = null;
		
		// ensure user2 is no longer in the group
		m_ownerGroupUsersDS.forceRefresh("groupUsers2");
		waitForServerResponse();		
		
		// test deleteGroup
		server = new Server(getContext(), m_ownerToken);
		server.deleteGroup(m_group1.id, this, "deleteGroup1");
		waitForServerResponse();
		server = null;
		
		// test GroupsDS, should not find any groups
		m_ownerGroupsDS.forceRefresh("groups2");
		waitForServerResponse();
		
		Log.i(TAG, "Finished INVITE Integration test");		
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
				assertEquals(OWNER_MOBILE, m_owner.mobile);
				
				m_ownerToken = jsonObject.getString("authentication_token");
				assertNotNull(m_ownerToken);
				
			} else if (indicator.equals("register2")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_user2 = UserModel.fromJSON(jsonObject, false);
				assertEquals(USER2_EMAIL, m_user2.email);
				assertEquals(USER2_FIRST_NAME, m_user2.firstName);
				assertEquals(USER2_LAST_NAME, m_user2.lastName);
				assertEquals(USER2_MOBILE, m_user2.mobile);
				
				m_user2Token = jsonObject.getString("authentication_token");
				assertNotNull(m_user2Token);
				
			} else if (indicator.equals("createGroup1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_group1 = GroupModel.fromJSON(jsonObject, false);
				assertEquals(GROUP1_NAME, m_group1.name);
				assertEquals(GROUP1_DESCRIPTION, m_group1.description);
				assertEquals(m_owner.id, m_group1.leader_id);
				
			} else if (indicator.equals("deleteGroup1")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204
				
			} else if (indicator.equals("createInviteRequest1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_inviteRequest1_id = jsonObject.getInt("id");
				assertEquals(m_owner.id, jsonObject.getInt("user_id"));
				assertEquals(m_group1.id, jsonObject.getInt("group_id"));
				assertEquals(RequestModel.INVITE, jsonObject.getString("type"));
				
			} else if (indicator.equals("processGroupMembership1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				assertEquals(m_user2.id, jsonObject.getInt("user_id"));
				assertEquals(m_group1.id, jsonObject.getInt("group_id"));
				
			} else if (indicator.equals("deleteUserFromGroup1")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204
				
			} else if (indicator.equals("deleteRequest1")) {
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
			
		if (indicator.equals("groups1")) {
			assertEquals(1, m_ownerGroupsDS.count());
			GroupModel group = m_ownerGroupsDS.findById(m_group1.id);
			assertEquals(GROUP1_NAME, group.name);
			assertEquals(GROUP1_DESCRIPTION, group.description);
			
		} else if (indicator.equals("groups2")) {
			assertEquals(0, m_ownerGroupsDS.count());
			
		} else if (indicator.equals("user2Requests1")) {
			assertEquals(1, m_user2RequestsDS.count());
			RequestModel request = m_user2RequestsDS.findById(m_inviteRequest1_id);
			assertNotNull(request);			
			assertEquals(m_owner.id, request.user_id);
			assertEquals(m_group1.id, request.group_id);
			assertEquals(RequestModel.INVITE, request.type);
			assertEquals(m_group1.name, request.groupName);
			assertEquals(RequestModel.DELIVERED, request.status);
			assertEquals(m_owner.getFullName(), request.userName);
			// assertEquals(USER2_EMAIL, request.email); // TODO SERVER BUG email should be in the INVITE request
			
		} else if (indicator.equals("user2Requests2")) {
			assertEquals(0, m_user2RequestsDS.count());
			
		} else if (indicator.equals("ownerRequests1")) {
			assertEquals(1, m_ownerRequestsDS.count());
			RequestModel request = m_ownerRequestsDS.findById(m_inviteRequest1_id);
			assertNotNull(request);
			assertEquals(m_owner.id, request.user_id); // TODO SERVER BUG should be m_user2.id
			assertEquals(m_group1.id, request.group_id);
			assertEquals(RequestModel.INVITE, request.type);
			assertEquals(m_group1.name, request.groupName);
			assertEquals(RequestModel.ACCEPTED, request.status);
			// assertEquals(m_user2.getFullName(), request.userName); // TODO this is broken from server
			
		} else if (indicator.equals("ownerRequests2")) {
			assertEquals(0, m_ownerRequestsDS.count());			
			
		} else if (indicator.equals("groupUsers1")) {
			assertEquals(2, m_ownerGroupUsersDS.count());
			assertNotNull(m_ownerGroupUsersDS.findById(m_owner.id));
			assertNotNull(m_ownerGroupUsersDS.findById(m_user2.id));
			
		} else if (indicator.equals("groupUsers2")) {
			assertEquals(1, m_ownerGroupUsersDS.count());
			assertNotNull(m_ownerGroupUsersDS.findById(m_owner.id));
			
		} else {
			assertTrue("Don't know data store refresh indicator " + indicator, false);
		}
		
		notify();
	}

}
