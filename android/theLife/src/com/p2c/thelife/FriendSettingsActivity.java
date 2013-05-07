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
import com.p2c.thelife.model.FriendModel;

/**
 * Edit the Friend's profile.
 * @author clarence
 *
 */
public class FriendSettingsActivity extends SlidingMenuPollingFragmentActivity implements ServerListener, ImageSelectSupport.Listener {
	
	private static final String TAG = "FriendSettingsActivity";
	
	private ProgressDialog m_progressDialog = null;	
	private Bitmap m_updatedBitmap = null;
	private FriendModel m_friend = null;
	private FriendModel m_updatedFriend = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_friend_settings, SlidingMenuSupport.FRIENDS_POSITION);
		
		// Get the friend for this deed
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = TheLifeConfiguration.getFriendsDS().findById(friendId);
		
		// Show the friend
		if (m_friend != null) {
			ImageView imageView = (ImageView)findViewById(R.id.friend_settings_image);
			imageView.setImageBitmap(FriendModel.getImage(m_friend.id));
			
			TextView firstNameView = (TextView)findViewById(R.id.friend_settings_first_name);
			firstNameView.setText(m_friend.firstName);
			
			TextView lastNameView = (TextView)findViewById(R.id.friend_settings_last_name);
			lastNameView.setText(m_friend.lastName);
		}		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.friend_settings, menu);
		return true;
	}
	
	
	public boolean setFriendProfile(View view) {
		
		// set the updated friend record
		TextView textView = null;
		textView = (TextView)findViewById(R.id.friend_settings_first_name);
		String firstName = textView.getText().toString();		
		textView = (TextView)findViewById(R.id.friend_settings_last_name);
		String lastName = textView.getText().toString();
		m_updatedFriend = new FriendModel(m_friend.id, firstName, lastName, FriendModel.Threshold.NewContact, null, null);
		
		// call the server
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.storing_friend_account), true, true);
		Server server = new Server(this);
		server.updateFriend(m_friend.id, firstName, lastName, this, "updateFriend");		
		return true;
	}

	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		
		try {
			if (indicator.equals("updateFriend")) {
				
				if (Utilities.isSuccessfulHttpCode(httpCode)) {
					
					// update the UI and friend model
					TextView firstNameView = (TextView)findViewById(R.id.friend_settings_first_name);
					firstNameView.setText(m_updatedFriend.firstName);
					TextView lastNameView = (TextView)findViewById(R.id.friend_settings_last_name);
					lastNameView.setText(m_updatedFriend.lastName);	
					
					// update the FriendModel
					m_friend.firstName = m_updatedFriend.firstName;
					m_friend.lastName = m_updatedFriend.lastName;
	
					// have updated the friend profile, so now update the friend image if necessary
					if (m_updatedBitmap != null) {
						updateImageOnServer(m_updatedBitmap);
					} else {
						finishEditingFriend();
					}
				} else {
					closeProgressBar();
				}
				
			} else if (indicator.equals("updateImage")) {
				
				if (Utilities.isSuccessfulHttpCode(httpCode)) {
					m_updatedBitmap = null;
					finishEditingFriend();
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
			Intent intent = new Intent("com.p2c.thelife.EventsForFriend");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);						
			intent.putExtra("friend_id", m_friend.id);			
			startActivity(intent);
		}
		
		return true;
	}
	
	
	/**
	 * Owner has selected their friend's image for a possible update.
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
		
		ImageView imageView = (ImageView)findViewById(R.id.friend_settings_image);
		imageView.setImageBitmap(m_updatedBitmap);		
	}	
	
	
	/**
	 * Update the image on the server.
	 * @param bitmap
	 */
	private void updateImageOnServer(Bitmap bitmap) {		
		BitmapCacheHandler.saveBitmapToCache("friends", m_friend.id, "image", bitmap);								
		Server server = new Server(this);
		server.updateImage("friends", m_friend.id, this, "updateImage");
	}
	
	
	private void closeProgressBar() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
		}	
	}
	
	
	/**
	 * Owner is done editing their friend, so back the Friend events page.
	 */
	private void finishEditingFriend() {
		closeProgressBar();
		
		// back to the Friend events page
		Intent intent = new Intent("com.p2c.thelife.EventsForFriend");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);						
		intent.putExtra("friend_id", m_friend.id);			
		startActivity(intent);
		
		finish();
	}

}
