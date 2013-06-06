package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

/**
 * Create an event. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * This does not create a change threshold event.
 * @author clarence
 *
 */
public class EventCreateDialog extends ServerAccessDialogAbstract {
	
	private static final String TAG = "EventCreateDialog";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final FriendModel friend = ((DeedForFriendActivity)m_listener).getSelectedFriend();		
		final DeedModel deed = ((DeedForFriendActivity)m_listener).getSelectedDeed();
		
		// set the message and content of the alert
		alertBuilder.setMessage(R.string.confirm_prayer_support);			

		// set the buttons of the alert
		alertBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createEvent");
				
				Server server = new Server(getActivity());
				server.createEvent(deed.id, friend.id, false, null, (Server.ServerListener)m_listener, "createEvent");		
			}
		});				
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createEvent");

				Server server = new Server(getActivity());
				server.createEvent(deed.id, friend.id, true, null, (Server.ServerListener)m_listener, "createEvent");						
			}
		});		
		
		return alertBuilder.create();				
	}

}
