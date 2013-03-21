package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeConfiguration;


public class FriendsDS extends AbstractDS<FriendModel> {
	
	public FriendsDS(Context context) {
		
		super(
				context, 
				"FriendsDS", 
				"friends.json",
				"refresh_friends_timestamp_key",
				TheLifeConfiguration.SERVER_URL + "friends.json",
				"refresh_friends_delta_key",
				TheLifeConfiguration.REFRESH_FRIENDS_DELTA
			);		
		
	}
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected FriendModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return FriendModel.fromJSON(json, useServer);
	}	

}
