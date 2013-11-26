package com.p2c.thelife;

import java.io.IOException;
import java.util.Locale;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.UserModel;
import com.testflightapp.lib.TestFlight;


/**
 * Login an existing user or register a new user.
 * @author clarence
 *
 */
public class SetupActivity extends SetupRegisterActivityAbstract implements Server.ServerListener, ServerAccessDialogAbstract.Listener {
	
	private static final String TAG = "SetupActivity";
	
	private AlertDialog m_createOrRegisterDialog = null;

	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		
		// hide the action bar; can only be done with later versions of Android
		if (Build.VERSION.SDK_INT >= 11) {
			getActionBar().hide();
		}
	}
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
		Session session = Session.getActiveSession();
		if (session != null) {        
			// facebook requestCode=64206 resultCode=-1
			session.onActivityResult(this, requestCode, resultCode, data);
		}
    }    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup, menu);
		return true;
	}
	
	
	/**
	 * Make a register or login dialog showing external accounts and manual options.
	 * @param isRegister	true if registering, false if logging in
	 * @return a new dialog showing the registration/login options
	 */
	private void showRegisterOrLoginDialog(final boolean isRegister) {
		
		// add the options: n google accounts, 1 facebook account and 1 manual entry
		AccountManager accountManager = AccountManager.get(this);
		final Account[] googleAccounts = accountManager.getAccountsByType("com.google");
		final Account[] facebookAccounts = accountManager.getAccountsByType("com.facebook.auth.login");		
		SetupAccountsAdapter accountsAdapter = new SetupAccountsAdapter(this, android.R.layout.simple_list_item_single_choice, googleAccounts.length);
		for (int index= 0; index < googleAccounts.length; index++) {
			accountsAdapter.add(googleAccounts[index].name);
		}
		accountsAdapter.add(facebookAccounts.length > 0 ? facebookAccounts[0].name : "facebook"); // facebook
		accountsAdapter.add(getResources().getString(R.string.manually)); // manual option
				
		// build the dialog
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(getResources().getString(isRegister ? R.string.choose_register_method_prompt : R.string.choose_login_method_prompt));
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View accountsLayout = inflater.inflate(R.layout.dialog_setup_account_select, null);
		alertBuilder.setView(accountsLayout);
		ListView accountsList = (ListView)accountsLayout.findViewById(R.id.dialog_setup_account_list);
		accountsList.setAdapter(accountsAdapter);
		accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int selectedIndex, long arg3) {
				m_createOrRegisterDialog.dismiss();
				m_createOrRegisterDialog = null;
				if (selectedIndex < googleAccounts.length) {
					registerOrLoginViaGoogle(isRegister, googleAccounts[selectedIndex].name);
				} else if (selectedIndex < googleAccounts.length + 1) {
					registerOrLoginViaFacebook(isRegister, facebookAccounts.length > 0 ? facebookAccounts[0].name : null);
				} else {
					if (isRegister) {
						registerManually();
					} else {
						loginManually();
					}
				}				
			}
		});

		// create and show the dialog
		m_createOrRegisterDialog = alertBuilder.create();
		m_createOrRegisterDialog.show();
	}
	
	
	/**
	 * Register or login using an external google token.
	 * @param isRegister	true if registering, false if logging in
	 * @param accountName
	 */
	private void registerOrLoginViaGoogle(final boolean isRegister, String accountName) {
		
		// progress bar while waiting
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_account), true, true);
		
		new AsyncTask<String, Void, String>() {
			
			private Exception m_e = null;
			private String m_accountName;
			private JSONObject m_account = null; // the user account info

			// background thread
			@Override
			protected String doInBackground(String... params) {
				
				m_accountName = params[0];
Utilities.showInfoToastSafe(SetupActivity.this, "Attempting to access Google Account for " + m_accountName, Toast.LENGTH_LONG);

				// get the Google Account token: see description and code in android developer docs for class GoogleAuthUtil
				try {				
					if (isRegister) {
						// read the Google user account info; this can result in a permission request to the user
Utilities.showInfoToastSafe(SetupActivity.this, "Attempting to get Google OAuth Token", Toast.LENGTH_LONG);						
						String userInfoToken = GoogleAuthUtil.getToken(SetupActivity.this, m_accountName,  
								"oauth2:https://www.googleapis.com/auth/userinfo.profile");
Utilities.showInfoToastSafe(SetupActivity.this, "Received Google OAuth Token " + (userInfoToken != null ? "NOT NULL" : "NULL") , Toast.LENGTH_LONG);						

						m_account = Utilities.readJSONFromServer("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + userInfoToken);
Utilities.showInfoToastSafe(SetupActivity.this, "Received Google OAuth User Account " + (m_account != null ? "NOT NULL" : "NULL") , Toast.LENGTH_LONG);
						Log.i(TAG, "received user info" + m_account);
						
						// read the Google account image, if available
						
Utilities.showInfoToastSafe(SetupActivity.this, "Attempting to get Google OAuth User Account Picture ", Toast.LENGTH_LONG);						
						String pictureURL = m_account.optString("picture");
Utilities.showInfoToastSafe(SetupActivity.this, "Received Google OAuth User Account Picture " + (pictureURL != null ? "NOT NULL" : "NULL") , Toast.LENGTH_LONG);						
						if (pictureURL != null && !pictureURL.isEmpty()) {
							m_bitmap = Utilities.getExternalBitmap(pictureURL);
							if (m_bitmap != null) {
								Log.i(TAG, "successfully got Google account image");
							}
						}
					}
					
					// TODO if login then update TheLife account with latest from Google
										
					// read the google account token, which will be verified by theLife server (no user permission needed)
					// see http://android-developers.blogspot.ro/2013/01/verifying-back-end-calls-from-android.html
					String token = null;
Utilities.showInfoToastSafe(SetupActivity.this, "Attempting to get Google User Token ", Toast.LENGTH_LONG);
					token = GoogleAuthUtil.getToken(SetupActivity.this, params[0], 
								"audience:server:client_id:" + TheLifeConfiguration.GOOGLE_WEB_CLIENT_ID);
Utilities.showInfoToastSafe(SetupActivity.this, "Received Google User Token " + (token != null ? "NOT NULL" : "NULL") , Toast.LENGTH_LONG);
					Log.i(TAG, "successfully got Google account token for account " + params[0]);
					
					return token;
				} catch (Exception e) {
					// some kind of error
					Log.e(TAG, "registerLoginViaGoogle()", e);
					m_e = e;
					return null;
				}
			}
			
			// UI thread		
			@Override
			protected void onPostExecute(String externalToken) {				
				if (m_e == null) {
					if (isRegister) {
						String firstName = m_account.optString("given_name");
						String lastName = m_account.optString("family_name");
Utilities.showInfoToastSafe(SetupActivity.this, "Attempting to register with token", Toast.LENGTH_LONG);
						registerWithToken(m_accountName, firstName, lastName, "google", externalToken);
					} else {
Utilities.showInfoToastSafe(SetupActivity.this, "Attempting to login with token", Toast.LENGTH_LONG);
						loginWithToken(m_accountName, "google", externalToken);
					}
Utilities.showInfoToastSafe(SetupActivity.this, "Successfully completed accessing Google Account", Toast.LENGTH_LONG);
				}
				else {
					m_progressDialog.dismiss();
Utilities.showInfoToastSafe(SetupActivity.this, "Error when accessing Google User Account: " + m_e.toString(), Toast.LENGTH_LONG);
					
					// handle the exception
					Log.e(TAG, "registerLoginViaGoogle", m_e);
					if (m_e instanceof GooglePlayServicesAvailabilityException) {
Utilities.showInfoToastSafe(SetupActivity.this, "Google Play Services Error when accessing Google User Account", Toast.LENGTH_LONG);
						// GooglePlay is not there?
						GooglePlayServicesAvailabilityException e2 = (GooglePlayServicesAvailabilityException)m_e;					
						Dialog alert = GooglePlayServicesUtil.getErrorDialog(e2.getConnectionStatusCode(), SetupActivity.this, 0);
						alert.show();
					} else if (m_e instanceof UserRecoverableAuthException) {
						String errorMessage = isRegister ? 
							SetupActivity.this.getResources().getString(R.string.recoverable_register_error, m_e.getMessage()) :
							SetupActivity.this.getResources().getString(R.string.recoverable_login_error, m_e.getMessage());
Utilities.showInfoToastSafe(SetupActivity.this, "UserRecoverableAuthException when accessing Google User Account", Toast.LENGTH_LONG);	
						Utilities.showErrorToast(SetupActivity.this, errorMessage, Toast.LENGTH_SHORT);
						// allow the user to try to recover
						startActivityForResult(((UserRecoverableAuthException)m_e).getIntent(), 0);
					} else if (m_e instanceof IOException) {
Utilities.showInfoToastSafe(SetupActivity.this, "IOException when accessing Google User Account: " + m_e.getMessage(), Toast.LENGTH_LONG);						
						Utilities.showConnectionErrorToast(SetupActivity.this, m_e.getMessage(), Toast.LENGTH_SHORT);
					} else if (m_e instanceof GoogleAuthException) {
Utilities.showInfoToastSafe(SetupActivity.this, "GoogleAuthException when accessing Google User Account: " + m_e.getMessage(), Toast.LENGTH_LONG);
						Utilities.showErrorToast(SetupActivity.this, m_e.getMessage(), Toast.LENGTH_SHORT);
					}				
				}

			}				
		}.execute(accountName);
		
	}
	
	
	/**
	 * Register or login using an external facebook account.
	 * @param isRegister	true if registering, false if logging in
	 * @param accountName	email of the account, can be null if not known
	 */
	private void registerOrLoginViaFacebook(final boolean isRegister, final String accountName) {

		// make a fresh session
		Session session = Session.getActiveSession();
		if (session != null) {
			session.closeAndClearTokenInformation();
		}
		
		Session.openActiveSession(this, true, new Session.StatusCallback() {
				
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				
				if (session.isOpened()) {
					// progress bar while waiting
					m_progressDialog = ProgressDialog.show(
						SetupActivity.this, 
						getResources().getString(R.string.waiting), 
						getResources().getString(R.string.retrieving_account), 
						true, 
						true);
	
					registerOrLoginViaFacebookInfo(isRegister, accountName, session);
				}
			}
		});
	}
	
	
	private void registerOrLoginViaFacebookInfo(final boolean isRegister, final String accountName, final Session session)
	{
		Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				
				if (Session.getActiveSession() == session && user != null) {
					final String externalToken = session.getAccessToken();

					// get the facebook account name (email address or make one up)
					String accountName2 = accountName;
					if (accountName2 == null) {
						AccountManager accountManager = AccountManager.get(SetupActivity.this);
						final Account[] facebookAccounts = accountManager.getAccountsByType("com.facebook.auth.login");
						accountName2 = (facebookAccounts != null && facebookAccounts.length > 0) ? 
							facebookAccounts[0].name : 
							"fb_" + System.currentTimeMillis() + "@p2c.com";
					}
				
					if (isRegister) {
						String id = user.getId();
						final String accountName3 = accountName2;
						final String firstName = user.getFirstName();
						final String lastName = user.getLastName();

						new AsyncTask<String, Void, Void>() {
							// get the user's image in the background thread
							@Override
							protected Void doInBackground(String... params) {
								String pictureURL = Utilities.makeOwnerFacebookPictureUrl(externalToken);
								m_bitmap = Utilities.getExternalBitmap(pictureURL);
								return null;
							}
							
							// UI thread		
							@Override
							protected void onPostExecute(Void _) {
								registerWithToken(accountName3, firstName, lastName, "facebook", externalToken);
							}
						}.execute();
					} else {
						loginWithToken(accountName2, "facebook", externalToken);
					}
			    }
				else {
					// no longer waiting
					if (m_progressDialog != null) {
						m_progressDialog.dismiss();
					}
				
					// deal with Facebook errors
					if (response.getError() != null) {
						Utilities.showErrorToast(
							SetupActivity.this, 
							SetupActivity.this.getResources().getString(R.string.facebook_error) + " :" + response.getError().getErrorMessage(), 
							Toast.LENGTH_SHORT);
						// TODO: see handleError() in Facebook's SDK SelectionFragment.java
					}
				}
			}
		}).executeAsync();		
	}

	
	public void loginUser(View view) {
		showRegisterOrLoginDialog(false);
	}	

	
	/**
	 * Register the user with an external token.
	 * Called on the UI thread.
	 * @param accountName
	 * @param provider
	 * @param externalToken
	 */
	private void loginWithToken(String accountName, String provider, String externalToken) {
		TestFlight.passCheckpoint(TAG + "::loginWithToken() " + accountName + ", " + provider);
		TestFlight.log(TAG + "::loginWithToken() " + accountName + ", " + provider);
		Server server = new Server(this);
		server.loginWithToken(accountName, provider, externalToken, this, "loginWithToken");
	}	
	
	
	public void loginManually() {
		TestFlight.passCheckpoint(TAG + "::loginManually()");	
		TestFlight.log(TAG + "::loginManually()");		
		SetupLoginManuallyDialog dialog = new SetupLoginManuallyDialog();		
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
	}	
	
	
	public void registerUser(View view) {
		showRegisterOrLoginDialog(true);
	}
	
	
	/**
	 * Register the user with external account information.
	 * Called on the UI thread.
	 * @param accountName
	 * @param provider
	 * @param externalToken
	 */
	private void registerWithToken(String accountName, String firstName, String lastName, String provider, String externalToken) {
		TestFlight.passCheckpoint(TAG + "::registerWithToken() " + accountName + ", " + provider);
		TestFlight.log(TAG + "::registerWithToken() " + accountName + ", " + provider);		
		String locale = Locale.getDefault().getLanguage();
		
		Server server = new Server(this);
		server.registerWithToken(accountName, firstName, lastName, locale, provider, externalToken, this, "registerWithToken");
	}
	
	
	private void registerManually() {
		// go to the register manually screen
		Intent intent = new Intent("com.p2c.thelife.SetupRegisterManually");
		startActivity(intent);		
	}	
	
	
	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_account), true, true);
	}
	
	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		if (indicator.equals("register") || indicator.equals("registerWithToken") || indicator.equals("updateImage")) 
		{
			// register workflow continues with the superclass
			super.notifyServerResponseAvailable(indicator, httpCode, jsonObject, errorString);
		} else {
			// login or loginWithToken
			
			// make sure the user hasn't already cancelled
			if (m_progressDialog.isShowing()) {
				// make sure that some data was returned
				if (Utilities.isSuccessfulHttpCode(httpCode) && jsonObject != null) {
					
					// the app user
					UserModel user = null;
					try {
						user = UserModel.fromJSON(jsonObject, false);
					} catch (Exception e) {
						Log.e(TAG, "notifyServerResponseAvailable " + indicator, e);
					}
					String token = jsonObject.optString("authentication_token", "");				
					
					if (user != null && user.id != 0 && token != "") {
						
						// successful login
						
						// store the user configuration result
						TheLifeConfiguration.getOwnerDS().setOwner(user);
						TheLifeConfiguration.getOwnerDS().setToken(token);
						
						// refresh data stores							
						fullRefresh(false);
						return;
					}					
				}
			}
			
			// failed login
			Utilities.showInfoToast(this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT); 
	
			closeProgressBar();
			if (m_progressDialog != null) {
				m_progressDialog.dismiss();
			}
		}
	}

}
