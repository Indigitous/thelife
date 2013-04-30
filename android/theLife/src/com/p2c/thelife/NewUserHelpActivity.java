package com.p2c.thelife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class NewUserHelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user_help);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_user_help, menu);
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
