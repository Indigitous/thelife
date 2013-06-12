package com.p2c.thelife.model;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeConfiguration;


/**
 * Deed/activities Model datastore
 * @author clarence
 *
 */
public class DeedsDS extends AbstractDS<DeedModel> {
	
	/** 
	 * Deeds (Activity Objects) Data Store.
	 * @param context
	 */
	public DeedsDS(Context context, String token) {
		
		super(
			context,
			token,
			"DeedsDS", 
			"deeds.json",
			"refresh_deeds_timestamp_key",
			"activities",
			"refresh_deeds_delta_key",
			TheLifeConfiguration.REFRESH_DEEDS_DELTA
		);
		
	}
	
	
	/**
	 * @param threshold
	 * @return all deed model objects applicable to the given threshold
	 */
	public ArrayList<DeedModel> findByThreshold(FriendModel.Threshold threshold) {
		ArrayList<DeedModel> deeds = new ArrayList<DeedModel>();
		
		for (DeedModel m:m_data) {
			if (m.isApplicable(threshold)) {
				deeds.add(m);
			}		
		}
		
		return deeds;
	}
	
	
	public ArrayList<DeedModel> findByCategoryAndThreshold(int categoryId, FriendModel.Threshold threshold) {
		ArrayList<DeedModel> deeds = new ArrayList<DeedModel>();
		
		for (DeedModel m:m_data) {
			if (m.category_id == categoryId && m.isApplicable(threshold)) {
				deeds.add(m);
			}		
		}
		
		return deeds;		
	}
	
	
	/**
	 * @return the DeedModel which reports that a friend has been added
	 */
	public DeedModel findAddFriend() {
		for (DeedModel m:m_data) {
			if (m.isAddFriend) {
				return m;
			}		
		}
		
		return null;		
	}
	
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected DeedModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return DeedModel.fromJSON(json, useServer);
	}
	
}
