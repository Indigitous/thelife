package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;

import com.p2c.thelife.model.EventModel;



/**
 * Show all events related to the owner.
 * @author clarence
 *
 */
public class EventsForCommunityAdapter extends EventsAdapterAbstract {
	
	private static final String TAG = "EventsForCommunityAdapter"; 	
		
	public EventsForCommunityAdapter(Context context, int mode) {
		super(context, mode);
				
		query();
	}
	
	
	protected void query() {
		
		// get all the events
		Collection<EventModel> events = TheLifeConfiguration.getEventsDS().findAll();
		for (EventModel m:events) {
			add(m);
		}				

	}

}
