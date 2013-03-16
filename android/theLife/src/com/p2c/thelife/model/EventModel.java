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
	public String  description;	// event description, derived from activity summary on server
	public long    timestamp; 	// in milliseconds, android.text.format.Time
	public boolean isPledge;   
	public int     pledgeCount;
	
	public EventModel(int event_id, int user_id, int friend_id, int deed_id, String description, long timestamp, boolean isPledge, int pledgeCount) {
		super (event_id);
		
		this.user_id = user_id;		
		this.friend_id = friend_id;
		this.deed_id = deed_id;
		this.description = description;
		this.timestamp = timestamp;
		this.isPledge = isPledge;
		this.pledgeCount = pledgeCount;
	}
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + friend_id + ", " + description + ", " + timestamp + ", " + isPledge + ", " + pledgeCount;
	}
	
	public static EventModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "IN EVENT MODEL from JSON");
		
		// create the event
		return new EventModel(
			json.getInt("event_id"),
			json.getInt("user_id"),
			json.getInt("friend_id"),
			json.getInt("deed_id"),			
			json.getString("description"),
			json.getLong("timestamp"),
			json.getBoolean("is_pledge"),
			json.getInt("pledge_count")
		);
	}	

}
