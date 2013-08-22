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
import android.view.Menu;
import android.view.View;
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


/**
 * Login or register a new user.
 * @author clarence
 *
 */
public class SetupActivity extends SetupRegisterActivityAbstract implements Server.ServerListener, ServerAccessDialogAbstract.Listener {
	
	private static final String TAG = "SetupActivity";

	
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
        
        // facebook requestCode=64206 resultCode=-1
 		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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
	private AlertDialog.Builder createRegisterOrLoginDialog(final boolean isRegister) {
		// add the options
		AccountManager accountManager = AccountManager.get(this);
		final Account[] googleAccounts = accountManager.getAccountsByType("com.google");
		final int[] selected = new int[] { 0 }; // must be final so it is an array
		String[] options = new String[googleAccounts.length + 1 /* facebook */ + 1 /* manual */];
		for (int index= 0; index < googleAccounts.length; index++) {
			options[index] = "Google " + googleAccounts[index].name;
		}
		options[options.length - 2] = "facebook";		
		options[options.length - 1] = getResources().getString(R.string.manually); // manual option
				
		// create the dialog
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(getResources().getString(isRegister ? R.string.choose_register_method_prompt : R.string.choose_login_method_prompt));;
		alertBuilder.setSingleChoiceItems(options, selected[0], new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				selected[0] = which;
			}
		});
			
		// set the buttons of the alert
		alertBuilder.setNegativeButton(R.string.cancel, null); 		
		alertBuilder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (selected[0] < googleAccounts.length) {
					registerOrLoginViaGoogle(isRegister, googleAccounts[selected[0]].name);
				} else if (selected[0] < googleAccounts.length + 1) {
					registerOrLoginViaFacebook(isRegister);
				} else {
					if (isRegister) {
						registerManually();
					} else {
						loginManually();
					}
				}
			}
		});

		return alertBuilder;		
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

				// get the Google Account token: see description and code in android developer docs for class GoogleAuthUtil
				try {				
					if (isRegister) {
						// read the Google user account info; this can result in a permission request to the user
						String userInfoToken = GoogleAuthUtil.getToken(SetupActivity.this, m_accountName,  
								"oauth2:https://www.googleapis.com/auth/userinfo.profile");
						m_account = Utilities.readJSONFromServer("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + userInfoToken);
						Log.i(TAG, "received user info" + m_account);
						
						// read the Google account image, if available
						String pictureURL = m_account.optString("picture");
						if (pictureURL != null) {
							m_bitmap = Utilities.getExternalBitmap(pictureURL);
							if (m_bitmap != null) {
								Log.i(TAG, "successfully got Google account image");
							}
						}
					}
					
					// TODO if login then update TheLife account with latest from Google
										
					// read the google account token, which will be verified by theLife server (no user permission needed)
					String token = null;
					token = GoogleAuthUtil.getToken(SetupActivity.this, params[0], 
								"audience:server:client_id:" + TheLifeConfiguration.GOOGLE_WEB_CLIENT_ID);
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
						registerWithToken(m_accountName, firstName, lastName, "google", externalToken);
					} else {
						loginWithToken(m_accountName, "google", externalToken);
					}
				}
				else {
					m_progressDialog.dismiss();
					
					// handle the exception
					Log.e(TAG, "registerLoginViaGoogle", m_e);
					if (m_e instanceof GooglePlayServicesAvailabilityException) {
						// GooglePlay is not there?
						GooglePlayServicesAvailabilityException e2 = (GooglePlayServicesAvailabilityException)m_e;					
						Dialog alert = GooglePlayServicesUtil.getErrorDialog(e2.getConnectionStatusCode(), SetupActivity.this, 0);
						alert.show();
					} else if (m_e instanceof UserRecoverableAuthException) {
						// TODO: too much info in the exception?
						// TODO: just retry?
						String errorMessage = isRegister ? 
							SetupActivity.this.getResources().getString(R.string.recoverable_register_error, m_e.getMessage()) :
							SetupActivity.this.getResources().getString(R.string.recoverable_login_error, m_e.getMessage());
						Utilities.showErrorToast(SetupActivity.this, errorMessage, Toast.LENGTH_SHORT);
						// allow the user to try to recover
						startActivityForResult(((UserRecoverableAuthException)m_e).getIntent(), 0);
					} else if (m_e instanceof IOException) {
						Utilities.showConnectionErrorToast(SetupActivity.this, m_e.getMessage(), Toast.LENGTH_SHORT);
					} else if (m_e instanceof GoogleAuthException) {
						Utilities.showErrorToast(SetupActivity.this, m_e.getMessage(), Toast.LENGTH_SHORT);
					}				
				}

			}				
		}.execute(accountName);
		
	}
	
	
	/**
	 * Register or login using an external facebook account.
	 * @param isRegister	true if registering, false if logging in
	 */
	private void registerOrLoginViaFacebook(final boolean isRegister) {
			
		Session session = Session.getActiveSession();
		if (session == null) {
			session = Session.openActiveSession(this, true, new Session.StatusCallback() {
				
				@Override
				public void call(Session session, SessionState state, Exception exception) {
System.out.println("FACEBOOK SESSION IS " + session);
System.out.println("FACEBOOK STATE IS " + state);
					if (session.isOpened()) {
System.out.println("FACEBOOK SESSION IS OPEN ");

						// progress bar while waiting
						m_progressDialog = ProgressDialog.show(
							SetupActivity.this, 
							getResources().getString(R.string.waiting), 
							getResources().getString(R.string.retrieving_account), 
							true, 
							true);

						// get the Facebook user account info
						final Session session2 = session;						
						Request.newMeRequest(session2, new Request.GraphUserCallback() {
							@Override
							public void onCompleted(GraphUser user, Response response) {
								if (Session.getActiveSession() == session2 && user != null) {
									String id = user.getId();
									String firstName = user.getFirstName();
									String lastName = user.getLastName();
									String externalToken = session2.getAccessToken();
									
									// get the facebook account name (email address or make one up)
									AccountManager accountManager = AccountManager.get(SetupActivity.this);
									final Account[] facebookAccounts = accountManager.getAccountsByType("com.facebook.auth.login");
									String accountName = (facebookAccounts != null && facebookAccounts.length > 0) ? 
										facebookAccounts[0].name : 
										"fb_" + System.currentTimeMillis() + "@p2c.com";
										
System.out.println("FACEBOOK ID: " + id);										
System.out.println("FACEBOOK EMAIL: " + accountName);
System.out.println("FACEBOOK FIRST NAME: " + firstName);
System.out.println("FACEBOOK LAST NAME: " + lastName);
									// TODO if login then update TheLife account with latest from facebook

									if (isRegister) {
										registerWithToken(accountName, firstName, lastName, "facebook", externalToken);
									} else {
										loginWithToken(accountName, "facebook", externalToken);
									}
							    }
								
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
						}).executeAsync();
					}
				}
			});
		}
	}

	
	public void loginUser(View view) {
		createRegisterOrLoginDialog(false).show();
	}	

	
	/**
	 * Register the user with an external token.
	 * Called on the UI thread.
	 * @param accountName
	 * @param provider
	 * @param externalToken
	 */
	private void loginWithToken(String accountName, String provider, String externalToken) {
			
		Server server = new Server(this);
		server.loginWithToken(accountName, provider, externalToken, this, "loginWithToken");
	}	
	
	
	public void loginManually() {
		SetupLoginDialog dialog = new SetupLoginDialog();		
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
	}	
	
	
	public void registerUser(View view) {
		createRegisterOrLoginDialog(true).show();
	}
	
	
	/**
	 * Register the user with external account information.
	 * Called on the UI thread.
	 * @param accountName
	 * @param provider
	 * @param externalToken
	 */
	private void registerWithToken(String accountName, String firstName, String lastName, String provider, String externalToken) {
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
					
					//TODO show errors correctly
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
