package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeConfiguration;


/**
 * The users in the given group.
 * This is a family of UserModel data stores, one for each group the user belongs to.
 * @author clarence
 *
 */
public class GroupUsersDS extends AbstractDS<UserModel> {
	
	public GroupUsersDS(Context context, int groupId) {
		
		super(
				context, 
				"GroupUsersDS-" + String.valueOf(groupId),
				"groupUsers-" + String.valueOf(groupId) + ".json",
				"refresh_users_timestamp_key-" + String.valueOf(groupId),
				"groups/" + String.valueOf(groupId) + "/users",
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
