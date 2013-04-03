package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeConfiguration;


public class RequestsDS extends AbstractDS<GroupModel> {
	
	public RequestsDS(Context context) {
		
		super(
				context, 
				"RequestsDS", 
				"requests.json",
				"refresh_requests_timestamp_key",
				"requests.json",
				"refresh_requests_delta_key",
				TheLifeConfiguration.REFRESH_REQUESTS_DELTA
			);		
		
	}
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected GroupModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return GroupModel.fromJSON(json, useServer);
	}	
}
