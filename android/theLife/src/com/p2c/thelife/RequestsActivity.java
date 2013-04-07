package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.RequestModel;

public class RequestsActivity extends SlidingMenuFragmentActivity implements Server.ServerListener, RequestDialog.Listener {
	
	private static final String TAG = "RequestsActivity";
	
	private ListView m_listView = null;
	private RequestsAdapter m_adapter = null;	
	private RequestModel m_request = null;
	private ProgressDialog m_progressDialog = null;	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_requests, SlidingMenuSupport.REQUESTS_POSITION);
		
		// TODO: put this code in a proper place
		RequestsPoller poller = new RequestsPoller(getApplication(), TheLifeConfiguration.getRequestsDS());
		poller.poll(2000);
		
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.activity_requests_list);
		m_adapter = new RequestsAdapter(this, android.R.layout.simple_list_item_1);
		m_listView.setAdapter(m_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.requests, menu);
		return true;
	}
	
	
	public void selectRequest(View view) {
		m_request = (RequestModel)view.getTag();
				
		RequestDialog dialog = new RequestDialog();
		dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());	
	}
	
	
	public RequestModel getSelectedRequest() {
		return m_request;
	}

	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		
		String dialogMessage = "";
		if (m_request.type.equals(RequestModel.INVITE)) {
			dialogMessage = getResources().getString(R.string.joining_group);
		} else if (m_request.type.equals(RequestModel.REQUEST_MEMBERSHIP)) {
			dialogMessage = getResources().getString(R.string.adding_new_member);
		}
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), dialogMessage, true, true);				
	}

	@Override
	public void notifyServerResponseAvailable(String indicator,	JSONObject jsonObject) {
		
		if (jsonObject != null) {
			int friendId = jsonObject.optInt("id", 0);
			if (friendId != 0) {
				
				// successful
				
				// refresh my groups
				TheLifeConfiguration.getGroupsDS().forceRefresh("postRequest");			
			
				// TO DO also need to immediately update my group info, while waiting for my_groups refresh?
			}
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}						
		
	}

}
