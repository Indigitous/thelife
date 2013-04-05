package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.p2c.thelife.model.GroupModel;

public class GroupsSearchActivity extends SlidingMenuFragmentActivity implements Server.ServerListener, GroupJoinDialog.Listener {
	
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.groups_search, menu);
		return true;
	}

	/**
	 * User has selected a group to join. 
	 * @param view
	 */
	public void selectGroup(View view) {
		m_group = (GroupModel)view.getTag();
		
		// confirm the choice
		GroupJoinDialog dialog = new GroupJoinDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());		
	}
	
	
	public GroupModel getSelectedGroup() {
		return m_group;
	}

	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		m_progressDialog = ProgressDialog.show(this, "Waiting", "Requesting to join group.", true, true);	// TODO translation			
	}

	@Override
	public void notifyServerResponseAvailable(String indicator,
			JSONObject jsonObject) {

		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}				
		
	}
	

}
