package com.p2c.thelife;

// Parts of this file are adapted from Facebook's SDK file com.facebook.samples.friendpicker.PickFriendsActivity which is licensed by the following:
/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

/**
 * Import friends from Facebook.
 * @author clarence
 *
 */
public class FriendsImportFacebookActivity extends SlidingMenuPollingFragmentActivity {
	
	FriendPickerFragment m_friendPickerFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_friend_import_manually, SlidingMenuSupport.FRIENDS_POSITION);		
		setContentView(R.layout.activity_friends_import_facebook);

		// FACEBOOK CODE ADAPTATION START
        FragmentManager fragmentManager = getSupportFragmentManager();		
        m_friendPickerFragment = new com.facebook.widget.FriendPickerFragment();
        fragmentManager.beginTransaction()
        	.add(R.id.facebook_friends_picker, m_friendPickerFragment)
            .commit();

        m_friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
            @Override
            public void onError(PickerFragment<?> fragment, FacebookException error) {
                Utilities.showErrorToast(
                	FriendsImportFacebookActivity.this, 
                	FriendsImportFacebookActivity.this.getResources().getString(R.string.facebook_error),  
                	Toast.LENGTH_SHORT);
            }
        });

        m_friendPickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
            @Override
            public void onDoneButtonClicked(PickerFragment<?> fragment) {
            	// gather the friends
            	List<GraphUser> friendSelections = m_friendPickerFragment.getSelection();
				ArrayList<String> facebookIds = new ArrayList<String>(friendSelections.size());
            	for (GraphUser friendSelection : friendSelections) {
					facebookIds.add(friendSelection.getId());
				}

            	// return the friends back to the import activity
				Intent intent = new Intent();
				intent.putExtra("facebook_ids", facebookIds);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        // FACEBOOK CODE ADAPTATION END		
        
        System.out.println("FINISHING ON CREATE");
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
			Intent intent = new Intent("com.p2c.thelife.FriendsImport");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);						
			startActivity(intent);
		}  else if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_friend_import_manually_help);
			intent.putExtra("position", SlidingMenuSupport.FRIENDS_POSITION);
			intent.putExtra("home", "com.p2c.thelife.FriendsImportFacebook");
			intent.putExtra("webview_data", getResources().getString(R.string.first_time_adding_friend_help));
			startActivity(intent);
		}
		
		return true;
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // facebook requestCode=64206 resultCode=-1
 		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    } 	
	
	
    // must load facebook data after onCreate()
	@Override
	protected void onStart() {
		super.onStart();
		
System.out.println("IN ON START");
		
		Session.openActiveSession(this, true, new Session.StatusCallback() {
			
			@Override
			public void call(Session session, SessionState state, Exception exception) {
System.out.println("RECEIVE FACEBOOK SESSION STATE " + state + "," + exception);
				
				if (session.isOpened()) {
					// load the friends data
System.out.println("LOADING FRIENDS DATA NOW");
					m_friendPickerFragment.loadData(true);
				}
			}
		});
	}

}
