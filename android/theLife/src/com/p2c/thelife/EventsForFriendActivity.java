package com.p2c.thelife;

import com.p2c.thelife.model.FriendModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class EventsForFriendActivity extends SlidingMenuActivity {
	
	private FriendModel m_friend = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, SlidingMenuActivity.NO_POSITION, R.layout.activity_friend);
		
		// Get the main application
		TheLifeApplication app = (TheLifeApplication)getApplication();				
		
		// Get the friend for this deed
		int groupId = getIntent().getIntExtra("group_id", 0);
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = app.getFriendsDS().findById(groupId, friendId);
		
		// Show the friend
		if (m_friend != null) {
			ImageView imageView = (ImageView)findViewById(R.id.activity_friend_image);
			imageView.setImageDrawable(m_friend.image);
			
			TextView nameView = (TextView)findViewById(R.id.activity_friend_name);
			nameView.setText(m_friend.get_full_name());
			
			TextView thresholdView = (TextView)findViewById(R.id.activity_friend_threshold);
			thresholdView.setText(m_friend.get_threshold_medium_string(getResources()));
		}
		
		// attach the event list view
		ListView listView = (ListView)findViewById(R.id.activity_friend_events);
		EventsForFriendAdapter adapter = new EventsForFriendAdapter(this, android.R.layout.simple_list_item_1, app, m_friend);
		listView.setAdapter(adapter);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend, menu);
		return true;
	}
	
	
	public boolean presentActivities(View view) {
				
		Intent intent = new Intent("com.p2c.thelife.DeedsForFriend");
		intent.putExtra("group_id", m_friend.group_id);
		intent.putExtra("friend_id", m_friend.friend_id);
		startActivity(intent);
		
		return true;
	}

}
