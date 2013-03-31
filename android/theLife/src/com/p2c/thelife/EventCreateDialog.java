package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

/**
 * Create an event. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class EventCreateDialog extends AbstractServerAccessDialog {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final FriendModel friend = ((DeedForFriendActivity)m_listener).getSelectedFriend();		
		final DeedModel deed = ((DeedForFriendActivity)m_listener).getSelectedDeed();		
			
		// set the message, content and buttons of the alert
		alertBuilder.setMessage(R.string.confirm_prayer_support);
		alertBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createEvent");

				Server server = new Server();
				server.createEvent(deed.id, friend.id, false, (Server.ServerListener)m_listener, "createEvent");		
				
				Toast.makeText(getActivity(), "CREATE event without prayer support ", Toast.LENGTH_SHORT).show();							
			}
		});				
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createEvent");

				Server server = new Server();
				server.createEvent(deed.id, friend.id, true, (Server.ServerListener)m_listener, "createEvent");		
				
				Toast.makeText(getActivity(), "CREATE event with prayer support ", Toast.LENGTH_SHORT).show();					
			}
		});		
		
		return alertBuilder.create();				
	}

}
