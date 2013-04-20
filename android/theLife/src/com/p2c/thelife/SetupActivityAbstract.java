package com.p2c.thelife;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.p2c.thelife.model.AbstractDS.DSRefreshedListener;

/**
 * Superclass for setup (login/register activities).
 * @author clarence
 *
 */
public abstract class SetupActivityAbstract extends FragmentActivity implements DSRefreshedListener {
	
	private static final String TAG = "BaseSetupActivity";
	
	protected ProgressDialog m_progressDialog = null;

	
	protected void closeProgressBar() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
			m_progressDialog = null;
		}	
	}		
	
	
	/**
	 * Full refresh of the data stores.
	 */
	protected void fullRefresh() {	
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}
		m_progressDialog = ProgressDialog.show(this, getResources().getString(R.string.waiting), getResources().getString(R.string.retrieving_configuration), true, true);		
		
		TheLifeConfiguration.getCategoriesDS().addDSRefreshedListener(this);
		TheLifeConfiguration.getCategoriesDS().forceRefresh("categories");
	}

	
	/**
	 * Chain together the data stores' refresh callbacks so that all data stores are refreshed sequentially.
	 * Not pretty but it works.
	 */
	@Override
	public void notifyDSRefreshed(String indicator) {
		if (indicator.equals("categories")) {
			TheLifeConfiguration.getCategoriesDS().removeDSRefreshedListener(this);
			TheLifeConfiguration.getDeedsDS().addDSRefreshedListener(this);			
			TheLifeConfiguration.getDeedsDS().forceRefresh("deeds");
		} else if (indicator.equals("deeds")) {
			TheLifeConfiguration.getDeedsDS().removeDSRefreshedListener(this);			
//			TheLifeConfiguration.getUsersDS().addDSRefreshedListener(this);			
//			TheLifeConfiguration.getUsersDS().forceRefresh("users");
//		} else if (indicator.equals("users")) {
//			TheLifeConfiguration.getUsersDS().removeDSRefreshedListener(this);			
			TheLifeConfiguration.getGroupsDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getGroupsDS().forceRefresh("groups");	
		} else if (indicator.equals("groups")) {
			TheLifeConfiguration.getGroupsDS().removeDSRefreshedListener(this);			
			TheLifeConfiguration.getFriendsDS().addDSRefreshedListener(this);
			TheLifeConfiguration.getFriendsDS().forceRefresh("friends");	
		} else if (indicator.equals("friends")) {
			TheLifeConfiguration.getFriendsDS().removeDSRefreshedListener(this);			
			TheLifeConfiguration.getEventsDS().addDSRefreshedListener(this);			
			TheLifeConfiguration.getEventsDS().forceRefresh("events");
		} else if (indicator.equals("events")) {
			TheLifeConfiguration.getEventsDS().removeDSRefreshedListener(this);			
			if (m_progressDialog != null) {
				m_progressDialog.dismiss();
				
				// go to the main screen
				Intent intent = new Intent("com.p2c.thelife.EventsForCommunity");
				startActivity(intent);
				return;				
			}					
		} else {
			Log.wtf(TAG, "unknown refresh indicator " + indicator);
		}
	}

}
