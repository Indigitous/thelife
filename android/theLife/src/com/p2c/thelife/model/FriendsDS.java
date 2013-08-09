package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.config.TheLifeConfiguration;


/**
 * Friend model data store.
 * @author clarence
 *
 */
public class FriendsDS extends AbstractDS<FriendModel> {
	
	private static final String SYSKEY_REFRESH_FRIENDS_TIMESTAMP = "refresh_friends_timestamp";
	
	
	public FriendsDS(Context context, String token) {
		
		super(
				context,
				token,
				"FriendsDS", 
				"friends.json",
				SYSKEY_REFRESH_FRIENDS_TIMESTAMP,
				"my_friends",
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
