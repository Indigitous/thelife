package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;

import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.EventModel;



/**
 * Show all events related to the owner.
 * @author clarence
 *
 */
public class EventsForOwnerAdapter extends EventsAdapterAbstract {
	
	private static final String TAG = "EventsForOwnerAdapter"; 	
		
	public EventsForOwnerAdapter(Context context, int mode) {
		super(context, mode);
				
		query();
	}
	
	
	protected void query() {
		
		// get all the events
		Collection<EventModel> events = TheLifeConfiguration.getEventsDS().findByUser(TheLifeConfiguration.getOwnerDS().getId());
		for (EventModel m:events) {
			add(m);
		}				

	}

}
