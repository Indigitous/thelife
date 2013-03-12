package com.p2c.thelife.model;

import java.util.EnumSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.util.Log;


// POJO - plain old java object
// TODO - deeds to move a friend to a different (lower or higher) threshold
/**
 * Deed/activity model
 *
 */
public class DeedModel extends AbstractModel {
	
	private static final String TAG = "DeedModel"; 
	
	public String 		title;
	public String		summary;
	public String 		description;
	public String		category;
	public Drawable     image;  // TODO is this an image id, image or what?
										    // TODO do we need a bigger image and a thumbnail?	
	public Set<FriendModel.Threshold> thresholds;
	
	public DeedModel(int deed_id, String title, String summary, String description, String category, Drawable image, Set<FriendModel.Threshold> thresholds) {
		
		super(deed_id);
		this.title = title;
		this.summary = summary;
		this.description = description;
		this.category = category;
		this.image = image;
		this.thresholds = thresholds;
	}
	
	public boolean is_applicable(FriendModel.Threshold threshold) {
		return thresholds.contains(threshold);
	}
	
	public static DeedModel fromJSON(JSONObject json, Drawable genericImage) throws JSONException {
		
		Log.d(TAG, "IN DEED MODEL from JSON");
		
		// set up the thresholds
		JSONArray jsThresholds = json.optJSONArray("thresholds");
		Set<FriendModel.Threshold> thresholds = EnumSet.noneOf(FriendModel.Threshold.class);
		for (int j = 0; j < jsThresholds.length(); j++) {
			int intThreshold = jsThresholds.getInt(j);
			thresholds.add(FriendModel.thresholdValues[intThreshold]);
		}
		
		// create the deed
		return new DeedModel(
			json.getInt("activity_id"),
			json.getString("title"),
			json.getString("summary"),
			json.getString("description"),
			json.getString("category"),
			genericImage,		
			thresholds
		);
	}
	
	@Override
	public String toString() {
		return id + ", " + title + ", " + summary + ", " + description + ", " + category + ", " + thresholds;
	}

}
