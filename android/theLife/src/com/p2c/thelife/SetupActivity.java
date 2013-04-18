package com.p2c.thelife;

import org.json.JSONObject;

import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.UserModel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class SetupActivity extends FragmentActivity implements Server.ServerListener, AbstractServerAccessDialog.Listener, DSRefreshedListener {
	
	private static final String TAG = "SetupActivity";
	
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
		// go to the register screen
		Intent intent = new Intent("com.p2c.thelife.SetupRegister");
		startActivity(intent);				
	}
	
	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		if (indicator.equals("login")) {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_account), true, true);
		} else {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.creating_account), true, true);
		}
	}
	
	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject) {
					
		// make sure the user hasn't already cancelled
		if (m_progressDialog.isShowing()) {
			// make sure that some data was returned
			if (jsonObject != null) {
				
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
						TheLifeConfiguration.setUser(user);
						TheLifeConfiguration.setToken(token);
						
						// refresh data stores							
						fullRefresh();
						return;
					}
					
				// REGISTER
				} else if (indicator.equals("register")) {
					
					if (user != null && user.id != 0 && token != "" && user.email != "") {
						Toast.makeText(this, getResources().getString(R.string.registration_successful), Toast.LENGTH_SHORT).show(); 
						
						// successful register
						
						// store the user configuration result
						TheLifeConfiguration.setUser(user);
						TheLifeConfiguration.setToken(token);
						
						// refresh data stores
						fullRefresh();
						return;
					}					
					
				}
				
				//TODO show errors correctly
			}
		}
		
		// failed login or register
		Toast.makeText(this, "INCORRECT " + indicator, Toast.LENGTH_SHORT).show(); 

		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}
	}
	
	
	/**
	 * Full refresh of the data stores.
	 */
	private void fullRefresh() {	
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_configuration), true, true);		
		
		TheLifeConfiguration.getCategoriesDS().addDSRefreshedListener(this);
		TheLifeConfiguration.getCategoriesDS().refresh("categories");
	}

	/**
	 * Chain together the data stores' refresh callbacks so that all data stores are refreshed sequentially.
	 * Not pretty but it works.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		if (indicator.equals("categories")) {
			TheLifeConfiguration.getCategoriesDS().removeDSRefreshedListener(this);
			TheLifeConfiguration.getDeedsDS().addDSRefreshedListener(this);			
			TheLifeConfiguration.getDeedsDS().refresh("deeds");
		} else if (indicator.equals("deeds")) {
			TheLifeConfiguration.getDeedsDS().removeDSRefreshedListener(this);			
//			TheLifeConfiguration.getUsersDS().addDSRefreshedListener(this);			
//			TheLifeConfiguration.getUsersDS().refresh("users");
//		} else if (indicator.equals("users")) {
//			TheLifeConfiguration.getUsersDS().removeDSRefreshedListener(this);			
			TheLifeConfiguration.getGroupsDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getGroupsDS().refresh("groups");	
		} else if (indicator.equals("groups")) {
			TheLifeConfiguration.getGroupsDS().removeDSRefreshedListener(this);			
			TheLifeConfiguration.getFriendsDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getFriendsDS().refresh("friends");	
		} else if (indicator.equals("friends")) {
			TheLifeConfiguration.getFriendsDS().removeDSRefreshedListener(this);			
			TheLifeConfiguration.getEventsDS().addDSRefreshedListener(this);			
			TheLifeConfiguration.getEventsDS().refresh("events");
		} else if (indicator.equals("events")) {
			TheLifeConfiguration.getEventsDS().removeDSRefreshedListener(this);			
			if (m_progressDialog != null) {
				m_progressDialog.dismiss();
				
				// go to the main screen
				Intent intent = new Intent("com.p2c.thelife.EventsForCommunity");
				startActivity(intent);
				return;				
			}					
		} else {
			Log.wtf(TAG, "unknown refresh indicator " + indicator);
		}
	}

}
