package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.UserModel;

/**
 * Remove a user from a group. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 *
 */
public class GroupDeleteUserDialog extends ServerAccessDialogAbstract {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final GroupModel group = ((GroupActivity)m_listener).getSelectedGroup();
		final UserModel user = ((GroupActivity)m_listener).getSelectedUser();
		
		// Make sure the current user is the group leader, and therefore allowed to delete the given user
		if (group.leader_id == TheLifeConfiguration.getOwnerDS().getId()) {		
		
			// set the message, content and buttons of the alert
			String message = getResources().getString(R.string.delete_user_from_group_prompt, user.getFullName(), group.name);
			alertBuilder.setMessage(Html.fromHtml(message));
			alertBuilder.setNegativeButton(R.string.cancel, null); 
			alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {	
					
					// enable a progress bar
					((Listener)m_listener).notifyAttemptingServerAccess("deleteUserFromGroup");
	
					Server server = new Server(getActivity());
					server.deleteUserFromGroup(group.id, user.id, (Server.ServerListener)m_listener, "deleteUserFromGroup");						
				}
			});	
		} else {
			
			// set the message, content and buttons of the alert
			String message = getResources().getString(R.string.cannot_delete_user_from_group, user.getFullName(), group.name);
			alertBuilder.setMessage(Html.fromHtml(message));
			alertBuilder.setNegativeButton(R.string.cancel, null); 		
		}		
		
		return alertBuilder.create();				
	}

}
