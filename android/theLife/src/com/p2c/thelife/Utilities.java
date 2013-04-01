package com.p2c.thelife;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.util.CharArrayBuffer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;

public class Utilities {
	
	private static final String TAG = "Utilities";
	
	public static String fillTemplateString(Resources resources, FriendModel friend, String templateString) {
		return templateString.replace("$f", (friend == null || friend.firstName == null) ? resources.getString(R.string.friend) : friend.firstName);
	}
	
	public static String fillTemplateString(Resources resources, UserModel user, FriendModel friend, String templateString) {
		String s = templateString.replace("$u", (user == null || user.firstName == null) ? resources.getString(R.string.user) : user.firstName);	
		return s.replace("$f", (friend == null || friend.firstName == null) ? resources.getString(R.string.friend) : friend.firstName);
	}
	
	public static Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		} else {
			throw new IllegalArgumentException();
		}
			
	}
	
	
	/**
	 * Read from the given stream.
	 * Note that InputStreamReader is buffered.
	 * @param is
	 * @return the string containing JSON, or null if an error
	 */
	public static String readBufferedStream(InputStreamReader is) {

		String jsonString = null;
		
		try {
			char buffer[] = new char[1024];
			CharArrayBuffer jsonBuffer = new CharArrayBuffer(1024);			

			int numBytesRead = is.read(buffer, 0, 1024);
			int totalBytesRead = 0;
			while (numBytesRead != -1) {
				totalBytesRead += numBytesRead;
				jsonBuffer.append(buffer, 0, numBytesRead);
				numBytesRead = is.read(buffer);
			}
			
			jsonString = new String(jsonBuffer.buffer(), 0, totalBytesRead);
		} catch (IOException e) {
			Log.e(TAG, "readBufferedStream()", e);
		}
		
		return jsonString;
	}		

}
