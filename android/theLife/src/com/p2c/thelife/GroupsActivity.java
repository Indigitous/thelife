package com.p2c.thelife;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.GroupModel;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


/**
 * Show the owner's groups.
 * 
 * This class does not poll the server for changes in the user's groups, 
 * because this data doesn't change unless the user initiates the add group/delete group.
 * So instead of polling, just refresh when an add/delete operation occurs.
 * @author clarence
 *
 */
public class GroupsActivity extends SlidingMenuPollingFragmentActivity 
	implements OnItemLongClickListener, OnItemClickListener, Server.ServerListener, GroupCreateDialog.Listener, DSRefreshedListener {
	
	private static final String TAG = "GroupsActivity"; 	
	
	private GroupsAdapter m_adapter = null;
	private ProgressDialog m_progressDialog = null;	
	private GroupModel m_group = null;
	private View m_noGroupsView = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_groups, SlidingMenuSupport.GROUPS_POSITION);	
						
		// attach the list view and adapter
		ListView groupsList = (ListView)findViewById(R.id.groups_list);
		m_adapter = new GroupsAdapter(this, android.R.layout.simple_list_item_1);
		groupsList.setAdapter(m_adapter);
		
		// show a message if there are no events
		m_noGroupsView = (TextView)findViewById(R.id.groups_none);
		m_noGroupsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);
		
		groupsList.setOnItemClickListener(this);
		groupsList.setOnItemLongClickListener(this);		
	}
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		// load the database from the server in the background
		TheLifeConfiguration.getGroupsDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getGroupsDS().addDSRefreshedListener(this);		
		TheLifeConfiguration.getGroupsDS().refresh(null);				
	}		
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		TheLifeConfiguration.getGroupsDS().removeDSChangedListener(m_adapter);
		TheLifeConfiguration.getGroupsDS().removeDSRefreshedListener(this);				
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.groups, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.GroupsHelp");
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_search) {
			Intent intent = new Intent("com.p2c.thelife.GroupsSearch");
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_new) {
			GroupCreateDialog dialog = new GroupCreateDialog();
			dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		} else if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.EventsForCommunity");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);			
		}
		
		return true;
	}

	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		if (indicator.equals("groupCreate")) {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.creating_new_group), true, true);
		} else if (indicator.equals("groupDelete")) {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.deleting_group), true, true);
		}
	}

	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		
		if (Utilities.isSuccessfulHttpCode(httpCode)) {
			
			// successful server call (createGroup, deleteGroup)
			
			// server call "deleteGroup" does not return the deleted id, so it can't be handled
			if (indicator.equals("createGroup")) {
				int groupId = jsonObject.optInt("id", 0);
				if (groupId != 0) {				
					String name = jsonObject.optString("name", "");
					String description = jsonObject.optString("full_description", "");
					
					// add the group to the list of known groups
					ArrayList<Integer> memberIds = new ArrayList<Integer>();
					memberIds.add(TheLifeConfiguration.getOwnerDS().getUserId());
					GroupModel group = new GroupModel(groupId, name, description, TheLifeConfiguration.getOwnerDS().getUserId(), memberIds);
					TheLifeConfiguration.getGroupsDS().add(group);
					TheLifeConfiguration.getGroupsDS().notifyDSChangedListeners();
				}
			}
			
			// refresh the cache
			TheLifeConfiguration.getGroupsDS().forceRefresh(null);			
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}				
	}
	
	@Override
	public void notifyDSRefreshed(String indicator) {
		m_noGroupsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);								
	}		

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		// get the group associated with this view
		GroupModel group = (GroupModel)arg1.getTag();
		
		Intent intent = new Intent("com.p2c.thelife.GroupActivity");
		intent.putExtra("group_id", group.id);
		startActivity(intent);
				
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		// get the group associated with this view
		m_group = (GroupModel)arg1.getTag();
		
		GroupDeleteDialog dialog = new GroupDeleteDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());		
				
		return true; // consumed		
		
	}	
	
	public GroupModel getSelectedGroup() {
		return m_group;
	}

}
