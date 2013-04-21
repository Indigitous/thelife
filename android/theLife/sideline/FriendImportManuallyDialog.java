package com.p2c.thelife;

import com.p2c.thelife.model.FriendModel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
		final View view = inflater.inflate(R.layout.activity_import_friend_manually, null);
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
				FriendModel.Threshold threshold = FriendModel.thresholdValues[thresholdIndex];				
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createFriend");

				Server server = new Server(getActivity());
				server.createFriend(firstName, lastName, threshold, (Server.ServerListener)m_listener, "createFriend");				
				
				Toast.makeText(getActivity(), "ADD FRIEND " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();					
			}
		});		
		
		return alertBuilder.create();				
	}

}
