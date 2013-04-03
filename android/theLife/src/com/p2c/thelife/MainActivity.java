package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.p2c.thelife.model.EventsDS;

public class MainActivity extends SlidingMenuActivity implements EventsDS.DSRefreshedListener {
	
	private static final String TAG = "MainActivity";
	
	private ListView m_listView = null;
	private MainEventsAdapter m_adapter = null;
	private Runnable m_refreshRunnable = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_main, SlidingMenuSupport.COMMUNITY_POSITION);
					
		if (TheLifeConfiguration.getUserId() == 0) {
			
			// no authenticated user, so login or register
			Intent intent = new Intent("com.p2c.thelife.Setup");
			startActivity(intent);			
			
		} else {
			
			// have an authenticated user, so go ahead with the rest of the app
			
			// attach the event list view
			m_listView = (ListView)findViewById(R.id.activity_main_events);
			m_adapter = new MainEventsAdapter(this, android.R.layout.simple_list_item_1);
			m_listView.setAdapter(m_adapter);
			
			m_refreshRunnable = new Runnable() {
				@Override
				public void run() {
					TheLifeConfiguration.getEventsDS().refresh();
				}
			};
		}
	}
	
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "In onResume()");
		
		// load the data store from the server in the background
		TheLifeConfiguration.getEventsDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getEventsDS().addDSRefreshedListener(this);
		TheLifeConfiguration.getEventsDS().refresh();
	}	
	
	
	/**
	 * Called when the data store refresh has completed.
	 * Will put another data store refresh onto the UI thread queue.
	 */
	@Override
	public void notifyDSRefreshed() {
		// keep polling the events in the background
		m_listView.postDelayed(m_refreshRunnable, TheLifeConfiguration.REFRESH_EVENTS_DELTA);
	}			
	
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "In onPause()");
		
		// stop polling the events in the background
		TheLifeConfiguration.getEventsDS().removeDSRefreshedListener(this);
		TheLifeConfiguration.getEventsDS().removeDSChangedListener(m_adapter);
		m_listView.removeCallbacks(m_refreshRunnable);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			startActivity(new Intent("com.p2c.thelife.CommunityHelp"));
		}
		return true;
	}

}
