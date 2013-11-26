package com.p2c.thelife;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.CharArrayBuffer;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.p2c.thelife.config.TheLifeConfiguration;
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
	

	/**
	 * Template support.
	 * @param resources
	 * @param friend
	 * @param templateString
	 * @return
	 */
	public static String fillTemplateString(Resources resources, FriendModel friend, String templateString) {
		return templateString.replace("$f", (friend == null || friend.getFirstNameOrLastName() == null) ? resources.getString(R.string.friend) : friend.getFirstNameOrLastName());
	}


	/**
	 * Template support.
	 * @param resources
	 * @param user
	 * @param friend
	 * @param templateString
	 * @return
	 */
	public static String fillTemplateString(Resources resources, UserModel user, FriendModel friend, String templateString) {
		String s = templateString.replace("$u", (user == null || user.firstName == null) ? resources.getString(R.string.user) : user.firstName);	
		return s.replace("$f", (friend == null || friend.getFirstNameOrLastName() == null) ? resources.getString(R.string.friend) : friend.getFirstNameOrLastName());
	}
	
	
	/**
	 * @param drawable
	 * @return the bitmap object from a drawable
	 */
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
	
	
	public static void showInfoToast(Context context, String text, int duration) {
		Toast.makeText(context, text, duration).show();
	}
	
	
	// Can be called from any thread
	public static void showInfoToastSafe(final Activity activity, final String text, final int duration) {
		activity.runOnUiThread(new Runnable() 
		{
			@Override
			public void run() {
				Toast.makeText(activity, text, duration).show();
			}
		});
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
	
	
	/**
	 * Handles "null" string values from the server.
	 */
	public static String getOptionalField(String key, JSONObject jsonObject) {
		String field = null;
		
		field = jsonObject.optString(key, null);
		if (field != null && field.equals("null")) {
			field = null;
		}		
		
		return field;
	}
	
	
	public static String getOptionalField(String key, Bundle bundle, String defaultValue) {
		return bundle.getString(key) != null ? bundle.getString(key) : defaultValue;
	}
	
	
	/**
	 * return the given value or empty string if it is null
	 */
	public static String getEmpty(String value) {
		return value != null ? value : "";
	}
	
	
	/**
	 * Must not be called on UI thread.
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
	
	
	/**
	 * Get a bitmap from a content provider.
	 * Must not be called on UI thread.
	 * @param context, for obtaining a content resolver
	 * @param uri
	 * @return bitmap sized for this app, or null if a problem
	 */
	public static Bitmap getExternalBitmap(Context context, Uri uri) {
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			// read bitmap from a content provider
			is = context.getContentResolver().openInputStream(uri);

			// first pass: just get the size of the image				
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, options);
			is.close();
			
			// second pass: get the scaled down version of the image
			is = context.getContentResolver().openInputStream(uri);
			options.inJustDecodeBounds = false;
			options.inSampleSize = Math.min(options.outHeight / TheLifeConfiguration.IMAGE_HEIGHT, options.outWidth / TheLifeConfiguration.IMAGE_WIDTH);
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (Exception e) {
			bitmap = null;
			Log.e(TAG, "getExternalBitmap()", e);
		} finally {
			if (is != null) {
				try { is.close(); } catch (Exception e) { }
				is = null;
			}
		}
		
		return bitmap;		
	}
	
	
	/**
	 * Get a bitmap from a URL.
	 * Must not be called on UI thread.
	 * @param urlString
	 * @return bitmap sized for this app, or null if a problem
	 */	
	public static Bitmap getExternalBitmap(String urlString) {
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			// read bitmap from a standard URL
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				is =  new BufferedInputStream(connection.getInputStream());
					
				// first pass: just get the size of the image				
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(is, null, options);
				is.close();
				connection.disconnect();
				
				// second pass: get the scaled down version of the image
				connection = (HttpURLConnection)url.openConnection();				
				is = new BufferedInputStream(connection.getInputStream());
				options.inJustDecodeBounds = false;
				options.inSampleSize = Math.min(options.outHeight / TheLifeConfiguration.IMAGE_HEIGHT, options.outWidth / TheLifeConfiguration.IMAGE_WIDTH);
				bitmap = BitmapFactory.decodeStream(is, null, options);
			} else {
				Log.e(TAG, "getExternalBitmap() HTTP code " + responseCode);
			}
		} catch (Exception e) {
			bitmap = null;
			Log.e(TAG, "getExternalBitmap()", e);
		} finally {
			if (is != null) {
				try { is.close(); } catch (Exception e) { }
				is = null;
			}
		}
		
		return bitmap;		
	}
	

	/**
	 * Get a string (example JSON) from a URL.
	 * Must not be called on UI thread.
	 * @param urlString
	 * @return string or null if any problem
	 */
	public static String getExternalString(String urlString) {
		InputStreamReader isr = null;
		String externalString = null;
		try {
			// read bitmap from a standard URL
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				isr = new InputStreamReader(connection.getInputStream());
				externalString = Utilities.readBufferedStream(isr);
			} else {
				Log.e(TAG, "getExternalString() HTTP code " + responseCode);
			}
		} catch (Exception e) {
			externalString = null;
			Log.e(TAG, "getExternalString()", e);
		} finally {
			if (isr != null) {
				try { isr.close(); } catch (Exception e) { }
				isr = null;
			}
		}
		
		return externalString;		
	}
	
	
	/**
	 * @param bitmap
	 * @return a square bitmap from the middle of the given bitmap
	 */
	public static Bitmap makeSquare(Bitmap bitmap) {
		
		if (bitmap != null) {
			int originalWidth = bitmap.getWidth();
			int originalHeight = bitmap.getHeight();
			
			if (originalWidth < originalHeight) {	
				// choose a square from the middle of the column
				int sideLength = originalWidth;
				int y = (originalHeight - sideLength) / 2;
				bitmap = Bitmap.createBitmap(bitmap, 0, y, sideLength, sideLength);
				
			} else if (originalHeight < originalWidth){
				// choose a square from the middle of the row
				int sideLength = originalHeight;
				int x = (originalWidth - sideLength) / 2;
				bitmap = Bitmap.createBitmap(bitmap, x, 0, sideLength, sideLength);
			}
		}
		
		return bitmap;
	}
	

	/**
	 * @param accessToken
	 * @return the URL for the owner's Facebook picture
	 */
	public static String makeOwnerFacebookPictureUrl(String accessToken) {
		return "https://graph.facebook.com/me/picture?access_token=" + accessToken +
				"&width=" + TheLifeConfiguration.IMAGE_WIDTH + "&height=" + TheLifeConfiguration.IMAGE_HEIGHT;		
	}
	
	
	/**
	 * @param facebookId
	 * @return the URL for the Facebook user's picture
	 */
	public static String makeFacebookUserPictureUrl(String facebookId) {
		return "https://graph.facebook.com/" + facebookId + "/picture" +
				"?width=" + TheLifeConfiguration.IMAGE_WIDTH + "&height=" + TheLifeConfiguration.IMAGE_HEIGHT;		
	}	
	
	
	/**
	 * @param facebookId
	 * @return the URL for the Facebook user's account info
	 */
	public static String makeFacebookUserAccountUrl(String facebookId) {
		return "https://graph.facebook.com/" + facebookId;		
	}		

}
