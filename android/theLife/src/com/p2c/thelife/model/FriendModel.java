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
		NewContact,
		Trusting,
		Curious,
		Open,
		Seeking,
		Entering,
		Christian
	}
	
// example EnumSet	
//	Set<FriendModel.Threshold> allThresholds = EnumSet.allOf(FriendModel.Threshold.class);
//	Set<FriendModel.Threshold> earlyThresholds = EnumSet.range(FriendModel.Threshold.NewContact, FriendModel.Threshold.Curious);	
	public static final Threshold thresholdValues[] = Threshold.values();
	
	public String firstName;
	public String lastName;
	public Bitmap image;  			// TODO is this an image id, image or what?
	public Bitmap thumbnail;
	public Threshold threshold;
	
	
	public FriendModel(int friend_id, String firstName, String lastName, Bitmap image, Bitmap thumbnail, Threshold threshold) {
		
		super(friend_id);
		
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
		
		this.threshold = threshold;
	}
	
	
	public FriendModel(int friend_id, String firstName, String lastName, Bitmap image, Threshold threshold) {
		this(friend_id, firstName, lastName, image, image, threshold);
	}
	
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	/**
	 * @return String   short string version of the threshold
	 */
	public String getThresholdShortString(Resources resources) {

		String thresholdStrings[] = resources.getStringArray(R.array.thresholds_short);
		
		switch (threshold) {
			case NewContact:
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
	public String getThresholdMediumString(Resources resources) {
		
		String thresholdStrings[] = resources.getStringArray(R.array.thresholds_short);
		
		switch (threshold) {
			case NewContact:
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
		return id + ", " + firstName + ", " + lastName + ", " + threshold;
	}
	
	public static FriendModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
		
		int thresholdInt = json.getInt("threshold");
		FriendModel.Threshold threshold = FriendModel.thresholdValues[thresholdInt];
			
		// create the friend
		String imageUrl = json.optString("image_url", null);			
		return new FriendModel(
			json.getInt("id"),
			json.getString("first_name"),
			json.getString("last_name"),
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeConfiguration.getGenericPersonImage()),
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeConfiguration.getGenericPersonThumbnail()),				
			threshold
		);
	}
		

}
