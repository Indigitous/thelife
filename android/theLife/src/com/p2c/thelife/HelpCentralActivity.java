package com.p2c.thelife;

import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


/**
 * Show the global help for the whole app.
 * @author clarence
 *
 */
public class HelpCentralActivity extends SlidingMenuActivity {
	
	private static final String TAG = "HelpCentralActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_help_central, SlidingMenuSupport.HELP_POSITION);
				
		WebView webView = (WebView)findViewById(R.id.help_central_webview);
		
		// Javascript support
		webView.getSettings().setJavaScriptEnabled(true);
		
		// get help
		String help = getResources().getString(R.string.style_sheet_help);
		help += getResources().getString(R.string.first_time_adding_friend_help);
		help += getResources().getString(R.string.using_new_contact_threshold_help);
		help += getResources().getString(R.string.first_time_using_trusting_threshold_help);
		help += getResources().getString(R.string.first_time_using_curious_threshold_help);		
		help += getResources().getString(R.string.first_time_using_open_threshold_help);		
		help += getResources().getString(R.string.first_time_using_seeking_threshold_help);		
		help += getResources().getString(R.string.first_time_using_entering_threshold_help);
		help += getResources().getString(R.string.feedback_help);
		
		// show help
		webView.loadDataWithBaseURL(null, help, "text/html", "utf-8", null);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.help_central, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			m_support.slideOpen();
		}
		
		return true;
	}			

}
