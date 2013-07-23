package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.p2c.thelife.BitmapCacheHandler;
import com.p2c.thelife.TheLifeConfiguration;
import com.p2c.thelife.Utilities;



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
