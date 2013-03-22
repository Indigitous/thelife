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

public class GroupsActivity extends SlidingMenuFragmentActivity {
	
	private static final String TAG = "DeedsDS"; 	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_groups, SlidingMenuSupport.GROUPS_POSITION);	
		
//		ExpandableListView groupsList = (ExpandableListView)findViewById(R.id.groups_list);
//		ExpandableGroupsAdapter adapter = new ExpandableGroupsAdapter(this, android.R.layout.simple_list_item_1);
		ListView groupsList = (ListView)findViewById(R.id.groups_list);
		GroupsAdapter adapter = new GroupsAdapter(this, android.R.layout.simple_list_item_1);
		groupsList.setAdapter(adapter);
		
		// load the database from the server in the background
		TheLifeConfiguration.getGroupsDS().addDataStoreListener(adapter);
		TheLifeConfiguration.getGroupsDS().refresh();	
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
			
System.out.println("SEARCH REQUESTED");
//			boolean proceeded = onSearchRequested(); // does nothing, don't know why
			Intent intent = new Intent("com.p2c.thelife.GroupsSearch");
			startActivity(intent);
			
		} else if (item.getItemId() == R.id.action_new) {
			
			GroupCreateDialog dialog = new GroupCreateDialog();
			dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		}
		
		return true;
	}	
	

}
