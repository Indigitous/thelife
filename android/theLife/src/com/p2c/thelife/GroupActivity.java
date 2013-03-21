package com.p2c.thelife;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.UserModel;

public class GroupActivity extends SlidingMenuActivity {
	
	private GroupModel m_group = null;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, SlidingMenuActivity.GROUPS_POSITION, R.layout.activity_group);
		
		// Get the group for this activity
		int groupId = getIntent().getIntExtra("group_id", 0);
		m_group = TheLifeConfiguration.getGroupsDS().findById(groupId);		
		
		// Show the group
		if (m_group != null) {		
			TextView nameView = (TextView)findViewById(R.id.activity_group_name);
			nameView.setText(m_group.name);
		}
		
		// attach the users-in-group list view
		GridView usersView = (GridView)findViewById(R.id.activity_group_users);
		GroupAdapter adapter = new GroupAdapter(this, android.R.layout.simple_list_item_1, m_group);
		usersView.setAdapter(adapter);
		TheLifeConfiguration.getUsersDS().refresh();		
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
		Toast.makeText(this, "Delete User " + user.get_full_name(), Toast.LENGTH_SHORT).show();
	}

}
