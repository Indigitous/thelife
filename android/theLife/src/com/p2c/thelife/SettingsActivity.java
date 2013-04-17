package com.p2c.thelife;

import java.io.InputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.Server.ServerListener;
import com.p2c.thelife.model.UserModel;

public class SettingsActivity extends SlidingMenuPollingActivity implements ServerListener {
	
	private static final String TAG = "SettingsActivity";
	
	private ProgressDialog m_progressDialog = null;	
	
	private static final int REQUESTCODE_CAMERA = 1;
	private static final int REQUESTCODE_GALLERY = 2;
	

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
		Toast.makeText(this, "User profile set", Toast.LENGTH_SHORT).show();
		
		TextView textView = null;
		textView = (TextView)findViewById(R.id.settings_name_by_image);
		String firstName = textView.getText().toString();
		textView = (TextView)findViewById(R.id.settings_first_name);
		String lastName = textView.getText().toString();
		textView = (TextView)findViewById(R.id.settings_last_name);
		String email = textView.getText().toString();	
		textView = (TextView)findViewById(R.id.settings_email);
		String phone = textView.getText().toString();
					
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.storing_account), true, true);
		Server server = new Server();
		server.updateUserProfile(TheLifeConfiguration.getUserId(), firstName, lastName, email, phone, this, "updateUserProfile");		
		return true;
	}

	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject) {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
		}
		
		try {
			// TODO use the existing record if the server call failed
			if (jsonObject != null) {
				
				UserModel user = TheLifeConfiguration.getUser();
				user.setFromPartialJSON(jsonObject);				
				TheLifeConfiguration.setUser(user);
				
				// update the UI with the result of the query
				if (indicator.equals("queryUserProfile")) {
					
					// the user using the app
					user = TheLifeConfiguration.getUser();
					
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
				}
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
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(getResources().getString(R.string.confirm_update_image));
		dialog.setNegativeButton(R.string.cancel, null);	
		dialog.setNeutralButton(R.string.get_camera_photo, new DialogInterface.OnClickListener() {
			
			/**
			 * Use the built in camera app to take a picture.
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUESTCODE_CAMERA);			
			}
		});
		dialog.setPositiveButton(R.string.get_gallery_photo, new DialogInterface.OnClickListener() {
			
			/**
			 * Use the built in gallery app to get an existing picture.
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, REQUESTCODE_GALLERY);	
			}
		});
		
		dialog.show();
	}
	
	
	/**
	 * Get the result of the photo gathering intent.
	 * See tutorial at http://developer.android.com/training/camera/photobasics.html.
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if (resultCode != Activity.RESULT_CANCELED) {
			
			if (requestCode == REQUESTCODE_CAMERA) {
				
				// just get the low res camera image from the activity result 
				Bundle bundle = intent.getExtras();
				Bitmap bitmap = (Bitmap)bundle.get("data");
				System.out.println("GOT A CAMERA BITMAP SIZE hxw " + bitmap.getHeight() + "x" + bitmap.getWidth());
				
				ImageView imageView = (ImageView)findViewById(R.id.settings_image);
				imageView.setImageBitmap(bitmap);
			} else if (requestCode == REQUESTCODE_GALLERY) {

				// waiting
				m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.processing_image), true, true);

				// activity result is a image content URI
				Uri contentUri = intent.getData();
				System.out.println("The URI is " + contentUri);
				
				InputStream is = null;
				try {
					// get the bitmap in a background thread
					new GetExternalBitmap().execute(contentUri);
				} catch (Exception e) {
					Log.e(TAG, "onActivityResult()", e);
				} finally {
					if (is != null) {
						try { is.close(); } catch (Exception e) { }
						is = null;
					}
				}
			}
		}
		
	}
	
	/**
	 * Because the external bitmap can be large and internet connections slow, get and process the external bitmap from another thread.
	 * @author clarence
	 *
	 */
	private class GetExternalBitmap extends AsyncTask<Uri, Void, Bitmap> {

		// background thread
		@Override
		protected Bitmap doInBackground(Uri... params) {
			
			InputStream is = null;
			Bitmap bitmap = null;
			try {
				// first pass: just get the size of the image
				is = getContentResolver().openInputStream(params[0]);								
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(is, null, options);
				is.close();
				System.out.println("GOT AN EXTERNAL BITMAP SIZE HxW " + options.outHeight + "x" + options.outWidth);			
				
				// second pass: get the scaled down version of the image
				is = getContentResolver().openInputStream(params[0]);
				options.inJustDecodeBounds = false;
				options.inSampleSize = Math.min(options.outHeight / 160, options.outWidth / 160);
				bitmap = BitmapFactory.decodeStream(is, null, options);								
			} catch (Exception e) {
				Log.e(TAG, "GetExternalBitmap()", e);
			} finally {
				if (is != null) {
					try { is.close(); } catch (Exception e) { }
					is = null;
				}
			}
			
			return bitmap;
		}
		
		// UI thread		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			
			if (m_progressDialog != null) {
				m_progressDialog.dismiss();
				m_progressDialog = null;
			}
			
			// display the final bitmap
			if (bitmap != null) {
				ImageView imageView = (ImageView)findViewById(R.id.settings_image);
				imageView.setImageBitmap(bitmap);
			}
		}		
	}
	
}
