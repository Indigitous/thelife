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
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); // all contacts
		startActivityForResult(intent, REQUESTCODE_IMPORT_FROM_CONTACTS);	
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent contactData) {
		
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUESTCODE_IMPORT_FROM_CONTACTS) {
				// user has selected a contact
				
				// TODO do this in a background thread?
				Cursor cursor = null;
				try {
					// ask for the contact information from the provider
					Uri selectedContact = contactData.getData();
					cursor = getContentResolver().query(
						    selectedContact,
						    new String[] { ContactsContract.Contacts._ID },
						    null,
						    null,
						    null);

					if (cursor == null || !cursor.moveToNext()) {
						Utilities.showErrorToast(this, getResources().getString(R.string.import_friend_error), Toast.LENGTH_SHORT);
					} else {
						// get the contact id
						int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
						cursor.close();
						cursor = null;
						
						// get the name information
						cursor = getContentResolver().query(
								ContactsContract.Data.CONTENT_URI,
							    new String[] { ContactsContract.Data._ID, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME },
							    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
							    new String[] { String.valueOf(contactId) },
							    null);
						
						if (cursor == null || !cursor.moveToNext()) {
							Utilities.showErrorToast(this, getResources().getString(R.string.import_friend_error), Toast.LENGTH_SHORT);
						} else {		
							
							// success: access the name information
							int fnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
							int lnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
							String firstName = cursor.getString(fnIndex);
							String lastName = cursor.getString(lnIndex);
							cursor.close();
							cursor = null;							
	
							// try to get email
							String email = null;
//							cursor = getContentResolver().query(
//									ContactsContract.Data.CONTENT_URI,
//								    new String[] { ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.TYPE  },
//								    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
//								    new String[] { String.valueOf(contactId) },
//								    null);
//							if (cursor != null && cursor.moveToNext()) {								
//								int eIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
//								email = cursor.getString(eIndex);
//							}
//							cursor.close();
//							cursor = null;							
							
							// try to get mobile phone number
							String mobile = null;
//							cursor = getContentResolver().query(
//									ContactsContract.Data.CONTENT_URI,
//								    new String[] { ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE },
//								    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
//								    new String[] { String.valueOf(contactId) },
//								    null);						
//							if (cursor != null && cursor.moveToNext()) {
//								// TODO: look for MOBILE type, else OTHER type 
//								int mIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//								mobile = cursor.getString(mIndex);
//							}
//							cursor.close();
//							cursor = null;							
							
							// try to get photo
							m_bitmap = null;
							cursor = getContentResolver().query(
									ContactsContract.Data.CONTENT_URI,
								    new String[] { ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Photo.PHOTO },
								    ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",                    // Selection criteria
								    new String[] { String.valueOf(contactId) },
								    null);
							if (cursor != null && cursor.moveToNext()) {
								int pIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO);
								byte[] photoBlob = cursor.getBlob(pIndex);
								if (photoBlob != null) {
									m_bitmap = Utilities.makeSquare(BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length));
								}
							}
							cursor.close();
							cursor = null;							
							
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
					if (cursor != null) {
						cursor.close();
					}
				}

			}
		}
	}
	
	
	public void importFriendsByFacebook(View view) {
		Utilities.showInfoToast(this, "importFriendsFacebook", Toast.LENGTH_SHORT);
	}

	
	public void importFriendManually(View view) {
		Intent intent = new Intent("com.p2c.thelife.FriendImportManually");
		startActivity(intent);		
	}

}
