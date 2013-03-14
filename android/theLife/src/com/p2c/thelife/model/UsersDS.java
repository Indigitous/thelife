package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeApplication;
import com.p2c.thelife.Utilities;


// TODO need to know current user?
public class UsersDS extends AbstractDS<UserModel> {
	
	public UsersDS(Context context) {
		
		super(
				context, 
				"UsersDS", 
				context.getCacheDir().getAbsolutePath() + "/users.json",
				"refresh_users_timestamp_key",
				TheLifeApplication.SERVER_URL + "/users.json",
				"refresh_users_delta_key",
				TheLifeApplication.REFRESH_USERS_DELTA
			);		
				
		m_data.add(new UserModel(1, "Geoff", "Moore", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.moore)), "john@martin@p2c.com", "555-123-4567"));
		m_data.add(new UserModel(2, "Mark", "Twain", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.twain)),"john@martin@p2c.com", "555-123-4567"));
		m_data.add(new UserModel(3, "Robert", "Rashdall", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.rashdall)), "john@martin@p2c.com", "555-123-4567"));
		m_data.add(new UserModel(4, "Tak", "Inouye", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.inouye)), "john@martin@p2c.com", "555-123-4567"));
		m_data.add(new UserModel(5, "Nelson", "Schock", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.schock)), "john@martin@p2c.com", "555-123-4567"));	
	}
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected UserModel createFromJSON(Context context, JSONObject json) throws JSONException {
		return null; // EventModel.fromJSON(json);
	}		

}
