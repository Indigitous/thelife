package com.p2c.thelife.model;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import com.p2c.thelife.TheLifeApplication;

import android.content.Context;


public class EventsDS extends AbstractDS<EventModel> {
		
	public EventsDS(Context context) {
		
		super(
				context, 
				"EventsDS", 
				context.getCacheDir().getAbsolutePath() + "/events.json",
				"refresh_events_timestamp_key",
				TheLifeApplication.SERVER_URL + "/events.json",
				"refresh_events_delta_key",
				TheLifeApplication.REFRESH_EVENTS_DELTA
			);		
		
		// TODO: get data from server
		m_data.add(new EventModel(1, 1, 1, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(2, 2, 2, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(3, 3, 3, 3, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(4, 3, 4, 4, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(5, 2, 5, 5, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(6, 5, 6, 6, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(7, 5, 7, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(8, 4, 8, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(9, 4, 9, 3, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(10, 1, 10, 4, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(11, 5, 11, 5, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(12, 1, 12, 6, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(13, 2, 1, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(14, 5, 2, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(15, 4, 3, 3, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(16, 1, 4, 4, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(17, 2, 5, 5, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(18, 3, 6, 6, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(19, 5, 7, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(20, 5, 8, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
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
	protected EventModel createFromJSON(Context context, JSONObject json) throws JSONException {
		return null; // EventModel.fromJSON(json);
	}	

}
