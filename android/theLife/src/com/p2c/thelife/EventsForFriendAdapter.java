package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;

import com.p2c.thelife.model.AbstractDS;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;

public class EventsForFriendAdapter extends EventsAdapterAbstract implements AbstractDS.DSChangedListener {
	
	private static final String TAG = "EventsForFriendAdapter"; 
	
	private FriendModel m_friend = null;
	
	public EventsForFriendAdapter(Context context, int mode, FriendModel friend) {
		super(context, mode);
		
		m_friend = friend;
		
		query();
	}
	
	
	@Override
	public void notifyDSChanged(ArrayList<Integer> oldModelIds, ArrayList<Integer> newModelIds) {
		
		// clear data and redo query
		clear();		
		query();
		
		// redisplay
		notifyDataSetChanged();
	}
	
	private void query() {
		// get all the Events for the current user
		Collection<EventModel> events = TheLifeConfiguration.getEventsDS().findByFriend(m_friend.id);
		for (EventModel m:events) {
			add(m);
		}
	}	

}
