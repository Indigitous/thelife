package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.p2c.thelife.config.TheLifeConfiguration;


/**
 * Requests model data store.
 * @author clarence
 *
 */
public class RequestsDS extends AbstractDS<RequestModel> {
	
	private static final String SYSKEY_REFRESH_REQUESTS_TIMESTAMP = "refresh_requests_timestamp";	
	private static final String SYSKEY_NUM_REQUESTS_NOTIFIED = "num_requests_notified";
	
	// number of unseen/notified requests (used by NotificationManager)
	private int m_numRequestsNotified = 0;
	
	
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
		
		// see if there are new requests
		SharedPreferences systemSettings = TheLifeConfiguration.getSystemSettings();
		m_numRequestsNotified = systemSettings.getInt(SYSKEY_NUM_REQUESTS_NOTIFIED, 0);
	}
	
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected RequestModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return RequestModel.fromJSON(m_context.getResources(), json, useServer);
	}
	
	
	public boolean hasRequestsNotified() {
		return m_numRequestsNotified > 0;
	}
	
	
	public int numRequestsNotified() {
		return m_numRequestsNotified;
	}	
	
	
	public void setNumRequestsNotified(int numNewRequests) {
		m_numRequestsNotified = numNewRequests;
		SharedPreferences.Editor editor = TheLifeConfiguration.getSystemSettings().edit();
		editor.putInt(SYSKEY_NUM_REQUESTS_NOTIFIED, numNewRequests);
		editor.commit();
	}	
	
	
	@Override
	public boolean add(RequestModel model) {
		boolean wasAdded = super.add(model);
		if (wasAdded) {
			setNumRequestsNotified(++m_numRequestsNotified);
		}
		
		return wasAdded;
	}	

}
