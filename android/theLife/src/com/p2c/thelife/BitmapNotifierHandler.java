package com.p2c.thelife;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


/**
 * 
 * @author clarence
 *
 */
public class BitmapNotifierHandler extends Handler {
	
	// start the Handler on the UI thread
	public BitmapNotifierHandler() {
		super(Looper.getMainLooper());
	}
	
	@Override
	public void handleMessage(Message message) {
		
System.out.println("BitmapNotifierHandler got message " + message);
	}

}
