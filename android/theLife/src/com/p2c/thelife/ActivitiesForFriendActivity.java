package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.p2c.thelife.model.ActivityModel;
import com.p2c.thelife.model.FriendModel;

public class ActivitiesForFriendActivity extends SlidingMenuActivity {
	
	private FriendModel m_friend = null;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, SlidingMenuActivity.NO_POSITION, R.layout.activity_activities_for_friend);
		
		// Get the main application
		TheLifeApplication app = (TheLifeApplication)getApplication();		
		
		// Get the friend for this activity
		int groupId = getIntent().getIntExtra("group_id", 0);
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = app.getFriendsDS().findById(groupId, friendId);
		
		// Show the friend
		if (m_friend != null) {		
			TextView name = (TextView)findViewById(R.id.activities_for_friend_name);
			name.setText(m_friend.get_full_name());
			
			TextView thresholdView = (TextView)findViewById(R.id.activities_for_friend_threshold);
			thresholdView.setText(m_friend.get_threshold_medium_string(getResources()));
			
			ListView activitiesView = (ListView)findViewById(R.id.activities_for_friend_list);
			ActivitiesForFriendAdapter adapter = new ActivitiesForFriendAdapter(this, android.R.layout.simple_list_item_1, app, m_friend);
			activitiesView.setAdapter(adapter);
			
			// load the database from the server in the background
			app.getActivitiesDS().addDataStoreListener(adapter);
			app.getActivitiesDS().refresh(getApplicationContext());
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activities_for_friend, menu);
		return true;
	}
	
	/**
	 * Friend has been selected.
	 * @param view
	 * @return
	 */
	public boolean selectActivity(View view) {
	
		// get the activity associated with this view
		ActivityModel activity = (ActivityModel)view.getTag();
		
		Intent intent = new Intent("com.p2c.thelife.ActivityForFriend");
		intent.putExtra("activity_id", activity.activity_id);
		intent.putExtra("group_id", m_friend.group_id);				
		intent.putExtra("friend_id", m_friend.friend_id);
		startActivity(intent);
		
		return true;
	}	

}
