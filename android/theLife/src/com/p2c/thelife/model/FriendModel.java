package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.p2c.thelife.BitmapCache;
import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeConfiguration;



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
			this.image = TheLifeConfiguration.genericPersonImage;
		} else {
			this.image = image;
		}
		if (thumbnail == null) {
			this.thumbnail = TheLifeConfiguration.genericPersonThumbnail;
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

		String thresholdStrings[] = resources.getStringArray(R.array.thresholds_short);
		
		switch (threshold) {
			case New_Contact:
				return thresholdStrings[0];
			case Trusting:
				return thresholdStrings[1];
			case Curious:
				return thresholdStrings[2];
			case Open:
				return thresholdStrings[3];	
			case Seeking:
				return thresholdStrings[4];	
			case Entering:
				return thresholdStrings[5];
			case Christian:
				return thresholdStrings[6];			
			default:
				return resources.getString(R.string.threshold_unknown_short);
		}
	}
	
	/**
	 * @return String   medium string version of the threshold
	 */
	public String get_threshold_medium_string(Resources resources) {
		
		String thresholdStrings[] = resources.getStringArray(R.array.thresholds_short);
		
		switch (threshold) {
			case New_Contact:
				return thresholdStrings[0];
			case Trusting:
				return thresholdStrings[1];
			case Curious:
				return thresholdStrings[2];
			case Open:
				return thresholdStrings[3];	
			case Seeking:
				return thresholdStrings[4];	
			case Entering:
				return thresholdStrings[5];
			case Christian:
				return thresholdStrings[6];	
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
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeConfiguration.genericPersonImage),
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeConfiguration.genericPersonThumbnail),				
			threshold
		);
	}
		

}
