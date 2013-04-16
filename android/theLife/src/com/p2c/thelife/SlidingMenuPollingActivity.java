package com.p2c.thelife;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * Superclass of all Activities that use the sliding menu and application-wide polling.
 * SlidingMenu is from https://github.com/jfeinstein10/SlidingMenu, Apache 2.0 license TODO license notice
 * @author clarence
 *
 */
public class SlidingMenuPollingActivity extends SherlockActivity {
	
	protected SlidingMenuSupport m_support = null;
		
	protected void onCreate(Bundle savedInstanceState, int layout_res, int slidingMenuPosition) {
		super.onCreate(savedInstanceState);
		setContentView(layout_res);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);		
		
		m_support = new SlidingMenuSupport(this, slidingMenuPosition);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (TheLifeConfiguration.isValidUser()) {
			TheLifeConfiguration.getRequestsPoller().start();
		}
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		TheLifeConfiguration.getRequestsPoller().stop();
	}	

}
