package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.p2c.thelife.TheLifeConfiguration;


/**
 * Requests/notifications model data store.
 * @author clarence
 *
 */
public class RequestsDS extends AbstractDS<RequestModel> {
	
	private static final String KEY_HAS_NEW_NOTIFICATIONS = "requests_has_new_notifications";
	
	private boolean m_hasNewNotifications = false;
	
	
	public RequestsDS(Context context, String token) {
		
		super(
				context,
				token,
				"RequestsDS", 
				"requests.json",
				"refresh_requests_timestamp_key",
				"my_requests",
				"refresh_requests_delta_key",
				TheLifeConfiguration.REFRESH_REQUESTS_DELTA
			);
		
		// see if there are new notifications
		SharedPreferences systemSettings = TheLifeConfiguration.getSystemSettings();
		m_hasNewNotifications = systemSettings.getBoolean(KEY_HAS_NEW_NOTIFICATIONS, false);
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
		editor.putBoolean(KEY_HAS_NEW_NOTIFICATIONS, hasNewNotifications);
		editor.commit();		
	}	
	
	
	public void notifyDSChangedListeners() {
		if (m_newModelIds.size() > 0) {
			setHasNewNotifications(true);
		}
		super.notifyDSChangedListeners();
	}
}
