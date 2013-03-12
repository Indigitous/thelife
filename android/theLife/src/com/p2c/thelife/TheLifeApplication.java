package com.p2c.thelife;
import com.p2c.thelife.model.DeedsDS;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.FriendsDS;
import com.p2c.thelife.model.GroupsDS;
import com.p2c.thelife.model.UsersDS;

import android.app.Application;



public class TheLifeApplication extends Application {
	
	private DeedsDS m_deedsDS = null;
	private FriendsDS m_friendsDS = null;
	private UsersDS m_usersDS = null;
	private GroupsDS m_groupsDS = null;
	private EventsDS m_eventsDS = null;
	
	public static final int HTTP_CONNECTION_TIMEOUT = 5000; // in millis
	public static final int HTTP_READ_TIMEOUT = 15000;  // in millis
	public static final String SYSTEM_PREFERENCES_FILE = "system_prefs";
	public static final long RELOAD_DEEDS_DELTA = 24 * 60 * 60 * 1000; // 24 hours in millis
	
	public void onCreate() {
		super.onCreate();

		// initialize the databases
		m_deedsDS = new DeedsDS(getApplicationContext());
		m_friendsDS = new FriendsDS(getApplicationContext());
		m_usersDS = new UsersDS(getApplicationContext());
		m_groupsDS = new GroupsDS(getApplicationContext());
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
