package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.FriendModel;

/**
 * Add a friend, allowing for an image from the Android system.
 * @author clarence
 *
 */
public class FriendImportManuallyActivity extends SlidingMenuPollingFragmentActivity implements Server.ServerListener, ImageSelectSupport.Listener {
	
	private static final String TAG = "FriendImportManuallyActivity";

	private ProgressDialog m_progressDialog = null;
	private Bitmap m_bitmap = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_friend_import_manually, SlidingMenuSupport.FRIENDS_POSITION);
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
			Intent intent = new Intent("com.p2c.thelife.Friends");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);						
			startActivity(intent);
		}  else if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_friend_import_manually_help);
			intent.putExtra("position", SlidingMenuSupport.FRIENDS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.FriendImportManually");
			intent.putExtra("webview_data", getResources().getString(R.string.first_time_adding_friend_help));
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

		Server server = new Server(this);
		server.createFriend(firstName, lastName, threshold, this, "createFriend");						
	}
	
	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {

		if (indicator.equals("createFriend")) {
			if (Utilities.isSuccessfulHttpCode(httpCode) && jsonObject != null) {
				int friendId = jsonObject.optInt("id", 0);
				if (friendId != 0) {
					
					// successful
									
					String firstName = jsonObject.optString("first_name", "");
					String lastName = jsonObject.optString("last_name", "");
					int thresholdIndex = FriendModel.thresholdId2Index(jsonObject.optInt("threshold_id"));
					
					// add the friend to the list of known friends
					FriendModel.Threshold threshold = FriendModel.thresholdValues[thresholdIndex]; 
					FriendModel friend = new FriendModel(friendId, firstName, lastName, threshold, "", "");
					TheLifeConfiguration.getFriendsDS().add(friend);
					TheLifeConfiguration.getFriendsDS().notifyDSChangedListeners();
					TheLifeConfiguration.getFriendsDS().forceRefresh(null);
					
					// check to see if there is an image needing to be sent to the server
					if (m_bitmap != null) {
						BitmapCacheHandler.saveBitmapToCache("friends", friend.id, "image", m_bitmap);						
						Server server = new Server(this);
						server.updateImage("friends", friend.id, this, "updateImage");
					} else {
						finishImport();
					}
					
					return;
				}
			}
				
		} else if (indicator.equals("updateImage")) {
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
		
		// set the image
		ImageView imageView = (ImageView)findViewById(R.id.friend_image);
		imageView.setImageBitmap(m_bitmap);
		
		// enable rotate buttons
		Button button = (Button)findViewById(R.id.image_rotate_cw);
		button.setEnabled(true);
		button = (Button)findViewById(R.id.image_rotate_ccw);
		button.setEnabled(true);		
	}	
	
	
	/**
	 * Have just added a friend.
	 */
	private void finishImport() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
		}
		
		Intent intent = new Intent("com.p2c.thelife.Friends");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	
	public void rotateImageCW(View view) {
		rotateImage(90.0f);
	}
	
	
	public void rotateImageCCW(View view) {
		rotateImage(-90.0f);
	}	
	
	
	private void rotateImage(float angle) {
		if (m_bitmap != null) {
			// rotate image in memory
			Matrix matrix = new Matrix();
			matrix.setRotate(angle);
			m_bitmap = Bitmap.createBitmap(m_bitmap, 0, 0, m_bitmap.getWidth(), m_bitmap.getHeight(), matrix, true);
			
			// save new image bitmap
			ImageView imageView = (ImageView)findViewById(R.id.friend_image);		
			imageView.setImageBitmap(m_bitmap);	
		}
	}	

}
