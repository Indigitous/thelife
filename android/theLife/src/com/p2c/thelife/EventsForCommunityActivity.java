package com.p2c.thelife;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.EventsDS;

/**
 * This activity uses polling to get fresh events.
 * @author clarence
 *
 */
public class EventsForCommunityActivity extends SlidingMenuPollingActivity implements EventsDS.DSRefreshedListener, ServerListener {
	
	private static final String TAG = "EventsForCommunityActivity";
	
	private ListView m_listView = null;
	private EventsForCommunityAdapter m_adapter = null;
	private Runnable m_refreshRunnable = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_events_for_community, SlidingMenuSupport.COMMUNITY_POSITION);
		
		// remove the application label at the top
		//super.setTitle("");
		
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.events_for_community_list);
		m_adapter = new EventsForCommunityAdapter(this, android.R.layout.simple_list_item_1);
		m_listView.setAdapter(m_adapter);
		
		// data store refresh runnable			
		m_refreshRunnable = new Runnable() {
			@Override
			public void run() {
				TheLifeConfiguration.getEventsDS().refresh(null);
			}
		};		
					
		// If the current user is an authenticated, go ahead with the rest of the activity.
		// But if the current user has not been authenticated, login or register.
		if (!TheLifeConfiguration.isValidUser()) {
			// not authenticated user, so login or register
			Intent intent = new Intent("com.p2c.thelife.Setup");
			startActivity(intent);
		}
			
	}
	
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		// load the data store from the server in the background
		if (TheLifeConfiguration.isValidUser()) {
			TheLifeConfiguration.getEventsDS().addDSChangedListener(m_adapter);
			TheLifeConfiguration.getEventsDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getEventsDS().refresh(null);
		}
	}	
	
	
	/**
	 * Called when the data store refresh has completed.
	 * Will put another data store refresh onto the UI thread queue.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		// keep polling the events in the background
		m_listView.postDelayed(m_refreshRunnable, TheLifeConfiguration.REFRESH_EVENTS_DELTA);
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
		m_listView.removeCallbacks(m_refreshRunnable);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.events_for_community, menu);
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
		Server server = new Server();
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
