package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

public class DeedsForFriendActivity extends SlidingMenuActivity {
	
	private FriendModel m_friend = null;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, SlidingMenuActivity.NO_POSITION, R.layout.activity_deeds_for_friend);
		
		// Get the main application
		TheLifeApplication app = (TheLifeApplication)getApplication();		
		
		// Get the friend for this deed
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = app.getFriendsDS().findById(friendId);
		
		// Show the friend
		if (m_friend != null) {		
			TextView name = (TextView)findViewById(R.id.deeds_for_friend_name);
			name.setText(m_friend.get_full_name());
			
			TextView thresholdView = (TextView)findViewById(R.id.deeds_for_friend_threshold);
			thresholdView.setText(m_friend.get_threshold_medium_string(getResources()));
			
			ListView activitiesView = (ListView)findViewById(R.id.deeds_for_friend_list);
			DeedsForFriendAdapter adapter = new DeedsForFriendAdapter(this, android.R.layout.simple_list_item_1, app, m_friend);
			activitiesView.setAdapter(adapter);
			
			// load the database from the server in the background
			app.getDeedsDS().addDataStoreListener(adapter);
			app.getDeedsDS().refresh();
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deeds_for_friend, menu);
		return true;
	}
	
	/**
	 * Friend has been selected.
	 * @param view
	 * @return
	 */
	public boolean selectDeed(View view) {
	
		// get the deed associated with this view
		DeedModel deed = (DeedModel)view.getTag();
		
		Intent intent = new Intent("com.p2c.thelife.DeedForFriend");
		intent.putExtra("deed_id", deed.id);
		intent.putExtra("friend_id", m_friend.id);
		startActivity(intent);
		
		return true;
	}	

}
