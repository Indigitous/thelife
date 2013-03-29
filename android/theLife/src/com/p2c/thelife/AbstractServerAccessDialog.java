package com.p2c.thelife;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Superclass of dialogs that send a message to the server and wait for a response.
 * Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public abstract class AbstractServerAccessDialog extends DialogFragment {
	
	public interface Listener {
		public void notifyAttemptingServerAccess(String indicator);
	}	
	
	protected Object m_listener = null;	
	
	@Override
	public abstract Dialog onCreateDialog(Bundle savedInstanceState);
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		m_listener = (Server.ServerListener)activity;
	}		

}
