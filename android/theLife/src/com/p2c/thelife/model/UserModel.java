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
	
	public static UserModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
					
		// create the deed
		String imageUrl = json.optString("image_url", null);		
		return new UserModel(
			json.getInt("id"),
			json.getString("first_name"),
			json.getString("last_name"),
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeConfiguration.getGenericPersonImage()),
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeConfiguration.getGenericPersonThumbnail()),			
			json.optString("email", ""),
			json.optString("phone", "")
		);
	}	

}
