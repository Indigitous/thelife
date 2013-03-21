package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



// POJO - plain old java object
public class RequestModel extends AbstractModel {
	
	private static final String TAG = "RequestModel"; 	
	
	public enum Type {
		Invite_With_Email,
		Invite_With_SMS,
		Request_Membership,
	}
	
	public static final Type typeValues[] = Type.values();
	
	public int    request_id;
	public int    user_id;			// user making the request
	public int    group_id;
	public Type	  type;	
	public String receiver;			// depending on type field this could be email, SMS or user_id
	
	
	public RequestModel(int request_id, int user_id, int group_id, Type type, String receiver) {
		
		super(request_id);
		
		this.user_id = user_id;
		this.group_id = group_id;
		this.type = type;		
		this.receiver = receiver;
	}
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + group_id + ", " + type + ", " + receiver;
	}
	
	public static RequestModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
		
		int typeInt = json.getInt("type");
		RequestModel.Type type = RequestModel.typeValues[typeInt];
			
		// create the request
		return new RequestModel(
			json.getInt("request_id"),
			json.getInt("user_id"),
			json.getInt("group_id"),
			type,
			json.getString("receiver")
		);
	}
		

}
