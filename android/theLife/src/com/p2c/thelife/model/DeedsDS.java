package com.p2c.thelife.model;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.TheLifeConfiguration;


/**
 * Deed Model datastore
 * @author clarence
 *
 */
public class DeedsDS extends AbstractDS<DeedModel> {
	
	/** 
	 * Deeds (Activity Objects) Data Store.
	 * @param context
	 */
	public DeedsDS(Context context) {
		
		super(
			context, 
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
	public Collection<DeedModel> findByThreshold(FriendModel.Threshold threshold) {
		ArrayList<DeedModel> deeds = new ArrayList<DeedModel>();
		
		for (DeedModel m:m_data) {
			if (m.isApplicable(threshold)) {
				deeds.add(m);
			}		
		}
		
		return deeds;
	}
	
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected DeedModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return DeedModel.fromJSON(json, useServer);
	}
	
}
