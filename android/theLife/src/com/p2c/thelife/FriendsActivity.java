package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.p2c.thelife.model.FriendModel;

/**
 * Show the friends of the current user.
 * @author clarence
 *
 */
public class FriendsActivity 
	extends SlidingMenuPollingFragmentActivity 
	implements OnItemLongClickListener, OnItemClickListener, Server.ServerListener, FriendDeleteDialog.Listener {
	
	private static final String TAG = "FriendsActivity"; 	
	
	private FriendsAdapter m_adapter = null;	
	private FriendModel m_friend = null; // selected friend
	private ProgressDialog m_progressDialog = null;	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_friends, SlidingMenuSupport.FRIENDS_POSITION);
		
		GridView friendsGrid = (GridView)findViewById(R.id.grid_friends);
		FriendsAdapter m_adapter = new FriendsAdapter(this, android.R.layout.simple_list_item_1);
		friendsGrid.setAdapter(m_adapter);
		
		friendsGrid.setOnItemClickListener(this);
		friendsGrid.setOnItemLongClickListener(this);
	}
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "In onResume()");
		
		// load the database from the server in the background
		TheLifeConfiguration.getFriendsDS().addDSChangedListener(m_adapter);  
		TheLifeConfiguration.getFriendsDS().refresh(null);			
	}		
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "In onPause()");
		
		TheLifeConfiguration.getEventsDS().removeDSChangedListener(m_adapter);
	}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Toast.makeText(this, "Friends Help", Toast.LENGTH_SHORT).show();	
		} else if (item.getItemId() == R.id.action_new) {
			Intent intent = new Intent("com.p2c.thelife.FriendsImport");
			startActivity(intent);
		}
		
		return true;
	}		
	

	/**
	 * View a friend.
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		// get the friend associated with this view
		m_friend = (FriendModel)arg1.getTag();
		
		Intent intent = new Intent("com.p2c.thelife.FriendActivity");
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
	public void notifyServerResponseAvailable(String indicator, JSONObject jsonObject) {
		
		if (jsonObject != null) {
			int friendId = jsonObject.optInt("id", 0);
			if (friendId != 0) {
				
				// successful
											
				// delete the friend from the list
				TheLifeConfiguration.getFriendsDS().delete(friendId);
				TheLifeConfiguration.getFriendsDS().notifyDSChangedListeners();
				TheLifeConfiguration.getFriendsDS().forceRefresh(null);
			}
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}				
	}		

}
