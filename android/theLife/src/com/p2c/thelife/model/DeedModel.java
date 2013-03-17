package com.p2c.thelife.model;

import java.util.EnumSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.p2c.thelife.TheLifeApplication;
import com.p2c.thelife.BitmapCache;


// POJO - plain old java object
// TODO - deeds to move a friend to a different (lower or higher) threshold
/**
 * Deed/activity model
 *
 */
public class DeedModel extends AbstractModel {
	
	private static final String TAG = "DeedModel"; 
	
	public String 	title;
	public String	summary;
	public String 	description;
	public String	category;
	public Bitmap	image;  		// TODO is this an image id, image or what?
	public Set<FriendModel.Threshold> thresholds;
	
	
	public DeedModel(int deed_id, String title, String summary, String description, String category, Bitmap image, Set<FriendModel.Threshold> thresholds) {
		super(deed_id);
		this.title = title;
		this.summary = summary;
		this.description = description;
		this.category = category;
		
		if (image == null) {
			this.image = TheLifeApplication.genericDeedImage;
		} else {
			this.image = image;
		}
		
		this.thresholds = thresholds;
	}	
	

	public boolean is_applicable(FriendModel.Threshold threshold) {
		return thresholds.contains(threshold);
	}
	
	public static DeedModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "IN DEED MODEL from JSON");
		
		// set up the thresholds
		JSONArray jsThresholds = json.optJSONArray("thresholds");
		Set<FriendModel.Threshold> thresholds = EnumSet.noneOf(FriendModel.Threshold.class);
		for (int j = 0; j < jsThresholds.length(); j++) {
			int intThreshold = jsThresholds.getInt(j);
			thresholds.add(FriendModel.thresholdValues[intThreshold]);
		}
		
		// create the deed
		String imageUrl = json.optString("image_url", null);
		return new DeedModel(
			json.getInt("activity_id"),
			json.getString("title"),
			json.getString("summary"),
			json.getString("description"),
			json.getString("category"),
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeApplication.genericDeedImage),
			thresholds
		);
	}
	
	@Override
	public String toString() {
		return id + ", " + title + ", " + summary + ", " + description + ", " + category + ", " + thresholds;
	}

}
