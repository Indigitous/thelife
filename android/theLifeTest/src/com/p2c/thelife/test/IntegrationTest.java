package com.p2c.thelife.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.p2c.thelife.Server;
import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.Utilities;
import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.CategoriesDS;
import com.p2c.thelife.model.DeedsDS;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.FriendsDS;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.GroupsDS;
import com.p2c.thelife.model.RequestsDS;
import com.p2c.thelife.model.UserModel;

/**
 * Integration test between Android client and Server. 
 * This integration test covers both server calls and data store refreshes.
 * @author clarence
 *
 */
public class IntegrationTest extends AndroidTestCase implements ServerListener, DSRefreshedListener {
	
	private static final String TAG = "IntegrationTest";
	
	// Owner test values
	private static final String OWNER1_EMAIL = "itemail1@ballistiq.com";
	private static final String OWNER1_PASSWORD = "123456";
	private static final String OWNER1_FIRST_NAME = "ITFIRST1";
	private static final String OWNER1_LAST_NAME = "ITLAST1";
	private static final String OWNER1_LOCALE = "en";
	private static final String OWNER1_MOBILE = ""; // TODO should be null
	
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
	
	
	// authentication token for the test owner
	private String m_token = null;

	// test objects
	private UserModel m_owner = null;
	private FriendModel m_friend1 = null;
	private EventModel m_event1 = null;
	private GroupModel m_group1 = null;
	
	// data stores
	private static DeedsDS m_deedsDS = null;
	private static CategoriesDS m_categoriesDS = null;
	private static FriendsDS m_friendsDS = null;
	private static GroupsDS m_groupsDS = null;
	private static EventsDS m_eventsDS = null;
	private static RequestsDS m_requestsDS = null;
	
	
	public IntegrationTest() {
		super();
	}
	
	@Override
	public void setUp() {
	}
	
	@Override
	public void tearDown() {
	}
	
	
	synchronized public void test1() {
		
		Server server = null;
		
		// test register
		server = new Server(getContext());
		server.register(OWNER1_EMAIL, OWNER1_PASSWORD, OWNER1_FIRST_NAME, OWNER1_LAST_NAME, OWNER1_LOCALE, this, "register1");
		waitForServerResponse();
		server = null;
		
		// test login
		server = new Server(getContext());
		server.login(OWNER1_EMAIL, OWNER1_PASSWORD, this, "login1");
		waitForServerResponse();
		server = null;
		
		// now that the authentication token is known, initialize the data stores
		m_categoriesDS = new CategoriesDS(getContext(), m_token);
		m_deedsDS = new DeedsDS(getContext(), m_token);
		m_groupsDS = new GroupsDS(getContext(), m_token);
		m_groupsDS.addDSRefreshedListener(this);
		m_friendsDS = new FriendsDS(getContext(), m_token);
		m_friendsDS.addDSRefreshedListener(this);		
		m_eventsDS = new EventsDS(getContext(), m_token);
		m_eventsDS.addDSRefreshedListener(this);
		m_requestsDS = new RequestsDS(getContext(), m_token);	
				
		// test createFriend
		server = new Server(getContext(), m_token);
		server.createFriend(FRIEND1_FIRST_NAME, FRIEND1_LAST_NAME, FRIEND1_THRESHOLD, this, "createFriend1");
		waitForServerResponse();
		server = null;
		
		// test FriendsDS
		m_friendsDS.forceRefresh("friends1");
		waitForServerResponse();
		
		// test updateFriend
		server = new Server(getContext(), m_token);
		server.updateFriend(m_friend1.id, FRIEND1_FIRST_NAME2, FRIEND1_LAST_NAME2, this, "updateFriend1");
		waitForServerResponse();
		server = null;
		
		// test FriendsDS
		m_friendsDS.forceRefresh("friends2");
		waitForServerResponse();		
		
		// test createEvent
		server = new Server(getContext(), m_token);
		server.createEvent(DEED_ID, m_friend1.id, true, FRIEND1_THRESHOLD2, this, "createEvent1");
		waitForServerResponse();
		server = null;
		
		// test EventsDS
		m_eventsDS.forceRefresh("events1");
		waitForServerResponse();
		
		// test deleteFriend
		server = new Server(getContext(), m_token);
		server.deleteFriend(m_friend1.id, this, "deleteFriend1");
		waitForServerResponse();
		server = null;
		
		// test FriendsDS
		m_friendsDS.forceRefresh("friends3");
		waitForServerResponse();			
		
		// test createGroup
		server = new Server(getContext(), m_token);
		server.createGroup(GROUP1_NAME, GROUP1_DESCRIPTION, this, "createGroup1");
		waitForServerResponse();
		server = null;
		
		// test GroupsDS
		m_groupsDS.forceRefresh("groups1");
		waitForServerResponse();			
		
		// test createRequest REQUEST_MEMBERSHIP
		
		
		// test processGroupMembershipRequest  REQUEST_MEMBERSHIP ACCEPT
		
	
		// test queryGroups
		
		
		// test removeUserFromGroup
		
		
		// test deleteGroup
		server = new Server(getContext(), m_token);
		server.deleteGroup(m_group1.id, this, "deleteGroup1");
		waitForServerResponse();
		server = null;
		
		// test GroupsDS
		m_groupsDS.forceRefresh("groups2");
		waitForServerResponse();		
	}
	

