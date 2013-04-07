package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



// POJO - plain old java object
public class RequestModel extends AbstractModel {
	
	private static final String TAG = "RequestModel"; 	
	
	public static final String REQUEST_MEMBERSHIP = "REQUEST_MEMBERSHIP";
	public static final String INVITE = "INVITE";
		
	// TODO decide on userName and groupName
	public int    request_id;
	public int    user_id;			// user making the request
	public String userName;			// the name of the user making the request
	public int    group_id;			// the group to join
	public String groupName;        // the name of the group to join
	public String type;				// either REQUEST_MEMBERSHIP or INVITE:
									// 		REQUEST_MEMBERSHIP means the user_id requests to join group group_id
									// 		INVITE means the user_id, who is the group leader of group_id, requests email/sms to join
	public String email;			// for INVITE: this is the invited person's email
	public String sms;				// for INVITE: this is the invited person's SMS
	public String description;
	
	
	public RequestModel(int request_id, int user_id, int group_id, String type, String email, String sms, String description) {
		super(request_id);
		
		this.user_id = user_id;
		this.group_id = group_id;
		this.type = type;
		this.email = email;		
		this.sms = sms;
		this.description = description;
	}
	
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + userName + "," + group_id + ", " + groupName + ", " + email + ", " + sms + "," + description;
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
			json.optString("email"),
			json.optString("sms"),
			json.getString("description")
		);
		
//		// set the description, which needs the Resources
//		if (type.equals(REQUEST_MEMBERSHIP)) {
//			request.description = context.getResources().getString(R.string.membership_request_description, request.userName, request.groupName);
//		} else if (type.equals(INVITE)) {
//			request.description = context.getResources().getString(R.string.invite_request_description, request.groupName);
//		} else {
//			request.description = "";
//		}
	}
		

}
