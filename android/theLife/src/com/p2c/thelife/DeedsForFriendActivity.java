package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;


/**
 * Show the deeds/activities applicable to the given friend's threshold.
 * @author clarence
 *
 */
public class DeedsForFriendActivity extends SlidingMenuFragmentActivity implements DSRefreshedListener, Server.ServerListener, EventCreateDialog.Listener {
	
	private static final String TAG = "DeedsForFriendActivity";
	
	private FriendModel m_friend = null;
	private DeedsForFriendAdapter m_adapter = null;
	private DeedModel m_deed = null;
	private ProgressDialog m_progressDialog = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_deeds_for_friend, SlidingMenuSupport.NO_POSITION);
					
		// Get the friend for this deed
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = TheLifeConfiguration.getFriendsDS().findById(friendId);
		
		// Show the friend
		if (m_friend != null) {		
			TextView name = (TextView)findViewById(R.id.deeds_for_friend_name);
			name.setText(m_friend.getFullName());
			
			TextView thresholdView = (TextView)findViewById(R.id.deeds_for_friend_threshold);
			thresholdView.setText(m_friend.getThresholdMediumString(getResources()));
			
			ExpandableListView activitiesView = (ExpandableListView)findViewById(R.id.deeds_for_friend_list);
			m_adapter = new DeedsForFriendAdapter(this, m_friend);
			activitiesView.setAdapter(m_adapter);
		}		
	}
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		// load the data store from the server in the background
		// note that categories and deeds are closely related, so first refresh the categories and then the deeds
		TheLifeConfiguration.getCategoriesDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getCategoriesDS().addDSRefreshedListener(this);
		TheLifeConfiguration.getDeedsDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getCategoriesDS().refresh(null); // first refresh categories, then refresh deeds in the notifyDSRefreshed callback		
	}	
	
	
	/**
	 * Called when the data store refresh has completed.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		TheLifeConfiguration.getDeedsDS().refresh(null);
	}			
	
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		TheLifeConfiguration.getDeedsDS().removeDSChangedListener(m_adapter);				
		TheLifeConfiguration.getCategoriesDS().removeDSRefreshedListener(this);
		TheLifeConfiguration.getCategoriesDS().removeDSChangedListener(m_adapter);
	}		
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.deeds_for_friend, menu);
		return true;
	}
	
	/**
	 * Deed/activity has been selected.
	 * @param view
	 * @return
	 */
	public boolean selectDeed(View view) {
	
		// get the deed associated with this view
		DeedModel deed = (DeedModel)view.getTag();
		
		Intent intent = new Intent("com.p2c.thelife.DeedForFriend");
		intent.putExtra("deed_id", deed.id);
		intent.putExtra("friend_id", m_friend.id);
		startActivity(intent);
		
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.EventsForFriend");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			intent.putExtra("friend_id", m_friend.id);
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_deeds_for_friend_help);
			intent.putExtra("position", SlidingMenuSupport.FRIENDS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.DeedsForFriend");
			intent.putExtra("friend_id", m_friend.id);			
			startActivity(intent);
		}
		
		return true;
	}
	
	
	/**
	 * Owner wants to change their friend's threshold.
	 * @param view
	 * @return
	 */
	public boolean changeThreshold(View view) {
		
		m_deed = TheLifeConfiguration.getDeedsDS().findById(m_adapter.getChangeThresholdId());
		ChangeThresholdDialog dialog = new ChangeThresholdDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		
		return true;
	}	
	
	
	public FriendModel getSelectedFriend() {
		return m_friend;
	}
	
	public DeedModel getSelectedDeed() {
		return m_deed;
	}

	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.creating_new_event), true, true);					
	}

	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		
		if (Utilities.isSuccessfulHttpCode(httpCode) && jsonObject != null) {
			int eventId = jsonObject.optInt("id", 0);
			if (eventId != 0) {
				
				// successful "createEvent"
								
				try {
					// add the new event to the data store
					EventModel event = EventModel.fromJSON(getResources(), jsonObject, false);
					TheLifeConfiguration.getEventsDS().add(event);
					TheLifeConfiguration.getEventsDS().notifyDSChangedListeners();
					
					// set the friend's threshold if necessary
					if (event.threshold != null) {
						m_friend.threshold = event.threshold;
					}
				} catch (Exception e) {
					Log.e(TAG, "notifyServerResponseAvailable()", e);
				}
				TheLifeConfiguration.getEventsDS().forceRefresh(null);
								
				// back to the friends screen
				Intent intent = new Intent("com.p2c.thelife.EventsForFriend");
				intent.putExtra("friend_id", m_friend.id);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);							
			}
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}		
	}			

}
