package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.FriendModel;

/**
 * Register the user, allowing for an image from the Android system.
 * @author clarence
 *
 */
public class FriendImportManuallyActivity extends SlidingMenuPollingFragmentActivity implements Server.ServerListener, ImageSelectSupport.Listener {
	
	private static final String TAG = "SetupRegisterActivity";

	private ProgressDialog m_progressDialog = null;
	private Bitmap m_bitmap = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_import_friend_manually, SlidingMenuSupport.FRIENDS_POSITION);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.friends_import, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.FriendsImport");
			startActivity(intent);
		}
		
		return true;
	}	
	
	/**
	 * User has chosen to import a friend.
	 * @param view
	 */
	public void importFriend(View view) {
		
		EditText firstNameField = (EditText)findViewById(R.id.import_friend_first_name);
		String firstName = firstNameField.getText().toString();
		EditText lastNameField = (EditText)findViewById(R.id.import_friend_last_name);
		String lastName = lastNameField.getText().toString();
		Spinner thresholdField = (Spinner)findViewById(R.id.import_friend_threshold);
		int thresholdIndex = thresholdField.getSelectedItemPosition();
		FriendModel.Threshold threshold = FriendModel.thresholdValues[thresholdIndex];					
		
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.adding_friend), true, true);		

		Server server = new Server();
		server.createFriend(firstName, lastName, threshold, this, "createFriend");						
	}
	
	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject) {

		if (indicator.equals("createFriend")) {
			if (Utilities.successfulHttpCode(httpCode) && jsonObject != null) {
				int friendId = jsonObject.optInt("id", 0);
				if (friendId != 0) {
					
					// successful
									
					String firstName = jsonObject.optString("first_name", "");
					String lastName = jsonObject.optString("last_name", "");
					int thresholdIndex = FriendModel.thresholdId2Index(jsonObject.optInt("threshold_id"));
					
					// add the friend to the list of known friends
					FriendModel.Threshold threshold = FriendModel.thresholdValues[thresholdIndex]; 
					FriendModel friend = new FriendModel(friendId, firstName, lastName, null, threshold, "", "");
					TheLifeConfiguration.getFriendsDS().add(friend);
					TheLifeConfiguration.getFriendsDS().notifyDSChangedListeners();
					TheLifeConfiguration.getFriendsDS().forceRefresh(null);
					
					// check to see if there is an image needing to be sent to the server
					if (m_bitmap != null) {
						BitmapCache.saveBitmapToCache("friends", friend.id, "image", m_bitmap);						
						Server server = new Server();
						server.updateBitmap("friends", friend.id, "image", m_bitmap, this, "updateBitmap");
					} else {
						finishImport();
					}
					
					return;
				}
			}
				
		} else if (indicator.equals("updateBitmap")) {
			finishImport();
			
			return;
		} 
		
		Toast.makeText(this, getResources().getString(R.string.friend_import_error), Toast.LENGTH_LONG).show();
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
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
		
		ImageView imageView = (ImageView)findViewById(R.id.friend_image);
		imageView.setImageBitmap(m_bitmap);		
	}	
	
	
	private void finishImport() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
		}		
		
		Intent intent = new Intent("com.p2c.thelife.FriendsImport");
		startActivity(intent);
	}

}