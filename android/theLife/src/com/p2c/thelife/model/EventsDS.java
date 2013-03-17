package com.p2c.thelife.model;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeApplication;


public class EventsDS extends AbstractDS<EventModel> {
		
	public EventsDS(Context context) {
		
		super(
				context, 
				"EventsDS", 
				"events.json",
				"refresh_events_timestamp_key",
				TheLifeApplication.SERVER_URL + "events.json",
				"refresh_events_delta_key",
				TheLifeApplication.REFRESH_EVENTS_DELTA
			);		
		
	}
	
	public Collection<EventModel> findByFriend(int friendId) {
		ArrayList<EventModel> events = new ArrayList<EventModel>();
		
		for (EventModel m:m_data) {
			if (m.friend_id == friendId) {
				events.add(m);
			}		
		}
		
		return events;
	}	
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected EventModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return EventModel.fromJSON(json, useServer);
	}	

}
