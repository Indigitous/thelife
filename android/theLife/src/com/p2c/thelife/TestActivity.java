package com.p2c.thelife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class TestActivity extends Activity implements OnEditorActionListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		// show the current server URL
		EditText editText = (EditText)findViewById(R.id.test_server_url);
		editText.setText(TheLifeConfiguration.getServerUrl());
		editText.setOnEditorActionListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			
			// set the server URL
			String urlString = (String)v.getText().toString();
			Toast.makeText(this, "server URL now " + urlString, Toast.LENGTH_SHORT).show();
			TheLifeConfiguration.setServerUrl(urlString);
			
			// log out of application
			TheLifeConfiguration.getOwnerDS().setUser(null);
			
			// go to main screen
			Intent intent = new Intent("com.p2c.thelife.Initial");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);				
		}
		return true;
	}

}
