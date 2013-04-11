package com.p2c.thelife.model;

import java.util.EnumSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.p2c.thelife.TheLifeConfiguration;
import com.p2c.thelife.BitmapCache;


// POJO - plain old java object
/**
 * Deed/activity model
 *
 */
public class DeedModel extends AbstractModel {
	
	private static final String TAG = "DeedModel";
	
	// this deed id is reserved for the change threshold deed
	private static final int CHANGE_THRESHOLD_ID = 1;
	
	public String 	title;
	public String	summary;
	public String 	description;
	public Bitmap	image;  		// TODO is this an image id, image or what?
	public Set<FriendModel.Threshold> thresholds;
	public int 		priority;
	public int		category_id;	
	
	
	public DeedModel(int deed_id, String title, String summary, String description, Bitmap image, Set<FriendModel.Threshold> thresholds, int priority, int category_id) {
		super(deed_id);
		this.title = title;
		this.summary = summary;
		this.description = description;

		if (image == null) {
			this.image = TheLifeConfiguration.getGenericDeedImage();
		} else {
			this.image = image;
		}
		
		this.thresholds = thresholds;
		this.priority = priority;
		this.category_id = category_id;
	}	
	

	public boolean isApplicable(FriendModel.Threshold threshold) {
		return thresholds.contains(threshold);
	}
	
	
	public boolean isThresholdChange() {
		return id == CHANGE_THRESHOLD_ID;
	}
	
	
	public static DeedModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "IN DEED MODEL from JSON");
		
		// set up the thresholds
		JSONArray jsThresholds = null;
		jsThresholds = json.optJSONArray("threshold_ids");
		
		Set<FriendModel.Threshold> thresholds = null;
		if (jsThresholds != null) {
			thresholds = EnumSet.noneOf(FriendModel.Threshold.class);
			for (int j = 0; j < jsThresholds.length(); j++) {
				int thresholdIndex = FriendModel.thresholdId2Index(jsThresholds.getInt(j));
				thresholds.add(FriendModel.thresholdValues[thresholdIndex]);
			}
		} else {
			// handle a missing threshold field from Server
			thresholds = EnumSet.allOf(FriendModel.Threshold.class);
		}
			
		// create the deed
		String imageUrl = json.optString("image_url", null);
		return new DeedModel(
			json.getInt("id"),
			json.getString("title"),
			json.getString("summary"),
			json.getString("full_description"),
			BitmapCache.getBitmapFromSystem(imageUrl, useServer, TheLifeConfiguration.getGenericDeedImage()),
			thresholds,
			json.getInt("priority"),
			json.getInt("category_id")
		);
	}
	
	@Override
	public String toString() {
		return id + ", " + title + ", " + summary + ", " + description + ", " + thresholds + ", " + priority + ", " + category_id;
	}

}
