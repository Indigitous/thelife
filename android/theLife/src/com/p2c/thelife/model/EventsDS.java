package com.p2c.thelife.model;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;


public class EventsDS {
	
	private ArrayList<EventModel> m_data = new ArrayList<EventModel>();
	
	public EventsDS(Context context) {
		m_data.add(new EventModel(1, 1, 1, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(2, 1, 2, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(3, 1, 3, 3, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(4, 1, 4, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(5, 1, 5, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(6, 1, 6, 3, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(7, 1, 7, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(8, 1, 8, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(9, 1, 9, 3, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(10, 1, 10, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(11, 1, 11, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(12, 1, 12, 3, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(13, 2, 1, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(14, 2, 2, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(15, 2, 3, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(16, 2, 4, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(17, 2, 5, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(18, 2, 6, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
		m_data.add(new EventModel(19, 2, 7, 1, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), true, 0));
		m_data.add(new EventModel(20, 2, 8, 2, "<b>$u</b> is talking to <b>$f</b> about the gospel.", System.currentTimeMillis(), false, 0));
	}
	
	public Collection<EventModel> findAll() {
		return m_data;
	}

	public EventModel findById(int eventId) {
		
		for (EventModel m:m_data) {
			if (m.event_id == eventId) {
				return m;
			}		
		}
		
		return null;
	}
	
	public Collection<EventModel> findByFriend(int groupId, int friendId) {
		ArrayList<EventModel> events = new ArrayList<EventModel>();
		
		for (EventModel m:m_data) {
			if (m.group_id == groupId && m.friend_id == friendId) {
				events.add(m);
			}		
		}
		
		return events;
	}	

}
