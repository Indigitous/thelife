package com.p2c.thelife;

import java.util.Locale;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.p2c.thelife.model.UserModel;
import com.testflightapp.lib.TestFlight;

/**
 * Register the user manually, allowing for an image from the Android system.
 * @author clarence
 *
 */
public class SetupRegisterManuallyActivity extends SetupRegisterActivityAbstract implements ImageSelectSupport.Listener {
	
	private static final String TAG = "SetupRegisterManuallyActivity";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_register_manually);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup_register_manually, menu);
		return true;
	}
	
	/**
	 * User has chosen to register.
	 * @param view
	 */
	public void register(View view) {
		TestFlight.passCheckpoint(TAG + "::register()");		
		EditText emailField = (EditText)findViewById(R.id.setup_register_manually_email);
		String email = emailField.getText().toString();
		EditText firstNameField = (EditText)findViewById(R.id.setup_register_manually_first_name);
		String firstName = firstNameField.getText().toString();
		EditText lastNameField = (EditText)findViewById(R.id.setup_register_manually_last_name);
		String lastName = lastNameField.getText().toString();
		
		// make sure the passwords agree
		EditText passwordField = (EditText)findViewById(R.id.setup_register_manually_password);
		String password = passwordField.getText().toString();
		EditText passwordFieldConfirm = (EditText)findViewById(R.id.setup_register_manually_password_confirm);
		String passwordConfirm = passwordFieldConfirm.getText().toString();
		if (!password.equals(passwordConfirm)) {
			Utilities.showInfoToast(this, getResources().getString(R.string.different_passwords_error), Toast.LENGTH_SHORT);
		} else {
			// send the registration to the server
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.creating_account), true, true);		
			Server server = new Server(this);
			server.register(email, password, firstName, lastName, Locale.getDefault().getLanguage(), this, "register");
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
		ImageView imageView = (ImageView)findViewById(R.id.setup_register_manually_image);
		imageView.setImageBitmap(m_bitmap);
		
		// enable rotate buttons
		Button button = (Button)findViewById(R.id.image_rotate_cw);
		button.setEnabled(true);
		button = (Button)findViewById(R.id.image_rotate_ccw);
		button.setEnabled(true);
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
			ImageView imageView = (ImageView)findViewById(R.id.setup_register_manually_image);		
			imageView.setImageBitmap(m_bitmap);	
		}
	}

}
