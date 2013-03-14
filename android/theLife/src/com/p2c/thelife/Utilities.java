package com.p2c.thelife;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;

public class Utilities {
	
	private static final String TAG = "Utilities";
	
	public static String fillTemplateString(FriendModel friend, String template_string) {
		return template_string.replace("$f", friend.first_name);
	}
	
	public static String fillTemplateString(UserModel user, FriendModel friend, String template_string) {
		String s = template_string.replace("$u", user.first_name);
		return s.replace("$f", friend.first_name);
	}
	
	/**
	 * Get the bitmap at the given URL.
	 * Note: cannot be called on the main/UI thread.
	 * @param urlString
	 * @return BitmapDrawable
	 * @throws Exception
	 */
	public static Bitmap getBitmapAtURL(String urlString) throws Exception {
		
		Exception exception = null;
		InputStream is = null;
		HttpURLConnection connection = null;
		
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection)url.openConnection();
			is =  new BufferedInputStream(connection.getInputStream());
			return BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			exception = e;
		} finally {
			if (is != null) {
				is.close();
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		
		if (exception != null) {
			throw exception;
		}
		
		return null; // this line is only to satisfy the compiler; will not be executed
	}
	
	public static Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		} else {
			throw new IllegalArgumentException();
		}
			
	}

}
