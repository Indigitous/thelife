package com.p2c.thelife;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class InitialActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial);
		
		// Check to see if the user has authenticated
		if (TheLifeConfiguration.getOwnerDS().isValidUser()) {
			// authenticated user, so main screen
			Intent intent = new Intent("com.p2c.thelife.EventsForCommunity");
			startActivity(intent);
		} else {
			// not authenticated user, so login or register
			Intent intent = new Intent("com.p2c.thelife.Setup");
			startActivity(intent);			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.initial, menu);
		return true;
	}

}
