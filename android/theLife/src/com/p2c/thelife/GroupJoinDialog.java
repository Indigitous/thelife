package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.RequestModel;

/**
 * Ask to join an existing group. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class GroupJoinDialog extends AbstractServerAccessDialog {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final GroupModel group = ((GroupsSearchActivity)m_listener).getSelectedGroup();		
			
		// set the message and buttons of the alert
		String message = getResources().getString(R.string.join_group_prompt, group.name);				
		alertBuilder.setMessage(message);
		alertBuilder.setNegativeButton(R.string.cancel, null); 
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createJoinGroupRequest");

				Server server = new Server();
				server.createRequest(group.id, RequestModel.REQUEST_MEMBERSHIP, null, null, (Server.ServerListener)m_listener, "createRequest");		
				
				Toast.makeText(getActivity(), "REQUEST TO JOIN GROUP " + group.name + " ", Toast.LENGTH_SHORT).show();					
			}
		});		
		
		return alertBuilder.create();				
	}

}
