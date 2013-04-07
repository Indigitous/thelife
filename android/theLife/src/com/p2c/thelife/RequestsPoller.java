package com.p2c.thelife;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.RequestsDS;

/**
 * Because Requests are polled regardless of which Activity is running, a separate polling mechanism is required.
 * This uses Handler, attached to the UI thread, to continually refresh() the Requests data store on the UI thread.
 */
public class RequestsPoller implements DSRefreshedListener {
	
	private Application m_app = null;
	private RequestsDS m_requestsDS = null;
	private Handler m_handler = null;
	private int m_notificationId = 1;  // use the request_id?
	
	
	/**
	 * This object must be instantiated in the UI thread!
	 * @param requestsDS
	 */
	public RequestsPoller(Application app, RequestsDS requestsDS) {
		
		m_app = app;
		m_requestsDS = requestsDS;
		m_handler = new Handler();
		m_requestsDS.addDSRefreshedListener(this);
		
		Toast.makeText(m_app, "BEGINNING A NEW REQUESTS POLLER! ", Toast.LENGTH_SHORT).show();		
	}
	
	/**
	 * Refresh the data store after the given delay.
	 * Called from the UI thread.
	 * @param delayMillis
	 */
	public void poll(long delayMillis) {
		
		m_handler.postDelayed(new Runnable() {

				// this method will run in the UI thread
				@Override
				public void run() {
					if (m_requestsDS != null) {
						m_requestsDS.refresh("RequestsPoller");
					}
				}
			}, 
			delayMillis
		);
		
	}
	
	/**
	 * Stop the polling.
	 * Called from the UI thread.
	 */
	public void finish() {

		if (m_handler != null) {
			m_handler.removeCallbacksAndMessages(null);
		}
		m_handler = null;
		m_requestsDS.removeDSRefreshedListener(this);
		m_requestsDS = null;
		m_app = null;
		
	}


	/**
	 * Callback when the data store has been refreshed.
	 * Called from the UI thread.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		
		Toast.makeText(m_app, "FINISHED ANOTHER REQUESTS DS REFRESH!", Toast.LENGTH_SHORT).show();
		
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(m_app.getApplicationContext());
//		builder.setSmallIcon(R.drawable.ic_launcher);
//		builder.setContentTitle("theLife");
//		builder.setContentText("A request to join your group has been received.");
//		
//		NotificationManager notificationManager = (NotificationManager)m_app.getSystemService(Context.NOTIFICATION_SERVICE);
//		notificationManager.notify(m_notificationId++, builder.build());
		
		// poll again
		poll(TheLifeConfiguration.REFRESH_REQUESTS_DELTA);
	}
	
}
