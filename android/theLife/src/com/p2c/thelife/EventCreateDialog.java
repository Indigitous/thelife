package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

/**
 * Create an event. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * This does not create a change threshold event.
 * @author clarence
 *
 */
public class EventCreateDialog extends ServerAccessDialogAbstract {
	
	private static final String TAG = "EventCreateDialog";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final FriendModel friend = ((DeedForFriendActivity)m_listener).getSelectedFriend();		
		final DeedModel deed = ((DeedForFriendActivity)m_listener).getSelectedDeed();
		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View view = inflater.inflate(R.layout.dialog_ask_for_prayer_support, null);
		
		// set the message and content of the alert
		alertBuilder.setMessage(R.string.confirm_activity);
		alertBuilder.setView(view);	

		// set the buttons of the alert
		alertBuilder.setNegativeButton(R.string.cancel, null);
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createEvent");
				
				Server server = new Server(getActivity());
				CheckBox prayerSupportCheckBox = (CheckBox)view.findViewById(R.id.ask_for_prayer_support);								
				server.createEvent(deed.id, friend.id, prayerSupportCheckBox.isChecked(), null, (Server.ServerListener)m_listener, "createEvent");						
			}
		});		
		
		return alertBuilder.create();				
	}

}
