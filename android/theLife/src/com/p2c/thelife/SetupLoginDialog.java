package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Login the user. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class SetupLoginDialog extends ServerAccessDialogAbstract {
	
	private static final String TAG = "SetupLoginDialog";
		
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View view = inflater.inflate(R.layout.dialog_setup_login, null);
			
		// make the dialog
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
			
		// set the message, content and buttons of the alert
		alertBuilder.setMessage(R.string.login_prompt);
		alertBuilder.setView(view);
		alertBuilder.setNegativeButton(R.string.cancel, null); 
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText usernameField = (EditText)view.findViewById(R.id.setup_login_username);
				String username = usernameField.getText().toString();
				EditText passwordField = (EditText)view.findViewById(R.id.setup_login_password);
				String password = passwordField.getText().toString();
							
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("login");

				Server server = new Server();
				server.login(username, password, (Server.ServerListener)m_listener, "login");
			}
		}); 
		
		return alertBuilder.create();				
	}
	

}
