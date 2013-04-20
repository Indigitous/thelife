package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;

import com.p2c.thelife.model.AbstractDS;
import com.p2c.thelife.model.EventModel;

public class EventsForCommunityAdapter extends EventsAdapterAbstract implements AbstractDS.DSChangedListener {
	
	private static final String TAG = "EventsForCommunityAdapter"; 	
		
	public EventsForCommunityAdapter(Context context, int mode) {
		super(context, mode);
				
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
		
		// get all the events
		Collection<EventModel> events = TheLifeConfiguration.getEventsDS().findAll();
		for (EventModel m:events) {
			add(m);
		}				

	}

}
