package com.p2c.thelife;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.p2c.thelife.model.CategoriesDS;
import com.p2c.thelife.model.DeedsDS;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.FriendsDS;
import com.p2c.thelife.model.GroupsDS;
import com.p2c.thelife.model.OwnerDS;
import com.p2c.thelife.model.RequestsDS;


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
	private static GroupsDS m_groupsDS = null;
	private static EventsDS m_eventsDS = null;
	private static RequestsDS m_requestsDS = null;
	private static OwnerDS m_ownerDS = null;
	
	public static final int HTTP_CONNECTION_TIMEOUT = 10000; // in millis
	public static final int HTTP_READ_TIMEOUT = 15000;  // in millis
	public static final int HTTP_SERVER_CONNECTION_TIMEOUT = 5000; // in millis
	
	// refresh deltas: time before a refresh
	public static final long REFRESH_DEEDS_DELTA = 60 * 60 * 1000; // 1 hour in millis
	public static final long REFRESH_CATEGORIES_DELTA = REFRESH_DEEDS_DELTA;
	public static final long REFRESH_EVENTS_DELTA = 2 * 60 * 1000; // 2 minutes in millis
	public static final long REFRESH_FRIENDS_DELTA = 60 * 60 * 1000; // 1 hour in millis
	public static final long REFRESH_GROUPS_DELTA = 60 * 60 * 1000; // 1 hour in millis
	public static final long REFRESH_USERS_DELTA = 60 * 60 * 1000; // 1 hour in millis
	public static final long REFRESH_REQUESTS_FIRST_DELTA = 4000; // 4 seconds before first Requests refresh
	public static final long REFRESH_REQUESTS_DELTA = 90 * 1000; // 90 seconds in millis
	
	// URL of the server
	// note: ends with a version and a forward slash
	//private static String m_serverURL = "http://75.157.251.192:3000"; // TODO debugging
	private static String m_serverURL = "https://srv1.thelifeapp.com"; // production
	//private static String m_serverURL = "https://thelifeapp.ballistiq.com"; // staging
	
	// Version number of the server
	private static String m_serverVersion = "/v1/";
	
	// stock images
	private static Bitmap m_genericPersonImage;
	private static Bitmap m_genericPersonThumbnail;
	private static Bitmap m_genericDeedImage;
	private static Bitmap m_missingDataImage;
	private static Bitmap m_missingDataThumbnail;
	
	// directory of local cache files
	private static String m_cacheDirectory = null; 
	
	// background thread Handler to read bitmaps from the server into the cache
	private static BitmapCacheHandler m_bitmapCacheHandler = null;
	
	// UI thread Handler to notify that a bitmap from the server has arrived
	private static BitmapNotifierHandler m_bitmapNotifier = null;	
	
	// application wide polling
	private static RequestsPoller m_requestsPoller = null;
	
	
	/*************************** System Preferences **********************/
	
	public static SharedPreferences getSystemSettings() {
		return m_systemSettings;
	}
	
	public static void loadSystemSettings(Context context) {
		m_systemSettings = context.getSharedPreferences("system_prefs", Context.MODE_PRIVATE);		
	}
	
	
	/*************************** Server Info *****************************/
	
	public static String getServerUrl() {
		return m_serverURL;
	}
	
	public static void setServerUrl(String serverURL) {
		m_serverURL = serverURL;
	}
	
	public static String getServerVersion() {
		return m_serverVersion;
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
	
	public static RequestsDS getRequestsDS() {
		return m_requestsDS;
	}
	
	public static void setRequestsDS(RequestsDS requestsDS) {
		m_requestsDS = requestsDS;
	}		
	
	public static OwnerDS getOwnerDS() {
		return m_ownerDS;
	}
	
	public static void setOwnerDS(OwnerDS ownerDS) {
		m_ownerDS = ownerDS;
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
	
	public static BitmapCacheHandler getBitmapCacheHandler() {
		return m_bitmapCacheHandler;
	}
	
	public static void setBitmapCacheHandler(BitmapCacheHandler bitmapCacheHandler) {
		m_bitmapCacheHandler = bitmapCacheHandler;
	}	
	
	public static BitmapNotifierHandler getBitmapNotifier() {
		return m_bitmapNotifier;
	}
	
	public static void setBitmapNotifier(BitmapNotifierHandler bitmapNotifier) {
		m_bitmapNotifier = bitmapNotifier;
	}		
	
	
	/********************** Application-wide polling ***********************/
	
	public static RequestsPoller getRequestsPoller() {
		return m_requestsPoller;
	}
	
	
	public static void setRequestsPoller(RequestsPoller requestsPoller) {
		m_requestsPoller = requestsPoller;
	}
	

}
