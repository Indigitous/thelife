package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.RequestModel;

/**
 * Invite a person to join your group. Uses a dialog fragment as per Android doc, using support library for Androids < 3.0.
 * @author clarence
 *
 */
public class GroupInviteManuallyDialog extends ServerAccessDialogAbstract {
	
	private static final String TAG = "GroupInviteManuallyDialog";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		final GroupModel group = ((GroupInviteActivity)m_listener).getSelectedGroup();
		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View view = inflater.inflate(R.layout.dialog_group_invite_manually, null);
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		// either email or mobile, but not both
		final EditText emailText = (EditText)view.findViewById(R.id.invite_person_email);
		final EditText mobileText = (EditText)view.findViewById(R.id.invite_person_phone);
		emailText.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mobileText.setText("");
				return false;
			}
		});
		mobileText.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				emailText.setText("");
				return false;
			}
		});		
			
		// set the message, content and buttons of the alert
		//alertBuilder.setMessage(Html.fromHtml(getResources().getString(R.string.invite_person_to_group)));
		alertBuilder.setView(view);
		alertBuilder.setNegativeButton(R.string.cancel, null); 
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {

				EditText emailField = (EditText)view.findViewById(R.id.invite_person_email);
				String email = emailField.getText().toString();
				EditText mobileField = (EditText)view.findViewById(R.id.invite_person_phone);
				String mobile = mobileField.getText().toString();			
				
				// enable a progress bar
				((Listener)m_listener).notifyAttemptingServerAccess("createRequest");
				
				// now invite the person
				boolean isEmailRequest = (email != null && !email.isEmpty());
				Log.i(TAG, "Invite a person manually: " + (isEmailRequest ? "EMAIL " + email : "MOBILE " + mobile));
				
				// SMS invitations are sent by Android
				if (!isEmailRequest) {
					SmsManager smsManager = SmsManager.getDefault();
					String invitation = getResources().getString(R.string.sms_invitation, TheLifeConfiguration.getOwnerDS().getOwner().getFullName());
					smsManager.sendTextMessage(mobile, null, invitation, null,  null);
				}

				Server server = new Server(getActivity());
				server.createRequest(group.id, RequestModel.INVITE, email, mobile, (Server.ServerListener)m_listener, "createRequest");
			}
		});
		
		// enable the dialog positive button when something has been entered
		alertBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				boolean shouldEnable = !emailText.getText().toString().trim().isEmpty() ||
									   !mobileText.getText().toString().trim().isEmpty();
				enableInvite(dialog, shouldEnable);
				return false;
			}
		});
		
		// disable the dialog positive button when it is shown
		AlertDialog dialog = alertBuilder.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				enableInvite(dialog, false);
			}
		});
		return dialog;
	}
	
	
	private void enableInvite(DialogInterface dialog, boolean isEnabled) {
		Button inviteButton = ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE);
		inviteButton.setEnabled(isEnabled);
	}

}
