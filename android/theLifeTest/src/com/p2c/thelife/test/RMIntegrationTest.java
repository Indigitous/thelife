package com.p2c.thelife.test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.util.Log;

import com.p2c.thelife.Server;
import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.Utilities;
import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.FriendsDS;
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
 * 		owner: create and update friend
 * 		owner: create an event with friend
 * 		owner: create group
 * 		user2: register
 * 		user2: look for group
 * 		user2: REQUEST_MEMBERSHIP for group to group leader, owner
 * 		owner: accepts request
 * 		user2: receives acceptance, is now member of the group
 * 		user2: pledges to pray for event between owner and owner's friend
 * 		owner: delete friend, remove user2 from group, delete group
 *  
 * @author clarence
 *
 */
public class RMIntegrationTest extends AndroidTestCase implements ServerListener, DSRefreshedListener {
	
	private static final String TAG = "RMIntegrationTest";
	
	// Owner test values
	private static final String OWNER_EMAIL = "itemail1@ballistiq.com";
	private static final String OWNER_PASSWORD = "123456";
	private static final String OWNER_FIRST_NAME = "ITFIRST1";
	private static final String OWNER_LAST_NAME = "ITLAST1";
	private static final String OWNER_LOCALE = "en";
	private static final String OWNER_MOBILE = null;
	
	// User2 test values
	private static final String USER2_EMAIL = "itemail2@ballistiq.com";
	private static final String USER2_PASSWORD = "123456";
	private static final String USER2_FIRST_NAME = "ITFIRST2";
	private static final String USER2_LAST_NAME = "ITLAST2";
	private static final String USER2_LOCALE = "en";
	private static final String USER2_MOBILE = null;	
	
	// other test values
	private static final String FRIEND1_FIRST_NAME = "ITFFIRST1";
	private static final String FRIEND1_LAST_NAME = "ITFLAST1";
	private static final FriendModel.Threshold FRIEND1_THRESHOLD = FriendModel.Threshold.Curious;
	private static final String FRIEND1_FIRST_NAME2 = "ITFFIRST1-2";
	private static final String FRIEND1_LAST_NAME2 = "ITFLAST1-2";
	private static final FriendModel.Threshold FRIEND1_THRESHOLD2 = FriendModel.Threshold.Trusting;	
	private static final int	DEED_ID = 1; // Change Threshold deed/activity
	private static final String GROUP1_NAME = "INTEGRATGROUP1";
	private static final String GROUP1_DESCRIPTION = "This is a system testing group only";
	
	// authentication token for the owner and user2
	private String m_ownerToken = null;
	private String m_user2Token = null;

	// test objects
	private UserModel m_owner = null;
	private UserModel m_user2 = null;
	private FriendModel m_friend1 = null;
	private EventModel m_event1 = null;
	private GroupModel m_group1 = null;
	private int		   m_rmRequest1_id = 0;
	
