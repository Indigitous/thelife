package com.p2c.thelife;
import android.graphics.Bitmap;

import com.p2c.thelife.model.CategoriesDS;
import com.p2c.thelife.model.DeedsDS;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.FriendsDS;
import com.p2c.thelife.model.GroupsDS;
import com.p2c.thelife.model.UsersDS;


/**
 * Application wide configuration values.
 * @author clarence
 *
 */
public class TheLifeConfiguration {
	
	private static final String TAG = "TheLifeConfiguration";
	
	// data stores
	private static DeedsDS m_deedsDS = null;
	private static CategoriesDS m_categoriesDS = null;
	private static FriendsDS m_friendsDS = null;
	private static UsersDS m_usersDS = null;
	private static GroupsDS m_groupsDS = null;
	private static EventsDS m_eventsDS = null;
	
	// user id and authentication token
	private static int m_userId = 0;
	private static String m_token = null;
	
	public static final int HTTP_CONNECTION_TIMEOUT = 5000; // in millis
	public static final int HTTP_READ_TIMEOUT = 15000;  // in millis
	public static final String SYSTEM_PREFERENCES_FILE = "system_prefs";	
	
	// refresh deltas: time before a refresh
	public static final long REFRESH_DEEDS_DELTA = 24 * 60 * 60 * 1000; // 1 day in millis
	public static final long REFRESH_CATEGORIES_DELTA = REFRESH_DEEDS_DELTA;
	public static final long REFRESH_EVENTS_DELTA = 5 * 60 * 1000; // 5 minutes in millis
	public static final long REFRESH_FRIENDS_DELTA = 7 * 24 * 60 * 60 * 1000; // 1 week in millis
	public static final long REFRESH_GROUPS_DELTA = 7 * 24 * 60 * 60 * 1000; // 1 week in millis
	public static final long REFRESH_USERS_DELTA = 1 * 60 * 60 * 1000; // 1 hour in millis
	public static final long REFRESH_REQUESTS_DELTA = 5 * 60 * 1000; // 5 minutes in millis
	
	// URL of the server
	public static final String SERVER_URL = "http://75.157.251.192:3000";
	
	// stock images
	public static Bitmap genericPersonImage;
	public static Bitmap genericPersonThumbnail;
	public static Bitmap genericDeedImage;
	public static Bitmap missingDataImage;
	public static Bitmap missingDataThumbnail;
	
	// directory of local cache files
	public static String cacheDirectory = null; 
	
	/*************************** Data Stores *****************************/
	
	public static DeedsDS getDeedsDS() {
		return m_deedsDS;
	}
	
	public static void setDeedsDS(DeedsDS deedsDS) {
		m_deedsDS = deedsDS;
	}
	
	public static CategoriesDS getCategoriesDS() {
		return m_categoriesDS;
	}
	
	public static void setCategoriesDS(CategoriesDS categoriesDS) {
		m_categoriesDS = categoriesDS;
	}	
	
	public static FriendsDS getFriendsDS() {
		return m_friendsDS;
	}
	
	public static void setFriendsDS(FriendsDS friendsDS) {
		m_friendsDS = friendsDS;
	}	
	
	public static UsersDS getUsersDS() {
		return m_usersDS;
	}
	
	public static void setUsersDS(UsersDS usersDS) {
		m_usersDS = usersDS;
	}	
	
	public static GroupsDS getGroupsDS() {
		return m_groupsDS;
	}
	
	public static void setGroupsDS(GroupsDS groupsDS) {
		m_groupsDS = groupsDS;
	}	
	
	public static EventsDS getEventsDS() {
		return m_eventsDS;
	}
	
	public static void setEventsDS(EventsDS eventsDS) {
		m_eventsDS = eventsDS;
	}	
	
	/************************** User information ***************************/
	
	public static int getUserId() {
		return m_userId;
	}
	
	public static void setUserId(int user_id) {
		m_userId = user_id;
	}	
	
	/**
	 * 
	 * @return authentication token
	 */
	public static String getToken() {
		return m_token;
	}	
	
	/**
	 * 
	 * @return authentication token
	 */
	public static void setToken(String token) {
		m_token = token;
	}	
	

}
