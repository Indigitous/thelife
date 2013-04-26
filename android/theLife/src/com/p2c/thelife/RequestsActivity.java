package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.RequestModel;


/**
 * Requests are automatically polled by the RequestsPoller class.
 */
public class RequestsActivity extends SlidingMenuPollingFragmentActivity implements Server.ServerListener, RequestDialog.Listener {
	
	private static final String TAG = "RequestsActivity";
	
	private ListView m_listView = null;
	private RequestsAdapter m_adapter = null;	
	private RequestModel m_request = null;
	private ProgressDialog m_progressDialog = null;	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_requests, SlidingMenuSupport.REQUESTS_POSITION);
				
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.activity_requests_list);
		m_adapter = new RequestsAdapter(this, android.R.layout.simple_list_item_1);
		m_listView.setAdapter(m_adapter);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.requests, menu);
		return true;
	}
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		// set the data store listener
		TheLifeConfiguration.getRequestsDS().addDSChangedListener(m_adapter);
	}		
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		// remove the data store listener
		TheLifeConfiguration.getRequestsDS().removeDSChangedListener(m_adapter);
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
		if (indicator.equals("reject")) {
			dialogMessage = getResources().getString(R.string.rejecting_request);
		} else if (m_request.isInvite()) {
			dialogMessage = getResources().getString(R.string.joining_group);
		} else if (m_request.isMembershipRequest()) {
			dialogMessage = getResources().getString(R.string.adding_new_member);
		}
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), dialogMessage, true, true);				
	}

	@Override
	public void notifyServerResponseAvailable(String indicator,	int httpCode, JSONObject jsonObject, String errorString) {
		
		if (Utilities.isSuccessfulHttpCode(httpCode) && jsonObject != null) {
			int friendId = jsonObject.optInt("id", 0);
			if (friendId != 0) {
				
				// successful
				
				// delete the request
				TheLifeConfiguration.getRequestsDS().delete(m_request.id);
				TheLifeConfiguration.getRequestsDS().notifyDSChangedListeners();
				
				// if the request was accepted, refresh my groups
				if (indicator.equals("accept")) {							
					TheLifeConfiguration.getGroupsDS().forceRefresh("postRequest");
				}
			}
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}						
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.EventsForCommunity");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);			
		}
		
		return true;
	}			

}
