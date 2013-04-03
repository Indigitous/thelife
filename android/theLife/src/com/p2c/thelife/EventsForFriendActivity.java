package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;

public class EventsForFriendActivity extends SlidingMenuActivity implements DSRefreshedListener {
	
	private static final String TAG = "EventsForFriendActivity";
	
	private FriendModel m_friend = null;
	private ListView m_listView = null;
	private EventsForFriendAdapter m_adapter = null;
	private Runnable m_refreshRunnable = null;	

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
		
		m_refreshRunnable = new Runnable() {
			@Override
			public void run() {
				TheLifeConfiguration.getEventsDS().refresh();
			}
		};				
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
		getMenuInflater().inflate(R.menu.friend, menu);
		return true;
	}
	
	
	public boolean presentActivities(View view) {
				
		Intent intent = new Intent("com.p2c.thelife.DeedsForFriend");
		intent.putExtra("friend_id", m_friend.id);
		startActivity(intent);
		
		return true;
	}

}
