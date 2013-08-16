package com.p2c.thelife;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.EventsDS;

/**
 * This activity uses View.postDelayed to update the timestamps on each displayed event.
 * 		[View.postDelayed, local UI thread only, every minute] 
 * @author clarence
 *
 */
public class EventsForCommunityActivity extends SlidingMenuPollingActivity implements EventsDS.DSRefreshedListener, ServerListener {
	
	private static final String TAG = "EventsForCommunityActivity";
	
	private ListView m_listView = null;
	private EventsForCommunityAdapter m_adapter = null;
	private View m_noEventsView = null;
	
	// refresh the events list view
	private Runnable m_displayRefreshRunnable = null;	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_events_for_community, SlidingMenuSupport.COMMUNITY_POSITION);
					
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.events_for_community_list);
		m_adapter = new EventsForCommunityAdapter(this, android.R.layout.simple_list_item_1);
		m_listView.setAdapter(m_adapter);
		
		// show a message if there are no events
		m_noEventsView = findViewById(R.id.events_for_community_none);
		if (m_adapter.getCount() == 0) {
			m_noEventsView.setVisibility(View.VISIBLE);
			Button button = (Button)m_noEventsView.findViewById(R.id.events_for_community_none_button);
			button.setVisibility(TheLifeConfiguration.getFriendsDS().count() == 0 ? View.VISIBLE : View.GONE);
		} else {
			m_noEventsView.setVisibility(View.GONE);	
		}
		
		// showing this activity means no notified events
		TheLifeConfiguration.getEventsDS().setNumEventsNotified(0);
		
		// this will refresh the timestamps inside the list view's events
		// this is all local -- server is not involved
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
		if (TheLifeConfiguration.getOwnerDS().isValidOwner()) {
			TheLifeConfiguration.getEventsDS().addDSChangedListener(m_adapter);
			TheLifeConfiguration.getEventsDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getEventsDS().refresh(null);
			TheLifeConfiguration.getBitmapNotifier().addUserBitmapListener(m_adapter);			
			TheLifeConfiguration.getBitmapNotifier().addFriendBitmapListener(m_adapter);
		}
		
		// refresh the display every minute
		m_listView.postDelayed(m_displayRefreshRunnable, 60 * 1000);
	}	
	
	
	/**
	 * Owner has decided to add a friend (from getting started button).
	 * @param view
	 */
	public void addFriend(View view) {
		Intent intent = new Intent("com.p2c.thelife.FriendsImport");
		startActivity(intent);
	}
	
	
	/**
	 * Called when the data store refresh has completed.
	 * Will put another data store refresh onto the UI thread queue.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		// show a message if there are no events		
		if (m_adapter.getCount() == 0) {
			m_noEventsView.setVisibility(View.VISIBLE);
			Button button = (Button)m_noEventsView.findViewById(R.id.events_for_community_none_button);
			button.setVisibility(TheLifeConfiguration.getFriendsDS().count() == 0 ? View.VISIBLE : View.GONE);
		} else {
			m_noEventsView.setVisibility(View.GONE);	
		}
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
		TheLifeConfiguration.getBitmapNotifier().removeUserBitmapListener(m_adapter);				
		TheLifeConfiguration.getBitmapNotifier().removeFriendBitmapListener(m_adapter);
		
		// remove the display refresh
		m_listView.removeCallbacks(m_displayRefreshRunnable);
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
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_events_for_community_help);
			intent.putExtra("position", SlidingMenuSupport.COMMUNITY_POSITION);
			intent.putExtra("home", "com.p2c.thelife.EventsForCommunity");
			startActivity(intent);
		} else if (item.getItemId() == android.R.id.home) {
			m_support.slideOpen();
		}
		
		return true;
	}
	
	
	/**
	 * Owner has pledged to pray for the event.
	 */
	public void pledgeToPray(View view) {
		
		// update the event immediately (optimistically expect the event will succeed at server)
		EventModel event = (EventModel)view.getTag();
		event.hasPledged = true;
		event.pledgeCount++;
		
		// redisplay
		m_adapter.notifyDataSetChanged();
		
		// send the pledge to the server
		Server server = new Server(this);
		server.pledgeToPray(event.id, this, "pledgeToPray");
	}


	@Override
	public void notifyServerResponseAvailable(String indicator,	int httpCode, JSONObject jsonObject, String errorString) {
		
		if (!Utilities.isSuccessfulHttpCode(httpCode)) {			
			new AlertDialog.Builder(this)
				.setMessage(getResources().getString(R.string.pledge_error_from_server))
				.setNegativeButton(R.string.cancel, null).show(); 
		}		
	}

}
