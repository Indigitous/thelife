package com.p2c.thelife;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Caching for bitmaps.
 * @author clarence
 *
 */
public class BitmapCache {
	
	private static final String TAG = "BitmapCache";
	
	private static String getCacheFileName(String dataType, int id, String type) {
		return TheLifeConfiguration.getCacheDirectory() + dataType + String.valueOf(id) + type + ".png";
	}

	/**
	 * Get the bitmap at the given URL and, if successful, write it to disk cache. Will not throw an exception.
	 * Note: must not be called on the main/UI thread. 
	 * @param urlString
	 * @return the bitmap or null if any exception
	 */
	private static Bitmap getBitmapAtURLSafe(String urlPath, String cacheFileName) {
		
		InputStream is = null;
		HttpURLConnection connection = null;
		Bitmap bitmap = null;
		OutputStream os = null;
		
		try {
			
			// attempt to get the bitmap from the server
			URL url = new URL(Utilities.makeServerUrlString(urlPath));
			connection = (HttpURLConnection)url.openConnection();
			is =  new BufferedInputStream(connection.getInputStream());
			bitmap = BitmapFactory.decodeStream(is);
			
			// save the bitmap to cache
			if (bitmap != null) {
				os = new BufferedOutputStream(new FileOutputStream(cacheFileName));
				bitmap.compress(CompressFormat.PNG, 90, os);
				os.close();
			}
			
		} catch (Exception e) {
			Log.e(TAG, "getBitmapAtURLSafe() ", e); //  + e.getMessage() + e.getCause());
		} finally {
			if (is != null) {
				try { is.close(); } catch (IOException e) { }
			}
			if (os != null) {
				try { os.close(); } catch (IOException e) { }
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		
		return bitmap;
	}
	
	
	/**
	 * Get the bitmap from the server if permitted and if available.
	 * @param dataType		dataType of bitmap: "friends", "users" or "activities"
	 * @param id			model id
	 * @param type			type of bitmap: "image" or "thumbnail"
	 * @param useServer		can only be true if not on UI thread
	 * @param fallbackBitmap
	 * @return
	 */
	public static Bitmap getBitmapFromSystem(String dataType, int id, String type, boolean useServer, Bitmap fallbackBitmap) {
		Bitmap bitmap = null;
		
		if (id != 0) {
			// first try to find the bitmap in the disk cache
			String fileName = getCacheFileName(dataType, id, type);
			if (new File(fileName).exists()) {
				bitmap = BitmapFactory.decodeFile(fileName);
			}
		
			// if not in the disk cache and if permitted, get the bitmap from the server
			if (bitmap == null && useServer) {
				bitmap = BitmapCache.getBitmapAtURLSafe(dataType + "/" + String.valueOf(id) + "/" + type, fileName);
			}
			
		}
		
		// use fall back if no image is available
		if (bitmap == null) {
			bitmap = fallbackBitmap;
		}
		
		return bitmap;
	}
	

}
