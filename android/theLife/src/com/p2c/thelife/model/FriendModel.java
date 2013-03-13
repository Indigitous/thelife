package com.p2c.thelife.model;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.p2c.thelife.R;



// POJO - plain old java object
public class FriendModel extends AbstractModel {
	
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
	public Drawable image;  // TODO is this an image id, image or what?
							// TODO do we need a bigger image and a thumbnail?
	public Threshold threshold;
	
	public FriendModel(int friend_id, String first_name, String last_name, Drawable image, Threshold threshold) {
		
		super(friend_id);
		
		this.first_name = first_name;
		this.last_name = last_name;
		this.image = image;
		this.threshold = threshold;
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

}
