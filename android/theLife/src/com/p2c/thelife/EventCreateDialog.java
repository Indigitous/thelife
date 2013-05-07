package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

/**
 * Create an event. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class EventCreateDialog extends ServerAccessDialogAbstract implements OnItemSelectedListener {
	
	private static final String TAG = "EventCreateDialog";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		final FriendModel friend = ((DeedForFriendActivity)m_listener).getSelectedFriend();		
		final DeedModel deed = ((DeedForFriendActivity)m_listener).getSelectedDeed();
				
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View view = inflater.inflate(R.layout.dialog_change_threshold, null);
		Spinner spinner = (Spinner)view.findViewById(R.id.change_threshold);
		spinner.setOnItemSelectedListener(this);
		
		// set the message and content of the alert
		if (deed.hasThreshold) {
			alertBuilder.setMessage(R.string.change_threshold_prompt);
			alertBuilder.setView(view);
		} else {
			alertBuilder.setMessage(R.string.confirm_prayer_support);			
		}

		// set the buttons of the alert
		alertBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createEvent");
				
				FriendModel.Threshold threshold = getThreshold(deed, view);
				Server server = new Server(getActivity());
				server.createEvent(deed.id, friend.id, false, threshold, (Server.ServerListener)m_listener, "createEvent");		
			}
		});				
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createEvent");

				FriendModel.Threshold threshold = getThreshold(deed, view);				
				Server server = new Server(getActivity());
				server.createEvent(deed.id, friend.id, true, threshold, (Server.ServerListener)m_listener, "createEvent");						
			}
		});		
		
		return alertBuilder.create();				
	}
	
	
	/**
	 * Listen for a selected threshold -- to show help if the threshold has not been used before.
	 */
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		FriendModel.Threshold threshold = getThreshold((Spinner)arg0);	
		
		if (!TheLifeConfiguration.getOwnerDS().getHasUsedThreshold(threshold))
		{
			// show help for this threshold, since it has not been used before now
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());			
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			final View view = inflater.inflate(R.layout.dialog_first_time_using_threshold_help, null);
			WebView webView = (WebView)view.findViewById(R.id.dialog_using_threshold_help_message);
			
			int resourceId = -1;
			switch (threshold) {
				case Trusting:
					resourceId = R.string.first_time_using_trusting_threshold_help;
					break;
				case Curious:
					resourceId = R.string.first_time_using_curious_threshold_help;					
					break;
				case Open:
					resourceId = R.string.first_time_using_open_threshold_help;					
					break;
				case Seeking:
					resourceId = R.string.first_time_using_seeking_threshold_help;					
					break;
				case Entering:
					resourceId = R.string.first_time_using_entering_threshold_help;
					break;
				case Christian:
					resourceId = R.string.first_time_using_christian_threshold_help;					
					break;
				default:
					Log.e(TAG, "Can't give first time threshold help for threshold " + threshold);
			}
			if (resourceId != -1) {
				webView.loadData(getResources().getString(resourceId), "text/html", null);
			}
			alertBuilder.setView(view);

			// set the buttons of the alert
			alertBuilder.setNeutralButton(R.string.done, null);	
					
			// display it
			alertBuilder.show();			
		}
	}

	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
		
	
	/**
	 * Helper routine to get the threshold enum from the view.
	 */
	private FriendModel.Threshold getThreshold(DeedModel deed, View view) {
		
		FriendModel.Threshold threshold = null;
		
		if (deed.hasThreshold) {
			Spinner thresholdField = (Spinner)view.findViewById(R.id.change_threshold);
			return getThreshold(thresholdField);
		}
		
		return threshold;
	}
	
	
	private FriendModel.Threshold getThreshold(Spinner view) {
		return FriendModel.thresholdValues[view.getSelectedItemPosition() + 1];  // add 1 because the first threshold, NewContact, is not shown
	}

}
