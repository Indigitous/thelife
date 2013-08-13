package com.p2c.thelife.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.p2c.thelife.R;
import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.RequestModel;


/**
 * Listen for Google Cloud Messaging push notifications.
 */
public class GCMReceiver extends BroadcastReceiver {
	
	private static final String TAG = "GCMReceiver";

	@Override
	/**
	 * On UI thread.
	 */
	public void onReceive(Context context, Intent intent) {
		Resources res = context.getResources();
		GoogleCloudMessaging messaging = GoogleCloudMessaging.getInstance(context);
		String messageType = messaging.getMessageType(intent);
		Log.i(TAG, "Got a GCMReceiver Notification with type " + messageType);
		if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
			Bundle extras = intent.getExtras();
			if (extras.getString("app_type").equals("request")) {
				
				// create the model object and add it to the data store
				RequestModel request = RequestModel.fromBundle(context.getResources(), extras);
				Log.i(TAG, "Received " + request);
				TheLifeConfiguration.getRequestsDS().add(request);
				
				// create the Android notification
				NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
				builder.setSmallIcon(R.drawable.ic_launcher);
				if (request.isDelivered()) {
					builder.setContentTitle(Html.fromHtml(res.getString(request.isInvite() ? R.string.notification_invitation_delivered : 
																							 R.string.notification_request_delivered, 
																							 request.userName)));
				} else if (request.isAccepted()) {
					builder.setContentTitle(res.getString(request.isInvite() ? R.string.notification_invitation_accepted : 
																			   R.string.notification_request_accepted));
				} else if (request.isRejected()) {
					builder.setContentTitle(res.getString(request.isInvite() ? R.string.notification_invitation_rejected : 
						   													   R.string.notification_request_rejected));					
				}
				String description = res.getString(R.string.notification_group, request.groupName);
				builder.setContentText(Html.fromHtml(description));
				
				// destination activity when notification is selected
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent("com.p2c.thelife.Requests"), 0);
				builder.setContentIntent(pendingIntent);
				
				// add the notification to the manager
				NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(request.id, builder.build());
			}
		} else if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_DELETED)) {
			;
		} else if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR)) {
			;
		}
		
		// this object will now die
	}

}
