package com.p2c.thelife;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class SetupRegisterActivity extends Activity {
	
	private static final String TAG = "SetupRegisterActivity";
	
	private String m_token = null;
	private String m_webClientId = "900671345436.apps.googleusercontent.com"; // from P2C's Google Console API
	private ProgressDialog m_progressDialog = null;	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_register);
		
		// look for Google accounts, and add them to the layout
		LinearLayout layout = (LinearLayout)findViewById(R.id.register_layout);		
		AccountManager accountManager = AccountManager.get(this);
		final Account[] googleAccounts = accountManager.getAccountsByType("com.google");
		for (int index= 0; index < googleAccounts.length; index++) {
			Button button = new Button(this);
			button.setId(index);
			button.setText("Google " + googleAccounts[index].name);
			button.setOnClickListener(new Button.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					registerViaGoogle(googleAccounts[v.getId()].name);
				}

			});
			layout.addView(button);
		}
		
		// add the manual register button
		Button button = new Button(this);
		button.setText(getResources().getString(R.string.manually));
		button.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				registerManually();
			}
			
		});
		layout.addView(button);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup_register, menu);
		return true;
	}
	
	
	// Get the Google Account token.
	private void registerViaGoogle(String accountName) {
		
		// progress bar while waiting
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_account), true, true);
		
		m_token = null;
		new AsyncTask<String, Void, Exception>() {

			// background thread
			@Override
			protected Exception doInBackground(String... params) {

				// get the Google Account token: see description and code in android developer docs for class GoogleAuthUtil
				try {
					// successfully got the token
					m_token = GoogleAuthUtil.getToken(SetupRegisterActivity.this, params[0], "audience:server:client_id:" + m_webClientId);
					Log.i(TAG, "successfully got Google account token for account " + params[0]);
					return null;
				} catch (Exception e) {
					// some kind of error
					return e;
				}
			}
			
			// UI thread		
			@Override
			protected void onPostExecute(Exception e) {
				m_progressDialog.dismiss();
				
				if (e != null) {
					Log.e(TAG, "registerViaGoogle", e);					
					if (e instanceof GooglePlayServicesAvailabilityException) {
						// GooglePlay is not there?
						GooglePlayServicesAvailabilityException e2 = (GooglePlayServicesAvailabilityException)e;					
						Dialog alert = GooglePlayServicesUtil.getErrorDialog(e2.getConnectionStatusCode(), SetupRegisterActivity.this, 0);
						alert.show();
					} else if (e instanceof UserRecoverableAuthException) {
						// try to recover via user action
						startActivityForResult(((UserRecoverableAuthException)e).getIntent(), 0);
					} else if (e instanceof IOException) {
						Utilities.showConnectionErrorToast(SetupRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
					} else if (e instanceof GoogleAuthException) {
						Utilities.showErrorToast(SetupRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
					}
				}
			}				
		}.execute(accountName);
		
	}	
	
	public void registerManually() {
		// go to the register manually screen
		Intent intent = new Intent("com.p2c.thelife.SetupRegisterManually");
		startActivity(intent);		
	}

}
