package com.p2c.thelife;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.UserModel;


/**
 * Show the events for the user (optionally a user in a group).
 * @author clarence
 *
 */
public class EventsForUserActivity extends SlidingMenuActivity implements EventsDS.DSRefreshedListener {
	
	private static final String TAG = "EventsForUserActivity";
	
	private int m_groupId = 0;
	private String m_userJSONString = null;
	private UserModel m_user = null;	
	private ListView m_listView = null;
	private EventsForUserAdapter m_adapter = null;
	private TextView m_noEventsView = null;	

	// refresh the events list view
	private Runnable m_displayRefreshRunnable = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_events_for_user, SlidingMenuSupport.NO_POSITION);
					
		// Get the group
		m_groupId = getIntent().getIntExtra("group_id", 0);
		GroupModel group = TheLifeConfiguration.getGroupsDS().findById(m_groupId); 
		
		// Get the user
		m_userJSONString = getIntent().getStringExtra("user_json");
		if (m_userJSONString != null) {
			try {
				JSONObject userJSON = new JSONObject(m_userJSONString);
				m_user = UserModel.fromJSON(userJSON, false);
			} catch (JSONException e) {
				Log.e(TAG, "onCreate()", e);
			}
		}		
		
		// Show the user
		if (m_user != null) {
			ImageView imageView = (ImageView)findViewById(R.id.activity_user_image);
			imageView.setImageBitmap(UserModel.getImage(m_user.id));
			
			TextView nameView = (TextView)findViewById(R.id.activity_user_name);
			nameView.setText(m_user.getFullName());
			
			TextView groupRoleView = (TextView)findViewById(R.id.activity_group_role);
			if (groupRoleView != null && group != null) {
				groupRoleView.setText(group.leader_id == m_user.id ? R.string.group_leader : R.string.group_member);
			}
		}
			
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.activity_user_events);
		m_adapter = new EventsForUserAdapter(this, android.R.layout.simple_list_item_1, (m_user != null) ? m_user.id : 0);
		m_listView.setAdapter(m_adapter);
		
		// show a message if there are no events
		m_noEventsView = (TextView)findViewById(R.id.events_for_user_none);
		m_noEventsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);
		
		// runnable to refresh the timestamps in the events list view
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
	 * Activity in view.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		// data may have changed (e.g. push notifications), so redisplay
		m_adapter.notifyDSChanged(null, null);		
		
		// load the data store from the server in the background
		TheLifeConfiguration.getEventsDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getEventsDS().addDSRefreshedListener(this);
		TheLifeConfiguration.getEventsDS().refresh(null);
		
		// refresh the display every 60 seconds
		m_listView.postDelayed(m_displayRefreshRunnable, 60 * 1000);		
	}	
	
	
	/**
	 * Called when the events data store refresh has completed.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		m_noEventsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);						
	}			
	
	
	/**
	 * Activity out of view.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		// stop receiving events in the background
		TheLifeConfiguration.getEventsDS().removeDSRefreshedListener(this);
		TheLifeConfiguration.getEventsDS().removeDSChangedListener(m_adapter);
		
		// remove the display refresh
		m_listView.removeCallbacks(m_displayRefreshRunnable);		
	}	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.events_for_user, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		if (item.getItemId() == android.R.id.home) {
			m_support.slideOpen();
		} else if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_events_for_user_help);
			intent.putExtra("position", SlidingMenuSupport.GROUPS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.EventsForUser");
			intent.putExtra("group_id", m_groupId);
			intent.putExtra("user_json", m_userJSONString);			
			startActivity(intent);
		}
		
		return true;
	}

}
