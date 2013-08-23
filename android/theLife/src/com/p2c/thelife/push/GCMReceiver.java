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
import com.p2c.thelife.Utilities;
import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.RequestModel;
import com.p2c.thelife.model.UserModel;
import com.testflightapp.lib.TestFlight;


/**
 * Listen for Google Cloud Messaging push notifications.
 */
public class GCMReceiver extends BroadcastReceiver {
	
	private static final String TAG = "GCMReceiver";
	
	private static final int REQUESTS_NOTIFICATION_ID = 1;
	private static final int EVENTS_NOTIFICATION_ID = 2;
	private static final int AFTER_DELETE_NOTIFICATION_ID = 3;
	

	@Override
	/**
	 * On UI thread.
	 */
	public void onReceive(Context context, Intent intent) {
		
		if (TheLifeConfiguration.getOwnerDS().isValidOwner()) {
			
			Resources res = context.getResources();
			GoogleCloudMessaging messaging = GoogleCloudMessaging.getInstance(context);
			String messageType = messaging.getMessageType(intent);
			Log.i(TAG, "Got a GCMReceiver Notification with type " + messageType);
			TestFlight.passCheckpoint(TAG + "::onReceive() " + messageType);
			TestFlight.log(TAG + "::onReceive() " + messageType);			
			
			if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
				Bundle extras = intent.getExtras();
				String appType = extras.getString("app_type");
				if (appType.equals("request")) {
					handleRequestMessage(context, res, extras);
				} else if (appType.equals("event")) {
					handleEventMessage(context, res, extras);					
				} else {
					Log.e(TAG, "Can't parse GCM message: " + appType);
				}
			} else if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_DELETED)) {
				handleDeleteMessage(context, res, intent);
				
			} else if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR)) {
				Log.e(TAG, "Received GCM send error message");
			}
		}
		
		// this object will now die
	}
	
	
	private void handleRequestMessage(Context context, Resources res, Bundle extras) {
		
		// create the model object
		RequestModel request = RequestModel.fromBundle(context.getResources(), extras);
		Log.i(TAG, "Received request: " + request);
		
		// make sure the request is for this user
		if (request.getDestinationId() != TheLifeConfiguration.getOwnerDS().getId()) {
			Log.e(TAG, "GCM message is not for the current user.");
			
		} else {
			
			// create the Android notification
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setSmallIcon(R.drawable.ic_launcher);

			int numRequestsNotified = TheLifeConfiguration.getRequestsDS().numRequestsNotified();
			if (numRequestsNotified > 0) {
				// more than one request to show in Notification Manager
				
				builder.setContentTitle(res.getString(R.string.notifications_received));
				builder.setContentText(res.getString(R.string.notifications_pending));
				builder.setNumber(numRequestsNotified + 1);
			} else {
				// first request to show in Notification Manager
				
				if (UserModel.isInCache(request.getAuthorId())) {
					builder.setLargeIcon(UserModel.getThumbnail(request.getAuthorId()));
				} 
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
			}
			
			// destination activity when notification is selected
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent("com.p2c.thelife.Requests"), 0);
			builder.setContentIntent(pendingIntent);
			builder.setAutoCancel(true);
			
			// add the notification to the manager
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(REQUESTS_NOTIFICATION_ID, builder.build());
			notificationManager.cancel(AFTER_DELETE_NOTIFICATION_ID);			

			// add the request to the data store and tell listeners
			TheLifeConfiguration.getRequestsDS().add(request);
			TheLifeConfiguration.getRequestsDS().notifyDSChangedListeners();
			TheLifeConfiguration.getRequestsDS().refresh("push"); // TODO more efficient way to make persistent?
		}		
	}
	
	
	private void handleEventMessage(Context context, Resources res, Bundle extras) {
		
		// create the model object
		EventModel event = EventModel.fromBundle(context.getResources(), extras);
		Log.i(TAG, "Received event: " + event);
		
		// make sure the request is for this user
		int ownerId = TheLifeConfiguration.getOwnerDS().getId();
		if (!event.isVisibleToUser(ownerId)) {
			Log.e(TAG, "GCM message is not for the current user.");
			
		// make sure this event was not caused by this user (because then it wouldn't need a notification)
		} else if (event.user_id != ownerId) {
			
			// create the Android notification
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setSmallIcon(R.drawable.ic_launcher);

			int numEventsNotified = TheLifeConfiguration.getEventsDS().numEventsNotified();
			if (numEventsNotified > 0) {
				// more than one request to show in Notification Manager
				
				builder.setContentTitle(res.getString(R.string.events_received));
				builder.setContentText(res.getString(R.string.events_pending));
				builder.setNumber(numEventsNotified + 1);
			} else {
				// first request to show in Notification Manager
				
				if (UserModel.isInCache(event.user_id)) {
					builder.setLargeIcon(UserModel.getThumbnail(event.user_id));
				}
				
				builder.setContentTitle(event.userName);
				builder.setContentText(Html.fromHtml(event.finalDescription));
			}
			
			// destination activity when notification is selected
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent("com.p2c.thelife.EventsForCommunity"), 0);
			builder.setContentIntent(pendingIntent);
			builder.setAutoCancel(true);
			
			// add the notification to the manager
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(EVENTS_NOTIFICATION_ID, builder.build());
			notificationManager.cancel(AFTER_DELETE_NOTIFICATION_ID);			

			// add the request to the data store and tell listeners
			TheLifeConfiguration.getEventsDS().add(event);
			TheLifeConfiguration.getEventsDS().notifyDSChangedListeners();
			TheLifeConfiguration.getEventsDS().refresh("push"); // TODO more efficient way to make persistent?
		}		
	}	
	
	
	/**
	 * Too many messages flooded GCM, so some were deleted.
	 * @param res
	 * @param intent
	 */
	private void handleDeleteMessage(Context context, Resources res, Intent intent) {
		Bundle extras = intent.getExtras();
		int numberDeleted = Integer.valueOf(Utilities.getOptionalField("total_deleted", extras, "0"));
		Log.i(TAG, "Received GCM delete message: " + numberDeleted);
		
		// refresh the requests data store from the server
		TheLifeConfiguration.getRequestsDS().forceRefresh("push_after_delete");
		// refresh the events data store from the server
		TheLifeConfiguration.getEventsDS().forceRefresh("push_after_delete");			
		
		// create the Android notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle(res.getString((numberDeleted > 1) ? R.string.messages_received : R.string.message_received));
		if (numberDeleted > 1) {
			builder.setNumber(numberDeleted);
		}
		builder.setAutoCancel(true);		
		
		// destination activity when notification is selected
		// TODO where should it go? RequestsActivity or EventsActivity?
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent("com.p2c.thelife.Requests"), 0);
		builder.setContentIntent(pendingIntent);
		
		// add the notification to the manager
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(AFTER_DELETE_NOTIFICATION_ID, builder.build());
		notificationManager.cancel(REQUESTS_NOTIFICATION_ID);
		notificationManager.cancel(EVENTS_NOTIFICATION_ID);
	}

}
