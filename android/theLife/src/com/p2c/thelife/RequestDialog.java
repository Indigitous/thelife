package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;

import com.p2c.thelife.model.RequestModel;

/**
 * Accept or reject a request. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class RequestDialog extends AbstractServerAccessDialog {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final RequestModel request = ((RequestsActivity)m_listener).getSelectedRequest();
			
		// set the message, content and buttons of the alert
		Spanned message = null;
		if (request.isInvite()) {
			message = Html.fromHtml(request.description);					
		} else if (request.isMembershipRequest()) {
			message = Html.fromHtml(request.description);					
		} else {
			message = Html.fromHtml("?");
		}
			
		alertBuilder.setMessage(message);
		alertBuilder.setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("reject");

				// tell the server to reject the request
				Server server = new Server();
				int userId = request.isInvite() ? TheLifeConfiguration.getUserId() : request.user_id;
				server.processGroupMembershipRequest(request.id, false, userId, request.group_id, (Server.ServerListener)m_listener, "processGroupMembershipRequest");				
			}
		});
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface di, int which) {	
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("accept");

				// tell the server to accept the request -- to add the user to the group
				Server server = new Server();
				int userId = request.isInvite() ? TheLifeConfiguration.getUserId() : request.user_id;
				server.processGroupMembershipRequest(request.id, true, userId, request.group_id, (Server.ServerListener)m_listener, "processGroupMembershipRequest");						
			}
		});		
		
		return alertBuilder.create();				
	}

}
