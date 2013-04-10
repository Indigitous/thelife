package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.UserModel;

public class GroupActivity extends SlidingMenuPollingFragmentActivity 
	implements Server.ServerListener, FriendDeleteDialog.Listener {
	
	private static final String TAG = "GroupActivity";
	
	private GroupModel m_group = null;	
	private GroupAdapter m_adapter = null;
	private UserModel m_user = null;
	private ProgressDialog m_progressDialog = null;	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_group, SlidingMenuSupport.GROUPS_POSITION);
		
		// Get the group for this activity
		int groupId = getIntent().getIntExtra("group_id", 0);
		m_group = TheLifeConfiguration.getGroupsDS().findById(groupId);
		
		// Show the group
		if (m_group != null) {		
			TextView textView = (TextView)findViewById(R.id.activity_group_name);
			textView.setText(m_group.name);
			textView = (TextView)findViewById(R.id.activity_group_description);
			textView.setText(m_group.description);
		}
		
		// attach the users-in-group view
		GridView usersView = (GridView)findViewById(R.id.activity_group_users);
		m_adapter = new GroupAdapter(this, android.R.layout.simple_list_item_1, m_group);
		usersView.setAdapter(m_adapter);
	}
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "In onResume()");
		
		// load the database from the server in the background
		TheLifeConfiguration.getUsersDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getUsersDS().refresh(null);				
	}		
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "In onPause()");
		
		TheLifeConfiguration.getUsersDS().removeDSChangedListener(m_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Toast.makeText(this, "Group Help", Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == R.id.action_new) {
			UserInviteDialog dialog = new UserInviteDialog();
			dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		}
		
		return true;
	}		
	
	
	public void selectUser(View view) {
		m_user = (UserModel)view.getTag();
		
		UserDeleteFromGroupDialog dialog = new UserDeleteFromGroupDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());			
	}
	
	
	public GroupModel getSelectedGroup() {
		return m_group;
	}	
	
	
	public UserModel getSelectedUser() {
		return m_user;
	}
	
	
	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		if (indicator.equals("deleteUser")) {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.deleting_user), true, true);
		} else if (indicator.equals("createRequest")) {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.inviting_person), true, true);
		}
	}

	
	@Override
	public void notifyServerResponseAvailable(String indicator, JSONObject jsonObject) {
		
		if (jsonObject != null) {
			int userId = jsonObject.optInt("id", 0);
			if (userId != 0) {
				
				// successful
				
				if (indicator.equals("deleteUser")) {
					// delete the user from the group data store
					m_group.removeUser(userId);
					TheLifeConfiguration.getGroupsDS().notifyDSChangedListeners();
					TheLifeConfiguration.getGroupsDS().forceRefresh(null);
				} 
			}
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}				
	}		
	

}
