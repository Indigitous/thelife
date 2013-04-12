package com.p2c.thelife;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.p2c.thelife.model.CategoriesDS;
import com.p2c.thelife.model.DeedsDS;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.FriendsDS;
import com.p2c.thelife.model.GroupsDS;
import com.p2c.thelife.model.RequestsDS;
import com.p2c.thelife.model.UsersDS;


/**
 * Initialize the application.
 * @author clarence
 *
 */
public class TheLifeApplication extends Application {
	
	private static final String TAG = "TheLifeApplication";
		
	/**
	 * Set up the application.
	 */
	public void onCreate() {
		super.onCreate();
					
		// initialize configuration from system settings
		SharedPreferences systemSettings = 
			getApplicationContext().getSharedPreferences("system_prefs", Context.MODE_PRIVATE);
		TheLifeConfiguration.loadSystemSettings(systemSettings);
		
		// initialize placeholder images before initializing data stores
		TheLifeConfiguration.setGenericPersonImage(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.generic_avatar_image)));
		TheLifeConfiguration.setGenericPersonThumbnail(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.generic_avatar_thumbnail)));
		TheLifeConfiguration.setGenericDeedImage(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_help)));
		TheLifeConfiguration.setMissingDataImage(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_flynav)));
		TheLifeConfiguration.setMissingDataThumbnail(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_flynav)));
				
		// initialize the data stores, reading from cache if available
		TheLifeConfiguration.setCacheDirectory(getApplicationContext().getCacheDir().getAbsolutePath() + "/");		
		TheLifeConfiguration.setCategoriesDS(new CategoriesDS(getApplicationContext()));				
		TheLifeConfiguration.setDeedsDS(new DeedsDS(getApplicationContext()));
		TheLifeConfiguration.setUsersDS(new UsersDS(getApplicationContext()));
		TheLifeConfiguration.setGroupsDS(new GroupsDS(getApplicationContext()));
		TheLifeConfiguration.setFriendsDS(new FriendsDS(getApplicationContext()));		
		TheLifeConfiguration.setEventsDS(new EventsDS(getApplicationContext()));
		TheLifeConfiguration.setRequestsDS(new RequestsDS(getApplicationContext()));
		
		// initialize the application wide polling and its notification listener
		TheLifeConfiguration.getRequestsDS().addDSChangedListener(new RequestsDSChangedListener(this));
		TheLifeConfiguration.setRequestsPoller(
			new RequestsPoller(TheLifeConfiguration.getRequestsDS(),
							   TheLifeConfiguration.REFRESH_REQUESTS_FIRST_DELTA,
							   TheLifeConfiguration.REFRESH_REQUESTS_DELTA));
	}

}
