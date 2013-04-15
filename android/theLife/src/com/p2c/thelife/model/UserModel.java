package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.p2c.thelife.BitmapCache;
import com.p2c.thelife.TheLifeConfiguration;



// POJO - plain old java object
public class UserModel extends AbstractModel {
	
	private static final String TAG = "UserModel";
	
	public String   firstName;
	public String   lastName;
	public Bitmap   image;  	// TODO is this an image id, image or what?
	public Bitmap   thumbnail;
	public String   email;
	public String   phone;
	
	public UserModel(int user_id, String firstName, String lastName, Bitmap image, Bitmap thumbnail, String email, String phone) {
		
		super(user_id);

		this.firstName = firstName;
		this.lastName = lastName;
		
		if (image == null) {
			this.image = TheLifeConfiguration.getGenericPersonImage();
		} else {
			this.image = image;
		}
		if (thumbnail == null) {
			this.thumbnail = TheLifeConfiguration.getGenericPersonThumbnail();
		} else {
			this.thumbnail = thumbnail;
		}
		
		this.email = email;
		this.phone = phone;
	}
	
	public UserModel(int user_id, String firstName, String lastName, Bitmap image, String email, String phone) {
		this(user_id, firstName, lastName, image, image, email, phone);
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	@Override
	public String toString() {
		return id + ", " + firstName + ", " + lastName + ", " + email + ", " + phone;
	}
	
	/**
	 * Will attempt to load the image or use the placeholder.
	 * @param id
	 * @param useServer
	 * @return
	 */
	public static Bitmap getImage(int id, boolean useServer) {
		return BitmapCache.getBitmapFromSystem("users", id, "image", useServer, TheLifeConfiguration.getGenericPersonImage());
	}
	
	/**
	 * Will attempt to load the image or use the placeholder.
	 * @param id
	 * @param useServer
	 * @return
	 */
	public static Bitmap getThumbnail(int id, boolean useServer) {
		return BitmapCache.getBitmapFromSystem("users", id, "thumbnail", useServer, TheLifeConfiguration.getGenericPersonThumbnail());
	}		
	
	public static UserModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
							
		// create the deed
		int id = json.getInt("id");		
		return new UserModel(
			id,
			json.getString("first_name"),
			json.getString("last_name"),
			getImage(id, useServer),
			getThumbnail(id, useServer),			
			json.optString("email", ""),
			json.optString("phone", "")
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
		String newPhone = json.optString("phone", null);
		if (newPhone != null) {
			this.phone = newPhone;
		}
		String newFirstName = json.optString("first_name", null);
		if (newFirstName != null) {
			this.firstName = newFirstName; 
		}
		String newLastName = json.optString("last_name", null);
		if (newLastName != null) {
			this.lastName = newLastName;
		}
	}
}
