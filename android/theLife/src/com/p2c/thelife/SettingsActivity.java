package com.p2c.thelife;

import android.os.Bundle;
import android.view.Menu;

public class SettingsActivity extends SlidingMenuPollingActivity {
	
	private static final String TAG = "SettingsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_settings, SlidingMenuSupport.SETTINGS_POSITION);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
}
