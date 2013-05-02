package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


/**
 * Show help for Groups Activity page.
 * @author clarence
 *
 */
public class GroupsHelpActivity extends SlidingMenuPollingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_groups_help, SlidingMenuSupport.GROUPS_POSITION);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.groups_help, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.Groups");
			startActivity(intent);
		}
		
		return true;
	}	

}
