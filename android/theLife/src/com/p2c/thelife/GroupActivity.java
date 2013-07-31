package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.GroupUsersDS;
import com.p2c.thelife.model.UserModel;


/**
 * Show the users in the specified group.
 * @author clarence
 *
 */
public class GroupActivity extends SlidingMenuPollingFragmentActivity implements Server.ServerListener, GroupDeleteUserDialog.Listener, OnItemLongClickListener {
	
	private static final String TAG = "GroupActivity";
	
	private GroupModel m_group = null;	
	private GroupAdapter m_adapter = null;
	private UserModel m_user = null;
	private ProgressDialog m_progressDialog = null;	
	private GroupUsersDS m_groupUsersDS = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_group, SlidingMenuSupport.GROUPS_POSITION);
		
		// Get the group
		int groupId = getIntent().getIntExtra("group_id", 0);
		m_group = TheLifeConfiguration.getGroupsDS().findById(groupId);
		
		// Show the group
		if (m_group != null) {		
			TextView textView = (TextView)findViewById(R.id.activity_group_name);
			textView.setText(m_group.name);
			textView = (TextView)findViewById(R.id.activity_group_description);
			textView.setText(m_group.description);
			
			// data store of users in this group
			m_groupUsersDS = new GroupUsersDS(this, null, m_group.id);			
			
			// attach the users-in-group view
			GridView usersView = (GridView)findViewById(R.id.activity_group_users);
			m_adapter = new GroupAdapter(this, android.R.layout.simple_list_item_1, m_group, m_groupUsersDS);
			usersView.setAdapter(m_adapter);
			
			usersView.setOnItemLongClickListener(this);
		}
	}
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		if (m_group != null) {
			// load the database from the server in the background
			m_groupUsersDS.addDSChangedListener(m_adapter);
			m_groupUsersDS.forceRefresh(null);
			
			// listen for user bitmaps
			TheLifeConfiguration.getBitmapNotifier().addUserBitmapListener(m_adapter);
		}
	}		
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		if (m_group != null) {
			m_groupUsersDS.removeDSChangedListener(m_adapter);
			
			// stop listening for user bitmaps
			TheLifeConfiguration.getBitmapNotifier().removeUserBitmapListener(m_adapter);			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.group, menu);
		
		// if the user is not the group leader then don't let the user add to the group
		if (m_group != null && m_group.leader_id != TheLifeConfiguration.getOwnerDS().getId()) {
			MenuItem menuItem = menu.findItem(R.id.action_new);
			menuItem.setVisible(false);
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_group_help);
			intent.putExtra("position", SlidingMenuSupport.GROUPS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.Group");
			if (m_group != null) {
				intent.putExtra("group_id", m_group.id);
			}
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_new) {
			if (m_group != null) {
				Intent intent = new Intent("com.p2c.thelife.GroupInvite");
				intent.putExtra("group_id", m_group.id);
				startActivity(intent);
			}
		}  else if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.Groups");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			startActivity(intent);			
		}
		
		return true;
	}		
	
	
	/**
	 * User has been selected for deletion.
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		m_user = (UserModel)arg1.getTag();
		GroupDeleteUserDialog dialog = new GroupDeleteUserDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		
		return true;
	}


	public void selectUser(View view) {
		m_user = (UserModel)view.getTag();
		
		GroupDeleteUserDialog dialog = new GroupDeleteUserDialog();
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
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		
		// deleteUser does not return the deleted id
		
		m_groupUsersDS.forceRefresh(null);		
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}				
	}

}
