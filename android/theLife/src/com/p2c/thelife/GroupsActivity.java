package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.p2c.thelife.model.GroupModel;

public class GroupsActivity extends SlidingMenuActivity {
	
	private static final String TAG = "DeedsDS"; 	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, SlidingMenuActivity.GROUPS_POSITION, R.layout.activity_groups);
		
		// Get the main application
		TheLifeApplication app = (TheLifeApplication)getApplication();				
		
		ListView groupsList = (ListView)findViewById(R.id.groups_list);
		GroupsAdapter groupAdapter = new GroupsAdapter(this, android.R.layout.simple_list_item_1, app);
		groupsList.setAdapter(groupAdapter);		
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

}
