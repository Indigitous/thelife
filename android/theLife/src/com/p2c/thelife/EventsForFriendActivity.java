package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.p2c.thelife.model.FriendModel;

public class EventsForFriendActivity extends SlidingMenuActivity {
	
	private FriendModel m_friend = null;

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
			nameView.setText(m_friend.get_full_name());
			
			TextView thresholdView = (TextView)findViewById(R.id.activity_friend_threshold);
			thresholdView.setText(m_friend.get_threshold_medium_string(getResources()));
		}
		
		// attach the event list view
		ListView listView = (ListView)findViewById(R.id.activity_friend_events);
		EventsForFriendAdapter adapter = new EventsForFriendAdapter(this, android.R.layout.simple_list_item_1, m_friend);
		listView.setAdapter(adapter);
		
		// load the database from the server in the background
		TheLifeConfiguration.getEventsDS().addDataStoreListener(adapter);
		TheLifeConfiguration.getEventsDS().refresh();		
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
