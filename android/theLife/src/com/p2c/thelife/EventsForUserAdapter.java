package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;

import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.EventModel;



/**
 * Show all events related to the given user.
 * @author clarence
 *
 */
public class EventsForUserAdapter extends EventsAdapterAbstract {
	
	private static final String TAG = "EventsForUserAdapter";
	
	private int m_userId = 0;
		
	public EventsForUserAdapter(Context context, int mode, int userId) {
		super(context, mode);
		
		m_userId = userId;
				
		query();
	}
	
	
	protected void query() {
		
		// get all the events
		Collection<EventModel> events = TheLifeConfiguration.getEventsDS().findByUser(m_userId);
		for (EventModel m:events) {
			add(m);
		}				

	}

}
