package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.model.UserModel;

public class SettingsActivity extends SlidingMenuPollingActivity implements ServerListener {
	
	private static final String TAG = "SettingsActivity";
	
	private ProgressDialog m_progressDialog = null;	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_settings, SlidingMenuSupport.SETTINGS_POSITION);
		
		// show the progress dialog while getting the user profile
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_account), true, true);
		Server server = new Server();
		server.queryUserProfile(TheLifeConfiguration.getUserId(), this, "queryUserProfile");		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	
	public boolean setUserProfile(View view) {
		Toast.makeText(this, "User profile set", Toast.LENGTH_SHORT).show();
		
		TextView textView = null;
		textView = (TextView)findViewById(R.id.settings_name_by_image);
		String firstName = textView.getText().toString();
		textView = (TextView)findViewById(R.id.settings_first_name);
		String lastName = textView.getText().toString();
		textView = (TextView)findViewById(R.id.settings_last_name);
		String email = textView.getText().toString();	
		textView = (TextView)findViewById(R.id.settings_email);
		String phone = textView.getText().toString();
					
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.storing_account), true, true);
		Server server = new Server();
		server.updateUserProfile(TheLifeConfiguration.getUserId(), firstName, lastName, email, phone, this, "updateUserProfile");		
		return true;
	}

	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject) {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}
		
		try {
			if (jsonObject != null) {
				
				UserModel user = TheLifeConfiguration.getUser();
				user.setFromPartialJSON(jsonObject);				
				TheLifeConfiguration.setUser(user);
				
				// update the UI with the result of the query
				if (indicator.equals("queryUserProfile")) {
					
					// the user using the app
					user = TheLifeConfiguration.getUser();
					
					// update the UI
					TextView textView = null;
					textView = (TextView)findViewById(R.id.settings_name_by_image);
					textView.setText(user.firstName);
					textView = (TextView)findViewById(R.id.settings_first_name);
					textView.setText(user.firstName);
					textView = (TextView)findViewById(R.id.settings_last_name);
					textView.setText(user.lastName);	
					textView = (TextView)findViewById(R.id.settings_email);
					textView.setText(user.email);
					textView = (TextView)findViewById(R.id.settings_phone);
					textView.setText(user.phone);	
				}
			}
		}
		catch (Exception e) {
			Log.e(TAG, "notifyServerResponseAvailable() " + indicator, e);
		}
	}	
	
}
