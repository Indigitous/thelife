package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.p2c.thelife.config.TheLifeConfiguration;



/**
 * Pledge data model. 
 * This model is not stored in a data store, but received from the server and used to update the events' pledge counts.
 * @author clarence
 *
 */
public class PledgeModel extends AbstractModel {
	
	private static final String TAG = "PledgeModel";
	
	public int     user_id;
	public int     targetEvent_id;  
	public int     pledgeCount;
	
	
	public PledgeModel(int pledge_id, int user_id, int targetEvent_id, int pledgeCount) {
		super (pledge_id);
		
		this.user_id = user_id;
		this.targetEvent_id = targetEvent_id;
		this.pledgeCount = pledgeCount;		
	}
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + targetEvent_id + ", " + pledgeCount;
	}
	
	
	/**
	 * @return whether or not this event can be seen by the given user
	 */
	public boolean isVisibleToUser(int userId) {
		
		if (user_id == userId)
			return true;
		else {
			for (GroupModel group: TheLifeConfiguration.getGroupsDS().findAll()) {
				if (group.containsUser(userId)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	public static PledgeModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
		
		// create the event
		return new PledgeModel(
			json.getInt("id"),
			json.getInt("user_id"),
			json.getInt("target_event_id"),
			json.optInt("pledges_count", 0)
		);
	}
	
	public static PledgeModel fromBundle(Resources resources, Bundle bundle) {
		Log.d(TAG, "fromBundle()");
		
		return new PledgeModel(
			Integer.valueOf(bundle.getString("id")),
			Integer.valueOf(bundle.getString("user_id")),
			Integer.valueOf(bundle.getString("target_event_id")),
			Integer.valueOf(bundle.getString("pledges_count"))
		);
	}	

}
