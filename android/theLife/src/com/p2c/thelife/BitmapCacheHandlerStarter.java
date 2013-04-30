package com.p2c.thelife;

import android.os.HandlerThread;


/**
 * Start the bitmap cache handler in a new thread (not the UI thread).
 * @author clarence
 *
 */
public class BitmapCacheHandlerStarter extends HandlerThread {
	
	private static final String TAG = "BitmapCacheHandlerStarter";
	
	public BitmapCacheHandlerStarter(String name) {
		super(name);
	}
	
	private BitmapCacheHandler m_BitmapCacheHandler = null;	

	@Override
	protected synchronized void onLooperPrepared() {
		m_BitmapCacheHandler = new BitmapCacheHandler();
	}
	
	public synchronized BitmapCacheHandler getBitmapCacheHandler() {
		return m_BitmapCacheHandler;
	}	
	
}
