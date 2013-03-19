package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.p2c.thelife.model.GroupModel;

public class GroupsActivity extends SlidingMenuActivity {
	
	private static final String TAG = "DeedsDS"; 	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, SlidingMenuActivity.GROUPS_POSITION, R.layout.activity_groups);
		
		// Get the main application
		TheLifeApplication app = (TheLifeApplication)getApplication();				
		
//		ExpandableListView groupsList = (ExpandableListView)findViewById(R.id.groups_list);
//		ExpandableGroupsAdapter adapter = new ExpandableGroupsAdapter(this, android.R.layout.simple_list_item_1, app);
		ListView groupsList = (ListView)findViewById(R.id.groups_list);
		GroupsAdapter adapter = new GroupsAdapter(this, android.R.layout.simple_list_item_1, app);
		groupsList.setAdapter(adapter);
		
		// load the database from the server in the background
		app.getGroupsDS().addDataStoreListener(adapter);
		app.getGroupsDS().refresh();	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.groups, menu);
		return true;
	}
	
	/**
	 * Group has been selected.
	 * @param view
	 * @return
	 */
	public boolean selectGroup(View view) {
		Log.d(TAG, "GROUP VIEW SELECTED TAG " + view.getTag());
		
		// get the group associated with this view
		GroupModel group = (GroupModel)view.getTag();
		
		Intent intent = new Intent("com.p2c.thelife.GroupActivity");
		intent.putExtra("group_id", group.id);
		startActivity(intent);
		
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Toast.makeText(this, "Groups Help", Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == R.id.action_search) {
			Toast.makeText(this, "Groups Search", Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == R.id.action_new) {
			Toast.makeText(this, "New Group", Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}	

}
