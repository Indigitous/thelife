package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



// POJO - plain old java object
public class RequestModel extends AbstractModel {
	
	private static final String TAG = "RequestModel"; 	
		
	public int    request_id;
	public int    user_id;			// user making the request
	public int    group_id;			// the group to join
	public String email;			// if the group leader is asking a person to join the group leader's group, then this is the person's email
	public String sms;				// if the group leader is asking a person to join the group leader's group, then this is the person's sms
	
	
	public RequestModel(int request_id, int user_id, int group_id, String email, String sms) {
		super(request_id);
		
		this.user_id = user_id;
		this.group_id = group_id;
		this.email = email;		
		this.sms = sms;
	}
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + group_id + ", " + email + ", " + sms;
	}
	
	public static RequestModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
					
		// create the request
		return new RequestModel(
			json.getInt("id"),
			json.getInt("user_id"),
			json.getInt("group_id"),
			json.getString("email"),
			json.getString("sms")
		);
	}
		

}