	// data stores
	private static FriendsDS m_ownerFriendsDS = null;
	private static GroupsDS m_ownerGroupsDS = null;
	private static GroupUsersDS m_ownerGroupUsersDS = null;
	private static EventsDS m_ownerEventsDS = null;
	private static EventsDS m_user2EventsDS = null;	
	private static RequestsDS m_ownerRequestsDS = null;
	private static RequestsDS m_user2RequestsDS = null;
	
	
	public RMIntegrationTest() {
		super();
	}
	
	
	@Override
	public void setUp() {
		try { super.setUp(); } catch (Exception e) { Log.e(TAG, "setUp()", e); }

		m_ownerToken = null;
		m_user2Token = null;
	}
	
	
	@Override
	public void tearDown() {
		try { super.setUp(); } catch (Exception e) { Log.e(TAG, "tearDown()", e); }

		// delete test owner
		if (m_ownerToken != null) {
			Server server = null;
			server = new Server(getContext(), m_ownerToken);
			server.deleteUser(this, "deleteUser");
			waitForServerResponse();			
		}

		// delete test user
		if (m_user2Token != null) {
			Server server = null;
			server = new Server(getContext(), m_user2Token);
			server.deleteUser(this, "deleteUser");
			waitForServerResponse();			
		}		
	}
	
	
	/**
	 * the test
	 */
	synchronized public void test1() {
		Log.i(TAG, "Beginning REQUEST MEMBERSHIP Integration test");
		
		Server server = null;
		
		// test register the owner
		server = new Server(getContext());
		server.register(OWNER_EMAIL, OWNER_PASSWORD, OWNER_FIRST_NAME, OWNER_LAST_NAME, OWNER_LOCALE, this, "register1");
		waitForServerResponse();
		server = null;
		
		// now that the authentication token is known, initialize the data stores
		m_ownerGroupsDS = new GroupsDS(getContext(), m_ownerToken);
		m_ownerGroupsDS.addDSRefreshedListener(this);
		m_ownerFriendsDS = new FriendsDS(getContext(), m_ownerToken);
		m_ownerFriendsDS.addDSRefreshedListener(this);		
		m_ownerEventsDS = new EventsDS(getContext(), m_ownerToken);
		m_ownerEventsDS.addDSRefreshedListener(this);
		m_ownerRequestsDS = new RequestsDS(getContext(), m_ownerToken);
		m_ownerRequestsDS.addDSRefreshedListener(this);		
				
		// test createFriend
		server = new Server(getContext(), m_ownerToken);
		server.createFriend(FRIEND1_FIRST_NAME, FRIEND1_LAST_NAME, FRIEND1_THRESHOLD, this, "createFriend1");
		waitForServerResponse();
		server = null;
		
		// test FriendsDS, should find the friend
		m_ownerFriendsDS.forceRefresh("friends1");
		waitForServerResponse();
		
		// test updateFriend
		server = new Server(getContext(), m_ownerToken);
		server.updateFriend(m_friend1.id, FRIEND1_FIRST_NAME2, FRIEND1_LAST_NAME2, this, "updateFriend1");
		waitForServerResponse();
		server = null;
		
		// test FriendsDS, should find the updated friend
		m_ownerFriendsDS.forceRefresh("friends2");
		waitForServerResponse();		
		
		// test createEvent
		server = new Server(getContext(), m_ownerToken);
		server.createEvent(DEED_ID, m_friend1.id, true, FRIEND1_THRESHOLD2, this, "createEvent1");
		waitForServerResponse();
		server = null;
		
		// test EventsDS, should find the event
		m_ownerEventsDS.forceRefresh("ownerEvents1");
		waitForServerResponse();		
		
		// test createGroup
		server = new Server(getContext(), m_ownerToken);
		server.createGroup(GROUP1_NAME, GROUP1_DESCRIPTION, this, "createGroup1");
		waitForServerResponse();
		server = null;
		
		// test GroupsDS, should find the group
		m_ownerGroupsDS.forceRefresh("groups1");
		waitForServerResponse();
		
		// test register user2
		server = new Server(getContext());
		server.register(USER2_EMAIL, USER2_PASSWORD, USER2_FIRST_NAME, USER2_LAST_NAME, USER2_LOCALE, this, "register2");
		waitForServerResponse();
		server = null;
		
		// test queryGroups, should find the group
		server = new Server(getContext(), m_user2Token);
		server.queryGroups(GROUP1_NAME, this, "queryGroups1");
		waitForServerResponse();
		server = null;		
		
		// test createRequest REQUEST_MEMBERSHIP from USER2, to group leader OWNER, to join GROUP1
		server = new Server(getContext(), m_user2Token);
		server.createRequest(m_group1.id, RequestModel.REQUEST_MEMBERSHIP, null, null, this, "createRMRequest1");
		waitForServerResponse();
		server = null;
		
		// set up the user2 requests data store
		m_user2RequestsDS = new RequestsDS(getContext(), m_user2Token);		
		m_user2RequestsDS.addDSRefreshedListener(this);		
		
		// ensure user2 does not receive the REQUEST_MEMBERSHIP request
		m_user2RequestsDS.forceRefresh("user2Requests1");
		waitForServerResponse();		
		
		// ensure owner receives the REQUEST_MEMBERSHIP request
		m_ownerRequestsDS.forceRefresh("ownerRequests1");
		waitForServerResponse();
		
		// test processGroupMembershipRequest  REQUEST_MEMBERSHIP ACCEPT
		server = new Server(getContext(), m_ownerToken);
		server.processGroupMembershipRequest(m_rmRequest1_id, true, m_user2.id, m_group1.id, this, "processGroupMembership1");
		waitForServerResponse();
		server = null;
		
		// ensure owner no longer sees the accepted REQUEST_MEMBERSHIP request
		m_ownerRequestsDS.forceRefresh("ownerRequests2");
		waitForServerResponse();
		
		// ensure user2 receives the ACCEPTED request
		m_user2RequestsDS.forceRefresh("user2Requests2");
		waitForServerResponse();
		
		// test remove the accepted REQUEST_MEMBERSHIP request
		server = new Server(getContext(), m_user2Token);
		server.deleteRequest(m_rmRequest1_id, this, "deleteRequest1");
		waitForServerResponse();
		server = null;
		
		// ensure user2 has no more requests
		m_user2RequestsDS.forceRefresh("user2Requests3");
		waitForServerResponse();		
		
		// set up the group users data store
		m_ownerGroupUsersDS = new GroupUsersDS(getContext(), m_ownerToken,  m_group1.id);
		m_ownerGroupUsersDS.addDSRefreshedListener(this);
		
		// ensure user2 is now in the group
		m_ownerGroupUsersDS.forceRefresh("groupUsers1");
		waitForServerResponse();
		
		// set up the user2 events data store
		m_user2EventsDS = new EventsDS(getContext(), m_user2Token);		
		m_user2EventsDS.addDSRefreshedListener(this);			
		
		// ensure user2 can see the event between the owner and the owner's friend
		m_user2EventsDS.forceRefresh("user2Events1");
		waitForServerResponse();		
		
		// test pledgeToPray
		server = new Server(getContext(), m_user2Token);
		server.pledgeToPray(m_event1.id, this, "pledgeToPray1");
		waitForServerResponse();
		server = null;		
		
		// test deleteFriend, should delete owner's friend
		server = new Server(getContext(), m_ownerToken);
		server.deleteFriend(m_friend1.id, this, "deleteFriend1");
		waitForServerResponse();
		server = null;
		
		// test FriendsDS, should not find any friends
		m_ownerFriendsDS.forceRefresh("friends3");
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
		
		Log.i(TAG, "Finished REQUEST MEMBERSHIP Integration test");
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
				
			} else if (indicator.equals("queryGroups1")) {
				assertServerSuccess(indicator, httpCode, errorString);

				JSONArray jsonArray = jsonObject.getJSONArray("a");
				assertEquals(1, jsonArray.length());
				GroupModel group = GroupModel.fromJSON(jsonArray.getJSONObject(0), false);
				assertEquals(m_group1.id, group.id);
				assertEquals(m_group1.name, group.name);			
				assertEquals(m_group1.description, group.description);
				assertEquals(m_owner.id, group.leader_id);
				
			} else if (indicator.equals("createFriend1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_friend1 = FriendModel.fromJSON(jsonObject, false);
				assertEquals(FRIEND1_FIRST_NAME, m_friend1.firstName);
				assertEquals(FRIEND1_LAST_NAME, m_friend1.lastName);
				assertEquals(FRIEND1_THRESHOLD, m_friend1.threshold);
				
			} else if (indicator.equals("updateFriend1")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204
				
			} else if (indicator.equals("createEvent1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_event1 = EventModel.fromJSON(getContext().getResources(), jsonObject, false);
				assertEquals(DEED_ID, m_event1.deed_id);
				assertEquals(m_friend1.id, m_event1.friend_id);
				assertEquals(m_owner.id, m_event1.user_id);
				assertEquals(true, m_event1.isPrayerRequested);
				assertEquals(FRIEND1_THRESHOLD2, m_event1.threshold);
				assertEquals("<b>" + "???" + "</b> has moved <b>" + "???" + "</b> to <b>" + 
					FriendModel.getThresholdShortString(getContext().getResources(), FRIEND1_THRESHOLD2) + "</b>", m_event1.finalDescription);
				
			} else if (indicator.equals("deleteFriend1")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204
				
			} else if (indicator.equals("createGroup1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_group1 = GroupModel.fromJSON(jsonObject, false);
				assertEquals(GROUP1_NAME, m_group1.name);
				assertEquals(GROUP1_DESCRIPTION, m_group1.description);
				assertEquals(m_owner.id, m_group1.leader_id);
				
			} else if (indicator.equals("deleteGroup1")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204
				
			} else if (indicator.equals("createRMRequest1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_rmRequest1_id = jsonObject.getInt("id");
				assertEquals(m_user2.id, jsonObject.getInt("user_id"));
				assertEquals(m_group1.id, jsonObject.getInt("group_id"));
				assertEquals(RequestModel.REQUEST_MEMBERSHIP, jsonObject.getString("type"));
				
			} else if (indicator.equals("processGroupMembership1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				assertEquals(m_user2.id, jsonObject.getInt("user_id"));
				assertEquals(m_group1.id, jsonObject.getInt("group_id"));
				
			} else if (indicator.equals("deleteUserFromGroup1")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204
				
			} else if (indicator.equals("deleteRequest1")) {
				assertServerSuccess(indicator, httpCode, errorString); // HTTP 204
				
			} else if (indicator.equals("pledgeToPray1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				assertEquals(m_user2.id, jsonObject.getInt("user_id"));
				assertEquals(m_event1.id, jsonObject.getInt("event_id"));
				assertEquals(1, jsonObject.getInt("event_pledges_count"));
				
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
		if (indicator.equals("friends1")) {
			assertEquals(1, m_ownerFriendsDS.count());
			FriendModel friend = m_ownerFriendsDS.findById(m_friend1.id);
			assertEquals(FRIEND1_FIRST_NAME, friend.firstName);
			assertEquals(FRIEND1_LAST_NAME, friend.lastName);
			assertEquals(FRIEND1_THRESHOLD, friend.threshold);
			
		} else if (indicator.equals("friends2")) {
			assertEquals(1, m_ownerFriendsDS.count());
			FriendModel friend = m_ownerFriendsDS.findById(m_friend1.id);
			assertEquals(FRIEND1_FIRST_NAME2, friend.firstName);
			assertEquals(FRIEND1_LAST_NAME2, friend.lastName);
			
		} else if (indicator.equals("friends3")) {
			assertEquals(0, m_ownerFriendsDS.count());
			
		} else if (indicator.equals("groups1")) {
			assertEquals(1, m_ownerGroupsDS.count());
			GroupModel group = m_ownerGroupsDS.findById(m_group1.id);
			assertEquals(GROUP1_NAME, group.name);
			assertEquals(GROUP1_DESCRIPTION, group.description);
			
		} else if (indicator.equals("groups2")) {
			assertEquals(0, m_ownerGroupsDS.count());
			
		} else if (indicator.equals("ownerEvents1")) {
			assertEquals(1, m_ownerEventsDS.count());
			EventModel myEvent = m_ownerEventsDS.findById(m_event1.id);			
			assertEquals(DEED_ID, myEvent.deed_id);
			assertEquals(m_friend1.id, myEvent.friend_id);
			assertEquals(m_owner.id, myEvent.user_id);
			assertEquals(true, myEvent.isPrayerRequested);
			assertEquals(FRIEND1_THRESHOLD2, myEvent.threshold);
			assertEquals(m_owner.firstName, myEvent.userName);
			assertEquals(FRIEND1_FIRST_NAME2, myEvent.friendName);
			assertEquals("<b>" + myEvent.userName + "</b> has moved <b>" + myEvent.friendName + "</b> to <b>" + 
				FriendModel.getThresholdShortString(getContext().getResources(), FRIEND1_THRESHOLD2) + "</b>", myEvent.finalDescription);
			assertEquals(false, myEvent.hasPledged);
			assertEquals(0, myEvent.targetEvent_id);
			
		} else if (indicator.equals("user2Events1")) {
			assertEquals(1, m_user2EventsDS.count());
			EventModel myEvent = m_user2EventsDS.findById(m_event1.id);			
			assertEquals(DEED_ID, myEvent.deed_id);
			assertEquals(m_friend1.id, myEvent.friend_id);
			assertEquals(m_owner.id, myEvent.user_id);
			assertEquals(true, myEvent.isPrayerRequested);
			assertEquals(FRIEND1_THRESHOLD2, myEvent.threshold);
			assertEquals(m_owner.firstName, myEvent.userName);
			assertEquals(FRIEND1_FIRST_NAME2, myEvent.friendName);
			assertEquals("<b>" + myEvent.userName + "</b> has moved <b>" + myEvent.friendName + "</b> to <b>" + 
				FriendModel.getThresholdShortString(getContext().getResources(), FRIEND1_THRESHOLD2) + "</b>", myEvent.finalDescription);
			assertEquals(false, myEvent.hasPledged);
			assertEquals(0, myEvent.targetEvent_id);			
			
		} else if (indicator.equals("user2Requests1")) {
			assertEquals(0, m_user2RequestsDS.count());
			
		} else if (indicator.equals("ownerRequests1")) {
			assertEquals(1, m_ownerRequestsDS.count());
			RequestModel request = m_ownerRequestsDS.findById(m_rmRequest1_id);
			assertNotNull(request);			
			assertEquals(m_user2.id, request.user_id);
			assertEquals(m_group1.id, request.group_id);
			assertEquals(RequestModel.REQUEST_MEMBERSHIP, request.type);
			assertEquals(m_group1.name, request.groupName);
			assertEquals(RequestModel.DELIVERED, request.status);
			assertEquals(m_user2.getFullName(), request.userName);
			
		} else if (indicator.equals("ownerRequests2")) {
			assertEquals(0, m_ownerRequestsDS.count());
			
		} else if (indicator.equals("user2Requests2")) {
			assertEquals(1, m_user2RequestsDS.count());
			RequestModel request = m_user2RequestsDS.findById(m_rmRequest1_id);
			assertNotNull(request);
			assertEquals(m_user2.id, request.user_id);
			assertEquals(m_group1.id, request.group_id);
			assertEquals(RequestModel.REQUEST_MEMBERSHIP, request.type);
			assertEquals(m_group1.name, request.groupName);
			assertEquals(RequestModel.ACCEPTED, request.status);
			// assertEquals(m_user2.getFullName(), request.userName); // TODO no user_name in JSON, just user_id
			
		} else if (indicator.equals("user2Requests3")) {
			assertEquals(0, m_user2RequestsDS.count());			
			
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
