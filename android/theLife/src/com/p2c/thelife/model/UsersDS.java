package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeConfiguration;


// TODO need to know current user?
public class UsersDS extends AbstractDS<UserModel> {
	
	public UsersDS(Context context) {
		
		super(
				context, 
				"UsersDS", 
				"users.json",
				"refresh_users_timestamp_key",
				TheLifeConfiguration.SERVER_URL + "users.json",
				"refresh_users_delta_key",
				TheLifeConfiguration.REFRESH_USERS_DELTA
			);		
				
	}
	
		
	/**
	 * Needed by the abstract superclass.
	 */
	protected UserModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return UserModel.fromJSON(json, useServer);		
	}		

}
