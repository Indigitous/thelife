package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.config.TheLifeConfiguration;


/**
 * Categories data store. 
 * @author clarence
 *
 */
public class CategoriesDS extends AbstractDS<CategoryModel> {
	
	public CategoriesDS(Context context, String token) {
		
		super(
				context,
				token,
				"CategoriesDS", 
				"categories.json",
				"refresh_categories_timestamp_key",
				"categories",
				"refresh_categories_delta_key",
				TheLifeConfiguration.REFRESH_CATEGORIES_DELTA
			);		
		
	}
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected CategoryModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return CategoryModel.fromJSON(json, useServer);
	}	
}
