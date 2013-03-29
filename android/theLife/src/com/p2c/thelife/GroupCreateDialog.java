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
import android.widget.Toast;

/**
 * Create a new group. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class GroupCreateDialog extends DialogFragment {
	
	public interface Listener {
		public void notifyAttemptingGroupCreate();
	}	
	
	private Object m_listener = null;	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View view = inflater.inflate(R.layout.dialog_group_create, null);
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
			
		// set the message, content and buttons of the alert
		alertBuilder.setMessage(R.string.create_group);
		alertBuilder.setView(view);
		alertBuilder.setNegativeButton(R.string.cancel, null); 
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {

				EditText nameField = (EditText)view.findViewById(R.id.group_create_name);
				String name = nameField.getText().toString();
				EditText descriptionField = (EditText)view.findViewById(R.id.group_create_description);
				String description = descriptionField.getText().toString();		
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingGroupCreate();

				Server server = new Server();
				server.createGroup(name, description, (Server.ServerListener)m_listener, "createGroup");		
				
				Toast.makeText(getActivity(), "CREATE GROUP " + name + " ", Toast.LENGTH_SHORT).show();					
			}
		});		
		
		return alertBuilder.create();				
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		m_listener = (Server.ServerListener)activity;
	}		

}
