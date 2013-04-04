package com.p2c.thelife.model;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeConfiguration;


public class EventsDS extends AbstractDS<EventModel> {
		
	public EventsDS(Context context) {
		
		super(
				context, 
				"EventsDS", 
				"events.json",
				"refresh_events_timestamp_key",
				"events.json",
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

			// look for a pledge count object in the event stream -- it is missing user_id
			if (json.optInt("user_id", -1) == -1  && json.optInt("pledge_count", -1) != -1) {
				int event_id = json.optInt("id");
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
			} else {
				// create the model object
				EventModel model = createFromJSON(json, useServer);
				list.add(model);
			}
		}
	}		

}
