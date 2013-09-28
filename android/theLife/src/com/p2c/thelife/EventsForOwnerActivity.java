package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.UserModel;


/**
 * Show the events for the owner.
 * @author clarence
 *
 */
public class EventsForOwnerActivity extends SlidingMenuActivity implements EventsDS.DSRefreshedListener {
	
	private static final String TAG = "EventsForOwnerActivity";
	
	private ListView m_listView = null;
	private EventsForUserAdapter m_adapter = null;
	private TextView m_noEventsView = null;	

	// refresh the events list view
	private Runnable m_displayRefreshRunnable = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_events_for_owner, SlidingMenuSupport.NO_POSITION);
					
		// Get the owner
		UserModel owner = TheLifeConfiguration.getOwnerDS().getOwner();
		
		// Show the owner
		if (owner != null) {
			ImageView imageView = (ImageView)findViewById(R.id.activity_owner_image);
			imageView.setImageBitmap(UserModel.getImage(owner.id));
			
			TextView nameView = (TextView)findViewById(R.id.activity_owner_name);
			nameView.setText(owner.getFullName());
		}
			
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.activity_owner_events);
		m_adapter = new EventsForUserAdapter(this, android.R.layout.simple_list_item_1, TheLifeConfiguration.getOwnerDS().getId());
		m_listView.setAdapter(m_adapter);
		
		// show a message if there are no events
		m_noEventsView = (TextView)findViewById(R.id.events_for_owner_none);
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
		getSupportMenuInflater().inflate(R.menu.events_for_owner, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		if (item.getItemId() == android.R.id.home) {
			m_support.slideOpen();
		} else if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_events_for_owner_help);
			intent.putExtra("position", SlidingMenuSupport.SETTINGS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.EventsForOwner");
			startActivity(intent);
		}
		
		return true;
	}
	
	
	/**
	 * Owner wants to edit their profile
	 * @param view
	 */
	public void editProfile(View view) {
		UserModel owner = TheLifeConfiguration.getOwnerDS().getOwner();
		if (owner != null) {
			Intent intent = new Intent("com.p2c.thelife.Settings");
			startActivity(intent);
		}		
	}

}
