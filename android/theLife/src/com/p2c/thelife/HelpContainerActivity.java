package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


/**
 * Help screen which is populated by parameters from the intent.
 * @author clarence
 *
 */
public class HelpContainerActivity extends SlidingMenuPollingActivity {
	
	private static final String TAG = "HelpContainerActivity";
	
	private int m_groupId = 0;
	private int m_friendId = 0;
	private int m_deedId = 0;
	private String m_home = null;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// read the setup values from the intent
		int layout = getIntent().getIntExtra("layout", 0);
		int position = getIntent().getIntExtra("position", 0);
		super.onCreate(savedInstanceState, layout, position);
		
		// look for any HTML data
		String webViewData = getIntent().getStringExtra("webview_data");
		if (webViewData != null) {
			WebView webView = (WebView)findViewById(R.id.help_message_webview);
			String help = getResources().getString(R.string.style_sheet_help) + webViewData;
			webView.loadDataWithBaseURL(null, help, "text/html", "utf-8", null);		
		}
		
		String title = getIntent().getStringExtra("title");
		if (title != null) {
			setTitle(title);
		}
		
		// read the remaining values 
		m_groupId = getIntent().getIntExtra("group_id", 0);
		m_friendId = getIntent().getIntExtra("friend_id", 0);
		m_deedId = getIntent().getIntExtra("deed_id", 0);
		m_home = getIntent().getStringExtra("home");
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.help_container, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(m_home);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			
			// add the parameters to the intent
			if (m_groupId != 0) {
				intent.putExtra("group_id", m_groupId);
			}
			if (m_friendId != 0) {
				intent.putExtra("friend_id", m_friendId);
			}
			if (m_deedId != 0) {
				intent.putExtra("deed_id", m_deedId);
			}
			
			// start the intent
			startActivity(intent);
		}
		
		return true;
	}					
}
