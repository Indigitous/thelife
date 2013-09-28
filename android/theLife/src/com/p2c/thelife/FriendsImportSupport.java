package com.p2c.thelife;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;

/**
 * Support class to add friends to the server.
 * @author clarence
 *
 */
public class FriendsImportSupport implements Server.ServerListener {
	
	private static final String TAG = "FriendsImportSupport";

	private Activity m_activity = null;
	private AddFriendListener m_listener = null;
	private ProgressDialog m_progressDialog = null;
	private Bitmap m_bitmap = null;
	private int m_friendId = 0;
	
	public interface AddFriendListener {
		public void notifyAddFriendFinished();
	}	

	
	public FriendsImportSupport(Activity activity, AddFriendListener listener) {
		m_activity = activity;
		m_listener = listener;
	}
	
	
	public void clearListener() {
		m_listener = null;
	}
	
	/**
	 * Start adding friends. Called before addFriend().
	 * @param numberToAdd
	 */
	protected void addFriendsStart(int messageResourceId) {
		m_progressDialog = ProgressDialog.show(
				m_activity, 
				m_activity.getResources().getString(R.string.waiting), 
				m_activity.getResources().getString(messageResourceId), 
				true, 
				true);
	}
	
	
	/**
	 * Add one friend.
	 */
	protected void addFriend(String firstName, String lastName, String email, String phone, FriendModel.Threshold threshold, Bitmap bitmap) {

		m_bitmap = bitmap;
		Server server = new Server(m_activity);
		server.createFriend(firstName, lastName, email, phone, threshold, this, "createFriend");						
	}
	
	
	/**
	 * Stop adding friends. Called after one or more calls of addFriend().
	 */
	protected void addFriendsFinish() {
		TheLifeConfiguration.getEventsDS().forceRefresh(null);
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
		}
		
		// back to friends activity
		Intent intent = new Intent("com.p2c.thelife.Friends");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		m_activity.startActivity(intent);		
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
						Server server = new Server(m_activity);
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
				EventModel event = EventModel.fromJSON(m_activity.getResources(), jsonObject, false);
				TheLifeConfiguration.getEventsDS().add(event);
				TheLifeConfiguration.getEventsDS().notifyDSChangedListeners();
			} catch (Exception e) {
				Log.e(TAG, "notifyServerResponseAvailable()", e);
			}

			finishImport();
			return;
		}
		
		// show for any error
		Utilities.showErrorToast(m_activity, m_activity.getResources().getString(R.string.friend_import_error), Toast.LENGTH_LONG);
		finishImport();
	}
	
	
	private void createAddFriendEvent() {
		DeedModel addFriendDeed = TheLifeConfiguration.getDeedsDS().findSpecial(DeedModel.SPECIAL_ADD_FRIEND);
		if (addFriendDeed != null) {
			Server server = new Server(m_activity);
			server.createEvent(addFriendDeed.id, m_friendId, false, null, this, "createEvent");
		}
	}
	
	
	private void finishImport() {
		if (m_listener != null) {
			m_listener.notifyAddFriendFinished();
		}
	}

}
