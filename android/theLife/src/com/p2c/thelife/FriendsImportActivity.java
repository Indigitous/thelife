package com.p2c.thelife;

import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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
public class FriendsImportActivity extends FriendImportActivityAbstract {
	
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
					    new String[] { ContactsContract.Contacts._ID },
					    null,
					    null,
					    null);
				if (mCursor == null || !mCursor.moveToNext()) {
					Utilities.showErrorToast(this, getResources().getString(R.string.import_friend_error), Toast.LENGTH_SHORT);
				} else {
					// get the contact id
					int contactId = mCursor.getInt(mCursor.getColumnIndex(ContactsContract.Contacts._ID));
					
					// get the name information
					mCursor = getContentResolver().query(
							ContactsContract.Data.CONTENT_URI,
						    new String[] { ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME },
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

						// try to get email
						String email = null;
						mCursor = getContentResolver().query(
								ContactsContract.Data.CONTENT_URI,
							    new String[] { ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.TYPE  },
							    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
							    new String[] { String.valueOf(contactId) },
							    null);
						if (mCursor != null && mCursor.moveToNext()) {
							
							// TODO: look for which TYPE of email?
							int eIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
							email = mCursor.getString(eIndex);
						}
						
						// try to get mobile phone number
						String mobile = null;
						mCursor = getContentResolver().query(
								ContactsContract.Data.CONTENT_URI,
							    new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE },
							    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
							    new String[] { String.valueOf(contactId) },
							    null);						
						if (mCursor != null && mCursor.moveToNext()) {
							
							// TODO: look for MOBILE type, else OTHER type 
							int mIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
							mobile = mCursor.getString(mIndex);
						}
						
						// try to get photo
						m_bitmap = null;
						mCursor = getContentResolver().query(
								ContactsContract.Data.CONTENT_URI,
							    new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO },
							    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
							    new String[] { String.valueOf(contactId) },
							    null);
						if (mCursor != null && mCursor.moveToNext()) {
							
							int pIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO);
							byte[] photoBlob = mCursor.getBlob(pIndex);
							m_bitmap = BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length);
						}
						
						Log.i(TAG, "Create a friend from contacts: " + firstName + ", " + lastName + ", " + mobile + ", " + ((m_bitmap != null) ? " with photo" : " without photo"));

						// now create the friend
						addFriend(firstName, lastName, email, mobile, FriendModel.Threshold.NewContact);
					}
					
				}
				

			}
		}
	}
	
	public void importFriendsByFacebook(View view) {
		// not yet implemented
	}	

	
	public void importFriendManually(View view) {
		Intent intent = new Intent("com.p2c.thelife.FriendImportManually");
		startActivity(intent);		
	}

}
