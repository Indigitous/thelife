package com.p2c.thelife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

public class DeedForFriendActivity extends SlidingMenuActivity {
	
	private static final String TAG = "DeedForFriendActivity"; 
	
	private FriendModel m_friend = null;
	private DeedModel m_deed = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, SlidingMenuActivity.NO_POSITION, R.layout.activity_deed_for_friend);
		
		// Get the main application
		TheLifeApplication app = (TheLifeApplication)getApplication();			
		
		// Get the friend model
		int groupId = getIntent().getIntExtra("group_id", 0);
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = app.getFriendsDS().findById(groupId, friendId);
		
		// Show the friend model
		if (m_friend != null) {		
			TextView name = (TextView)findViewById(R.id.deed_for_friend_name);
			name.setText(m_friend.get_full_name());
			
			TextView thresholdView = (TextView)findViewById(R.id.deed_for_friend_threshold);
			thresholdView.setText(m_friend.get_threshold_medium_string(getResources()));
		}
		
		// Get the Deed model
		int deedId = getIntent().getIntExtra("deed_id", 0);
		m_deed = app.getDeedsDS().findById(deedId);
		
		// Show the Deed model
		if (m_deed != null) {
			
			// title with icon			
			TextView title = (TextView)findViewById(R.id.deed_for_friend_title);
			m_deed.image.setBounds(0,  0,  40,  40); // TODO should not be hardcoded
			title.setCompoundDrawables(m_deed.image, null, null, null);
			title.setText(Utilities.fill_template_string(m_friend, m_deed.title));	
			
			// description
			TextView description = (TextView)findViewById(R.id.deed_for_friend_description);
			description.setText(Utilities.fill_template_string(m_friend, m_deed.description));				
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deed_for_friend, menu);
		return true;
	}
	
	public boolean doDeed(View view) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		
		// set the message and buttons of the alert
		alertBuilder.setMessage(R.string.confirm_prayer_support);
		alertBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// create event with m_friend, m_deed and without prayer_support
				Log.d(TAG, "CREATE DEED EVENT without prayer support");
				
				// go back to the friend screen
				Intent intent = new Intent("com.p2c.thelife.FriendActivity");
				intent.putExtra("group_id", m_friend.group_id);
				intent.putExtra("friend_id", m_friend.friend_id);
				startActivity(intent);				
			}
		});		
		alertBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// create event with m_friend, m_deed and with prayer_support
				Log.d(TAG, "CREATE DEED EVENT with prayer support");
				
				// go back to the friend screen
				Intent intent = new Intent("com.p2c.thelife.FriendActivity");
				intent.putExtra("group_id", m_friend.group_id);
				intent.putExtra("friend_id", m_friend.friend_id);
				startActivity(intent);						
			}
		});		
		
		alertBuilder.show();
		
		return true;
	}

}
