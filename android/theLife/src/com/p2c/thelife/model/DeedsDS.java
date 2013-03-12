package com.p2c.thelife.model;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeApplication;


/**
 * Deed Model datastore
 * @author clarence
 *
 */
public class DeedsDS extends AbstractDS<DeedModel> {
	
	private Drawable m_genericIcon = null;

	
	/** 
	 * Deeds (Activity Objects) Data Store.
	 * @param context
	 */
	public DeedsDS(Context context) {
		
		super(
			context, 
			"DeedsDS", 
			context.getCacheDir().getAbsolutePath() + "/deeds.json",
			"refresh_deeds_timestamp_key",
			"http://thelife.ballistiq.com/deeds.json",
			"refresh_deeds_delta_key",
			TheLifeApplication.RELOAD_DEEDS_DELTA
		);
		
		// initialize instance vars
		m_genericIcon = context.getResources().getDrawable(R.drawable.pray);
	}
	
	
	/**
	 * @param threshold
	 * @return all deed model objects applicable to the given threshold
	 */
	public Collection<DeedModel> findByThreshold(FriendModel.Threshold threshold) {
		ArrayList<DeedModel> deeds = new ArrayList<DeedModel>();
		
		for (DeedModel m:m_data) {
			if (m.is_applicable(threshold)) {
				deeds.add(m);
			}		
		}
		
		return deeds;
	}
	
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected DeedModel createFromJSON(JSONObject json) throws JSONException {
		return DeedModel.fromJSON(json, m_genericIcon);
	}
	
}
