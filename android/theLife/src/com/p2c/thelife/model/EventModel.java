package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



// POJO - plain old java object
// TODO: not need activity_id?
public class EventModel extends AbstractModel {
	
	private static final String TAG = "EventModel";
	
	public int     user_id;
	public int     friend_id;
	public int     deed_id;
	public int     targetEvent_id;  // if it is nonzero then this event is about the target event, which only happens for pledgeCount updates
	public String  description;		// event description, derived from activity summary on server
	public long    timestamp; 		// in milliseconds, android.text.format.Time
	public boolean isPrayerRequested;   
	public int     pledgeCount;
	
	public EventModel(int event_id, int user_id, int friend_id, int deed_id, int targetEvent_id, 
					  String description, long timestamp, boolean isPrayerRequested, int pledgeCount) {
		super (event_id);
		
		this.user_id = user_id;		
		this.friend_id = friend_id;
		this.deed_id = deed_id;
		this.targetEvent_id = targetEvent_id;
		this.description = description;
		this.timestamp = timestamp;
		this.isPrayerRequested = isPrayerRequested;
		this.pledgeCount = pledgeCount;
	}
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + friend_id + ", " + deed_id + ", " + targetEvent_id + ", " + description + ", " + timestamp + ", " + isPrayerRequested + ", " + pledgeCount;
	}
	
	public static EventModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "IN EVENT MODEL from JSON");
		
		// create the event
		return new EventModel(
			json.getInt("id"),
			json.getInt("user_id"),
			json.getInt("friend_id"),
			json.getInt("activity_id"),
			json.optInt("event_id", 0),
			json.getString("description"),
			json.optLong("created_at", System.currentTimeMillis()),
			json.getBoolean("prayer_requested"),
			json.optInt("pledge_count", 0)
		);
	}	

}
