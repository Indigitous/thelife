package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.p2c.thelife.model.FriendModel;

/**
 * Delete an existing friend. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class FriendDeleteDialog extends ServerAccessDialogAbstract {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final FriendModel friend = ((FriendsActivity)m_listener).getSelectedFriend();
			
		// set the message, content and buttons of the alert
		String message = getResources().getString(R.string.delete_friend_prompt, friend.getFullName());		
		alertBuilder.setMessage(message);
		alertBuilder.setNegativeButton(R.string.cancel, null); 
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {	
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("deleteFriend");

				Server server = new Server();
				server.deleteFriend(friend.id, (Server.ServerListener)m_listener, "deleteFriend");						
			}
		});		
		
		return alertBuilder.create();				
	}

}
