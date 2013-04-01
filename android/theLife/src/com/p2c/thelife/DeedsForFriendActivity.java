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
		super.onCreate(savedInstanceState, R.layout.activity_deeds_for_friend, SlidingMenuSupport.NO_POSITION);
			
		// Get the friend for this deed
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = TheLifeConfiguration.getFriendsDS().findById(friendId);
		
		// Show the friend
		if (m_friend != null) {		
			TextView name = (TextView)findViewById(R.id.deeds_for_friend_name);
			name.setText(m_friend.getFullName());
			
			TextView thresholdView = (TextView)findViewById(R.id.deeds_for_friend_threshold);
			thresholdView.setText(m_friend.getThresholdMediumString(getResources()));
			
			ListView activitiesView = (ListView)findViewById(R.id.deeds_for_friend_list);
			DeedsForFriendAdapter adapter = new DeedsForFriendAdapter(this, android.R.layout.simple_list_item_1, m_friend);
			activitiesView.setAdapter(adapter);
			
			// load the database from the server in the background
			TheLifeConfiguration.getDeedsDS().addDataStoreListener(adapter);
			TheLifeConfiguration.getDeedsDS().refresh();
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
