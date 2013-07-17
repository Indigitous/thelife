package com.p2c.thelife;

import java.io.IOException;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.p2c.thelife.model.UserModel;


/**
 * Login or register a new user.
 * @author clarence
 *
 */
public class SetupActivity extends SetupActivityAbstract implements Server.ServerListener, ServerAccessDialogAbstract.Listener {
	
	private static final String TAG = "SetupActivity";
	
	public static final int REQUESTCODE_AUTHENTICATION = 1;
	
	
	Account m_googleAccount = null;		// type "com.google"
	Account m_facebookAccount = null;	// type "com.facebook.auth.login"

	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		
		// hide the action bar; can only be done with later versions of Android
		if (Build.VERSION.SDK_INT >= 11) {
			getActionBar().hide();
		}
		
		
//		AuthenticatorDescription[] authenticators = accountManager.getAuthenticatorTypes();
//		for (AuthenticatorDescription authenticatorDescription : authenticators) {
//System.out.println("AUTHENTICATOR " + authenticatorDescription.toString());
//		}
		
//		AccountManagerFuture<Bundle> amFuture = 
//			accountManager.getAuthToken(
//				m_googleAccount, 
//				"gmail", 
//				null, 
//				this, 
//				new AccountManagerCallback<Bundle>() {
//					
//					@Override
//					public void run(AccountManagerFuture<Bundle> arg0) {
//						System.out.println("GOT CALLBACK WITH " + arg0);
//						
//					}
//				}, 
//				null);

	}
	
	/********************************** FROM GoogleAuthUtil ****************************************/
	
	//GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
	
//	getAndUseAuthTokenInAsyncTask();
	
//	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//	       if (requestCode == REQUESTCODE_AUTHENTICATION) {
//	           if (resultCode == RESULT_OK) {
//	               getAndUseAuthTokenInAsyncTask();
//	           }
//	       }
//	   }
//	   
//	  // Example of how to use the GoogleAuthUtil in a blocking, non-main thread context
//	   void getAndUseAuthTokenBlocking() {
//	       try {
//	          // Retrieve a token for the given account and scope. It will always return either
//	          // a non-empty String or throw an exception.
//System.out.println("HERE IS MY CALL TO GOOGLE AUTH UTIL");
//	          final String token = GoogleAuthUtil.getToken(this, "clarence@ballistiq.com", "audience:server:client_id:" + m_appClientId);
//System.out.println("HERE IS MY CALL RESULT FROM GOOGLE AUTH UTIL " + token);
//	          // Do work with token.
//	          //...
////	          if (server indicates token is invalid) {
////	              // invalidate the token that we found is bad so that GoogleAuthUtil won't
////	              // return it next time (it may have cached it)
////	              GoogleAuthUtil.invalidateToken(this, token);
////	              // consider retrying getAndUseTokenBlocking() once more
////	              return;
////	          }
//	          return;
//	       } catch (GooglePlayServicesAvailabilityException playEx) {
//System.out.println("GOOGLE PLAY EXCEPTION " + playEx);	    	   
//	         Dialog alert = GooglePlayServicesUtil.getErrorDialog(
//	             playEx.getConnectionStatusCode(),
//	             this,
//	             REQUESTCODE_AUTHENTICATION);
//	         // ...
//	       } catch (UserRecoverableAuthException userAuthEx) {
//System.out.println("GOOGLE USERREC EXCEPTION " + userAuthEx);	    	   	    	   
//	          // Start the user recoverable action using the intent returned by
//	          // getIntent()
//	          startActivityForResult(
//	                  userAuthEx.getIntent(),
//	                  REQUESTCODE_AUTHENTICATION);
//	          return;
//	       } catch (IOException transientEx) {
//System.out.println("GOOGLE IO EXCEPTION " + transientEx);	    	   
//	          // network or server error, the call is expected to succeed if you try again later.
//	          // Don't attempt to call again immediately - the request is likely to
//	          // fail, you'll hit quotas or back-off.
//	          //...
//	          return;
//	       } catch (GoogleAuthException authEx) {
//	          // Failure. The call is not expected to ever succeed so it should not be
//	          // retried.
//	          //...
//System.out.println("GOOGLE AUTH EXCEPTION " + authEx);
//	          return;
//	       }
//	   }

//	   // Example of how to use AsyncTask to call blocking code on a background thread.
//	   void getAndUseAuthTokenInAsyncTask() {
//	       AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
//	           protected Void doInBackground(Void... params) {
//	               getAndUseAuthTokenBlocking();
//	               return null;
//	           }
//	       };
//	       task.execute((Void)null);
//	   }	
//	
	
	

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
		// go to the register screen
		Intent intent = new Intent("com.p2c.thelife.SetupRegister");
		startActivity(intent);				
	}
	
	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_account), true, true);
	}
	
	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
					
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
				
				// LOGIN
				if (indicator.equals("login")) {
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
				
				//TODO show errors correctly
			}
		}
		
		// failed login
		Toast.makeText(this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show(); 

		closeProgressBar();
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}
	}

}
