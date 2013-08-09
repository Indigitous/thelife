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
	
	private static final String SYSKEY_REFRESH_CATEGORIES_TIMESTAMP = "refresh_categories_timestamp";
	
	
	public CategoriesDS(Context context, String token) {
		
		super(
				context,
				token,
				"CategoriesDS", 
				"categories.json",
				SYSKEY_REFRESH_CATEGORIES_TIMESTAMP,
				"categories",
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