	/**
	 * Test the server callback values.
	 */
	@Override
	synchronized public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		try {
			if (indicator.equals("register1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				m_owner = UserModel.fromJSON(jsonObject, false);
				assertEquals(OWNER1_EMAIL, m_owner.email);
				assertEquals(OWNER1_FIRST_NAME, m_owner.firstName);
				assertEquals(OWNER1_LAST_NAME, m_owner.lastName);
				assertEquals(OWNER1_MOBILE, m_owner.mobile);
				
				m_token = jsonObject.getString("authentication_token");
				assertNotNull(m_token);
				
			} else if (indicator.equals("login1")) {
				assertServerSuccess(indicator, httpCode, errorString);
				
				UserModel user = UserModel.fromJSON(jsonObject, false);
				assertEquals(OWNER1_EMAIL, user.email);
				assertEquals(OWNER1_FIRST_NAME, user.firstName);
				assertEquals(OWNER1_LAST_NAME, user.lastName);
				assertEquals(OWNER1_MOBILE, user.mobile);
				
				assertEquals(m_owner.id, user.id);
				String token = jsonObject.getString("authentication_token");
				assertEquals(token, m_token);
				
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
		
		if (indicator.equals("friends1")) {
			assertEquals(1, m_friendsDS.count());
			FriendModel friend = m_friendsDS.findById(m_friend1.id);
			assertEquals(FRIEND1_FIRST_NAME, friend.firstName);
			assertEquals(FRIEND1_LAST_NAME, friend.lastName);
			assertEquals(FRIEND1_THRESHOLD, friend.threshold);
			
		} else if (indicator.equals("friends2")) {
			assertEquals(1, m_friendsDS.count());
			FriendModel friend = m_friendsDS.findById(m_friend1.id);
			assertEquals(FRIEND1_FIRST_NAME2, friend.firstName);
			assertEquals(FRIEND1_LAST_NAME2, friend.lastName);
			
		} else if (indicator.equals("friends3")) {
			assertEquals(0, m_friendsDS.count());
			
		} else if (indicator.equals("groups1")) {
			assertEquals(1, m_groupsDS.count());
			GroupModel group = m_groupsDS.findById(m_group1.id);
			assertEquals(GROUP1_NAME, group.name);
			assertEquals(GROUP1_DESCRIPTION, group.description);
			
		} else if (indicator.equals("groups2")) {
			assertEquals(0, m_groupsDS.count());
			
		} else if (indicator.equals("events1")) {
			assertEquals(1, m_eventsDS.count());
			EventModel myEvent = m_eventsDS.findById(m_event1.id);			
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
		} else {
			assertTrue("Don't know data store refresh indicator " + indicator, false);
		}
		
		notify();
	}

}
