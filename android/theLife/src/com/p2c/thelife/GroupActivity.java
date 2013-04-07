package com.p2c.thelife;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.UserModel;

public class GroupActivity extends SlidingMenuPollingActivity {
	
	private static final String TAG = "GroupActivity";
	
	private GroupModel m_group = null;	
	private GroupAdapter m_adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_group, SlidingMenuSupport.GROUPS_POSITION);
		
		// Get the group for this activity
		int groupId = getIntent().getIntExtra("group_id", 0);
		m_group = TheLifeConfiguration.getGroupsDS().findById(groupId);
		
		// Show the group
		if (m_group != null) {		
			TextView nameView = (TextView)findViewById(R.id.activity_group_name);
			nameView.setText(m_group.name);
		}
		
		// attach the users-in-group view
		GridView usersView = (GridView)findViewById(R.id.activity_group_users);
		GroupAdapter m_adapter = new GroupAdapter(this, android.R.layout.simple_list_item_1, m_group);
		usersView.setAdapter(m_adapter);
	}
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "In onResume()");
		
		// load the database from the server in the background
		TheLifeConfiguration.getUsersDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getUsersDS().refresh(null);				
	}		
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "In onPause()");
		
		TheLifeConfiguration.getUsersDS().removeDSChangedListener(m_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Toast.makeText(this, "Group Help", Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == R.id.action_new) {
			Toast.makeText(this, "Invite User to Group", Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}		
	
	
	public void selectUser(View view) {
		UserModel user = (UserModel)view.getTag();
		Toast.makeText(this, "Delete User " + user.getFullName(), Toast.LENGTH_SHORT).show();
	}

}
