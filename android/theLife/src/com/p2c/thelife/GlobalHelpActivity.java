package com.p2c.thelife;

import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

public class GlobalHelpActivity extends SlidingMenuActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_global_help, SlidingMenuSupport.HELP_POSITION);
		
		WebView webView = (WebView)findViewById(R.id.global_help_webview);
		
		// TODO check for a network connection
		// TODO version and language specific page
		webView.loadUrl("http://p2c.com");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.global_help, menu);
		return true;
	}

}
