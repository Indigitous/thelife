package com.p2c.thelife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Import friends. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class FriendImportManuallyDialog extends AbstractServerAccessDialog {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View view = inflater.inflate(R.layout.dialog_import_friend_manually, null);
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
			
		// set the message, content and buttons of the alert
		alertBuilder.setMessage(R.string.add_friend);
		alertBuilder.setView(view);
		alertBuilder.setNegativeButton(R.string.cancel, null); 
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {

				EditText firstNameField = (EditText)view.findViewById(R.id.import_friend_first_name);
				String firstName = firstNameField.getText().toString();
				EditText lastNameField = (EditText)view.findViewById(R.id.import_friend_last_name);
				String lastName = lastNameField.getText().toString();
				Spinner thresholdField = (Spinner)view.findViewById(R.id.import_friend_threshold);
				int thresholdIndex = thresholdField.getSelectedItemPosition();
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createFriend");

				Server server = new Server();
				server.createFriend(firstName, lastName, thresholdIndex, (Server.ServerListener)m_listener, "createFriend");				
				
				Toast.makeText(getActivity(), "ADD FRIEND " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();					
			}
		});		
		
		return alertBuilder.create();				
	}

}
