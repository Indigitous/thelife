package com.p2c.thelife;

import java.io.IOException;
import java.util.Locale;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
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

public class SetupRegisterActivity extends SetupRegisterActivityAbstract {
	
	private static final String TAG = "SetupRegisterActivity";
	
	private JSONObject m_externalUserAccount = null; // the user account info
	
	
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
		
		new AsyncTask<String, Void, String>() {
			
			private Exception m_e;
			private String m_accountName;

			// background thread
			@Override
			protected String doInBackground(String... params) {
				
				m_accountName = params[0];

				// get the Google Account token: see description and code in android developer docs for class GoogleAuthUtil
				try {
					
					// read the Google user account info; this can result in a permission request to the user
					String userInfoToken = GoogleAuthUtil.getToken(SetupRegisterActivity.this, m_accountName,  
							"oauth2:https://www.googleapis.com/auth/userinfo.profile");
					m_externalUserAccount = Utilities.readJSONFromServer("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + userInfoToken);
					Log.i(TAG, "received user info" + m_externalUserAccount);
										
					// read the google account token, which will be verified by theLife server (no user permission needed)
					String token = null;
					token = GoogleAuthUtil.getToken(SetupRegisterActivity.this, params[0], 
								"audience:server:client_id:" + TheLifeConfiguration.WEB_CLIENT_ID);
					Log.i(TAG, "successfully got Google account token for account " + params[0]);
					
					return token;
				} catch (Exception e) {
					// some kind of error
					Log.e(TAG, "registerViaGoogle()", e);
					m_e = e;
					return null;
				}
			}
			
			// UI thread		
			@Override
			protected void onPostExecute(String externalToken) {
				m_progressDialog.dismiss();
				
				if (m_e == null) {
					registerWithToken(m_accountName, "google", externalToken);
				}
				else {
					Log.e(TAG, "registerViaGoogle", m_e);					
					if (m_e instanceof GooglePlayServicesAvailabilityException) {
						// GooglePlay is not there?
						GooglePlayServicesAvailabilityException e2 = (GooglePlayServicesAvailabilityException)m_e;					
						Dialog alert = GooglePlayServicesUtil.getErrorDialog(e2.getConnectionStatusCode(), SetupRegisterActivity.this, 0);
						alert.show();
					} else if (m_e instanceof UserRecoverableAuthException) {
						// allow the user to try to recover
						startActivityForResult(((UserRecoverableAuthException)m_e).getIntent(), 0);
					} else if (m_e instanceof IOException) {
						Utilities.showConnectionErrorToast(SetupRegisterActivity.this, m_e.getMessage(), Toast.LENGTH_SHORT);
					} else if (m_e instanceof GoogleAuthException) {
						Utilities.showErrorToast(SetupRegisterActivity.this, m_e.getMessage(), Toast.LENGTH_SHORT);
					}
				}
System.out.println("THE GOOGLE USER ACCOUNT IS " + m_externalUserAccount);
System.out.println("THE GOOGLE USER ACCOUNT FIRST NAME IS " + m_externalUserAccount.optString("given_name"));
System.out.println("THE GOOGLE USER ACCOUNT LAST NAME IS " + m_externalUserAccount.optString("family_name"));
System.out.println("THE GOOGLE TOKEN IS " + externalToken);
			}				
		}.execute(accountName);
		
	}
	
	// this routine must not called from the UI thread
	private void registerWithToken(String accountName, String provider, String externalToken) {
		
		// progress bar while waiting
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.creating_account), true, true);
				
		String firstName = m_externalUserAccount.optString("given_name");
		String lastName = m_externalUserAccount.optString("family_name");
		String locale = Locale.getDefault().getLanguage();
		
		Server server = new Server(this);
		server.registerWithToken(accountName, firstName, lastName, locale, provider, externalToken, this, "registerWithToken");
	}
	
	
	private void registerManually() {
		// go to the register manually screen
		Intent intent = new Intent("com.p2c.thelife.SetupRegisterManually");
		startActivity(intent);		
	}

}
