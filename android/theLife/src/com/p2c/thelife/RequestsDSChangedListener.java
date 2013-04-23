package com.p2c.thelife;

import java.util.ArrayList;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.p2c.thelife.model.RequestModel;
import com.p2c.thelife.model.RequestsDS;


/**
 * In order to create Android notifications, listen for changes in the requests data store. 
 * @author clarence
 *
 */
public class RequestsDSChangedListener implements RequestsDS.DSChangedListener {
	
	private static final String TAG = "RequestsDSChangedListener";
	
	private Application m_app = null;
	
	public RequestsDSChangedListener(Application app) {
		m_app = app;
	}

	@Override
	public void notifyDSChanged(ArrayList<Integer> oldModelIds, ArrayList<Integer> newModelIds) {
		
		// create notifications for each of the new requests
		for (Integer id:newModelIds) {

			// if the old list of ids does not contain the new id, it is a new request
			if (!oldModelIds.contains(id)) {
								
				RequestModel newRequest = TheLifeConfiguration.getRequestsDS().findById(id);
				
				NotificationCompat.Builder builder = new NotificationCompat.Builder(m_app.getApplicationContext());
				builder.setSmallIcon(R.drawable.ic_launcher);
				builder.setContentTitle("theLife");
				String description = newRequest.finalDescription;
				builder.setContentText(Html.fromHtml(description));
				
				PendingIntent intent = PendingIntent.getActivity(m_app.getApplicationContext(), 0, new Intent("com.p2c.thelife.Requests"), 0);
				builder.setContentIntent(intent);
				
				NotificationManager notificationManager = (NotificationManager)m_app.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(id, builder.build());
			}
		}		
		
	}

}
