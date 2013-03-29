package com.p2c.thelife;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.p2c.thelife.model.CategoriesDS;
import com.p2c.thelife.model.DeedsDS;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.FriendsDS;
import com.p2c.thelife.model.GroupsDS;
import com.p2c.thelife.model.UsersDS;


// TODO: divide the final and initialize-once values from this class, to avoid dependencies on application
/**
 * Initialize the application.
 * @author clarence
 *
 */
public class TheLifeApplication extends Application {
	
	private static final String TAG = "TheLifeConfiguration";
		
	/**
	 * Set up the configuration values for the application.
	 */
	public void onCreate() {
		super.onCreate();
			
		Log.e(TAG, "onCreate()");  // TODO for debugging only
			
		// initialize configuration from system settings
		SharedPreferences systemSettings = 
			getApplicationContext().getSharedPreferences("system_prefs", Context.MODE_PRIVATE);
		TheLifeConfiguration.setSystemSettings(systemSettings);
		
		// initialize stock images before initializing data stores
		TheLifeConfiguration.genericPersonImage = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_help));
		TheLifeConfiguration.genericPersonThumbnail = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_help));
		TheLifeConfiguration.genericDeedImage = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_help));
		TheLifeConfiguration.missingDataImage = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_search));
		TheLifeConfiguration.missingDataThumbnail = Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_search));
		
		TheLifeConfiguration.cacheDirectory = getApplicationContext().getCacheDir().getAbsolutePath() + "/";
		
		// initialize the data stores, reading from cache if available
		TheLifeConfiguration.setCategoriesDS(new CategoriesDS(getApplicationContext()));				
		TheLifeConfiguration.setDeedsDS(new DeedsDS(getApplicationContext()));
		TheLifeConfiguration.setUsersDS(new UsersDS(getApplicationContext()));
		TheLifeConfiguration.setGroupsDS(new GroupsDS(getApplicationContext()));
		TheLifeConfiguration.setFriendsDS(new FriendsDS(getApplicationContext()));		
		TheLifeConfiguration.setEventsDS(new EventsDS(getApplicationContext()));
	}


}
