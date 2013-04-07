package com.p2c.thelife;

import android.os.Handler;
import android.util.Log;

import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.RequestsDS;

/**
 * Because Requests are polled regardless of which Activity is running, a separate polling mechanism is required.
 * This uses Handler, attached to the UI thread, to continually refresh() the Requests data store on the UI thread.
 */
public class RequestsPoller implements DSRefreshedListener {
	
	private static final String TAG = "RequestsPoller";
	
	private RequestsDS m_requestsDS = null;
	private Handler m_handler = null;
	private boolean m_isAppVisible = false;
	private boolean m_isPolling = false;
	private final long m_firstDelayMillis;
	private final long m_normalDelayMillis;
	private boolean m_isFirstPoll = true;
	
	
	/**
	 * This object must be instantiated in the UI thread!
	 * @param requestsDS
	 */
	public RequestsPoller(RequestsDS requestsDS, long firstDelayMillis, long normalDelayMillis) {
		
		m_requestsDS = requestsDS;
		m_handler = new Handler();
		m_requestsDS.addDSRefreshedListener(this);
		m_firstDelayMillis = firstDelayMillis;
		m_normalDelayMillis = normalDelayMillis;
	}

	
	/**
	 * Android activity has resumed/started. 
	 */
	public void start() {
		m_isAppVisible = true;
		
		if (!m_isPolling) {
			m_isPolling = true;
			poll(m_isFirstPoll ? m_firstDelayMillis : m_normalDelayMillis);
			m_isFirstPoll = false;
		}
	}
	
	/**
	 * Android activity has paused/stopped.
	 */
	public void stop() {
		m_isAppVisible = false;
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
	 * Callback when the data store has been refreshed, even if the refresh failed or there were no updates.
	 * Called from the UI thread.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
			
// Log.e(TAG, "FINISHED REQUEST POLL!");
			
		// poll again if the app is still on screen
		if (m_isAppVisible) {
			m_isPolling = true;
			poll(m_normalDelayMillis);
		} else {
			m_isPolling = false;
		}
	}
	
	
	/**
	 * Stop the polling.
	 * Normally this would be called when the application is finishing, but that doesn't seem to be supported in Android.
	 * Called from the UI thread.
	 */
	public void finish() {

		if (m_handler != null) {
			m_handler.removeCallbacksAndMessages(null);
		}
		m_handler = null;
		m_requestsDS.removeDSRefreshedListener(this);
		m_requestsDS = null;
		
	}	
	
}
