package com.p2c.thelife.model;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.p2c.thelife.TheLifeConfiguration;


public class EventsDS extends AbstractDS<EventModel> {
		
	public EventsDS(Context context) {
		
		super(
				context, 
				"EventsDS", 
				"events.json",
				"refresh_events_timestamp_key",
				"my_events",
				"refresh_events_delta_key",
				TheLifeConfiguration.REFRESH_EVENTS_DELTA
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
	
	
	/**
	 * This override method handles special objects (pledge count updates) in the event stream.
	 */
	@Override
	protected void addModels(JSONArray jsonArray, boolean useServer, ArrayList<EventModel> list) throws JSONException {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			Log.d(TAG, "ADD ANOTHER JSON OBJECT EVENT " + json);			

			// look for a pledge count event in the event stream -- it has a nonzero target event_id
			if (json.optInt("event_id", 0) != 0) {
				int event_id = json.optInt("event_id");
				int pledge_count = json.optInt("pledge_count");
				
				// The pledge count object gives a new value for the pledge count.
				// Find the referenced event and update its pledge count.
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).id == event_id) {
						EventModel event = list.get(j);
						// only update if the new value is bigger than what is already there 						
						event.pledgeCount = Math.max(event.pledgeCount, pledge_count); 
						break;
					}
				}
				
			// regular EventModel object
			// note that threshold change events don't need special handling -- the friend record is already updated by the server when the HTTP POST event is received
			} else {
				// create the model object
				EventModel model = createFromJSON(json, useServer);
				list.add(model);
			}
		}
	}		

}
