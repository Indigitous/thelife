package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.GroupModel;

public class GroupInviteActivity extends SlidingMenuPollingFragmentActivity implements GroupInviteManuallyDialog.Listener, Server.ServerListener {

	public static final String TAG = "GroupInviteActivity";
	
	private GroupModel m_group = null;
	private ProgressDialog m_progressDialog = null;		
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_group_invite, SlidingMenuSupport.GROUPS_POSITION);
		
		// Get the group
		int groupId = getIntent().getIntExtra("group_id", 0);
		m_group = TheLifeConfiguration.getGroupsDS().findById(groupId);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.group_invite, menu);		
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_group_invite_help);
			intent.putExtra("position", SlidingMenuSupport.GROUPS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.GroupInvite");
			if (m_group != null) {
				intent.putExtra("group_id", m_group.id);
			}
			startActivity(intent);
		}  else if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.Group");
			if (m_group != null) {
				intent.putExtra("group_id", m_group.id);
			}			
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			startActivity(intent);			
		}
		
		return true;
	}
	
	
	public void inviteManually(View view) {
		if (m_group != null) {
			GroupInviteManuallyDialog dialog = new GroupInviteManuallyDialog();
			dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		}
	}
	
	
	public GroupModel getSelectedGroup() {
		return m_group;
	}		
	
	
	public void inviteByFacebook(View view) {
		// not yet implemented
	}
	
	
	public void inviteByInternalContact(View view) {
		
	}
	
	
	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		if (indicator.equals("createRequest")) {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.inviting_person), true, true);
		}
	}

	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}				
	}
	

}
