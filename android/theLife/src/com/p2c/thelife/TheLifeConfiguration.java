package com.p2c.thelife;
import android.content.SharedPreferences;
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
	
	// shared preferences
	private static SharedPreferences m_systemSettings;
	
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
	// note: ends with a version and a forward slash
	public static final String SERVER_URL = "http://75.157.251.192:3000/v1/"; // TODO debugging
	//public static final String SERVER_URL = "http://thelifeapp.com:3000/v1/";
	
	
	// stock images
	private static Bitmap m_genericPersonImage;
	private static Bitmap m_genericPersonThumbnail;
	private static Bitmap m_genericDeedImage;
	private static Bitmap m_missingDataImage;
	private static Bitmap m_missingDataThumbnail;
	
	// directory of local cache files
	private static String m_cacheDirectory = null; 
	
	
	/*************************** System Preferences **********************/
	public static void setSystemSettings(SharedPreferences systemSettings) {
		m_systemSettings = systemSettings;
		
		m_userId = m_systemSettings.getInt("user_id", 0);
		m_token = m_systemSettings.getString("token", "");
	}
	
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
	
	
	/****************** Cache and image related information ******************/
	
	public static String getCacheDirectory() {
		return m_cacheDirectory;
	}	
	
	public static void setCacheDirectory(String cacheDirectory) {
		m_cacheDirectory = cacheDirectory;
	}
	
	public static Bitmap getGenericPersonImage() {
		return m_genericPersonImage;
	}
	
	public static void setGenericPersonImage(Bitmap genericPersonImage) {
		m_genericPersonImage = genericPersonImage;
	}	
	
	public static Bitmap getGenericPersonThumbnail() {
		return m_genericPersonThumbnail;
	}
	
	public static void setGenericPersonThumbnail(Bitmap genericPersonThumbnail) {
		m_genericPersonThumbnail = genericPersonThumbnail;
	}		
	
	public static Bitmap getGenericDeedImage() {
		return m_genericDeedImage;
	}
	
	public static void setGenericDeedImage(Bitmap genericDeedImage) {
		m_genericDeedImage = genericDeedImage;
	}		
	
	public static Bitmap getMissingDataImage() {
		return m_missingDataImage;
	}
	
	public static void setMissingDataImage(Bitmap missingDataImage) {
		m_missingDataImage = missingDataImage;
	}	
	
	public static Bitmap getMissingDataThumbnail() {
		return m_missingDataThumbnail;
	}
	
	public static void setMissingDataThumbnail(Bitmap missingDataThumbnail) {
		m_missingDataThumbnail = missingDataThumbnail;
	}		
	
	/************************** User information ***************************/
	
	public static int getUserId() {
		return m_userId;
	}
	
	public static boolean isValidUser() {
		return m_userId != 0;
	}
	
	public static void setUserId(int user_id) {
		m_userId = user_id;
		
		SharedPreferences.Editor systemSettingsEditor = m_systemSettings.edit();
		systemSettingsEditor.putInt("user_id", m_userId);
		systemSettingsEditor.commit();		
	}	
	
	/**
	 * 
	 * @return authentication token
	 */
	public static String getToken() {
		return "CAdLfkoRDTuprzDhta52"; // TODO debugging
		// return m_token;
	}	
	
	/**
	 * 
	 * @return authentication token
	 */
	public static void setToken(String token) {
		m_token = token;
		
		SharedPreferences.Editor systemSettingsEditor = m_systemSettings.edit();
		systemSettingsEditor.putString("token", m_token);
		systemSettingsEditor.commit();				
	}	
	

}
