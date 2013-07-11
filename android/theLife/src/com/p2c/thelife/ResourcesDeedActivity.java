package com.p2c.thelife;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.DeedModel;



/**
 * Show a selected deed/activity.
 * @author clarence
 *
 */
public class ResourcesDeedActivity extends SlidingMenuPollingFragmentActivity {
	
	private static final String TAG = "ResourcesDeedActivity"; 
	
	private DeedModel m_deed = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_resources_deed, SlidingMenuSupport.RESOURCES_POSITION);
			
		// Get the Deed model
		int deedId = getIntent().getIntExtra("deed_id", 0);
		m_deed = TheLifeConfiguration.getDeedsDS().findById(deedId);
		
		// Show the Deed model
		if (m_deed != null) {
			
			// icon
//			ImageView image = (ImageView)findViewById(R.id.deed_for_friend_image);
//			image.setImageBitmap(m_deed.image);  // TODO deed image
			
			// title			
			TextView title = (TextView)findViewById(R.id.deed_for_friend_title);
			title.setText(m_deed.title);				
			
			// description
			WebView description = (WebView)findViewById(R.id.deed_for_friend_description);
			description.loadDataWithBaseURL(null, m_deed.description, "text/html", "utf-8", null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.resources_deed, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent("com.p2c.thelife.Resources");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_resources_deed_help);
			intent.putExtra("position", SlidingMenuSupport.RESOURCES_POSITION);
			intent.putExtra("home", "com.p2c.thelife.Resources");
			intent.putExtra("deed_id", m_deed.id);
			startActivity(intent);
		}
		
		return true;
	}

}
