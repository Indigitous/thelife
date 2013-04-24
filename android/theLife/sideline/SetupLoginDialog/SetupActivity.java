package com.p2c.thelife;

import org.json.JSONObject;

import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.UserModel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SetupActivity 
	extends FragmentActivity 
	implements DSRefreshedListener, Server.ServerListener, ServerAccessDialogAbstract.Listener, ImageSelectSupport.Listener {
	
	private static final String TAG = "SetupActivity";
	
	private ProgressDialog m_progressDialog = null;	
	private Bitmap m_bitmap = null;
	private UserModel m_user = null;
	private String m_token = null;	
	private SetupRegisterDialog m_registerDialog = null;
	
	
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
		m_registerDialog = new SetupRegisterDialog();		
		m_registerDialog.show(getSupportFragmentManager(), m_registerDialog.getClass().getSimpleName());
	}
	
	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		if (indicator.equals("login")) {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_account), true, true);
		} else if (indicator.equals("register")) {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.creating_account), true, true);
		}
	}
	
	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject) {
					
		if (indicator.equals("login") || indicator.equals("register")) {

			// make sure the user hasn't already cancelled
			if (m_progressDialog.isShowing()) {
				// make sure that some data was returned
				if (jsonObject != null) {
					
					// the app user
					m_user = null;
					try {
						m_user = UserModel.fromJSON(jsonObject, false);
					} catch (Exception e) {
						Log.e(TAG, "notifyServerResponseAvailable " + indicator, e);
					}
					m_token = jsonObject.optString("authentication_token", "");			
					
					if (m_user != null && m_user.id != 0 && m_token != "" && m_user.email != "") {
						
						// successful login or registration
						
						if (indicator.equals("register") && m_bitmap != null) {
							// successful registration, so now update the user profile image
							updateImageOnServer(m_bitmap);				
						} else {
							finishLoginOrRegistration(m_user, m_token);
						}
						
						return;
					}					
				}
			}
			
			// failed login or register
			if (indicator.equals("login")) {
				Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
			}
			closeProgressBar();
			
		// Update bitmap
		} else if (indicator.equals("updateBitmap")) {
			finishLoginOrRegistration(m_user, m_token);	
		}
	}
		
		
	/**
	 * User has selected their image for a possible update.
	 * @param view
	 */
	public void selectImage(View view) {
Toast.makeText(this, "IN selectImage", Toast.LENGTH_SHORT).show();		
		ImageSelectDialog dialog = new ImageSelectDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());			
	}
	
	
	/**
	 * Get the result of the photo gathering intent.
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
Toast.makeText(this, "IN onActivityResult", Toast.LENGTH_SHORT).show();
		// let the support object handle it
		ImageSelectSupport support = new ImageSelectSupport(this, this);
		support.onActivityResult(requestCode, resultCode, intent);
	}
	
	
	/**
	 * Callback from ImageSelectSupport
	 */
	@Override
	public void notifyImageSelected(Bitmap bitmap) {
		m_bitmap = bitmap;
		
Toast.makeText(this, "IN notifyImageSelected", Toast.LENGTH_SHORT).show();
		ImageView imageView = m_registerDialog.getImageView();
		imageView.setImageBitmap(bitmap);	
	}	
	
	
	/**
	 * Update the image on the server.
	 * @param bitmap
	 */
	private void updateImageOnServer(Bitmap bitmap) {
		System.out.println("GOT A BITMAP SIZE hxw " + bitmap.getHeight() + "x" + bitmap.getWidth());
		
		BitmapCache.saveBitmapToCache("users", TheLifeConfiguration.getOwnerDS().getUserId(), "image", bitmap);										
		Server server = new Server(this);
		server.updateBitmap("users", TheLifeConfiguration.getOwnerDS().getUserId(), "image", bitmap, this, "updateBitmap");
	}
	
	
	private void closeProgressBar() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
		}	
	}		
	
				
	private void finishLoginOrRegistration(UserModel user, String token) {
		
		closeProgressBar();
				
		// store the user configuration result
		TheLifeConfiguration.getOwnerDS().setUser(user);
		TheLifeConfiguration.getOwnerDS().setToken(token);
		
		// refresh data stores
		fullRefresh();
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
		TheLifeConfiguration.getCategoriesDS().forceRefresh("categories");
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
			TheLifeConfiguration.getDeedsDS().forceRefresh("deeds");
		} else if (indicator.equals("deeds")) {
			TheLifeConfiguration.getDeedsDS().removeDSRefreshedListener(this);			
//			TheLifeConfiguration.getUsersDS().addDSRefreshedListener(this);			
//			TheLifeConfiguration.getUsersDS().forceRefresh("users");
//		} else if (indicator.equals("users")) {
//			TheLifeConfiguration.getUsersDS().removeDSRefreshedListener(this);			
			TheLifeConfiguration.getGroupsDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getGroupsDS().forceRefresh("groups");	
		} else if (indicator.equals("groups")) {
			TheLifeConfiguration.getGroupsDS().removeDSRefreshedListener(this);			
			TheLifeConfiguration.getFriendsDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getFriendsDS().forceRefresh("friends");	
		} else if (indicator.equals("friends")) {
			TheLifeConfiguration.getFriendsDS().removeDSRefreshedListener(this);			
			TheLifeConfiguration.getEventsDS().addDSRefreshedListener(this);			
			TheLifeConfiguration.getEventsDS().forceRefresh("events");
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
