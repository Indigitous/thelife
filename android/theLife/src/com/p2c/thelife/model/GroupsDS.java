package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeConfiguration;


/**
 * The current user's groups.
 * @author clarence
 *
 */
public class GroupsDS extends AbstractDS<GroupModel> {
	
	public GroupsDS(Context context) {
		
		super(
				context, 
				"GroupsDS", 
				"groups.json",
				"refresh_groups_timestamp_key",
				"groups.json",
				"refresh_groups_delta_key",
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
