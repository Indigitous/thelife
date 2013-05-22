package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeConfiguration;


/**
 * Requests/notifications model data store.
 * @author clarence
 *
 */
public class RequestsDS extends AbstractDS<RequestModel> {
	
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
		
	}
	
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected RequestModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return RequestModel.fromJSON(m_context.getResources(), json, useServer);
	}	
}
