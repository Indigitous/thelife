package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.p2c.thelife.BitmapCache;
import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeConfiguration;



// POJO - plain old java object
public class FriendModel extends AbstractModel {
	
	private static final String TAG = "FriendModel"; 	
	
	public enum Threshold {
		NewContact(1),
		Trusting(2),
		Curious(3),
		Open(4),
		Seeking(5),
		Entering(6),
		Christian(7);
		
		public int serverId;
		
		Threshold(int serverId) {
			this.serverId = serverId;
		}
	}
	

	// all threshold values
	public static final Threshold thresholdValues[] = Threshold.values();
	
	/**
	 * The server is using threshold ids, which start at one.
	 * The device wants to use a base zero index.
	 * @param thresholdId
	 * @return
	 */
	public static int thresholdId2Index(int thresholdId) {
		return thresholdId - 1;
	}
	
	
	/**
	 * Convert the given server threshold id to a Threshold enum.
	 * @param thresholdId
	 * @return
	 */
	public static Threshold thresholdId2Threshold(int thresholdId) {
		int thresholdIndex = thresholdId2Index(thresholdId);
		return FriendModel.thresholdValues[thresholdIndex];
	}
	
	
	
	public String firstName;
	public String lastName;
	public Bitmap image;  			// TODO is this an image id, image or what?
	public Bitmap thumbnail;
	public Threshold threshold;
	public String email;
	public String mobile;
	
	
	public FriendModel(int friend_id, String firstName, String lastName, Bitmap image, Bitmap thumbnail, Threshold threshold, String email, String mobile) {
		
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
		this.email = email;
		this.mobile = mobile;
	}
	
	
	public FriendModel(int friend_id, String firstName, String lastName, Bitmap image, Threshold threshold, String email, String mobile) {
		this(friend_id, firstName, lastName, image, image, threshold, email, mobile);
	}
	
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	
	public static String getThresholdShortString(Resources resources, Threshold threshold) {

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
	 * @return String   short string version of the threshold
	 */
	public String getThresholdShortString(Resources resources) {
		return FriendModel.getThresholdShortString(resources, threshold);
	}
	
	
	public static String getThresholdMediumString(Resources resources, Threshold threshold) {
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
	
	
	/**
	 * @return String   medium string version of the threshold
	 */
	public String getThresholdMediumString(Resources resources) {
		return FriendModel.getThresholdMediumString(resources, threshold);
	}	
	
	
	/**
	 * Will attempt to load the image or use the placeholder.
	 * @param id
	 * @param useServer
	 * @return
	 */
	public static Bitmap getImage(int id, boolean useServer) {
		return BitmapCache.getBitmapFromSystem("friends", id, "image", useServer, TheLifeConfiguration.getGenericPersonImage());
	}
	
	/**
	 * Will attempt to load the image or use the placeholder.
	 * @param id
	 * @param useServer
	 * @return
	 */
	public static Bitmap getThumbnail(int id, boolean useServer) {
		return BitmapCache.getBitmapFromSystem("friends", id, "thumbnail", useServer, TheLifeConfiguration.getGenericPersonThumbnail());
	}	
	
	@Override
	public String toString() {
		return id + ", " + firstName + ", " + lastName + ", " + threshold;
	}
	
	public static FriendModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
			
		FriendModel.Threshold threshold = thresholdId2Threshold(json.getInt("threshold_id"));
			
		// create the friend
		int id = json.getInt("id");
		return new FriendModel(
			id,
			json.getString("first_name"),
			json.getString("last_name"),
			getImage(id, useServer),
			getThumbnail(id, useServer),				
			threshold,
			json.getString("email"),
			json.getString("mobile")
		);
	}
		

}
