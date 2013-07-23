package com.p2c.thelife;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.CharArrayBuffer;
import org.json.JSONObject;

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

	
	public static String makeServerUrlStringNoToken(String urlPath) {
		return TheLifeConfiguration.getServerUrl() + TheLifeConfiguration.getServerVersion() + urlPath;
	}
	
	
	public static String makeServerUrlString(String urlPath, String token) {
		if (token == null) {
			token = TheLifeConfiguration.getOwnerDS().getToken();
		}
		return makeServerUrlStringNoToken(urlPath) + "?authentication_token=" + token;
	}
	
	
	public static String makeServerUrlString(String urlPath) {
		return Utilities.makeServerUrlString(urlPath, TheLifeConfiguration.getOwnerDS().getToken());
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
	
	
	public static boolean hasData(String string) {
		return string != null && !string.isEmpty() && !string.trim().isEmpty(); 
	}
	
	
	public static String getOptionalField(String key, JSONObject jsonObject) {
		String field = null;
		
		field = jsonObject.optString(key, null);
		if (field != null && field.equals("null")) {
			field = null;
		}		
		
		return field;
	}
	
	
	/**
	 * @param serverURL
	 * @return the JSONArray read from the server, or null if there was a problem
	 */
	public static JSONObject readJSONFromServer(String serverURL) throws Exception {
		JSONObject jsonArray = null;
		InputStreamReader isr = null;
		
		try {
			URL userInfoURL = new URL(serverURL);
			HttpURLConnection connection = (HttpURLConnection)userInfoURL.openConnection();
			connection.setConnectTimeout(TheLifeConfiguration.HTTP_SERVER_CONNECTION_TIMEOUT);
			connection.setReadTimeout(TheLifeConfiguration.HTTP_READ_TIMEOUT);
			
			if (isSuccessfulHttpCode(connection.getResponseCode())) {
				String jsonString = null;
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					isr = new InputStreamReader(connection.getInputStream());
					jsonString = Utilities.readBufferedStream(isr);
					jsonArray = new JSONObject(jsonString);						
				}
			}
		} finally {
			if (isr != null) {
				try { isr.close(); } catch (Exception e) { }
			}
		}
		
		return jsonArray;
	}

}
