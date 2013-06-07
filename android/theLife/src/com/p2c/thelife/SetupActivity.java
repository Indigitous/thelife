package com.p2c.thelife;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.p2c.thelife.model.UserModel;


/**
 * Login or register a new user.
 * @author clarence
 *
 */
public class SetupActivity extends SetupActivityAbstract implements Server.ServerListener, ServerAccessDialogAbstract.Listener {
	
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
