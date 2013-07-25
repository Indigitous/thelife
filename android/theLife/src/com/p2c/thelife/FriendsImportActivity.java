package com.p2c.thelife;

import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.FriendModel;


/**
 * Import friends.
 * @author clarence
 *
 */
public class FriendsImportActivity extends SlidingMenuPollingFragmentActivity implements Server.ServerListener {
	
	private static String TAG = "FriendsImportActivity";
	
	private static final int REQUESTCODE_IMPORT_FROM_CONTACTS = 1;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_friends_import, SlidingMenuSupport.FRIENDS_POSITION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.friends_import, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.Friends");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			startActivity(intent);
		}  else if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_friend_import_manually_help);
			intent.putExtra("position", SlidingMenuSupport.FRIENDS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.FriendsImport");
			intent.putExtra("webview_data", getResources().getString(R.string.first_time_adding_friend_help));
			startActivity(intent);
		}			
		
		return true;
	}		
	
	public void importFriendsByPhone(View view) {
//		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI);
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		
		startActivityForResult(intent, REQUESTCODE_IMPORT_FROM_CONTACTS);	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent contactData) {
		
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUESTCODE_IMPORT_FROM_CONTACTS) {
				Uri selectedContact = contactData.getData();
				
				Cursor mCursor = getContentResolver().query(
					    selectedContact,
					    null,              // The columns to return for each row
					    null,              // Selection criteria
					    null,              // Selection criteria
					    null);
				if (mCursor == null || !mCursor.moveToNext()) {
					Utilities.showErrorToast(this, getResources().getString(R.string.import_friend_error), Toast.LENGTH_SHORT);
				} else {
					// get the contact id
					int contactId = mCursor.getInt(mCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
					
					// get the name information
					mCursor = getContentResolver().query(
							ContactsContract.Data.CONTENT_URI,
						    null,
						    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
						    new String[] { String.valueOf(contactId) },
						    null);
					if (mCursor == null || !mCursor.moveToNext()) {
						Utilities.showErrorToast(this, getResources().getString(R.string.import_friend_error), Toast.LENGTH_SHORT);
					} else {
						
						// success: get the name information
						int fnIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
						int lnIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
						String firstName = mCursor.getString(fnIndex);
						String lastName = mCursor.getString(lnIndex);
						
						Server server = new Server(this);
						server.createFriend(firstName, lastName, null, null, FriendModel.Threshold.NewContact, this, "createFriend");
			
					}
					
				}
				

			}
		}
	}
	
	public void importFriendsByFacebook(View view) {
		
	}	

	
	public void importFriendManually(View view) {
		Intent intent = new Intent("com.p2c.thelife.FriendImportManually");
		startActivity(intent);		
	}

	@Override
	public void notifyServerResponseAvailable(String indicator, int httpCode,
			JSONObject jsonObject, String errorString) {

		// finish import
		Intent intent = new Intent("com.p2c.thelife.Friends");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

}
