package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.util.Log;

import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeConfiguration;



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
	public String finalDescription; // description with template place holders replaced with real values
	
	
	public RequestModel(Resources resources, int request_id, int user_id, String userName, int group_id, String groupName, String type, String email, String sms) {
		super(request_id);
		
		this.user_id = user_id;
		this.group_id = group_id;
		this.type = type;
		this.email = email;		
		this.sms = sms;
		this.finalDescription = getFinalDescription(resources);
	}
	
	public boolean isInvite() {
		return type.equals(INVITE);
	}
	
	public boolean isMembershipRequest() {
		return type.equals(REQUEST_MEMBERSHIP);
	}
	
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + userName + "," + group_id + ", " + groupName + ", " + email + ", " + sms + "," + finalDescription;
	}
	
	
	/**
	 * Replace the template place holders in the description with the real values.
	 * 		$u = user's full name
	 * 		$g = group name
	 */
	private String getFinalDescription(Resources resources) {
		String finalDescription = null;
		
		// user name parameter
		String paramUserName = this.userName;
		if (paramUserName == null) {
			UserModel user = TheLifeConfiguration.getUsersDS().findById(user_id);
			if (user != null) {
				paramUserName = user.getFullName();
			}
		}
		if (paramUserName == null) {
			paramUserName = "";
		}
		
		// friend name parameter
		String paramGroupName = this.groupName;
		if (paramGroupName == null) {
			GroupModel group = TheLifeConfiguration.getGroupsDS().findById(group_id);
			if (group != null) {
				paramGroupName = group.name;
			}
		}
		if (paramGroupName == null) {
			paramGroupName = "";
		}
		
		if (this.type.equals(INVITE)) {
			finalDescription = resources.getString(R.string.invite_request_description, paramUserName, paramGroupName);
		} else if (this.type.equals(REQUEST_MEMBERSHIP)) {
			finalDescription = resources.getString(R.string.membership_request_description, paramUserName, paramGroupName);
		} else {
			finalDescription = "<" + this.type + ">";
		}
		
		return finalDescription;
	}	
	
	
	public static RequestModel fromJSON(Resources resources, JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "fromJSON()");
							
		// create the request
		return new RequestModel(
			resources,
			json.getInt("id"),
			json.getInt("user_id"),
			json.optString("user_name", null),			
			json.getInt("group_id"),
			json.optString("group_name", null),		
			json.getString("type"),
			json.optString("email"),
			json.optString("sms")
		);
		
	}
		

}
