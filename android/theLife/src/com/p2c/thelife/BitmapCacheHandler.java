package com.p2c.thelife;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Caching for bitmaps.
 * 
 * The Handler retrieves requested images from the server in a background thread, and if found, notifies the UI.
 * @author clarence
 *
 */
public class BitmapCacheHandler extends Handler {
	
	private static final String TAG = "BitmapCacheHandler";
	
	// Handler Message.what choices
	public static final int OP_NULL = 0;
	public static final int OP_GET_USER_IMAGE_FROM_SERVER = 1;
	public static final int OP_GET_USER_THUMBNAIL_FROM_SERVER = 2;
	public static final int OP_GET_FRIEND_IMAGE_FROM_SERVER = 3;
	public static final int OP_GET_FRIEND_THUMBNAIL_FROM_SERVER = 4;
	public static final int OP_GET_ACTIVITY_IMAGE_FROM_SERVER = 5;
	public static final int OP_GET_ACTIVITY_THUMBNAIL_FROM_SERVER = 6;
	
	
	/**
	 * Generate the cache file name, including path, from the given values.
	 * @param dataType  	either "images", "friends" or "activities"
	 * @param id			model id
	 * @param imageType		either "image" or "thumbnail"
	 * @return
	 */
	public static String generateFullCacheFileName(String dataType, int id, String imageType) {
		imageType = "image"; // TODO only support image not thumbnail for now
		return TheLifeConfiguration.getCacheDirectory() + dataType + String.valueOf(id) + imageType + ".jpeg";
	}
	
	
	private static void saveBitmapToCache(String cacheFileName, Bitmap bitmap) {
		if (bitmap != null) {
			
			OutputStream os = null;
			try {
				os = new BufferedOutputStream(new FileOutputStream(cacheFileName));
				bitmap.compress(CompressFormat.JPEG, 90, os);
			} catch (Exception e) {
				Log.e(TAG, "saveBitmapToCache " + cacheFileName, e);
			} finally {
				try { if (os != null) os.close(); } catch (Exception e) { }
			}
			
		}
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
		
		try {
			
			// attempt to get the bitmap from the server
			URL url = new URL(Utilities.makeServerUrlString(urlPath));
			connection = (HttpURLConnection)url.openConnection();
			
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				is =  new BufferedInputStream(connection.getInputStream());
				bitmap = BitmapFactory.decodeStream(is);
				
				// save the bitmap to cache
				saveBitmapToCache(cacheFileName, bitmap);
			} else {
				Log.e(TAG, "getBitmapAtURLSafe() HTTP code " + responseCode);
			}
			
		} catch (Exception e) {
			Log.e(TAG, "getBitmapAtURLSafe() ", e); //  + e.getMessage() + e.getCause());
		} finally {
			if (is != null) {
				try { is.close(); } catch (IOException e) { }
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		
		return bitmap;
	}
	
	
	public static void saveBitmapToCache(String dataType, int id, String type, Bitmap bitmap) {
		type = "image"; // TODO only support image not thumbnail for now		
		String fileName = generateFullCacheFileName(dataType, id, type);
		saveBitmapToCache(fileName, bitmap);
	}
	
	/**
	 * Get the bitmap from the local file cache, if available.
	 * If not available, use the fallbackBitmap. 
	 * @param dataType		dataType of bitmap: "friends", "users" or "activities"
	 * @param id			model id
	 * @param imageType			type of bitmap: "image" or "thumbnail"
	 * @param fallbackBitmap
	 * @return
	 */
	public static Bitmap getBitmapFromSystem(String dataType, int id, String imageType, Bitmap fallbackBitmap) {
		imageType = "image"; // TODO only support image not thumbnail for now		
		Bitmap bitmap = null;
		
		if (id != 0) {
			// first try to find the bitmap in the disk cache
			String fileName = generateFullCacheFileName(dataType, id, imageType);
			if (new File(fileName).exists()) {
				bitmap = BitmapFactory.decodeFile(fileName);
			} else {
				// since the bitmap is not in the cache, ask the Handler to get the image from the server
				int what = OP_NULL;
				if (dataType.equals("users")) {
					what = OP_GET_USER_IMAGE_FROM_SERVER;
				} else if (dataType.equals("friends")) {
					what = OP_GET_FRIEND_IMAGE_FROM_SERVER;
				} else if (dataType.equals("activities")) {
					what = OP_GET_ACTIVITY_IMAGE_FROM_SERVER;
				} else {
					Log.e(TAG, "BAD datatype in getBitmapFromSystem " + dataType);
				}
				
				if (what != OP_NULL) {
					BitmapCacheHandler handler = TheLifeConfiguration.getBitmapCacheHandler();					
					Message message = handler.obtainMessage(what, id, 0);
					handler.sendMessage(message);
				}
			}
				
		}
		
		// use fall back if no image is available
		if (bitmap == null) {
			bitmap = fallbackBitmap;
		}
		
		return bitmap;
	}
	
	
	/** 
	 * Ensure that the owner image is in the file cache.
	 */
	public static void loadOwnerBitmap() {
		BitmapCacheHandler handler = TheLifeConfiguration.getBitmapCacheHandler();					
		Message message = handler.obtainMessage(OP_GET_USER_IMAGE_FROM_SERVER, TheLifeConfiguration.getOwnerDS().getUserId(), 0);
		handler.sendMessage(message);
	}
	
	
	/**
	 * Remove all the bitmaps in the cache.
	 */
	public static void removeAllBitmaps() {
		File cacheDirectory = new File(TheLifeConfiguration.getCacheDirectory());
		
		File cacheFiles[] = cacheDirectory.listFiles( new FilenameFilter() { 
			
			@Override
			public boolean accept(File dir, String filename) {
				// JPEG files and temporary JPEG files
				return filename.endsWith(".jpeg") || filename.endsWith(".jpeg_");
			}
			
		});
		
		for (File f:cacheFiles) {
			boolean wasDeleted = f.delete();
			if (!wasDeleted) {
				Log.e(TAG, "Unable to delete cache file " + f.getName());		
			}
		}
	}
	
	
	
	/**
	 * Runs in the Handler's thread, not on the UI thread.
	 * 		message.what = the operation to perform
	 * 		message.arg1 = the model id
	 */
	@Override
	public void handleMessage(Message message) {
				
		// parse the message
		String dataType = null;
		String imageType = null;
		switch (message.what) {
			case OP_GET_USER_IMAGE_FROM_SERVER:
			case OP_GET_USER_THUMBNAIL_FROM_SERVER:
				dataType = "users";
				imageType = "image";
				break;
			case OP_GET_FRIEND_IMAGE_FROM_SERVER:
			case OP_GET_FRIEND_THUMBNAIL_FROM_SERVER:
				dataType = "friends";
				imageType = "image";
				break;		
			case OP_GET_ACTIVITY_IMAGE_FROM_SERVER:
			case OP_GET_ACTIVITY_THUMBNAIL_FROM_SERVER:
				dataType = "activities";
				imageType = "image";
				break;
			default:
				Log.e(TAG, "Bad message in BitmapCacheHandler " + message.what);
				return;
		}
		int id = message.arg1;
		
		// see if the cache file is already there
		String cacheFileName = generateFullCacheFileName(dataType, id, imageType);
		if (!new File(cacheFileName).exists()) {
			
			// since the cache file is missing, get the image from the server and store it in a temporary file
			String temporaryCacheFileName = generateFullCacheFileName(dataType, id, imageType) + "_";
			Bitmap bitmap = BitmapCacheHandler.getBitmapAtURLSafe("image/" + dataType + "/" + String.valueOf(id), temporaryCacheFileName);
			
			if (bitmap != null) {
				// tell the UI about the new image cache file
				Message displayerMessage = TheLifeConfiguration.getBitmapNotifier().obtainMessage(message.what, message.arg1, 0, temporaryCacheFileName);
				TheLifeConfiguration.getBitmapNotifier().sendMessage(displayerMessage);
			}
		}
	}	

}
