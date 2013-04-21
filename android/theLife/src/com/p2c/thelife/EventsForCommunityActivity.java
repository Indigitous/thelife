package com.p2c.thelife;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.EventsDS;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * This activity uses polling to get fresh events into the data store and display while the activity is visible.
 * @author clarence
 *
 */
public class EventsForCommunityActivity extends SlidingMenuPollingActivity implements EventsDS.DSRefreshedListener, ServerListener {
	
	private static final String TAG = "EventsForCommunityActivity";
	
	private ListView m_listView = null;
	private EventsForCommunityAdapter m_adapter = null;
	
	// refresh the data store and display
	private Runnable m_datastoreRefreshRunnable = null;
	// refresh the events list view
	private Runnable m_displayRefreshRunnable = null;	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_events_for_community, SlidingMenuSupport.COMMUNITY_POSITION);
		
		// no up arrow for home activity
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);		
		
		// If the current user has not been authenticated, jump to login or register instead.
		if (!TheLifeConfiguration.getOwnerDS().isValidUser()) {
			// not authenticated user, so login or register
			Intent intent = new Intent("com.p2c.thelife.Setup");
			startActivity(intent);
		}		
		
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.events_for_community_list);
		m_adapter = new EventsForCommunityAdapter(this, android.R.layout.simple_list_item_1);
		m_listView.setAdapter(m_adapter);
		
		// events data store refresh runnable
		// this will refresh the data store from the server.
		m_datastoreRefreshRunnable = new Runnable() {
			@Override
			public void run() {
				TheLifeConfiguration.getEventsDS().refreshAfter(null);
			}
		};
		
		// timestamps in events list view refresh runnable
		m_displayRefreshRunnable = new Runnable() {
			@Override
			public void run() {
				if (m_adapter != null && m_listView != null) {
					m_adapter.notifyDataSetChanged();
					
					// refresh the display again in one minute
					m_listView.postDelayed(m_displayRefreshRunnable, 60 * 1000);
				}
			}
		};
	}
	
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		// load the data store from the server in the background
		if (TheLifeConfiguration.getOwnerDS().isValidUser()) {
			TheLifeConfiguration.getEventsDS().addDSChangedListener(m_adapter);
			TheLifeConfiguration.getEventsDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getEventsDS().refresh(null);  // TODO refreshAfter(null) ?
		}
		
		// refresh the display every 60 seconds
		m_listView.postDelayed(m_displayRefreshRunnable, 60 * 1000);
	}	
	
	
	/**
	 * Called when the data store refresh has completed.
	 * Will put another data store refresh onto the UI thread queue.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		// keep polling the events in the background
System.out.println("JUST FINISHED ANOTHER EVENTS DS REFRESH!!!");
		m_listView.postDelayed(m_datastoreRefreshRunnable, TheLifeConfiguration.REFRESH_EVENTS_DELTA);
	}			
	
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		// stop polling the events in the background
		TheLifeConfiguration.getEventsDS().removeDSRefreshedListener(this);
		TheLifeConfiguration.getEventsDS().removeDSChangedListener(m_adapter);
		m_listView.removeCallbacks(m_datastoreRefreshRunnable);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.events_for_community, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			startActivity(new Intent("com.p2c.thelife.CommunityHelp"));
		}
		
		return true;
	}
	
	
	/**
	 * User has chosen to pledge prayer for the event.
	 */
	public void pledgeToPray(View view) {
		EventModel event = (EventModel)view.getTag();
		
		// send the pledge to the server
		Server server = new Server(this);
		server.pledgeToPray(event.id, this, "pledgeToPray");
	}


	@Override
	public void notifyServerResponseAvailable(String indicator,	int httpCode, JSONObject jsonObject) {
		if (jsonObject == null) {
			
			new AlertDialog.Builder(this)
				.setMessage(getResources().getString(R.string.pledge_error_from_server))
				.setNegativeButton(R.string.cancel, null).show(); 

		}		
	}

}
