package com.p2c.thelife;

import java.util.EnumSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.p2c.thelife.config.TheLifeConfiguration;
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
	
	// system settings key
	private static final String KEY_THRESHOLDS_FILTER = "thresholds";
	
	private ResourcesAdapter m_adapter = null;
	private TextView m_filterDescriptionView = null; 
    private String[] m_thresholdStrings = null;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_resources, SlidingMenuSupport.RESOURCES_POSITION);
					
		// use the remembered thresholds filter
		m_adapter = new ResourcesAdapter(this, retrieveThresholdsFilter());
		ExpandableListView activitiesView = (ExpandableListView)findViewById(R.id.deeds_list);		
		activitiesView.setAdapter(m_adapter);
		
		EnumSet<FriendModel.Threshold> thresholds = m_adapter.getFilter();		
		m_filterDescriptionView = (TextView)findViewById(R.id.filter_description);
		m_thresholdStrings = getResources().getStringArray(R.array.thresholds_medium_all);		
		m_filterDescriptionView.setText(getFilterDescription(m_thresholdStrings, thresholds));		
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
		
		storeThresholdsFilter();		
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
			showFilterDialog();
		} else if (item.getItemId() == android.R.id.home) {
			m_support.slideOpen();
		}		
		
		return true;
	}
	
	
	private void showFilterDialog() {
		
		// view to set filter
		LayoutInflater inflater = getLayoutInflater();		
		final View view = inflater.inflate(R.layout.dialog_filter_resources, null);
		
		// set the options in the filter
        EnumSet<FriendModel.Threshold> thresholds = m_adapter.getFilter();
        final CheckBox checkBoxes[] = new CheckBox[7];
        int checkBoxIds[] = new int[7];
        checkBoxIds[0] = R.id.checkBox0;
        checkBoxIds[1] = R.id.checkBox1;
        checkBoxIds[2] = R.id.checkBox2;
        checkBoxIds[3] = R.id.checkBox3;
        checkBoxIds[4] = R.id.checkBox4;
        checkBoxIds[5] = R.id.checkBox5;
        checkBoxIds[6] = R.id.checkBox6;
        for (int i = 0; i < checkBoxes.length; i++) {
        	checkBoxes[i] = (CheckBox)view.findViewById(checkBoxIds[i]);
        	checkBoxes[i].setText(m_thresholdStrings[i]);
        	checkBoxes[i].setChecked(thresholds.contains(FriendModel.thresholdValues[i]));
        }
        
		// show the dialog
		final AlertDialog dialog = new AlertDialog.Builder(this)
			.setMessage(getResources().getString(R.string.threshold_filter_prompt))
			.setView(view)
			.setNegativeButton(R.string.cancel, null)
			.setPositiveButton(R.string.filter, new DialogInterface.OnClickListener() {
			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					// set the threshold filter
					EnumSet<FriendModel.Threshold> thresholds = EnumSet.noneOf(FriendModel.Threshold.class);
					for (int i = 0; i < checkBoxes.length; i++) {
						if (checkBoxes[i].isChecked()) {
							thresholds.add(FriendModel.thresholdValues[i]);
						}
					}
					m_adapter.filter(thresholds);
					
					// set the filter description
					m_filterDescriptionView.setText(getFilterDescription(m_thresholdStrings, thresholds));
				}
			}).create();
		
		// enable/disable the filter based on checkboxes
		for (CheckBox checkbox: checkBoxes) {
			checkbox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CheckBox checkbox = (CheckBox)v;
					if (checkbox.isChecked()) {
						enableFilter(dialog, true);
					} else {
						for (CheckBox cb: checkBoxes) {
							if (cb.isChecked()) {
								return;
							}
						}
						enableFilter(dialog, false);
					}
				}
			});
        }	
		
		// support the all/nothing switch
		final CheckBox allThresholds = (CheckBox)view.findViewById(R.id.allThresholdsSelector);
		allThresholds.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (CheckBox checkBox: checkBoxes) {
					checkBox.setChecked(allThresholds.isChecked());
				}
				
				// enable/disable the filter based on the all/nothing switch
				enableFilter(dialog, allThresholds.isChecked());				
			}
		});
		allThresholds.setChecked(thresholds.size() == m_thresholdStrings.length);		
		
		dialog.show();
	}
	
	
	private String getFilterDescription(String[] thresholdStrings, EnumSet<FriendModel.Threshold> thresholds) {
        if (thresholds.size() == thresholdStrings.length) {
        	return getResources().getString(R.string.all_thresholds);
        } else if (thresholds.size() == 0) {
        	return getResources().getString(R.string.no_thresholds);
        } else if (thresholds.size() == 1) {
        	FriendModel.Threshold threshold = thresholds.iterator().next();
        	return thresholdStrings[threshold.ordinal()];
        } else {
        	return getResources().getString(R.string.multiple_thresholds);
        }
	}
	
	
	private void enableFilter(AlertDialog dialog, boolean isEnabled) {
		Button filterButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
		filterButton.setEnabled(isEnabled);
	}
	
	
	/**
	 * @return the thresholds filter
	 */
	private EnumSet<FriendModel.Threshold> retrieveThresholdsFilter() {

		// default is all filters
		EnumSet<FriendModel.Threshold> thresholds = EnumSet.allOf(FriendModel.Threshold.class);
		
		// look for a thresholds filter in system settings
		String thresholdsString = TheLifeConfiguration.getSystemSettings().getString(KEY_THRESHOLDS_FILTER, null);
		if (thresholdsString != null) {
			thresholds = EnumSet.noneOf(FriendModel.Threshold.class);			
			String[] thresholdIntStrings = thresholdsString.split(",");
			for (String thresholdIntString : thresholdIntStrings) {
				try {
					int thresholdInt = Integer.valueOf(thresholdIntString);
					thresholds.add(FriendModel.Threshold.values()[thresholdInt]);
				} catch (Exception e) {
					Log.e(TAG, "Incorrect threshold filter " + thresholdsString, e);
				}
			}
		}
		
		return thresholds;
	}
	
	
	/**
	 * store the thresholds filter so that the user won't need to recreate it later
	 */
	private void storeThresholdsFilter() {
		
		// store the thresholds filter in system settings
		SharedPreferences.Editor systemSettingsEditor = TheLifeConfiguration.getSystemSettings().edit();
		StringBuilder thresholdsString = new StringBuilder(25);		
		EnumSet<FriendModel.Threshold> thresholds = m_adapter.getFilter();
		for (FriendModel.Threshold threshold : thresholds) {
			if (thresholdsString.length() > 0) {
				thresholdsString.append(',');
			}
			thresholdsString.append(threshold.ordinal());
		}		
		systemSettingsEditor.putString(KEY_THRESHOLDS_FILTER, thresholdsString.toString());
		systemSettingsEditor.commit();
	}
	
}