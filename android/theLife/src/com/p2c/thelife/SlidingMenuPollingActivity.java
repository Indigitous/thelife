package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * Superclass of all Activities that use the sliding menu and application-wide polling and Sherlock ActionBar.
 * SlidingMenu is from https://github.com/jfeinstein10/SlidingMenu, Apache 2.0 license
 * SherlockActionBar is from http://actionbarsherlock.com/, Apache 2.0 license
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
		
		// guard against a not-valid user
		if (!TheLifeConfiguration.getOwnerDS().isValidOwner()) {
			Intent intent = new Intent("com.p2c.thelife.Setup");
			startActivity(intent);
			finish();
		}
		
		TheLifeConfiguration.getRequestsDS().addDSChangedListener(m_support);		
		TheLifeConfiguration.getRequestsPoller().start();
		TheLifeConfiguration.getOwnerDS().addDSChangedListener(m_support);
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		TheLifeConfiguration.getRequestsPoller().stop();
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
