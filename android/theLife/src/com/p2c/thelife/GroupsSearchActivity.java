package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.GroupModel;



/*
 * Support querying for groups.
 */
public class GroupsSearchActivity extends SlidingMenuPollingFragmentActivity implements Server.ServerListener, OnItemClickListener, GroupRequestJoinDialog.Listener {
	
	private static final String TAG = "GroupsSearchActivity";
	
	private GroupModel m_group = null;
	private ProgressDialog m_progressDialog = null;		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_groups_search, SlidingMenuSupport.GROUPS_POSITION);
		
		EditText editText = (EditText)findViewById(R.id.search_groups_text);
		
		// attach the event list view
		ListView listView = (ListView)findViewById(R.id.search_groups_list);
		GroupsSearchAdapter adapter = new GroupsSearchAdapter(this, android.R.layout.simple_list_item_1, editText);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(this);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.groups_search, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.Groups");
			startActivity(intent);			
		}
		
		return true;
	}			

	/**
	 * User has selected a group to join. 
	 * @param view
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {	
		m_group = (GroupModel)arg1.getTag();
		
		// confirm the choice
		GroupRequestJoinDialog dialog = new GroupRequestJoinDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());		
	}
	
	
	public GroupModel getSelectedGroup() {
		return m_group;
	}

	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.requesting_to_join_group), true, true);			
	}

	@Override
	public void notifyServerResponseAvailable(String indicator,	int httpCode, JSONObject jsonObject, String errorString) {

		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}				
		
	}
	

}
