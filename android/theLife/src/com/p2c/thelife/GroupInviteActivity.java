package com.p2c.thelife;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.RequestModel;

public class GroupInviteActivity extends SlidingMenuPollingFragmentActivity implements GroupInviteManuallyDialog.Listener, Server.ServerListener {

	public static final String TAG = "GroupInviteActivity";
	
	private static final int REQUESTCODE_IMPORT_FROM_CONTACTS_EMAIL = 1;
	private static final int REQUESTCODE_IMPORT_FROM_CONTACTS_SMS = 2;		

	
	private GroupModel m_group = null;
	private ProgressDialog m_progressDialog = null;
	private boolean m_isEmailRequest = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_group_invite, SlidingMenuSupport.GROUPS_POSITION);
		
		// Get the group
		int groupId = getIntent().getIntExtra("group_id", 0);
		m_group = TheLifeConfiguration.getGroupsDS().findById(groupId);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.group_invite, menu);		
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_group_invite_help);
			intent.putExtra("position", SlidingMenuSupport.GROUPS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.GroupInvite");
			if (m_group != null) {
				intent.putExtra("group_id", m_group.id);
			}
			startActivity(intent);
		}  else if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.Group");
			if (m_group != null) {
				intent.putExtra("group_id", m_group.id);
			}			
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			startActivity(intent);			
		}
		
		return true;
	}
	
	
	public void inviteManually(View view) {
		if (m_group != null) {
			GroupInviteManuallyDialog dialog = new GroupInviteManuallyDialog();
			dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
		}
	}
	
	
	public GroupModel getSelectedGroup() {
		return m_group;
	}		
	
	
	public void inviteByFacebook(View view) {
		// not yet implemented
	}
	
	
	public void inviteByInternalContactEmail(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI);
		startActivityForResult(intent, REQUESTCODE_IMPORT_FROM_CONTACTS_EMAIL);			
	}
	
	
	public void inviteByInternalContactSMS(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		startActivityForResult(intent, REQUESTCODE_IMPORT_FROM_CONTACTS_SMS);			
	}	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent contactData) {
		
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUESTCODE_IMPORT_FROM_CONTACTS_EMAIL || requestCode == REQUESTCODE_IMPORT_FROM_CONTACTS_SMS) {
				// user has selected a contact
				// TODO don't invite someone who is already a group member
				
				m_isEmailRequest = requestCode == REQUESTCODE_IMPORT_FROM_CONTACTS_EMAIL;
				String contactField = (m_isEmailRequest) ?
					/* ContactsContract.CommonDataKinds.Email.ADDRESS API 11 */ "data1" :
					ContactsContract.CommonDataKinds.Phone.NUMBER;
				
				String email = null;
				String mobile = null;
				Cursor cursor = null;
				try {				
					// ask for the contact information from the provider
					Uri selectedContact = contactData.getData();
					cursor = getContentResolver().query(
						selectedContact, 
						new String[] { ContactsContract.Contacts._ID, contactField }, 
						null, 
						null, 
						null);

					if (cursor == null || !cursor.moveToNext()) {
						Utilities.showErrorToast(this, getResources().getString(R.string.invite_person_error), Toast.LENGTH_SHORT);
					} else {
						// get the email/mobile
						int eIndex = cursor.getColumnIndex(contactField);
						if (m_isEmailRequest) {
							email = cursor.getString(eIndex);
						} else {
							mobile = cursor.getString(eIndex);
						}
						cursor.close();
						cursor = null;
						
						// now invite the person
						Log.i(TAG, "Invite a person from contacts: " + (m_isEmailRequest ? "EMAIL " + email : "MOBILE " + mobile));
						notifyAttemptingServerAccess("createRequest");
						
						// SMS invitations are sent by Android
						if (!m_isEmailRequest) {
							SmsManager smsManager = SmsManager.getDefault();
							String invitation = getResources().getString(R.string.sms_invitation, TheLifeConfiguration.getOwnerDS().getOwner().getFullName());
							smsManager.sendTextMessage(mobile, null, invitation, null,  null);
						}
						
						Server server = new Server(this);
						server.createRequest(m_group.id, RequestModel.INVITE, email, mobile, this, "createRequest");						
					}
				} catch (Exception e) {
					Log.e(TAG, "onActivityResult()", e);
					Utilities.showErrorToast(this, getResources().getString(R.string.invite_person_error), Toast.LENGTH_SHORT);
					
					if (m_progressDialog != null) {
						m_progressDialog.dismiss();
						m_progressDialog = null;
					}
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}
			}				
		}
	}
	
	
	@Override
	public void notifyAttemptingServerAccess(String indicator) {
		if (indicator.equals("createRequest")) {
			m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.inviting_person), true, true);
		}
	}

	
	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode, JSONObject jsonObject, String errorString) {
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}
		
		if (Utilities.isSuccessfulHttpCode(httpCode)) {
			Utilities.showInfoToast(this, getResources().getString(R.string.person_invited), Toast.LENGTH_SHORT);
		}
	}

}
