package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.config.TheLifeConfiguration;


/**
 * The users in the given group.
 * This is a family of UserModel data stores, one for each group the user belongs to.
 * @author clarence
 *
 */
public class GroupUsersDS extends AbstractDS<UserModel> {
	
	private static final String SYSKEY_REFRESH_GROUPUSERS_TIMESTAMP = "refresh_groupusers_timestamp-";
	
	
	public GroupUsersDS(Context context, String token, int groupId) {
		
		super(
				context,
				token, 
				"GroupUsersDS-" + String.valueOf(groupId),
				"groupUsers-" + String.valueOf(groupId) + ".json",
				SYSKEY_REFRESH_GROUPUSERS_TIMESTAMP + String.valueOf(groupId),
				"groups/" + String.valueOf(groupId) + "/users",
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
