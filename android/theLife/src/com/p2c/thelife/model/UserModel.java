package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.p2c.thelife.BitmapCache;
import com.p2c.thelife.TheLifeApplication;



// POJO - plain old java object
public class UserModel extends AbstractModel {
	
	private static final String TAG = "UserModel";
	
	public String   first_name;
	public String   last_name;
	public Bitmap   image;  	// TODO is this an image id, image or what?
	public Bitmap   thumbnail;
	public String   email;
	public String   phone;
	
	public UserModel(int user_id, String first_name, String last_name, Bitmap image, Bitmap thumbnail, String email, String phone) {
		
		super(user_id);

		this.first_name = first_name;
		this.last_name = last_name;
		
		if (image == null) {
			this.image = TheLifeApplication.genericPersonImage;
		} else {
			this.image = image;
		}
		if (thumbnail == null) {
			this.thumbnail = TheLifeApplication.genericPersonThumbnail;
		} else {
			this.thumbnail = thumbnail;
		}
		
		this.email = email;
		this.phone = phone;
	}
	
	public UserModel(int user_id, String first_name, String last_name, Bitmap image, String email, String phone) {
		this(user_id, first_name, last_name, image, image, email, phone);
	}
	
	public String get_full_name() {
		return first_name + " " + last_name;
	}
	
	@Override
	public String toString() {
		return id + ", " + first_name + ", " + last_name + ", " + email + ", " + phone;
	}
	
	public static UserModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
					
		// create the deed
		String imageUrl = json.optString("image_url", null);		
		return new UserModel(
			json.getInt("user_id"),
			json.getString("first_name"),
			json.getString("last_name"),
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeApplication.genericPersonImage),
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeApplication.genericPersonThumbnail),			
			json.getString("email"),
			json.getString("phone")
		);
	}	

}
