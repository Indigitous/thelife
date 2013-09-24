package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

/**
 * Create an change threshold event. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class ChangeThresholdDialog extends ServerAccessDialogAbstract {
	
	private static final String TAG = "ChangeThresholdDialog";
	
	private int m_selection = 0;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final FriendModel friend = ((DeedsForFriendActivity)m_listener).getSelectedFriend();		
		final DeedModel deed = ((DeedsForFriendActivity)m_listener).getSelectedDeed();
		
		// set the message and content of the alert
		alertBuilder.setTitle(R.string.change_threshold_prompt); // Android bug: should be setMessage() but that won't display the singleChoiceItems
		
		// set the thresholds to choose from
		m_selection = friend.threshold.ordinal() - 1; // - 1 because the first, NewContact, is not in the list
		m_selection = m_selection < 0 ? 0 : m_selection; 
		alertBuilder.setSingleChoiceItems(R.array.thresholds_medium_change, m_selection, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				m_selection = which;
			}
		});
		
		// set the buttons of the alert
		alertBuilder.setNegativeButton(R.string.cancel, null);
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createEvent");

				FriendModel.Threshold threshold = FriendModel.thresholdValues[m_selection + 1]; // + 1 because the first, NewContact, is not in the list		
				Server server = new Server(getActivity());
				server.createEvent(deed.id, friend.id, true, threshold, (Server.ServerListener)m_listener, "createEvent");						
			}
		});		
		
		return alertBuilder.create();				
	}

}
