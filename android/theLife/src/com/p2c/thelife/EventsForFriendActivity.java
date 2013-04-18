package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * This activity uses polling to get new events into the data store and display while the activity is visible.
 * @author clarence
 *
 */
public class EventsForFriendActivity extends SlidingMenuPollingActivity implements DSRefreshedListener {
	
	private static final String TAG = "EventsForFriendActivity";
	
	private FriendModel m_friend = null;
	private ListView m_listView = null;
	private EventsForFriendAdapter m_adapter = null;
	
	// refresh the data store and display
	private Runnable m_datastoreRefreshRunnable = null;	
	// refresh the events list view
	private Runnable m_displayRefreshRunnable = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_friend, SlidingMenuSupport.NO_POSITION);
					
		// Get the friend for this deed
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = TheLifeConfiguration.getFriendsDS().findById(friendId);
		
		// Show the friend
		if (m_friend != null) {
			ImageView imageView = (ImageView)findViewById(R.id.activity_friend_image);
			imageView.setImageBitmap(m_friend.image);
			
			TextView nameView = (TextView)findViewById(R.id.activity_friend_name);
			nameView.setText(m_friend.getFullName());
			
			TextView thresholdView = (TextView)findViewById(R.id.activity_friend_threshold);
			thresholdView.setText(m_friend.getThresholdMediumString(getResources()));
		}
		
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.activity_friend_events);
		m_adapter = new EventsForFriendAdapter(this, android.R.layout.simple_list_item_1, m_friend);
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
		TheLifeConfiguration.getEventsDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getEventsDS().addDSRefreshedListener(this);
		TheLifeConfiguration.getEventsDS().refresh(null); // TODO refreshAfter(null) ?
		
		// refresh the display every 60 seconds
		m_listView.postDelayed(m_displayRefreshRunnable, 60 * 1000);		
	}	
	
	
	/**
	 * Called when the events data store refresh has completed.
	 * Will put another events data store refresh onto the UI thread queue.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		// keep polling the events in the background
		m_listView.postDelayed(m_datastoreRefreshRunnable, TheLifeConfiguration.REFRESH_EVENTS_DELTA);
	}			
	
	
	/**
	 * Activity out of view, so stop the events data store refresh mechanism.
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
		getSupportMenuInflater().inflate(R.menu.friend, menu);
		return true;
	}
	
	
	public boolean presentActivities(View view) {
				
		Intent intent = new Intent("com.p2c.thelife.DeedsForFriend");
		intent.putExtra("friend_id", m_friend.id);
		startActivity(intent);
		
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.Friends");
			startActivity(intent);
		}
		
		return true;
	}		

}
