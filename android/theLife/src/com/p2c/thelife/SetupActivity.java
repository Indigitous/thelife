package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class SetupActivity extends FragmentActivity implements Server.ServerListener, SetupLoginDialog.Listener {
	
	private ProgressDialog m_progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup, menu);
		return true;
	}
	
	public void loginUser(View view) {
		SetupLoginDialog dialog = new SetupLoginDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		
	}
	
	public void registerUser(View view) {
		
	}
	
	@Override
	public void notifyAttemptingLogin() {
		m_progressDialog = ProgressDialog.show(this, "Waiting", "Retrieving account.", true, true);	// TODO cancelable and translation
	}			
	
	
	@Override
	public void notifyResponseAvailable(int indicator, JSONObject jsonObject) {
				
		System.out.println("Server callback with indicator " + indicator);
		if (jsonObject != null) {
			int userId = jsonObject.optInt("id", 0);
			String token = jsonObject.optString("authentication_token", "");
			if (userId != 0 && token != "") {
				Toast.makeText(this, "THE USER ID IS " + userId, Toast.LENGTH_SHORT).show(); 
				System.out.println("THE TOKEN IS " + token);
			}
		} else {
			Toast.makeText(this, "INCORRECT LOGIN", Toast.LENGTH_SHORT).show(); 
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}
	}

}
