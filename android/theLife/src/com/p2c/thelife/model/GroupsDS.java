package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.config.TheLifeConfiguration;


/**
 * Data store of the current user's groups.
 * @author clarence
 *
 */
public class GroupsDS extends AbstractDS<GroupModel> {
	
	private static final String SYSKEY_REFRESH_GROUPS_TIMESTAMP = "refresh_groups_timestamp";
	
	
	public GroupsDS(Context context, String token) {
		
		super(
				context,
				token,
				"GroupsDS", 
				"groups.json",
				SYSKEY_REFRESH_GROUPS_TIMESTAMP,
				"my_groups",
				TheLifeConfiguration.REFRESH_GROUPS_DELTA
			);		
		
	}
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected GroupModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return GroupModel.fromJSON(json, useServer);
	}	
}
