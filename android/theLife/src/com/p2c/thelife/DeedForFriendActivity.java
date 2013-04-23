package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;



public class DeedForFriendActivity extends SlidingMenuPollingFragmentActivity implements Server.ServerListener, EventCreateDialog.Listener {
	
	private static final String TAG = "DeedForFriendActivity"; 
	
	private FriendModel m_friend = null;
	private DeedModel m_deed = null;
	private ProgressDialog m_progressDialog = null;	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_deed_for_friend, SlidingMenuSupport.NO_POSITION);
		
		// Get the friend model
		int friendId = getIntent().getIntExtra("friend_id", 0);
		m_friend = TheLifeConfiguration.getFriendsDS().findById(friendId);
		
		// Show the friend model
		if (m_friend != null) {		
			TextView name = (TextView)findViewById(R.id.deed_for_friend_name);
			name.setText(m_friend.getFullName());
			
			TextView thresholdView = (TextView)findViewById(R.id.deed_for_friend_threshold);
			thresholdView.setText(m_friend.getThresholdMediumString(getResources()));
		}
		
		// Get the Deed model
		int deedId = getIntent().getIntExtra("deed_id", 0);
		m_deed = TheLifeConfiguration.getDeedsDS().findById(deedId);
		
		// Show the Deed model
		if (m_deed != null) {
			
			// icon
//			ImageView image = (ImageView)findViewById(R.id.deed_for_friend_image);
//			image.setImageBitmap(m_deed.image);  // TODO deed image
			
			// title			
			TextView title = (TextView)findViewById(R.id.deed_for_friend_title);
			title.setText(Utilities.fillTemplateString(getResources(), m_friend, m_deed.title));				
			
			// description
			WebView description = (WebView)findViewById(R.id.deed_for_friend_description);
			description.loadData(m_deed.description, "text/html", null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.deed_for_friend, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.DeedsForFriend");
			intent.putExtra("friend_id", m_friend.id);			
			startActivity(intent);
		}
		
		return true;
	}				
	
	public void doDeed(View view) {
		EventCreateDialog dialog = new EventCreateDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
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
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject) {
		
		if (jsonObject != null) {
			int eventId = jsonObject.optInt("id", 0);
			if (eventId != 0) {
				
				// successful "createEvent"
				
				Toast.makeText(this, "THE event ID IS " + eventId, Toast.LENGTH_SHORT).show();
				
				try {
					// add the new event to the data store
					EventModel event = EventModel.fromJSON(getResources(), jsonObject, false);
					TheLifeConfiguration.getEventsDS().add(event);
					TheLifeConfiguration.getEventsDS().notifyDSChangedListeners();
				} catch (Exception e) {
					Log.e(TAG, "notifyServerResponseAvailable()", e);
				}
				TheLifeConfiguration.getEventsDS().forceRefresh(null);
				
				// back to the friends screen
				Intent intent = new Intent("com.p2c.thelife.Friends");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);							
			}
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}		
	}		

}
