package com.p2c.thelife.model;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.p2c.thelife.TheLifeConfiguration;


/**
 * Data store of the owner's events.
 * @author clarence
 *
 */
public class EventsDS extends AbstractDS<EventModel> {
	
	private static final String TAG = "EventsDS";
	
	// maximum number of events on a full refresh
	public static final int MAX_EVENTS = 100;
		
	public EventsDS(Context context, String token) {
		
		super(
				context,
				token,
				"EventsDS", 
				"events.json",
				"refresh_events_timestamp_key",
				"my_events",
				"refresh_events_delta_key",
				TheLifeConfiguration.REFRESH_EVENTS_DELTA
			);		
		
	}
	
	public ArrayList<EventModel> findByFriend(int friendId) {
		ArrayList<EventModel> events = new ArrayList<EventModel>();
		
		for (EventModel m:m_data) {
			if (m.friend_id == friendId) {
				events.add(m);
			}		
		}
		
		return events;
	}	
	
	
	@Override
	public void refresh(String indicator) {
		super.refresh(indicator, MAX_EVENTS);
	}
	
	@Override
	public void forceRefresh(String indicator) {
		super.forceRefresh(indicator, MAX_EVENTS);
	}
	
	
	/**
	 * Refresh with events newer than the latest event in the datastore.
	 * @param indicator
	 */
	public void refreshAfter(String indicator) {
		
		EventModel newestEvent = null;
		if (m_data.size() > 0) {
			// newest event is at the start			
			newestEvent = m_data.get(0);
		}
		
		// refresh after the newest event, if there is one
		if (newestEvent != null) {
			super.refresh(indicator, false, 0, MAX_EVENTS, "&after=" + String.valueOf(newestEvent.id), MODE_PREPEND);
		} else {
			super.refresh(indicator, MAX_EVENTS);
		}
	}
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected EventModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return EventModel.fromJSON(m_context.getResources(), json, useServer);
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
			if (json.optInt("target_event_id", 0) != 0) {
				int event_id = json.optInt("target_event_id");
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
