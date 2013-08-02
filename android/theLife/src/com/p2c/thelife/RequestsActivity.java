package com.p2c.thelife;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.RequestModel;
import com.p2c.thelife.model.RequestsDS;


/**
 * Show the requests/notifications for the owner.
 * Requests are automatically polled by the separate RequestsPoller class.
 * A local time refreshes the displayed timestamps every minute.
 */
public class RequestsActivity extends SlidingMenuPollingFragmentActivity implements Server.ServerListener, RequestsDS.DSChangedListener, ServerAccessDialogAbstract.Listener {
	
	private static final String TAG = "RequestsActivity";
	
	private ListView m_listView = null;
	private RequestsAdapter m_adapter = null;
	private TextView m_noRequestsView = null;	
	private RequestModel m_request = null;
	private ProgressDialog m_progressDialog = null;	
	
	// refresh the requests list view
	private Runnable m_displayRefreshRunnable = null;		
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_requests, SlidingMenuSupport.REQUESTS_POSITION);
				
		// attach the event list view
		m_listView = (ListView)findViewById(R.id.activity_requests_list);
		m_adapter = new RequestsAdapter(this, android.R.layout.simple_list_item_1);
		m_listView.setAdapter(m_adapter);
		
		// show a message if there are no requests
		m_noRequestsView = (TextView)findViewById(R.id.requests_none);
		m_noRequestsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);
		
		// this will refresh the timestamps inside the list view's requests
		// this is all local -- server is not involved
		m_displayRefreshRunnable = new Runnable() {
			@Override
			public void run() {
				if (m_adapter != null && m_listView != null) {
					m_adapter.notifyDataSetChanged();
					
					// refresh the display again in one minute
					m_listView.postDelayed(m_displayRefreshRunnable, 60 * 1000);
				}
			}
		};
		
		// clear the new notifications flag
		TheLifeConfiguration.getRequestsDS().setHasNewNotifications(false);
		showNotificationNumber();
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
		TheLifeConfiguration.getRequestsDS().addDSChangedListener(this);		
		
		// set the bitmap listener
		TheLifeConfiguration.getBitmapNotifier().addUserBitmapListener(m_adapter);
		
		// refresh the display every minute
		m_listView.postDelayed(m_displayRefreshRunnable, 60 * 1000);		
	}		
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		// remove the data store listener
		TheLifeConfiguration.getRequestsDS().removeDSChangedListener(m_adapter);
		TheLifeConfiguration.getRequestsDS().removeDSChangedListener(this);		
		
		// remove the bitmap listener
		TheLifeConfiguration.getBitmapNotifier().removeUserBitmapListener(m_adapter);
		
		// stop the display refreshes
		m_listView.removeCallbacks(m_displayRefreshRunnable);
	}		
	
	
	public void selectRequest(View view) {
		m_request = (RequestModel)view.getTag();

		ServerAccessDialogAbstract dialog = null;
		if (m_request.isDelivered()) {
			dialog = new RequestAcceptOrRejectDialog();
		} else if (m_request.isAccepted() || m_request.isRejected()) {
			dialog = new RequestFinishDialog();
		}
			
		if (dialog != null) {
			dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		} else {
			Log.e(TAG, "Can't show dialog for request " + m_request);
		}	
	}
	
	
	public RequestModel getSelectedRequest() {
		return m_request;
	}
	
	
	@Override
	public void notifyDSChanged(ArrayList<Integer> oldModelIds, ArrayList<Integer> newModelIds) {
		m_noRequestsView.setVisibility(m_adapter.getCount() == 0 ? View.VISIBLE : View.GONE);								
	}	
	

	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		
		String dialogMessage = "";
		if (indicator.equals("reject")) {
			dialogMessage = getResources().getString(R.string.rejecting_request);
		} else if (indicator.equals("accept") && m_request.isInvite()) {
			dialogMessage = getResources().getString(R.string.joining_group);
		} else if (indicator.equals("accept") && m_request.isMembershipRequest()) {
			dialogMessage = getResources().getString(R.string.adding_new_member);
		} else if (indicator.equals("finish")) {
			dialogMessage = getResources().getString(R.string.deleting_notification);
		}
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), dialogMessage, true, true);				
	}

	@Override
	public void notifyServerResponseAvailable(String indicator,	int httpCode, JSONObject jsonObject, String errorString) {
		
		if (Utilities.isSuccessfulHttpCode(httpCode)) {
				
			// successful
			
			// delete the request
			TheLifeConfiguration.getRequestsDS().delete(m_request.id);
			TheLifeConfiguration.getRequestsDS().notifyDSChangedListeners();
			
			// if the request was accepted, refresh my groups
			if (indicator.equals("accept")) {							
				TheLifeConfiguration.getGroupsDS().forceRefresh("postRequest");
			}
		}
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}						
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			m_support.slideOpen();		
		}
		
		return true;
	}			

}
