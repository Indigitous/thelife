package com.p2c.thelife;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.EventsDS;

/**
 * This activity uses polling to get new events into the data store and display while the activity is visible.
 * @author clarence
 *
 */
public class EventsForFriendActivity extends SlidingMenuPollingActivity implements EventsDS.DSRefreshedListener, ServerListener {
	
	private static final String TAG = "EventsForFriendActivity";
	
	private FriendModel m_friend = null;
	private ListView m_listView = null;
	private EventsForFriendAdapter m_adapter = null;
	private TextView m_noEventsView = null;	
	
	// refresh the data store and display
	private Runnable m_datastoreRefreshRunnable = null;	
	// refresh the events list view
	private Runnable m_displayRefreshRunnable = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_events_for_friend, SlidingMenuSupport.NO_POSITION);
					
		// Get the friend for this deed
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = TheLifeConfiguration.getFriendsDS().findById(friendId);
		
		// Show the friend
		if (m_friend != null) {
			ImageView imageView = (ImageView)findViewById(R.id.activity_friend_image);
			imageView.setImageBitmap(FriendModel.getImage(m_friend.id));
			
			TextView nameView = (TextView)findViewById(R.id.activity_friend_name);
			nameView.setText(m_friend.getFullName());
			
			TextView thresholdView = (TextView)findViewById(R.id.activity_friend_threshold);
			thresholdView.setText(m_friend.getThresholdMediumString(getResources()));
		}
			
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.activity_friend_events);
		m_adapter = new EventsForFriendAdapter(this, android.R.layout.simple_list_item_1, m_friend);
		m_listView.setAdapter(m_adapter);
		
		// show a message if there are no events
		m_noEventsView = (TextView)findViewById(R.id.events_for_friend_none);
		m_noEventsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);				
		
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
		m_noEventsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);						
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
		
		// remove the display refresh
		m_listView.removeCallbacks(m_displayRefreshRunnable);		
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
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.Help");
			intent.putExtra("layout", R.layout.activity_events_for_friend_help);
			intent.putExtra("position", SlidingMenuSupport.FRIENDS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.EventsForFriend");
			intent.putExtra("friend_id", m_friend.id);			
			startActivity(intent);
		}
		
		return true;
	}
	
	
	/**
	 * Owner wants to edit their friend
	 * @param view
	 */
	public void editFriend(View view) {
		if (m_friend != null) {
			Intent intent = new Intent("com.p2c.thelife.FriendSettings");
			intent.putExtra("friend_id", m_friend.id);			
			startActivity(intent);
		}		
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
