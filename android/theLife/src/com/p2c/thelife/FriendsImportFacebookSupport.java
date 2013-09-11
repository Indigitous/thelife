package com.p2c.thelife;

import java.util.ArrayList;

import org.json.JSONObject;

import com.p2c.thelife.FriendsImportSupport.AddFriendListener;
import com.p2c.thelife.model.FriendModel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


/**
 * Support for adding friends using Facebook account information.
 * Uses a support class to individually add each friend to the server.
 * @author clarence
 *
 */
public class FriendsImportFacebookSupport implements AddFriendListener {
	
	private static final String TAG = "FriendsImportFacebookSupport";

	private class FacebookUserInfo {
		public final Bitmap bitmap;
		public final String jsonString;
		
		public FacebookUserInfo(Bitmap bitmap, String jsonString) {
			this.bitmap = bitmap;
			this.jsonString = jsonString;
		}
	}	
	
	private Activity m_activity = null;
	private FriendsImportSupport m_friendsImportSupport = null;
	private ArrayList<FacebookUserInfo> m_facebookUserInfos	= null;
	private int m_facebookUserInfosIndex = 0;


	public FriendsImportFacebookSupport(Activity activity) {
		m_activity = activity;
	}
	
	
	/**
	 * Add the given Facebook accounts.
	 */
	public void addFriendsFromFacebook(final ArrayList<String> facebookIds)
	{
		
		new AsyncTask<Void, Void, ArrayList<FacebookUserInfo>>() {

			@Override
			protected ArrayList<FacebookUserInfo> doInBackground(Void... params) {
				
				ArrayList<FacebookUserInfo> userInfos = new ArrayList<FacebookUserInfo>(facebookIds.size());
				for (String facebookId : facebookIds) {
			
					// get the Facebook picture
					String pictureURL = Utilities.makeFacebookUserPictureUrl(facebookId);
					Bitmap bitmap = Utilities.getExternalBitmap(pictureURL).copy(Bitmap.Config.ARGB_8888, false);
					
					// get the Facebook user info
					String accountURL = Utilities.makeFacebookUserAccountUrl(facebookId);
					String jsonString = Utilities.getExternalString(accountURL);
					
					// add to the list
					userInfos.add(new FacebookUserInfo(bitmap, jsonString));
				}
				
				return userInfos;
			}
			
			/**
			 * on UI thread
			 */
			@Override
			protected void onPostExecute(ArrayList<FacebookUserInfo> facebookUsers) {
				
				if (facebookUsers.size() > 0) {
					m_friendsImportSupport = new FriendsImportSupport(m_activity, FriendsImportFacebookSupport.this);
					m_friendsImportSupport.addFriendsStart(facebookIds.size());
					
					m_facebookUserInfosIndex = 0;
					m_facebookUserInfos = facebookUsers;
					createAnotherFriendFromFacebook();
				}
			}
		}.execute();
		
	}
	

	/**
	 * When notified that the last friend has been added, add the next friend (which will cause another notify).
	 */
	public void notifyAddFriendFinished() {
		createAnotherFriendFromFacebook();
	}
	
	
	/**
	 * Add the next friend. The support class will notify when done.
	 */
	private void createAnotherFriendFromFacebook() {
		if (m_facebookUserInfosIndex < m_facebookUserInfos.size()) {
			try {
				FacebookUserInfo userInfo = m_facebookUserInfos.get(m_facebookUserInfosIndex++);
				JSONObject jsonObject = new JSONObject(userInfo.jsonString);
				String firstName = jsonObject.getString("first_name");
				String lastName = jsonObject.getString("last_name");
				Log.i(TAG, "WILL ADD FRIEND FROM FACEBOOK WITH NAME " + firstName + "," + lastName);			
				m_friendsImportSupport.addFriend(firstName, lastName, null, null, FriendModel.Threshold.NewContact, userInfo.bitmap);
			} catch (Exception e) {
				Log.i(TAG, "createAnotherFriendFromFacebook()", e);
				Utilities.showErrorToast(m_activity, m_activity.getResources().getString(R.string.facebook_account_error), Toast.LENGTH_SHORT);
			}			
		} else {
			// finish and cleanup
			m_friendsImportSupport.addFriendsFinish();
			m_friendsImportSupport.clearListener();
			m_activity = null;
			m_friendsImportSupport = null;
			m_facebookUserInfos = null;
		}
	}	

}
