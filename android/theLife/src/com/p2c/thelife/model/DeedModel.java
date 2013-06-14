package com.p2c.thelife.model;

import java.util.EnumSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.p2c.thelife.TheLifeConfiguration;
import com.p2c.thelife.BitmapCacheHandler;
import com.p2c.thelife.Utilities;


/**
 * Deed/activity model. Deeds are defined by admins in the server. 
 *
 */
public class DeedModel extends AbstractModel {
	
	private static final String TAG = "DeedModel";
	
	// values for special field
	public static String SPECIAL_CHANGE_THRESHOLD = "change_threshold";
	public static String SPECIAL_ADD_FRIEND = "add_friend";
	
	
	public String 	title;
	public String	summary;
	public String 	description;
	public Set<FriendModel.Threshold> thresholds;
	public int 		priority;
	public int		category_id;
	public boolean 	hasThreshold;
	public String  	special;
	
	
	public DeedModel(int deed_id, String title, String summary, String description, 
		Set<FriendModel.Threshold> thresholds, int priority, int category_id, boolean hasThreshold, String special) {
		super(deed_id);
		this.title = title;
		this.summary = summary;
		this.description = description;
		this.thresholds = thresholds;
		this.priority = priority;
		this.category_id = category_id;
		this.hasThreshold = hasThreshold;
		this.special = special;
	}	
	

	/**
	 * whether or not the deed/activity is applicable to the given threshold
	 * @param threshold
	 * @return
	 */
	public boolean isApplicable(FriendModel.Threshold threshold) {
		return thresholds.contains(threshold);
	}
	
	
	/**
	 * Whether or not the deed/activity is generally applicable to all thresholds.
	 * Deeds/activities not marked with any thresholds are generally applicable to all thresholds.
	 * @return
	 */
	public boolean isGenerallyApplicable() {
		return thresholds.size() == 0;
	}
	
	
	/**
	 * @return Whether or not the deed/activity is ChangeThreshold. 
	 */
	public boolean isChangeThreshold() {
		return special != null && special.equals(SPECIAL_CHANGE_THRESHOLD);
	}
	
	
	/**
	 * @return Whether or not the deed/activity operates in a special way.
	 */
	public boolean isSpecial() {
		return Utilities.hasData(special);
	}
	
	
	public static DeedModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
				
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
		int id = json.getInt("id");
		String special = json.optString("special", null);
		if (special.equals("null")) {
			special = null;
		}		
		return new DeedModel(
			id,
			json.getString("title"),
			json.getString("summary"),
			json.getString("full_description"),
			thresholds,
			json.getInt("priority"),
			json.optInt("category_id", 0),
			json.optBoolean("has_threshold", false),
			special
		);
	}
	
	
	/**
	 * Will attempt to load the image or use the placeholder.
	 * @param id
	 * @return
	 */
	public static Bitmap getThumbnail(int id) {
		return BitmapCacheHandler.getBitmapFromSystem("activities", id, "thumbnail", TheLifeConfiguration.getGenericDeedImage());
	}
	
	
	@Override
	public String toString() {
		return id + ", " + title + ", " + summary + ", " + description + ", " + thresholds + ", " + priority + ", " + category_id + ", " + special;
	}

}
