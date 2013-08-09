package com.p2c.thelife;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.UserModel;

/**
 * Superclass for account registration.
 * @author clarence
 *
 */
public class SetupRegisterActivityAbstract extends SetupActivityAbstract implements Server.ServerListener {
	
	private static final String TAG = "SetupRegisterActivityAbstract";
	
	protected Bitmap m_bitmap = null;
	protected UserModel m_user = null;
	protected String m_token = null;
	
	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {

		// Register
		if (indicator.equals("register") || indicator.equals("registerWithToken")) {
			
			// make sure that some data was returned
			if (Utilities.isSuccessfulHttpCode(httpCode) && jsonObject != null) {
				
				// get the app user and token
				try {
					m_user = UserModel.fromJSON(jsonObject, false);
				} catch (Exception e) {
					Log.e(TAG, "notifyServerResponseAvailable " + indicator, e);
				}
				m_token = jsonObject.optString("authentication_token", "");	
			
				if (m_user != null && m_user.id != 0 && m_token != "" && m_user.email != "") {
					
					Utilities.showInfoToast(this, getResources().getString(R.string.registration_successful), Toast.LENGTH_SHORT); 
					
					// successful registration, so now update the user profile image if necessary
					if (m_bitmap != null) {
						updateImageOnServer(m_bitmap);
					} else {
						finishRegistration(m_user, m_token);
					}					
				}					
			
			} else {
				// failed register
				Utilities.showInfoToast(this, getResources().getString(R.string.registration_failed), Toast.LENGTH_SHORT); 
				
				closeProgressBar();
			}
				
		// Update bitmap
		} else if (indicator.equals("updateImage")) {
			finishRegistration(m_user, m_token);	
		}
	}
	
	
	@Override
	public void onBackPressed() {
		// Because the previous activity, SetupActivity, has noHistory=true, 
		// support the back arrow in code here.
		Intent intent = new Intent("com.p2c.thelife.Setup");
		startActivity(intent);
	}
	
	
	/**
	 * Update the image on the server.
	 * @param bitmap
	 */
	private void updateImageOnServer(Bitmap bitmap) {		
		BitmapCacheHandler.saveBitmapToCache("users", m_user.id, "image", bitmap);										
		Server server = new Server(this, m_token); // use the new token, which has not yet been registered in the device
		server.updateImage("users", m_user.id, this, "updateImage");
	}
	
	
	private void finishRegistration(UserModel user, String token) {
		
		closeProgressBar();
				
		// store the user configuration result
		TheLifeConfiguration.getOwnerDS().setOwner(user);
		TheLifeConfiguration.getOwnerDS().setToken(token);
		
		// refresh data stores
		fullRefresh(true);
	}

}
