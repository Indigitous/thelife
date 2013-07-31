package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.RequestModel;

/**
 * Invite a person to join your group. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class GroupInvitationManuallyDialog extends ServerAccessDialogAbstract {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		final GroupModel group = ((GroupActivity)m_listener).getSelectedGroup();
		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View view = inflater.inflate(R.layout.dialog_group_invitation_manually, null);
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
			
		// set the message, content and buttons of the alert
		alertBuilder.setMessage(R.string.invite_person_to_group);
		alertBuilder.setView(view);
		alertBuilder.setNegativeButton(R.string.cancel, null); 
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {

				EditText emailField = (EditText)view.findViewById(R.id.invite_person_email);
				String email = emailField.getText().toString();
				EditText phoneField = (EditText)view.findViewById(R.id.invite_person_phone);
				String phone = phoneField.getText().toString();			
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createRequest");

				Server server = new Server(getActivity());
				server.createRequest(group.id, RequestModel.INVITE, email, phone, (Server.ServerListener)m_listener, "createRequest");
			}
		});		
		
		return alertBuilder.create();				
	}

}
