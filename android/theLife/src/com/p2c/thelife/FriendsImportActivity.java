package com.p2c.thelife;

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
	
	public void importFriendsByInternalContact(View view) {
//		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI);
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); // all contacts
		
		startActivityForResult(intent, REQUESTCODE_IMPORT_FROM_CONTACTS);	
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent contactData) {
		
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUESTCODE_IMPORT_FROM_CONTACTS) {
				// user has selected a contact
				
				// TODO do this in a background thread?
				Cursor mCursor = null;
				try {
					// ask for the contact information from the provider
					Uri selectedContact = contactData.getData();
					mCursor = getContentResolver().query(
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
						mCursor.close();
						mCursor = null;
						
						// get the name information
						mCursor = getContentResolver().query(
								ContactsContract.Data.CONTENT_URI,
							    new String[] { ContactsContract.Data._ID, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME },
							    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
							    new String[] { String.valueOf(contactId) },
							    null);
						
						if (mCursor == null || !mCursor.moveToNext()) {
							Utilities.showErrorToast(this, getResources().getString(R.string.import_friend_error), Toast.LENGTH_SHORT);
						} else {		
							
							// success: access the name information
							int fnIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
							int lnIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
							String firstName = mCursor.getString(fnIndex);
							String lastName = mCursor.getString(lnIndex);
							mCursor.close();
							mCursor = null;							
	
							// try to get email
							String email = null;
							mCursor = getContentResolver().query(
									ContactsContract.Data.CONTENT_URI,
								    new String[] { ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.TYPE  },
								    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
								    new String[] { String.valueOf(contactId) },
								    null);
							if (mCursor != null && mCursor.moveToNext()) {								
								int eIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
								email = mCursor.getString(eIndex);
							}
							mCursor.close();
							mCursor = null;							
							
							// try to get mobile phone number
							String mobile = null;
							mCursor = getContentResolver().query(
									ContactsContract.Data.CONTENT_URI,
								    new String[] { ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE },
								    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
								    new String[] { String.valueOf(contactId) },
								    null);						
							if (mCursor != null && mCursor.moveToNext()) {
								// TODO: look for MOBILE type, else OTHER type 
								int mIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
								mobile = mCursor.getString(mIndex);
							}
							mCursor.close();
							mCursor = null;							
							
							// try to get photo
							m_bitmap = null;
							mCursor = getContentResolver().query(
									ContactsContract.Data.CONTENT_URI,
								    new String[] { ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Photo.PHOTO },
								    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
								    new String[] { String.valueOf(contactId) },
								    null);
							if (mCursor != null && mCursor.moveToNext()) {
								int pIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO);
								byte[] photoBlob = mCursor.getBlob(pIndex);
								if (photoBlob != null) {
									m_bitmap = BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length);
								}
							}
							mCursor.close();
							mCursor = null;							
							
							// TODO only add a friend once
							
							// now create the friend
							Log.i(TAG, "Create a friend from contacts: " + firstName + ", " + lastName + ", " + email + ", " + mobile + ", " + ((m_bitmap != null) ? " with photo" : " without photo"));							
							addFriend(firstName, lastName, email, mobile, FriendModel.Threshold.NewContact);
						}
						
					}
				} catch (Exception e) {
					Log.e(TAG, "onActivityResult()", e);
					Utilities.showErrorToast(this, getResources().getString(R.string.import_friend_error), Toast.LENGTH_SHORT);
					
					if (m_progressDialog != null) {
						m_progressDialog.dismiss();
						m_progressDialog = null;
					}
				} finally {
					if (mCursor != null) {
						mCursor.close();
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
