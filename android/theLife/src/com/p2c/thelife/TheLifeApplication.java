package com.p2c.thelife;
import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.p2c.thelife.model.DeedsDS;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.FriendsDS;
import com.p2c.thelife.model.GroupsDS;
import com.p2c.thelife.model.UsersDS;



public class TheLifeApplication extends Application {
	
	private static final String TAG = "TheLifeApplication";
	
	private DeedsDS m_deedsDS = null;
	private FriendsDS m_friendsDS = null;
	private UsersDS m_usersDS = null;
	private GroupsDS m_groupsDS = null;
	private EventsDS m_eventsDS = null;
	
	public static final int HTTP_CONNECTION_TIMEOUT = 5000; // in millis
	public static final int HTTP_READ_TIMEOUT = 15000;  // in millis
	public static final String SYSTEM_PREFERENCES_FILE = "system_prefs";
	
	// refresh deltas: time before a refresh
	public static final long REFRESH_DEEDS_DELTA = 5 * 60 * 1000; // 5 minutes in millis
	public static final long REFRESH_EVENTS_DELTA = 24 * 60 * 60 * 1000; // 24 hours in millis
	public static final long REFRESH_FRIENDS_DELTA = 7 * 24 * 60 * 60 * 1000; // 1 week in millis
	public static final long REFRESH_GROUPS_DELTA = 7 * 24 * 60 * 60 * 1000; // 1 week in millis
	public static final long REFRESH_USERS_DELTA = 1 * 60 * 60 * 1000; // 1 hour in millis
	
	// URL of the server
	public static final String SERVER_URL = "http://thelife.ballistiq.com/api/v1/";
	
	// stock images
	public static Bitmap genericPersonImage;
	public static Bitmap genericPersonThumbnail;
	public static Bitmap genericDeedImage;
	public static Bitmap missingDataImage;
	public static Bitmap missingDataThumbnail;
	
	// directory of local cache files
	public static String cacheDirectory = null; 
	
	public void onCreate() {
		super.onCreate();
		
		Log.e(TAG, "onCreate()");  // TODO for debugging only
		
		// initialize stock images before initializing datastores
		genericPersonImage = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_help));
		genericPersonThumbnail = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_help));
		genericDeedImage = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_help));
		missingDataImage = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_search));
		missingDataThumbnail = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_search));
		
		cacheDirectory = getApplicationContext().getCacheDir().getAbsolutePath() + "/";
		
		// initialize the datastores
		m_deedsDS = new DeedsDS(getApplicationContext());
		m_usersDS = new UsersDS(getApplicationContext());
		m_groupsDS = new GroupsDS(getApplicationContext());
		m_friendsDS = new FriendsDS(getApplicationContext());		
		m_eventsDS = new EventsDS(getApplicationContext());
	}
	
	public DeedsDS getDeedsDS() {
		return m_deedsDS;
	}
	
	public FriendsDS getFriendsDS() {
		return m_friendsDS;
	}
	
	public UsersDS getUsersDS() {
		return m_usersDS;
	}
	
	public GroupsDS getGroupsDS() {
		return m_groupsDS;
	}
	
	public EventsDS getEventsDS() {
		return m_eventsDS;
	}	

}
