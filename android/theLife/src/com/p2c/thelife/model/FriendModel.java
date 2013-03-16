package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeApplication;
import com.p2c.thelife.Utilities;



// POJO - plain old java object
public class FriendModel extends AbstractModel {
	
	private static final String TAG = "FriendModel"; 	
	
	public enum Threshold {
		New_Contact,
		Trusting,
		Curious,
		Open,
		Seeking,
		Entering,
		Christian
	}
	
// example EnumSet	
//	Set<FriendModel.Threshold> allThresholds = EnumSet.allOf(FriendModel.Threshold.class);
//	Set<FriendModel.Threshold> earlyThresholds = EnumSet.range(FriendModel.Threshold.New_Contact, FriendModel.Threshold.Curious);	
	public static final Threshold thresholdValues[] = Threshold.values();
	
	public int    friend_id;
	public String first_name;
	public String last_name;
	public Bitmap image;  			// TODO is this an image id, image or what?
	public Bitmap thumbnail;
	public Threshold threshold;
	
	
	public FriendModel(int friend_id, String first_name, String last_name, Bitmap image, Bitmap thumbnail, Threshold threshold) {
		
		super(friend_id);
		
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
		
		this.threshold = threshold;
	}
	
	
	public FriendModel(int friend_id, String first_name, String last_name, Bitmap image, Threshold threshold) {
		this(friend_id, first_name, last_name, image, image, threshold);
	}
	
	
	public String get_full_name() {
		return first_name + " " + last_name;
	}
	
	/**
	 * @return String   short string version of the threshold
	 */
	public String get_threshold_short_string(Resources resources) {
		switch (threshold) {
			case New_Contact:
				return resources.getString(R.string.threshold_new_contact_short);
			case Trusting:
				return resources.getString(R.string.threshold_trusting_short);
			case Curious:
				return resources.getString(R.string.threshold_curious_short);
			case Open:
				return resources.getString(R.string.threshold_open_short);	
			case Seeking:
				return resources.getString(R.string.threshold_seeking_short);	
			case Entering:
				return resources.getString(R.string.threshold_entering_short);
			case Christian:
				return resources.getString(R.string.threshold_christian_short);	
			default:
				return resources.getString(R.string.threshold_unknown_short);
		}
	}
	
	/**
	 * @return String   medium string version of the threshold
	 */
	public String get_threshold_medium_string(Resources resources) {
		switch (threshold) {
			case New_Contact:
				return resources.getString(R.string.threshold_new_contact_medium);
			case Trusting:
				return resources.getString(R.string.threshold_trusting_medium);
			case Curious:
				return resources.getString(R.string.threshold_curious_medium);
			case Open:
				return resources.getString(R.string.threshold_open_medium);	
			case Seeking:
				return resources.getString(R.string.threshold_seeking_medium);	
			case Entering:
				return resources.getString(R.string.threshold_entering_medium);
			case Christian:
				return resources.getString(R.string.threshold_christian_medium);	
			default:
				return resources.getString(R.string.threshold_unknown_medium);
		}
	}	
	
	@Override
	public String toString() {
		return id + ", " + first_name + ", " + last_name + ", " + threshold;
	}
	
	public static FriendModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
		
		int thresholdInt = json.getInt("threshold");
		FriendModel.Threshold threshold = FriendModel.thresholdValues[thresholdInt];
			
		// create the friend
		String imageUrl = json.optString("image_url", null);			
		return new FriendModel(
			json.getInt("friend_id"),
			json.getString("first_name"),
			json.getString("last_name"),
			Utilities.getBitmapFromSystem(imageUrl, useServer, TheLifeApplication.genericPersonImage),
			Utilities.getBitmapFromSystem(imageUrl, useServer, TheLifeApplication.genericPersonThumbnail),				
			threshold
		);
	}
		

}
