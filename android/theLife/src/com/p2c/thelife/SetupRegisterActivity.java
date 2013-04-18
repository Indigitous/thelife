package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.UserModel;

/**
 * Register the user, allowing for an image from the Android system.
 * @author clarence
 *
 */
public class SetupRegisterActivity extends FragmentActivity	implements Server.ServerListener, DSRefreshedListener, ImageSelectSupport.Listener {
	
	private static final String TAG = "SetupRegisterActivity";
	
	private ProgressDialog m_progressDialog = null;
	private Bitmap m_bitmap = null;
	private UserModel m_user = null;
	private String m_token = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_register);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup_register, menu);
		return true;
	}
	
	/**
	 * User has chosen to register.
	 * @param view
	 */
	public void register(View view) {
		EditText emailField = (EditText)findViewById(R.id.setup_register_email);
		String email = emailField.getText().toString();
		EditText passwordField = (EditText)findViewById(R.id.setup_register_password);
		String password = passwordField.getText().toString();
		EditText firstNameField = (EditText)findViewById(R.id.setup_register_first_name);
		String firstName = firstNameField.getText().toString();
		EditText lastNameField = (EditText)findViewById(R.id.setup_register_last_name);
		String lastName = lastNameField.getText().toString();

		// send the registration to the server
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.creating_account), true, true);		
		Server server = new Server();
		server.register(email, password, firstName, lastName, this, "register");		
	}
	
	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject) {

		// Register
		if (indicator.equals("register")) {
			
			// make sure that some data was returned
			if (jsonObject != null) {
				
				// get the app user and token
				try {
					m_user = UserModel.fromJSON(jsonObject, false);
				} catch (Exception e) {
					Log.e(TAG, "notifyServerResponseAvailable " + indicator, e);
				}
				m_token = jsonObject.optString("authentication_token", "");	
			
				if (m_user != null && m_user.id != 0 && m_token != "" && m_user.email != "") {
					
					Toast.makeText(this, getResources().getString(R.string.registration_successful), Toast.LENGTH_SHORT).show(); 
					
					// successful registration, so now update the user profile image if necessary
					if (m_bitmap != null) {
						updateImageOnServer(m_bitmap);
					} else {
						finishRegistration(m_user, m_token);
					}					
				}					
			
			} else {
				// failed register
				Toast.makeText(this, "Incorrect Registration", Toast.LENGTH_SHORT).show(); 
				
				closeProgressBar();
			}
				
		// Update bitmap
		} else if (indicator.equals("updateBitmap")) {
			finishRegistration(m_user, m_token);	
		}
	}
		
		
	/**
	 * User has selected their image for a possible update.
	 * @param view
	 */
	public void selectImage(View view) {
		ImageSelectDialog dialog = new ImageSelectDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());			
	}
	
	
	/**
	 * Get the result of the photo gathering intent.
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
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
		
		ImageView imageView = (ImageView)findViewById(R.id.settings_image);
		imageView.setImageBitmap(m_bitmap);		
	}	
	
	
	/**
	 * Update the image on the server.
	 * @param bitmap
	 */
	private void updateImageOnServer(Bitmap bitmap) {
		System.out.println("GOT A BITMAP SIZE hxw " + bitmap.getHeight() + "x" + bitmap.getWidth());
		
		Server server = new Server();
		server.updateBitmap("users", TheLifeConfiguration.getUserId(), "image", bitmap, this, "updateBitmap");
	}
	
	
	private void finishRegistration(UserModel user, String token) {
		
		closeProgressBar();
				
		// store the user configuration result
		TheLifeConfiguration.setUser(user);
		TheLifeConfiguration.setToken(token);
		
		// refresh data stores
		fullRefresh();
	}
	
	
	private void closeProgressBar() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
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
