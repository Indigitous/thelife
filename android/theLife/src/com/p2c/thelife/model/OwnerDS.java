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
	
	// app user and authentication token
	private UserModel m_user = null;
	private String m_token = null;	
	
	public interface DSChangedListener {
		public void notifyOwnerDSChanged();
	}
	
	
	public OwnerDS() {
		SharedPreferences systemSettings = TheLifeConfiguration.getSystemSettings();

		int userId = systemSettings.getInt("user_id", 0);
		if (userId != 0) {
			String firstName = systemSettings.getString("user_first_name", "");
			String lastName = systemSettings.getString("user_last_name", "");	
			String email = systemSettings.getString("user_email", "");		
			String mobile = systemSettings.getString("user_mobile", "");		
			
			m_user = new UserModel(userId, firstName, lastName, null, email, mobile);
		}
		
		m_token = systemSettings.getString("token", "");		
	}
	
	
	public int getUserId() {
		return (m_user != null) ? m_user.id : 0;
	}
	
	
	public UserModel getUser() {
		return m_user;
	}
	
	
	public boolean isValidUser() {
		return m_user != null;
	}
	
	
	public void setUser(UserModel user) {
		m_user = user;
		SharedPreferences.Editor systemSettingsEditor = TheLifeConfiguration.getSystemSettings().edit();
		
		if (m_user != null) {
			systemSettingsEditor.putInt("user_id", m_user.id);
			systemSettingsEditor.putString("user_first_name", m_user.firstName);
			systemSettingsEditor.putString("user_last_name", m_user.lastName);	
			systemSettingsEditor.putString("user_email", m_user.email);		
			systemSettingsEditor.putString("user_mobile", m_user.mobile);		
			systemSettingsEditor.commit();		
		} else {
			systemSettingsEditor.putInt("user_id", 0); // marks a not-valid user
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
		systemSettingsEditor.putString("token", m_token);
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
