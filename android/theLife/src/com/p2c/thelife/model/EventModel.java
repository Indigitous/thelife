package com.p2c.thelife.model;

import android.text.format.Time;


// POJO - plain old java object
// TODO: not need activity_id?
public class EventModel {
	
	public int     event_id;	// unique within the group
	public int     group_id;
	public int     friend_id;	// belongs to the group
	public int     user_id;		// belongs to the group
	public String  description;	// event description, derived from activity summary on server
	public long    timestamp; 	// in milliseconds, android.text.format.Time
	public boolean isPledge;   
	public int     pledgeCount;
	
	public EventModel(int event_id, int group_id, int friend_id, int user_id, String description, long timestamp, boolean isPledge, int pledgeCount) {
		this.event_id = event_id;
		this.group_id = group_id;
		this.friend_id = friend_id;
		this.user_id = user_id;
		this.description = description;
		this.timestamp = timestamp;
		this.isPledge = isPledge;
		this.pledgeCount = pledgeCount;
	}
	
	@Override
	public String toString() {
		Time t = new Time();
		t.set(timestamp);
		
		return event_id + ", " + group_id + ", " + friend_id + ", " + user_id + ", " + description + ", " + t + ", " + isPledge + ", " + pledgeCount;
	}

}
