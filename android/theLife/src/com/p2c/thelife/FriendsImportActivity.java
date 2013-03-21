package com.p2c.thelife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


/**
 * Import friends. Uses support library for Androids < 3.0.
 * @author clarence
 *
 */
public class FriendsImportActivity extends FragmentActivity {
	
	private static String TAG = "FriendsImportActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_import);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.import_friends, menu);
		return true;
	}
	
	public boolean importFriendsByPhone(View view) {
		return true;
	}
	
	public boolean importFriendsByFacebook(View view) {
		return true;
	}	
	
	public boolean importFriendManually(View view) {
		
		FriendImportManuallyDialog fragment = new FriendImportManuallyDialog();
		fragment.show(getSupportFragmentManager(), "FriendImportManuallyFragment");
		
		return true;
	}

}
