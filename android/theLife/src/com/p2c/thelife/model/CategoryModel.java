package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



// POJO - plain old java object
public class CategoryModel extends AbstractModel {
	
	private static final String TAG = "CategoryModel";
	
	public String   name;
	public String   description;
	
	public CategoryModel(int category_id, String name, String description) {
		
		super(category_id);

		this.name = name;
		this.description = description;
	}
	
	@Override
	public String toString() {
		return id + ", " + name + ", " + description;
	}
	
	public static CategoryModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "IN CATEGORY MODEL from JSON");
		
		// create the category
		return new CategoryModel(
			json.getInt("category_id"),
			json.getString("name"),
			json.getString("description")
		);
	}	


}
