package com.p2c.thelife;

import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends SlidingMenuActivity implements DSRefreshedListener {
	
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
			ListView listView = (ListView)findViewById(R.id.list);
			MainEventsAdapter adapter = new MainEventsAdapter(this, android.R.layout.simple_list_item_1);
			listView.setAdapter(adapter);
			
			// load the database from the server in the background
			// note that events and users are closely related, so first refresh the users and then the events
			TheLifeConfiguration.getUsersDS().addDSChangedListener(adapter);
			TheLifeConfiguration.getUsersDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getEventsDS().addDSChangedListener(adapter);
			TheLifeConfiguration.getUsersDS().refresh(); // first refresh users, then refresh events in the notifyDSRefreshed callback			
		}
	}
	
	@Override
	public void notifyDSRefreshed() {
		TheLifeConfiguration.getEventsDS().refresh();
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
