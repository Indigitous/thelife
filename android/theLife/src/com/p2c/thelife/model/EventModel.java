package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.util.Log;

import com.p2c.thelife.TheLifeConfiguration;



// POJO - plain old java object
// TODO: not need activity_id?
public class EventModel extends AbstractModel {
	
	private static final String TAG = "EventModel";
	
	public int     user_id;
	public int     friend_id;
	public int     deed_id;
	public int     targetEvent_id;  // if it is nonzero then this event is about the target event, which only happens for pledgeCount updates
	public long    timestamp; 		// in milliseconds, android.text.format.Time
	public boolean isPrayerRequested;   
	public int     pledgeCount;
	public String  thresholdString; // threshold in short string form
	public String  userName;		// redundant info, but helps event templates
	public String  friendName;		// redundant info, but helps event templates
	public String  finalDescription; // description with template place holders replaced with real values
	
	
	
	public EventModel(Resources resources, int event_id, int user_id, int friend_id, int deed_id, int targetEvent_id, 
					  String description, long timestamp, boolean isPrayerRequested, int pledgeCount, int threshold_id, String userName, String friendName) {
		super (event_id);
		
		this.user_id = user_id;		
		this.friend_id = friend_id;
		this.deed_id = deed_id;
		this.targetEvent_id = targetEvent_id;
		this.timestamp = timestamp;
		this.isPrayerRequested = isPrayerRequested;
		this.pledgeCount = pledgeCount;
		this.thresholdString = getThresholdString(resources, threshold_id);
		this.userName = userName;
		this.friendName = friendName;
		this.finalDescription = getFinalDescription(description);
	}
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + friend_id + ", " + deed_id + ", " + targetEvent_id + ", " + 
			finalDescription + ", " + timestamp + ", " + isPrayerRequested + ", " + pledgeCount + ", " + thresholdString + "," + userName + ", " + friendName;
	}
	
	
	private String getThresholdString(Resources resources, int thresholdId) {
		String thresholdString = null;
		
		if (thresholdId != 0) {
			FriendModel.Threshold threshold = FriendModel.thresholdId2Threshold(thresholdId);
			thresholdString = FriendModel.getThresholdShortString(resources, threshold);
		}
		
		return thresholdString;
	}
	
	/**
	 * Replace the template place holders in the description with the real values.
	 * 		$u = user's first name
	 * 		$f = friend's first name
	 * 		$t = threshold short string
	 * @return
	 */
	private String getFinalDescription(String description) {
		String finalDescription = null;
		
		// user name parameter
		String paramUserName = this.userName;
		if (paramUserName == null) {
			UserModel user = TheLifeConfiguration.getUsersDS().findById(user_id);
			if (user != null) {
				paramUserName = user.firstName;
			}
		}
		if (paramUserName == null) {
			paramUserName = "";
		}
		
		// friend name parameter
		String paramFriendName = this.friendName;
		if (paramFriendName == null) {
			FriendModel friend = TheLifeConfiguration.getFriendsDS().findById(friend_id);
			if (friend != null) {
				paramFriendName = friend.firstName;
			}
		}
		if (paramFriendName == null) {
			paramFriendName = "";
		}
		
		finalDescription = description.replace("$u", paramUserName);
		finalDescription = finalDescription.replace("$f", paramFriendName);
		if (thresholdString != null) {
			finalDescription = finalDescription.replace("$t", thresholdString);
		}
		
		return finalDescription;
	}
	
	public static EventModel fromJSON(Resources resources, JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "IN EVENT MODEL from JSON");
		
		// create the event
		return new EventModel(
			resources,
			json.getInt("id"),
			json.getInt("user_id"),
			json.getInt("friend_id"),
			json.getInt("activity_id"),
			json.optInt("event_id", 0),
			json.getString("description"),
			json.optLong("created_at", 1366950022126L),
			json.getBoolean("prayer_requested"),
			json.optInt("pledges_count", 0),
			json.optInt("threshold_id", 0),
			json.optString("user_name", null),
			json.optString("friend_name", null)
		);
	}	

}
