package com.p2c.thelife;
import android.app.Application;
import android.widget.Toast;

import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.CategoriesDS;
import com.p2c.thelife.model.DeedsDS;
import com.p2c.thelife.model.EventsDS;
import com.p2c.thelife.model.FriendsDS;
import com.p2c.thelife.model.GroupsDS;
import com.p2c.thelife.model.OwnerDS;
import com.p2c.thelife.model.RequestsDS;
import com.testflightapp.lib.TestFlight;


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
		
		// TestFlight
		TestFlight.takeOff(this, TheLifeConfiguration.TESTFLIGHT_APP_ID);
					
		// initialize configuration from system settings
		TheLifeConfiguration.loadSystemSettings(getApplicationContext());
		
		// initialize placeholder images and cache directory before initializing data stores
		TheLifeConfiguration.setGenericPersonImage(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.generic_avatar_image)));
		TheLifeConfiguration.setGenericPersonThumbnail(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.generic_avatar_thumbnail)));
		TheLifeConfiguration.setGenericDeedImage(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_help)));
		TheLifeConfiguration.setMissingDataImage(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_flynav)));
		TheLifeConfiguration.setMissingDataThumbnail(Utilities.getBitmapFromDrawable(getResources().getDrawable(R.drawable.action_flynav)));
		TheLifeConfiguration.setCacheDirectory(getApplicationContext().getCacheDir().getAbsolutePath() + "/");		
				
		// initialize the data stores, reading from cache if available
		TheLifeConfiguration.setOwnerDS(new OwnerDS());				
		TheLifeConfiguration.setCategoriesDS(new CategoriesDS(getApplicationContext(), null));				
		TheLifeConfiguration.setDeedsDS(new DeedsDS(getApplicationContext(), null));
		TheLifeConfiguration.setGroupsDS(new GroupsDS(getApplicationContext(), null));
		TheLifeConfiguration.setFriendsDS(new FriendsDS(getApplicationContext(), null));		
		TheLifeConfiguration.setEventsDS(new EventsDS(getApplicationContext(), null));
		TheLifeConfiguration.setRequestsDS(new RequestsDS(getApplicationContext(), null));
		
		// start the bitmap notifier and remember it (which is for the UI thread)
		BitmapNotifierHandler notifierHandler = new BitmapNotifierHandler();
		TheLifeConfiguration.setBitmapNotifier(notifierHandler);

		// start the bitmap cache handler (which is for the background thread)
		BitmapCacheHandlerStarter starter = new BitmapCacheHandlerStarter("bitmapcachehandlerstarter");
		starter.start();
				
		// make sure the handler is going, this call is supposed to block. 
		starter.getLooper();
		
		// really make sure the handler is going
		int i = 0;
		while (starter.getBitmapCacheHandler() == null && i < 20) {
			try { Thread.sleep(200); } catch (Exception e) { }
		}
		
		// error message if the handler couldn't start
		if (starter.getBitmapCacheHandler() == null) {
			Utilities.showErrorToast(this, "Unable to initialize Bitmap Cache Handler", Toast.LENGTH_LONG);
		}
		
		// remember the handler 
		TheLifeConfiguration.setBitmapCacheHandler(starter.getBitmapCacheHandler());
	}

}
