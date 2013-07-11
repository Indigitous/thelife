package com.p2c.thelife;

import java.util.EnumSet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;
import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

/**
 * Show all the deeds/activities.
 * @author clarence
 *
 */
public class ResourcesActivity extends SlidingMenuPollingFragmentActivity implements DSRefreshedListener {
	
	private static final String TAG = "ResourcesActivity";
	
	private ResourcesAdapter m_adapter = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_resources, SlidingMenuSupport.RESOURCES_POSITION);
					
		ExpandableListView activitiesView = (ExpandableListView)findViewById(R.id.deeds_list);
		m_adapter = new ResourcesAdapter(this);
		activitiesView.setAdapter(m_adapter);
	}
	
	/**
	 * Activity in view, so start the data store refresh mechanism.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// load the data store from the server in the background
		// note that categories and deeds are closely related, so first refresh the categories and then the deeds
		TheLifeConfiguration.getCategoriesDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getCategoriesDS().addDSRefreshedListener(this);
		TheLifeConfiguration.getDeedsDS().addDSChangedListener(m_adapter);
		TheLifeConfiguration.getCategoriesDS().refresh(null); // first refresh categories, then refresh deeds in the notifyDSRefreshed callback		
	}	
	
	
	/**
	 * Called when the data store refresh has completed.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		TheLifeConfiguration.getDeedsDS().refresh(null);
	}			
	
	
	/**
	 * Activity out of view, so stop the data store refresh mechanism.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		TheLifeConfiguration.getDeedsDS().removeDSChangedListener(m_adapter);				
		TheLifeConfiguration.getCategoriesDS().removeDSRefreshedListener(this);
		TheLifeConfiguration.getCategoriesDS().removeDSChangedListener(m_adapter);
	}		
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.resources, menu);
		return true;
	}
	
	
	/**
	 * Deed/activity has been selected.
	 * @param view
	 * @return
	 */
	public boolean selectDeed(View view) {
	
		// get the deed associated with this view
		DeedModel deed = (DeedModel)view.getTag();
		
		Intent intent = new Intent("com.p2c.thelife.ResourcesDeed");
		intent.putExtra("deed_id", deed.id);
		startActivity(intent);
		
		return true;
	}	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.action_help) {
			Intent intent = new Intent("com.p2c.thelife.HelpContainer");
			intent.putExtra("layout", R.layout.activity_resources_help);
			intent.putExtra("position", SlidingMenuSupport.RESOURCES_POSITION);
			intent.putExtra("home", "com.p2c.thelife.Resources");
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_filter) {
			
			// view to set filter
			LayoutInflater inflater = getLayoutInflater();		
			final View view = inflater.inflate(R.layout.dialog_filter_resources, null);
			
			// options in the filter
	        String[] thresholdStrings = getResources().getStringArray(R.array.thresholds_medium_all);
	        CheckBox checkBox;
	        checkBox = (CheckBox)view.findViewById(R.id.checkBox1);
	        checkBox.setText(thresholdStrings[0]);
	        checkBox = (CheckBox)view.findViewById(R.id.checkBox2);
	        checkBox.setText(thresholdStrings[1]);
	        checkBox = (CheckBox)view.findViewById(R.id.checkBox3);
	        checkBox.setText(thresholdStrings[2]);
	        checkBox = (CheckBox)view.findViewById(R.id.checkBox4);
	        checkBox.setText(thresholdStrings[3]);
	        checkBox = (CheckBox)view.findViewById(R.id.checkBox5);
	        checkBox.setText(thresholdStrings[4]);
	        checkBox = (CheckBox)view.findViewById(R.id.checkBox6);
	        checkBox.setText(thresholdStrings[5]);
	        checkBox = (CheckBox)view.findViewById(R.id.checkBox7);
	        checkBox.setText(thresholdStrings[6]);
			
			new AlertDialog.Builder(this)
				.setMessage(getResources().getString(R.string.threshold_filter_prompt))
				.setView(view)
				.setNegativeButton(R.string.cancel, null)
				.setPositiveButton(R.string.filter, new DialogInterface.OnClickListener() {
				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						EnumSet<FriendModel.Threshold> thresholds = EnumSet.noneOf(FriendModel.Threshold.class);
						thresholds.add(FriendModel.Threshold.Curious);
						m_adapter.filter(thresholds);
						
					}
				}).show();
		} else if (item.getItemId() == android.R.id.home) {
			m_support.slideOpen();
		}		
		
		return true;
	}
}