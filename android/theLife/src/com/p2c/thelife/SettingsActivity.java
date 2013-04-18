package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.model.UserModel;

public class SettingsActivity extends SlidingMenuPollingFragmentActivity implements ServerListener, ImageSelectSupport.Listener {
	
	private static final String TAG = "SettingsActivity";
	
	private ProgressDialog m_progressDialog = null;	
	private Bitmap m_updatedBitmap = null;
	private UserModel m_updatedUser = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_settings, SlidingMenuSupport.SETTINGS_POSITION);
				
		// show the progress dialog while getting the user profile
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_account), true, true);
		Server server = new Server();
		server.queryUserProfile(TheLifeConfiguration.getUserId(), this, "queryUserProfile");		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	
	public boolean setUserProfile(View view) {
		
		// set the updated user record
		TextView textView = null;
		textView = (TextView)findViewById(R.id.settings_first_name);
		String firstName = textView.getText().toString();		
		textView = (TextView)findViewById(R.id.settings_last_name);
		String lastName = textView.getText().toString();		
		textView = (TextView)findViewById(R.id.settings_email);
		String email = textView.getText().toString();
		textView = (TextView)findViewById(R.id.settings_phone);
		String phone = textView.getText().toString();
		m_updatedUser = new UserModel(TheLifeConfiguration.getUserId(), firstName, lastName, null, email, phone);
		
		// call the server
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.storing_account), true, true);
		Server server = new Server();
		server.updateUserProfile(TheLifeConfiguration.getUserId(), firstName, lastName, email, phone, this, "updateUserProfile");		
		return true;
	}

	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject) {
		
		try {
			if (indicator.equals("queryUserProfile")) {
				
				closeProgressBar();				
				
				// update the UI with the result of the query

				// use the existing app user record
				UserModel user = TheLifeConfiguration.getUser();
				
				// update app user record with latest from server 
				if (jsonObject != null) {	
					user.setFromPartialJSON(jsonObject);				
					TheLifeConfiguration.setUser(user);
				}
					
				// update the UI
				TextView textView = null;
				textView = (TextView)findViewById(R.id.settings_name_by_image);
				textView.setText(user.getFullName());
				textView = (TextView)findViewById(R.id.settings_first_name);
				textView.setText(user.firstName);
				textView = (TextView)findViewById(R.id.settings_last_name);
				textView.setText(user.lastName);	
				textView = (TextView)findViewById(R.id.settings_email);
				textView.setText(user.email);
				textView = (TextView)findViewById(R.id.settings_phone);
				textView.setText(user.phone);
				
			} else if (indicator.equals("updateUserProfile")) {
				
				if (Utilities.successfulHttpCode(httpCode)) {
					
					// update the UI and app user
					TextView textView = (TextView)findViewById(R.id.settings_name_by_image);				
					textView.setText(m_updatedUser.getFullName());
					TheLifeConfiguration.setUser(m_updatedUser);					
	
					// have updated the user profile, so now update the user profile image if necessary
					if (m_updatedBitmap != null) {
						updateImageOnServer(m_updatedBitmap);
					} else {
						closeProgressBar();
					}
				} else {
					closeProgressBar();
				}
				
			} else if (indicator.equals("updateBitmap")) {
				
				if (Utilities.successfulHttpCode(httpCode)) {
					m_updatedBitmap = null;
				}
				closeProgressBar();			
			}
		}
		catch (Exception e) {
			Log.e(TAG, "notifyServerResponseAvailable() " + indicator, e);
		}
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.EventsForCommunity");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		
		return true;
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
		m_updatedBitmap = bitmap;
		
		
		ImageView imageView = (ImageView)findViewById(R.id.settings_image);
		imageView.setImageBitmap(m_updatedBitmap);		
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
	
	
	private void closeProgressBar() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
		}	
	}
	
}
