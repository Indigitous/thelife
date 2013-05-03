package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;

import com.p2c.thelife.model.RequestModel;

/**
 * Finish looking at a completed request (it has already been accepted or rejected). 
 * Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class RequestFinishDialog extends ServerAccessDialogAbstract {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final RequestModel request = ((RequestsActivity)m_listener).getSelectedRequest();
			
		// set the message, content and buttons of the alert
		Spanned message = Html.fromHtml(request.finalDescription);	
			
		alertBuilder.setMessage(message);
		alertBuilder.setNegativeButton(R.string.cancel, null); 
		alertBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface di, int which) {	
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("finish");

				// tell the server to remove the completed request/notification
				Server server = new Server(getActivity());
				server.deleteRequest(request.id, (Server.ServerListener)m_listener, "deleteRequest");						
			}
		});		
		
		return alertBuilder.create();				
	}

}
