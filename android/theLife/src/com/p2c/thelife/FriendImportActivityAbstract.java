package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;

/**
 * Superclass to add a friend.
 * @author clarence
 *
 */
public class FriendImportActivityAbstract extends SlidingMenuPollingFragmentActivity implements Server.ServerListener {
	
	private static final String TAG = "FriendImportActivityAbstract";

	protected ProgressDialog m_progressDialog = null;
	protected Bitmap m_bitmap = null;
	protected int m_friendId = 0;

	
	/**
	 * Add a friend.
	 */
	protected void addFriend(String firstName, String lastName, String email, String phone, FriendModel.Threshold threshold) {
		
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.adding_friend), true, true);		

		Server server = new Server(this);
		server.createFriend(firstName, lastName, email, phone, threshold, this, "createFriend");						
	}
	
	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {

		if (indicator.equals("createFriend")) {
			if (Utilities.isSuccessfulHttpCode(httpCode) && jsonObject != null) {
				m_friendId = jsonObject.optInt("id", 0);
				if (m_friendId != 0) {
					
					// successful
									
					String firstName = jsonObject.optString("first_name", "");
					String lastName = jsonObject.optString("last_name", "");
					String email = jsonObject.optString("email", "");
					String phone = jsonObject.optString("phone", "");
					
					int thresholdIndex = FriendModel.thresholdId2Index(jsonObject.optInt("threshold_id"));
					
					// add the friend to the list of known friends
					FriendModel.Threshold threshold = FriendModel.thresholdValues[thresholdIndex]; 
					FriendModel friend = new FriendModel(m_friendId, firstName, lastName, threshold, email, phone);
					TheLifeConfiguration.getFriendsDS().add(friend);
					TheLifeConfiguration.getFriendsDS().notifyDSChangedListeners();
					TheLifeConfiguration.getFriendsDS().forceRefresh(null);
					
					// check to see if there is an image needing to be sent to the server
					if (m_bitmap != null) {
						BitmapCacheHandler.saveBitmapToCache("friends", friend.id, "image", m_bitmap);						
						Server server = new Server(this);
						server.updateImage("friends", friend.id, this, "updateImage");
					} else {
						createAddFriendEvent();
					}
					return;
				}
			}
				
		} else if (indicator.equals("updateImage")) {
			createAddFriendEvent();
			return;
			
		} else if (indicator.equals("createEvent")) {
			
			// successful "createEvent"
			try {
				// add the new event to the data store
				EventModel event = EventModel.fromJSON(getResources(), jsonObject, false);
				TheLifeConfiguration.getEventsDS().add(event);
				TheLifeConfiguration.getEventsDS().notifyDSChangedListeners();
			} catch (Exception e) {
				Log.e(TAG, "notifyServerResponseAvailable()", e);
			}
			TheLifeConfiguration.getEventsDS().forceRefresh(null);
			
			// finish adding the friend
			finishImport();
			return;
		}
		
		Utilities.showErrorToast(this, getResources().getString(R.string.friend_import_error), Toast.LENGTH_LONG);
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
		}		

	}
	
	
	private void createAddFriendEvent() {
		DeedModel addFriendDeed = TheLifeConfiguration.getDeedsDS().findSpecial(DeedModel.SPECIAL_ADD_FRIEND);
		if (addFriendDeed != null) {
			Server server = new Server(this);
			server.createEvent(addFriendDeed.id, m_friendId, false, null, this, "createEvent");
		} else {
			finishImport();
		}
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

}
