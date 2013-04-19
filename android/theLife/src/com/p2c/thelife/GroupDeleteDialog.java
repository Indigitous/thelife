package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

import com.p2c.thelife.model.GroupModel;

/**
 * Delete an existing group. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class GroupDeleteDialog extends AbstractServerAccessDialog {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final GroupModel group = ((GroupsActivity)m_listener).getSelectedGroup();
		
		// Make sure the user is the group leader, and therefore allowed to delete the group
		if (group.leader_id == TheLifeConfiguration.getOwnerDS().getUserId()) {
			
			// set the message, content and buttons of the alert
			String message = getResources().getString(R.string.delete_group_prompt, group.name);
			alertBuilder.setMessage(Html.fromHtml(message));
			alertBuilder.setNegativeButton(R.string.cancel, null); 
			alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {	
					
					// enable a progress bar
					((Listener)m_listener).notifyAttemptingServerAccess("deleteGroup");
	
					Server server = new Server();
					server.deleteGroup(group.id, (Server.ServerListener)m_listener, "deleteGroup");						
				}
			});	
		} else {
			
			// set the message, content and buttons of the alert
			String message = getResources().getString(R.string.cannot_delete_group, group.name);
			alertBuilder.setMessage(Html.fromHtml(message));
			alertBuilder.setNegativeButton(R.string.cancel, null); 		
		}
		
		return alertBuilder.create();				
	}

}
