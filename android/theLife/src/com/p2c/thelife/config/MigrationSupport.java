package com.p2c.thelife.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class MigrationSupport {
	
	private static final String TAG = "MigrationSupport";
	
	private static final String SYSKEY_VERSION_CODE = "app_version_code";

	
	public static int getAppVersionCode() {
		return TheLifeConfiguration.getSystemSettings().getInt(SYSKEY_VERSION_CODE, 0);
	}
	
	
	public static void setAppVersionCode(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			int versionCode = info.versionCode;
			SharedPreferences.Editor editor = TheLifeConfiguration.getSystemSettings().edit();
			editor.putInt(SYSKEY_VERSION_CODE, versionCode);
			editor.commit();
		} catch (Exception e) {
			Log.e(TAG, "setAppVersionCode()", e);
		}
	}
	
	/**
	 * @return whether or not the app has been updated
	 */
	public static boolean hasUpdated(Context context) {
		boolean updated = true;
		
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);		
			updated = (info.versionCode != getAppVersionCode());
		} catch (Exception e) {
			Log.e(TAG, "hasUpdated()", e);
		}
		
		return updated;
	}	

}
