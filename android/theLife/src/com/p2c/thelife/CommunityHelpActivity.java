package com.p2c.thelife;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CommunityHelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Clarence;
		setContentView(R.layout.activity_community_help);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.community_help, menu);
		return true;
	}

}
