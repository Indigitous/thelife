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
		if (request.type.equals(RequestModel.INVITE)) {
			message = Html.fromHtml(request.description); // + getResources().getString(R.string.request_invite_prompt));						
		} else if (request.type.equals(RequestModel.REQUEST_MEMBERSHIP)) {
			message = Html.fromHtml(request.description); // + getResources().getString(R.string.request_membership_prompt));						
		} else {
			message = Html.fromHtml("?");
		}
			
		alertBuilder.setMessage(message);
		alertBuilder.setNegativeButton(R.string.no_thanks, null); 
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {	
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("request");

				Server server = new Server();
				server.addUserToGroup(request.id, request.user_id, request.group_id, (Server.ServerListener)m_listener, "join");						
			}
		});		
		
		return alertBuilder.create();				
	}

}
