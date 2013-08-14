package com.p2c.thelife.push;

import java.io.IOException;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.p2c.thelife.R;
import com.p2c.thelife.Server;
import com.p2c.thelife.Utilities;
import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.UserModel;

public class GCMSupport implements Server.ServerListener {
	
	private static final String TAG = "GCMSupport";
	
	// note: app backup must not include registation id
	private static final String SYSKEY_GCM_REGISTRATION_ID = "gcm_registration_id";
	private static final String SYSKEY_GCM_REGISTRATION_EXPIRY = "gcm_registration_expiry";
	private static final long   EXPIRY_DELTA = 4 * 24 * 60 * 60 * 1000; // 4 days in millis
	
	private static GCMSupport m_support = null;
	private Context m_context = null; 
	private Listener m_listener = null;
	private String m_indicator = null;	
	
	
	/**
	 * reports when the registration id is available
	 *
	 */
	public interface Listener {
		/**
		 * @param indicator			returned to the listener
		 * @param errorString		error message, can be null for no error
		 */
		public void notifyGCMRegistrationAvailable(String indicator, String errorString);
	}	

	
	public static GCMSupport getInstance() {
		if (m_support == null) {
			m_support = new GCMSupport();
		}
		
		return m_support;
	}
	
	
	/**
	 * restricted constructor
	 */
	private GCMSupport() {
	}
	
	
	/**
	 * @return	the registration ID from the system settings, if it exists; can return null.
	 */
	public String getRegistrationId() {
		return TheLifeConfiguration.getSystemSettings().getString(SYSKEY_GCM_REGISTRATION_ID, null);
	}
	
	
	/**
	 * Nullify the registration ID.
	 */
	public void clearRegistration() {
		setRegistrationId(null);
	}
	
	
	/**
	 * Save the GCM registration ID in the system settings.
	 * @param registrationId
	 */
	private void setRegistrationId(String registrationId) {
		SharedPreferences.Editor editor = TheLifeConfiguration.getSystemSettings().edit();
		editor.putString(SYSKEY_GCM_REGISTRATION_ID, registrationId);
		editor.putLong(SYSKEY_GCM_REGISTRATION_EXPIRY, System.currentTimeMillis() + EXPIRY_DELTA);
		editor.commit();
	}

	
	/**
	 * Get the GCM registration ID from the system settings.
	 * If necessary, get a new GCM registration ID from the provider and notify the listener.
	 * @param context
	 */
	public void accessRegistration(Context context, GCMSupport.Listener listener, String indicator) {
		m_context = context;
		m_listener = listener;
		m_indicator = indicator;
		
		String registrationId =  TheLifeConfiguration.getSystemSettings().getString(SYSKEY_GCM_REGISTRATION_ID, null);
		if (registrationId != null) {
			
			// check if the registration id has expired
			long registrationExpiry = TheLifeConfiguration.getSystemSettings().getLong(SYSKEY_GCM_REGISTRATION_EXPIRY, 0);
			if (System.currentTimeMillis() > registrationExpiry) {
				registrationId = null;
				clearRegistration();
			}
		}
		
		if (registrationId == null) {
			getNewRegistrationId(context, listener, indicator);
		} else {
			Log.i(TAG, "Local GCM registration ID: " + registrationId);			
			notifyGCMListener(null);
		}
	}
	
		
	/**
	 * Get a new registration ID from Google.
	 * @param context
	 * @param listener
	 * @param indicator
	 */
	private void getNewRegistrationId(final Context context, final Listener listener, final String indicator) {
		
		new AsyncTask<Void, Void, String>() 
		{
			@Override
			protected String doInBackground(Void... params) {
				String registrationId = null;
				int sleepTime = TheLifeConfiguration.EXPONENTIAL_BACKOFF_START; // 1 second
				boolean shouldRegister = true;
				while (shouldRegister) {
					try {
						Log.i(TAG, "Getting a GCM registration ID from Google");
						GoogleCloudMessaging messaging = GoogleCloudMessaging.getInstance(context);
						registrationId = messaging.register(TheLifeConfiguration.PROJECT_ID);
						Log.i(TAG, "Got a new GCM registration ID from Google: " + registrationId);
	
						// send it to the server
						if (registrationId != null) {
							Server server = new Server(context);
							UserModel owner = TheLifeConfiguration.getOwnerDS().getOwner();
							server.updateUserProfile(owner.id, owner.firstName, owner.lastName, owner.email, owner.mobile, registrationId, GCMSupport.this, "updateRegistration");
						}
						shouldRegister = false;
					} catch (IOException e) {
						Log.e(TAG, "getNewRegistrationId()", e);
						// exponential back off						
						try { Thread.sleep(sleepTime); } catch (Exception e2) { }
						sleepTime *= 2;
						shouldRegister = sleepTime < TheLifeConfiguration.EXPONENTIAL_BACKOFF_MAX;
					} catch (Exception e) {
						Log.e(TAG, "getNewRegistrationId()", e);
						shouldRegister = false;
					}
				}
				
				return registrationId;
			}
			
			@Override
			protected void onPostExecute(String registrationId) {
				if (registrationId == null) {
					// tell the listener about the error
					notifyGCMListener(m_context.getResources().getString(R.string.gcm_error));
				}
			}
		}.execute();
	}


	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		if (!Utilities.isSuccessfulHttpCode(httpCode)) {
			// server failed to store registration ID, so clear it locally
			clearRegistration();
		}
		notifyGCMListener(Utilities.isSuccessfulHttpCode(httpCode) ? null : m_context.getResources().getString(R.string.gcm_error));
	}
	
	
	private void notifyGCMListener(String errorString) {
		m_listener.notifyGCMRegistrationAvailable(m_indicator, errorString);
		
		// clean up object references
		m_listener = null;
		m_context = null;
		m_indicator = null;
	}

}
