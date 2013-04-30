package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Superclass of all FragmentActivities that use the sliding menu. 
 * SlidingMenu is from https://github.com/jfeinstein10/SlidingMenu, Apache 2.0 license TODO license notice
 * 
 * Uses support library for Androids < 3.0.
 * @author clarence
 *
 */
public class SlidingMenuPollingFragmentActivity extends SherlockFragmentActivity {
	
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
		
		// guard against an not-valid user
		if (!TheLifeConfiguration.getOwnerDS().isValidUser()) {
			Intent intent = new Intent("com.p2c.thelife.Setup");
			startActivity(intent);
			finish();
		}
		
		TheLifeConfiguration.getRequestsPoller().start();
		TheLifeConfiguration.getOwnerDS().addDSChangedListener(m_support);
		TheLifeConfiguration.getRequestsDS().addDSChangedListener(m_support);		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		TheLifeConfiguration.getRequestsPoller().stop();
		TheLifeConfiguration.getOwnerDS().removeDSChangedListener(m_support);
		TheLifeConfiguration.getRequestsDS().removeDSChangedListener(m_support);		
	}		

}
