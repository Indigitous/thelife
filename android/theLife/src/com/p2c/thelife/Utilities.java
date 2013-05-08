package com.p2c.thelife;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.util.CharArrayBuffer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;


/**
 * General purpose routines.
 * 
 * @author clarence
 *
 */
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
	 * @return the string from the file, or null if an error
	 */
	public static String readBufferedStream(InputStreamReader is) {

		String streamData = null;
		
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
			
			streamData = new String(jsonBuffer.buffer(), 0, totalBytesRead);
		} catch (IOException e) {
			Log.e(TAG, "readBufferedStream()", e);
		}
		
		return streamData;
	}		

	
	public static String makeServerUrlStringDebug(String server, String urlPath) {
		return server + urlPath + "?authentication_token=" + TheLifeConfiguration.getOwnerDS().getToken();
	}
	
	public static String makeServerUrlString(String urlPath) {
		return TheLifeConfiguration.getServerUrl() + urlPath + "?authentication_token=" + TheLifeConfiguration.getOwnerDS().getToken();
	}
	
	public static String makeServerUrlStringNoToken(String urlPath) {
		return TheLifeConfiguration.getServerUrl() + urlPath;
	}	
	
	
	public static boolean isSuccessfulHttpCode(int httpCode) {
		return (httpCode >= 200) && (httpCode <= 299);
	}
	
	
	public static void showConnectionErrorToast(Context context, String text, int duration) {
		Toast toast = new Toast(context);
		TextView textView = new TextView(context);
		textView.setText(text);
		textView.setBackgroundColor(Color.RED);
		textView.setTextColor(Color.WHITE);
		toast.setView(textView);
		toast.show();
		toast = null;
	}	
	
	
	public static void showErrorToast(Context context, String text, int duration) {
		Toast toast = new Toast(context);
		TextView textView = new TextView(context);
		textView.setText(text);
		textView.setBackgroundColor(Color.RED);
		textView.setTextColor(Color.WHITE);
		toast.setView(textView);
		toast.show();
		toast = null;
	}
}
