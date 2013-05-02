package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


/**
 * General help screen.
 * @author clarence
 *
 */
public class HelpActivity extends SlidingMenuPollingActivity {
	
	private static final String TAG = "HelpActivity";
	
	private int m_groupId = 0;
	private int m_friendId = 0;
	private int m_deedId = 0;
	private String m_home = null;
	private boolean m_shouldClear = false;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// read the setup values from the intent
		int layout = getIntent().getIntExtra("layout", 0);
		int position = getIntent().getIntExtra("position", 0);
		
		super.onCreate(savedInstanceState, layout, position);
		
		// read the remaining values 
		m_groupId = getIntent().getIntExtra("group_id", 0);
		m_friendId = getIntent().getIntExtra("friend_id", 0);
		m_deedId = getIntent().getIntExtra("deed_id", 0);
		m_home = getIntent().getStringExtra("home");
		m_shouldClear = getIntent().getBooleanExtra("shouldClear", false);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.help, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(m_home);
			
			// add the parameters to the intent
			if (m_groupId != 0) {
				intent.putExtra("group_id", m_groupId);
			}
			if (m_friendId != 0) {
				intent.putExtra("friend_id", m_friendId);
			}
			if (m_deedId != 0) {
				intent.putExtra("deed_id", m_deedId);
			}			
			if (m_shouldClear) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			
			// start the intent
			startActivity(intent);
		}
		
		return true;
	}					
}
