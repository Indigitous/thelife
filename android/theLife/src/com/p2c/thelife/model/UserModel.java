package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.p2c.thelife.BitmapCacheHandler;
import com.p2c.thelife.Utilities;
import com.p2c.thelife.config.TheLifeConfiguration;



/**
 * User data model.
 * @author clarence
 *
 */
public class UserModel extends AbstractModel {
	
	private static final String TAG = "UserModel";
	
	public String   firstName;
	public String   lastName;
	public String   email;
	public String   mobile;
	public String   provider;
	
	public UserModel(int user_id, String firstName, String lastName, String email, String mobile, String provider) {
		
		super(user_id);

		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobile = mobile;
		this.provider = provider;
	}
	
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	@Override
	public String toString() {
		return id + ", " + firstName + ", " + lastName + ", " + email + ", " + mobile;
	}
	
	/**
	 * Will attempt to load the image or use the placeholder.
	 * @param id
	 * @return
	 */
	public static Bitmap getImage(int id) {
		return BitmapCacheHandler.getBitmapFromSystem("users", id, "image", TheLifeConfiguration.getGenericPersonImage());
	}
	
	/**
	 * @param id	user id
	 * @return whether or not the user's image bitmap is in the cache
	 */
	public static boolean isInCache(int id) {
		return BitmapCacheHandler.hasBitmapInCache("users", id, "image");
	}
	
	/**
	 * Will attempt to load the image or use the placeholder.
	 * @param id
	 * @return
	 */
	public static Bitmap getThumbnail(int id) {
		return BitmapCacheHandler.getBitmapFromSystem("users", id, "thumbnail", TheLifeConfiguration.getGenericPersonThumbnail());
	}		
	
	public static UserModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
							
		// create the user
		return new UserModel(
			json.getInt("id"),
			json.getString("first_name"),
			json.getString("last_name"),	
			json.optString("email", ""),
			Utilities.getOptionalField("mobile", json),
			Utilities.getOptionalField("provider", json)
		);
	}
	
	
	public JSONObject toJSON() {
		
		JSONObject json = null;
		try {
			json = new JSONObject();
			json.put("id", id);
			json.put("first_name", firstName);
			json.put("last_name", lastName);
			json.put("email", email);
			json.put("mobile", mobile);
			json.put("provider", provider);
		} catch (JSONException e) {
			Log.e(TAG, "toJSON()", e);
			json = null;
		}
		
		return json;
	}
	
	
	/**
	 * Read optional fields from the JSON, and if present, change the current object. 
	 * @param json
	 */
	public void setFromPartialJSON(JSONObject json) {
		String newEmail = json.optString("email",  null);
		if (newEmail != null) {
			this.email = newEmail; 
		}
		String newMobile = Utilities.getOptionalField("mobile", json);
		if (newMobile != null) {
			this.mobile = newMobile;
		}
		String newFirstName = json.optString("first_name", null);
		if (newFirstName != null) {
			this.firstName = newFirstName; 
		}
		String newLastName = json.optString("last_name", null);
		if (newLastName != null) {
			this.lastName = newLastName;
		}
		String newProvider = Utilities.getOptionalField("provider", json);
		if (newProvider != null) {
			this.provider = newProvider;
		}		
	}
}
