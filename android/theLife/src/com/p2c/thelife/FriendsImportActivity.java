package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.p2c.thelife.model.FriendModel;


/**
 * Import friends.
 * @author clarence
 *
 */
public class FriendsImportActivity extends SlidingMenuPollingFragmentActivity implements Server.ServerListener, FriendImportManuallyDialog.Listener {
	
	private static String TAG = "FriendsImportActivity";
	
	private ProgressDialog m_progressDialog = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_friends_import, SlidingMenuSupport.FRIENDS_POSITION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.import_friends, menu);
		return true;
	}
	
	public boolean importFriendsByPhone(View view) {
		return true;
	}
	
	public boolean importFriendsByFacebook(View view) {
		return true;
	}	
	
	public boolean importFriendManually(View view) {
		
		FriendImportManuallyDialog dialog = new FriendImportManuallyDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		
		return true;
	}

	@Override
	public void notifyServerResponseAvailable(String indicator, JSONObject jsonObject) {
		
		if (jsonObject != null) {
			int friendId = jsonObject.optInt("id", 0);
			if (friendId != 0) {
				
				// successful
								
				String firstName = jsonObject.optString("first_name", "");
				String lastName = jsonObject.optString("last_name", "");
				int thresholdIndex = FriendModel.thresholdId2Index(jsonObject.optInt("threshold_id"));
				
				// add the friend to the list of known friends
				FriendModel.Threshold threshold = FriendModel.thresholdValues[thresholdIndex]; 
				FriendModel friend = new FriendModel(friendId, firstName, lastName, null, threshold);
				TheLifeConfiguration.getFriendsDS().add(friend);
				TheLifeConfiguration.getFriendsDS().notifyDSChangedListeners();
				TheLifeConfiguration.getFriendsDS().forceRefresh(null);				
			}
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}		
	}

	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.adding_friend), true, true);		
	}

}
