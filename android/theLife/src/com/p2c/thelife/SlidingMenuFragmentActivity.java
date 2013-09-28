package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.p2c.thelife.config.TheLifeConfiguration;

/**
 * Superclass of all FragmentActivities that use the sliding menu and application-wide initialization and Sherlock ActionBar. 
 * SlidingMenu is from https://github.com/jfeinstein10/SlidingMenu, Apache 2.0 license
 * 
 * Uses support library for Androids < 3.0.
 * @author clarence
 *
 */
public class SlidingMenuFragmentActivity extends SherlockFragmentActivity {
	
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
		
		// guard against a not-valid user
		if (!TheLifeConfiguration.getOwnerDS().isValidOwner()) {
			Intent intent = new Intent("com.p2c.thelife.Setup");
			startActivity(intent);
			finish();
		}
		
		TheLifeConfiguration.getRequestsDS().addDSChangedListener(m_support);		
		TheLifeConfiguration.getOwnerDS().addDSChangedListener(m_support);
		
		// defensive programming: in case of problems with GCM, refresh requests datastore		
		TheLifeConfiguration.getRequestsDS().refresh("application");		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		TheLifeConfiguration.getOwnerDS().removeDSChangedListener(m_support);
		TheLifeConfiguration.getRequestsDS().removeDSChangedListener(m_support);		
	}
	
	
	/**
	 * Show the notifications number in the sliding menu.
	 */
	protected void showNotificationNumber() {
		m_support.showNotificationNumber();
	}	
}
