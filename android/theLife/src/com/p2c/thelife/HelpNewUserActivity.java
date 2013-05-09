package com.p2c.thelife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


/**
 * Show a help screen for a new user.
 * @author clarence
 *
 */
public class HelpNewUserActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_new_user);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help_new_user, menu);
		return true;
	}
	
	/**
	 * Finished looking at the new user help.
	 * @param view
	 */
	public void finishNewUserHelp(View view) {
		Intent intent = new Intent("com.p2c.thelife.EventsForCommunity");
		startActivity(intent);		
	}

}