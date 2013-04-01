package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;

public class DeedForFriendActivity extends SlidingMenuFragmentActivity implements Server.ServerListener, EventCreateDialog.Listener {
	
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
			ImageView image = (ImageView)findViewById(R.id.deed_for_friend_image);
			image.setImageBitmap(m_deed.image);
			
			// title			
			TextView title = (TextView)findViewById(R.id.deed_for_friend_title);
			title.setText(Utilities.fillTemplateString(getResources(), m_friend, m_deed.title));				
			
			// description
			TextView description = (TextView)findViewById(R.id.deed_for_friend_description);
			description.setText(Utilities.fillTemplateString(getResources(), m_friend, m_deed.description));				
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deed_for_friend, menu);
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
		m_progressDialog = ProgressDialog.show(this, "Waiting", "Create New Group", true, true);	// TODO translation						
	}

	@Override
	public void notifyResponseAvailable(String indicator, JSONObject jsonObject) {
		
		if (jsonObject != null) {
			int eventId = jsonObject.optInt("id", 0);
			if (eventId != 0) {
				
				// successful
				
				Toast.makeText(this, "THE event ID IS " + eventId, Toast.LENGTH_SHORT).show();
				
				int userId = jsonObject.optInt("user_id", 0);
				int friendId = jsonObject.optInt("friend_id", 0);
				int deedId = jsonObject.optInt("deed_id", 0);				
				String description = jsonObject.optString("description", "");
				long timestamp = jsonObject.optLong("timestamp", 0);
				boolean isPledge = jsonObject.optBoolean("is_pledge", false);
				int pledgeCount = jsonObject.optInt("pledge_count", 0);
				
				// add the event to the list of known events
				EventModel event = new EventModel(eventId, userId, friendId, deedId, description, timestamp, isPledge, pledgeCount);
				TheLifeConfiguration.getEventsDS().add(event);			
			}
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}		
	}		

}
