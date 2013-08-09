package com.p2c.thelife.push;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMReceiver extends BroadcastReceiver {

	@Override
	/**
	 * On UI thread.
	 */
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging messaging = GoogleCloudMessaging.getInstance(context);
System.out.println("THE INTENT IS " + intent);
System.out.println("THE INTENT EXTRAS ARE " + intent.getExtras());
		String messageType = messaging.getMessageType(intent);
System.out.println("THE MESSAGE TYPE IS " + messageType);
		if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
System.out.println("NORMAL MESSAGE");

		Bundle extras = intent.getExtras();
		System.out.println("The extras for 'Clarence' is: " + extras.getString("Clarence"));
		System.out.println("The extras for 'clarence' is: " + extras.getString("clarence"));
		System.out.println("The extras for 'message_id' is: " + extras.getString("message_id"));


		} else if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_DELETED)) {
System.out.println("DELETED TYPE MESSAGE");
		} else if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR)) {
System.out.println("SEND ERROR MESSAGE");
		}
		
		// this object will now die
	}

}
