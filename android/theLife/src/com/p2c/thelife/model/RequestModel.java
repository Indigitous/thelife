package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



// POJO - plain old java object
public class RequestModel extends AbstractModel {
	
	private static final String TAG = "RequestModel"; 	
	
	public static final String REQUEST_MEMBERSHIP = "REQUEST_MEMBERSHIP";
	public static final String INVITE = "INVITE";
		
	public int    request_id;
	public int    user_id;			// user making the request
	public int    group_id;			// the group to join
	public String type;				// either REQUEST_MEMBERSHIP or INVITE
	public String email;			// for INVITE: this is the invited person's email
	public String sms;				// for INVITE: this is the invited person's SMS
	
	
	public RequestModel(int request_id, int user_id, int group_id, String type, String email, String sms) {
		super(request_id);
		
		this.user_id = user_id;
		this.group_id = group_id;
		this.type = type;
		this.email = email;		
		this.sms = sms;
	}
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + group_id + ", " + email + ", " + sms;
	}
	
	public static RequestModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
		
		// TODO decide on "kind" or "type"
		String type = json.optString("type");
		if (type == null) {
			type = json.optString("kind");
		}
					
		// create the request
		return new RequestModel(
			json.getInt("id"),
			json.getInt("user_id"),
			json.getInt("group_id"),
			json.getString("kind"),
			json.getString("email"),
			json.getString("sms")
		);
	}
		

}
