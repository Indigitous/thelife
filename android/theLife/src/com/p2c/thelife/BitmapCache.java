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

//	/**
//	 * Get the bitmap at the given URL.
//	 * Note: must not be called on the main/UI thread.
//	 * @param urlString
//	 * @return BitmapDrawable
//	 * @throws Exception
//	 */
//	public static Bitmap getBitmapAtURL(String urlString) throws Exception {
//		
//		Exception exception = null;
//		InputStream is = null;
//		HttpURLConnection connection = null;
//		
//		try {
//			URL url = new URL(TheLifeApplication.SERVER_URL + urlString);
//			connection = (HttpURLConnection)url.openConnection();
//			is =  new BufferedInputStream(connection.getInputStream());
//			return BitmapFactory.decodeStream(is);
//		} catch (Exception e) {
//			exception = e;
//		} finally {
//			if (is != null) {
//				is.close();
//			}
//			if (connection != null) {
//				connection.disconnect();
//			}
//		}
//		
//		if (exception != null) {
//			throw exception;
//		}
//		
//		return null; // this line is only to satisfy the compiler; will not be executed
//	}

	/**
	 * Get the bitmap at the given URL.
	 * Note: must not be called on the main/UI thread. 
	 * @param urlString
	 * @return the bitmap or null if any exception
	 */
	public static Bitmap getBitmapAtURLSafe(String urlString) {
		
		InputStream is = null;
		HttpURLConnection connection = null;
		Bitmap bitmap = null;
		OutputStream os = null;
		
		try {
			
			// attempt to get the bitmap from the server
			URL url = new URL(TheLifeApplication.SERVER_URL + urlString);
			connection = (HttpURLConnection)url.openConnection();
			is =  new BufferedInputStream(connection.getInputStream());
			bitmap = BitmapFactory.decodeStream(is);
			
			// save the bitmap to cache
			if (bitmap != null) {
				os = new BufferedOutputStream(   new FileOutputStream(TheLifeApplication.cacheDirectory + urlString));
				bitmap.compress(CompressFormat.PNG, 90, os);
				os.close();
			}
			
		} catch (Exception e) {
			Log.e(TAG, "getBitmapAtURLSafe()", e);
		} finally {
			if (is != null) {
				try { is.close(); } catch (IOException e) { e.printStackTrace(); }
				try { os.close(); } catch (IOException e) { e.printStackTrace(); }
				
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		
		return bitmap;
	}
	
	
	/**
	 * Get the bitmap from the server if permitted and if available.
	 * @param urlString		where to get bitmap
	 * @param useServer		can only be true if not on UI thread
	 * @param fallbackBitmap
	 * @return
	 */
	public static Bitmap getBitmapFromSystem(String url, boolean useServer, Bitmap fallbackBitmap) {
		Bitmap bitmap = null;
		
		if (url != null) {
			// first try to find the bitmap in the disk cache
			if (new File(TheLifeApplication.cacheDirectory + url).exists()) {
				bitmap = BitmapFactory.decodeFile(TheLifeApplication.cacheDirectory + url);
			}
		
			// if not in the disk cache and if permitted, get the bitmap from the server
			if (bitmap == null && useServer) {
				bitmap = BitmapCache.getBitmapAtURLSafe(url);
			}
		}
		
		// use generic image if no image is available
		if (bitmap == null) {
			bitmap = TheLifeApplication.genericDeedImage;
		}
		
		return bitmap;
	}
	

}
