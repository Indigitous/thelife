package com.p2c.thelife.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeApplication;


public class GroupsDS extends AbstractDS<GroupModel> {
	
	public GroupsDS(Context context) {
		
		super(
				context, 
				"GroupsDS", 
				context.getCacheDir().getAbsolutePath() + "/groups.json",
				"refresh_groups_timestamp_key",
				"http://thelife.ballistiq.com/groups.json",
				"refresh_groups_delta_key",
				TheLifeApplication.REFRESH_GROUPS_DELTA
			);		
		
		ArrayList<Integer> member_ids = new ArrayList<Integer>();
		
		member_ids.add(1);
		member_ids.add(2);
		member_ids.add(3);		
		m_data.add(new GroupModel(1, "John Martin's Group", 1, member_ids));

		member_ids.clear();
		member_ids.add(4);
		member_ids.add(5);		
		m_data.add(new GroupModel(2, "St-Marc Life Group", 1, member_ids));
	}
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected GroupModel createFromJSON(Context context, JSONObject json) throws JSONException {
		return null; // EventModel.fromJSON(json);
	}	
}
