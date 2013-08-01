package com.p2c.thelife.model;

import java.util.ArrayList;

import android.content.SharedPreferences;

import com.p2c.thelife.TheLifeConfiguration;


/**
 * Access the Owner/App User information.
 * Unlike other data stores, this does not communicate with the server 
 * but makes its data persistent with the Android SharedPreferences system.
 * @author clarence
 *
 */
public class OwnerDS {
	
	private static final String TAG = "OwnerDS";
	
	// app user/owner and authentication token
	private UserModel m_owner = null;
	private String m_token = null;
	private boolean m_hasAddedFriend = false;
	
	private static final String KEY_ID = "owner_id";
	private static final String KEY_FIRST_NAME = "owner_first_name";
	private static final String KEY_LAST_NAME = "owner_last_name";
	private static final String KEY_EMAIL = "owner_email";
	private static final String KEY_MOBILE = "owner_mobile";
	private static final String KEY_PROVIDER = "owner_provider";
	private static final String KEY_TOKEN = "owner_token";
	private static final String KEY_HAS_ADDED_FRIEND= "owner_has_added_friend";
	private static final String KEY_HAS_USED_THRESHOLD = "owner_has_used_threshold";
	
	
	public interface DSChangedListener {
		public void notifyOwnerDSChanged();
	}
	
	
	/**
	 * Get the owner information from the system shared preferences.
	 */
	public OwnerDS() {
		SharedPreferences systemSettings = TheLifeConfiguration.getSystemSettings();

		// if ownerId == 0 then nobody is logged in
		int ownerId = systemSettings.getInt(KEY_ID, 0);
		if (ownerId != 0) {
			String firstName = systemSettings.getString(KEY_FIRST_NAME, "");
			String lastName = systemSettings.getString(KEY_LAST_NAME, "");	
			String email = systemSettings.getString(KEY_EMAIL, "");		
			String mobile = systemSettings.getString(KEY_MOBILE, "");
			String provider = systemSettings.getString(KEY_PROVIDER, "");
			
			m_owner = new UserModel(ownerId, firstName, lastName, email, mobile, provider);
			m_token = systemSettings.getString(KEY_TOKEN, "");
			m_hasAddedFriend = systemSettings.getBoolean(KEY_HAS_ADDED_FRIEND, false);			
		}
	}
	
	
	public int getId() {
		return (m_owner != null) ? m_owner.id : 0;
	}
	
	
	public UserModel getOwner() {
		return m_owner;
	}
	
	
	public boolean isValidOwner() {
		return m_owner != null;
	}
	
	
	public void setOwner(UserModel owner) {
		m_owner = owner;
		SharedPreferences.Editor systemSettingsEditor = TheLifeConfiguration.getSystemSettings().edit();
		
		if (m_owner != null) {
			systemSettingsEditor.putInt(KEY_ID, m_owner.id);
			systemSettingsEditor.putString(KEY_FIRST_NAME, m_owner.firstName);
			systemSettingsEditor.putString(KEY_LAST_NAME, m_owner.lastName);	
			systemSettingsEditor.putString(KEY_EMAIL, m_owner.email);		
			systemSettingsEditor.putString(KEY_MOBILE, m_owner.mobile);
			systemSettingsEditor.putString(KEY_PROVIDER, m_owner.provider);		
			systemSettingsEditor.commit();		
		} else {
			systemSettingsEditor.putInt(KEY_ID, 0); // marks a not-valid owner
			systemSettingsEditor.commit();			
		}
		
		// tell all the listeners about the change
		notifyDSChangedListeners();
	}	
	
	
	/**
	 * 
	 * @return authentication token
	 */
	public String getToken() {
		return m_token;
	}	
	
	
	/**
	 * set the authentication token
	 */
	public void setToken(String token) {
		m_token = token;
		
		SharedPreferences.Editor systemSettingsEditor = TheLifeConfiguration.getSystemSettings().edit();
		systemSettingsEditor.putString(KEY_TOKEN, m_token);
		systemSettingsEditor.commit();				
	}
	
	
	/**
	 * @return the external provider of the user account, if any
	 */
	public String getProvider() {
		return (m_owner != null) ? m_owner.provider : null;
	}	
	
	
	/**
	 * @return whether or not the owner has added a friend before now
	 */
	public boolean getHasAddedFriend() {
		return m_hasAddedFriend;
	}
	
	
	/**
	 * @param hasAddedFriend	whether or not the owner has added a friend before now
	 */
	public void setHasAddedFriend() {
		m_hasAddedFriend = true;
		
		SharedPreferences.Editor systemSettingsEditor = TheLifeConfiguration.getSystemSettings().edit();
		systemSettingsEditor.putBoolean(KEY_HAS_ADDED_FRIEND, m_hasAddedFriend);
		systemSettingsEditor.commit();			
	}
	
	
	/**
	 * @return whether or not the owner has used the given threshold level before now
	 */
	public boolean getHasUsedThreshold(FriendModel.Threshold threshold) {
		return TheLifeConfiguration.getSystemSettings().getBoolean(KEY_HAS_USED_THRESHOLD + threshold.serverId, false);
	}	
	
	
	/**
	 * @param hasAddedFriend	whether or not the owner has used the given threshold level before now
	 */
	public void setHasUsedThreshold(FriendModel.Threshold threshold) {
		SharedPreferences.Editor systemSettingsEditor = TheLifeConfiguration.getSystemSettings().edit();
		systemSettingsEditor.putBoolean(KEY_HAS_USED_THRESHOLD + threshold.serverId, true);
		systemSettingsEditor.commit();			
	}	
		
	
	
	/************************************* DSListeners *****************************************/
	
	/**
	 * OwnerDSChanged listener
	 */
	protected ArrayList<DSChangedListener> m_changedListeners = new ArrayList<DSChangedListener>();
	
	public void addDSChangedListener(DSChangedListener theListener) {
		m_changedListeners.add(theListener);
	}
	
	public void notifyDSChangedListeners() {
		for (DSChangedListener listener:m_changedListeners) {
			if (listener != null) {
				listener.notifyOwnerDSChanged();
			}
		}
	}
	
	public void removeDSChangedListener(DSChangedListener theListener) {
		m_changedListeners.remove(theListener);
	}
	
	public void clearAllDSChangedListeners() {
		m_changedListeners.clear();
	}	

}
