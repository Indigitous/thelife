package com.p2c.thelife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.p2c.thelife.model.AbstractDS;


/**
 * Starting Activity, handles whether or not the user has authenticated.
 * This activity is necessary in part because of the way Android manages the back stack.
 * @author clarence
 *
 */
public class InitialActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial);
		
		// Check to see if the user has authenticated
		if (TheLifeConfiguration.getOwnerDS().isValidOwner()) {
			// authenticated user, so main screen
			Intent intent = new Intent("com.p2c.thelife.EventsForCommunity");
			startActivity(intent);
		} else {
			// not authenticated user, so first delete the cache files
			BitmapCacheHandler.removeAllBitmaps();
			AbstractDS.removeAllJSONFiles();
			
			// login or register
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
