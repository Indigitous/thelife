package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;

import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;


/**
 * Show the events for the given friend in a list.
 * @author clarence
 *
 */
public class EventsForFriendAdapter extends EventsAdapterAbstract {
	
	private static final String TAG = "EventsForFriendAdapter"; 
	
	private FriendModel m_friend = null;
	
	public EventsForFriendAdapter(Context context, int mode, FriendModel friend) {
		super(context, mode);
		
		m_friend = friend;
		
		query();
	}
	
	
	@Override
	protected void query() {
		// get all the Events for the current user
		Collection<EventModel> events = TheLifeConfiguration.getEventsDS().findByFriend(m_friend.id);
		for (EventModel m:events) {
			add(m);
		}
	}	

}
