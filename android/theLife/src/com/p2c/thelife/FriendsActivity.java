package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.FriendModel;


/**
 * Show the friends of the current user.
 * @author clarence
 *
 */
public class FriendsActivity 
	extends SlidingMenuPollingFragmentActivity 
	implements OnItemLongClickListener, OnItemClickListener, Server.ServerListener, FriendDeleteDialog.Listener, DSRefreshedListener {
	
	private static final String TAG = "FriendsActivity"; 	
	
	private FriendsAdapter m_adapter = null;	
	private FriendModel m_friend = null; // selected friend
	private ProgressDialog m_progressDialog = null;
	private View m_noFriendsView = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_friends, SlidingMenuSupport.FRIENDS_POSITION);
				
		GridView friendsGrid = (GridView)findViewById(R.id.grid_friends);
		m_adapter = new FriendsAdapter(this, android.R.layout.simple_list_item_1);
		friendsGrid.setAdapter(m_adapter);
		
		// show a message if there are no friends
		m_noFriendsView = (TextView)findViewById(R.id.friends_none);
		m_noFriendsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);		
		
		friendsGrid.setOnItemClickListener(this);
		friendsGrid.setOnItemLongClickListener(this);
	}
	
	
	/**
	 * Activity in view, so start the listeners.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		// load the database from the server in the background
		TheLifeConfiguration.getFriendsDS().addDSChangedListener(m_adapter);  
		TheLifeConfiguration.getFriendsDS().addDSRefreshedListener(this);				
		TheLifeConfiguration.getFriendsDS().refresh(null);
		TheLifeConfiguration.getBitmapNotifier().addFriendBitmapListener(m_adapter);
	}
	
	
	/**
	 * Activity out of view, so stop the listeners.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		TheLifeConfiguration.getFriendsDS().removeDSChangedListener(m_adapter);
		TheLifeConfiguration.getGroupsDS().removeDSRefreshedListener(this);
		TheLifeConfiguration.getBitmapNotifier().removeFriendBitmapListener(m_adapter);		
	}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.friends, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.FriendsHelp");
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_new) {
			Intent intent = new Intent("com.p2c.thelife.FriendsImport");
			startActivity(intent);
		} else if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.EventsForCommunity");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);			
		}
		
		return true;
	}	

	
	@Override
	public void notifyDSRefreshed(String indicator) {
		m_noFriendsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);								
	}		
	

	/**
	 * View a friend.
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		// get the friend associated with this view
		m_friend = (FriendModel)arg1.getTag();
		
		Intent intent = new Intent("com.p2c.thelife.EventsForFriend");
		intent.putExtra("friend_id", m_friend.id);
		startActivity(intent);
	}
	
	
	/**
	 * Delete a friend.
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		// get the friend associated with this view
		m_friend = (FriendModel)arg1.getTag();
		
		FriendDeleteDialog dialog = new FriendDeleteDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());		
				
		return true; // consumed
	}	
	
	
	public FriendModel getSelectedFriend() {
		return m_friend;
	}
	
	
	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.deleting_friend), true, true);				
	}

	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		
		if (Utilities.isSuccessfulHttpCode(httpCode)) {
			
			// successful server call (deleteFriend)
			
			// refresh the cache
			TheLifeConfiguration.getFriendsDS().forceRefresh(null);			
		}		
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}				
	}

}
