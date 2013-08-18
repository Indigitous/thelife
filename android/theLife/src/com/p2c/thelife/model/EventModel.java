package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.p2c.thelife.Utilities;
import com.p2c.thelife.config.TheLifeConfiguration;



/**
 * Event data model.
 * @author clarence
 *
 */
public class EventModel extends AbstractModel {
	
	private static final String TAG = "EventModel";
	
	public int     user_id;
	public String  userName;		// redundant info, but helps event templates
	public int     friend_id;
	public String  friendName;		// redundant info, but helps event templates	
	public int     deed_id;
	public int     targetEvent_id;  // if it is nonzero then this event is about the target event, which only happens for pledgeCount updates
	public String  finalDescription; // description with template place holders replaced with real values	
	public long    timestamp; 		// in milliseconds, android.text.format.Time
	public boolean isPrayerRequested;   
	public int     pledgeCount;
	public boolean hasPledged;		// whether or not the app owner has already pledged for this event
	public FriendModel.Threshold threshold;		
	
	
	public EventModel(Resources resources, int event_id, int user_id, String userName, int friend_id, String friendName, int deed_id, int targetEvent_id, 
					  String description, long timestamp, boolean isPrayerRequested, int pledgeCount, boolean hasPledged, int threshold_id) {
		super (event_id);
		
		this.user_id = user_id;
		this.userName = userName;		
		this.friend_id = friend_id;
		this.friendName = friendName;
		this.deed_id = deed_id;
		this.targetEvent_id = targetEvent_id;
		
		this.timestamp = timestamp;
		this.isPrayerRequested = isPrayerRequested;
		this.pledgeCount = pledgeCount;
		this.hasPledged = hasPledged;
		
		// set the threshold if the threshold is part of the event
		this.threshold = null;
		if (threshold_id != 0) {
			this.threshold = FriendModel.thresholdId2Threshold(threshold_id);
		}
		
		// final description needs to have the template parameters replaced with the real values
		this.finalDescription = getFinalDescription(resources, description);		
	}
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + userName + ", " + friend_id + ", " + friendName + "," + deed_id + ", " + targetEvent_id + ", " + 
			finalDescription + ", " + timestamp + ", " + isPrayerRequested + ", " + pledgeCount + ", " + hasPledged + ", " + threshold;
	}

	
	/**
	 * Replace the template place holders in the description with the real values.
	 * 		$u = user's first name
	 * 		$f = friend's first name
	 * 		$t = threshold short string
	 * @return
	 */
	private String getFinalDescription(Resources resources, String description) {
		String finalDescription = null;
		
		// user name parameter
		String paramUserName = this.userName;
		if (paramUserName == null) {
			paramUserName = "???";
		}
		paramUserName = "<b>" + paramUserName + "</b>";		
		
		// friend name parameter
		String paramFriendName = this.friendName;
		if (paramFriendName == null) {
			FriendModel friend = TheLifeConfiguration.getFriendsDS().findById(friend_id);
			if (friend != null) {
				paramFriendName = friend.getFirstNameOrLastName();
			}
		}
		if (paramFriendName == null) {
			paramFriendName = "???";
		}
		paramFriendName = "<b>" + paramFriendName + "</b>";
		
		finalDescription = description.replace("$u", paramUserName);
		finalDescription = finalDescription.replace("$f", paramFriendName);
		if (threshold != null) {
			String thresholdString = FriendModel.getThresholdShortString(resources, threshold);
			finalDescription = finalDescription.replace("$t", "<b>" + thresholdString + "</b>");
		}
		
		return finalDescription;
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
	
	
	public static EventModel fromJSON(Resources resources, JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
		
		// create the event
		return new EventModel(
			resources,
			json.getInt("id"),
			json.getInt("user_id"),
			json.optString("user_name", null),			
			json.getInt("friend_id"),
			json.optString("friend_name", null),		
			json.getInt("activity_id"),
			json.optInt("event_id", 0),
			json.getString("description"),
			json.optLong("created_at", 0L) * 1000, // convert seconds from server into millis
			json.getBoolean("prayer_requested"),
			json.optInt("pledges_count", 0),
			json.optBoolean("has_pledged", false),
			json.optInt("threshold_id", 0)
		);
	}
	
	public static EventModel fromBundle(Resources resources, Bundle bundle) {
		Log.d(TAG, "fromBundle()");
		
		return new EventModel(
			resources,
			Integer.valueOf(bundle.getString("id")),
			Integer.valueOf(bundle.getString("user_id")),
			Utilities.getOptionalField("user_name", bundle, null),
			Integer.valueOf(bundle.getString("friend_id")),
			Utilities.getOptionalField("friend_name", bundle, null),
			Integer.valueOf(bundle.getString("activity_id")),
			Integer.valueOf(Utilities.getOptionalField("event_id", bundle, "0")),			
			bundle.getString("description"),
			Long.valueOf(Utilities.getOptionalField("created_at", bundle, "0")) * 1000, // convert seconds from server into millis
			Boolean.parseBoolean(bundle.getString("prayer_requested")),
			Integer.valueOf(Utilities.getOptionalField("pledges_count", bundle, "0")),
			Boolean.parseBoolean(Utilities.getOptionalField("has_pledged", bundle, "false")),
			Integer.valueOf(Utilities.getOptionalField("threshold_id", bundle, "0"))
		);
	}	

}
