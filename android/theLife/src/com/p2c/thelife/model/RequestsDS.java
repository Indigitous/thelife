package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.p2c.thelife.config.TheLifeConfiguration;


/**
 * Requests/notifications model data store.
 * @author clarence
 *
 */
public class RequestsDS extends AbstractDS<RequestModel> {
	
	private static final String SYSKEY_REFRESH_REQUESTS_TIMESTAMP = "refresh_requests_timestamp";	
	private static final String SYSKEY_HAS_NEW_NOTIFICATIONS = "requests_has_new_notifications";
	
	private boolean m_hasNewNotifications = false;
	
	
	public RequestsDS(Context context, String token) {
		
		super(
				context,
				token,
				"RequestsDS", 
				"requests.json",
				SYSKEY_REFRESH_REQUESTS_TIMESTAMP,
				"my_requests",
				TheLifeConfiguration.REFRESH_REQUESTS_DELTA
			);
		
		// see if there are new notifications
		SharedPreferences systemSettings = TheLifeConfiguration.getSystemSettings();
		m_hasNewNotifications = systemSettings.getBoolean(SYSKEY_HAS_NEW_NOTIFICATIONS, false);
		if (count() == 1) {
			setHasNewNotifications(false);
		}
	}
	
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected RequestModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return RequestModel.fromJSON(m_context.getResources(), json, useServer);
	}
	
	
	public boolean hasNewNotifications() {
		return m_hasNewNotifications;
	}
	
	
	public void setHasNewNotifications(boolean hasNewNotifications) {
		m_hasNewNotifications = hasNewNotifications;
		SharedPreferences.Editor editor = TheLifeConfiguration.getSystemSettings().edit();
		editor.putBoolean(SYSKEY_HAS_NEW_NOTIFICATIONS, hasNewNotifications);
		editor.commit();
	}	
	
	
	@Override
	public boolean add(RequestModel model) {
		boolean wasAdded = super.add(model);
		if (wasAdded) {
			setHasNewNotifications(true);
		}
		
		return wasAdded;
	}	

}
