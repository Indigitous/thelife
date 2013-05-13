package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.util.Log;

import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeConfiguration;



/**
 * Request/notification data model.
 * @author clarence
 *
 */
public class RequestModel extends AbstractModel {
	
	private static final String TAG = "RequestModel"; 
	
	// type and status field values
	public static final String REQUEST_MEMBERSHIP = "REQUEST_MEMBERSHIP";
	public static final String INVITE = "INVITE";
	public static final String DELIVERED = "DELIVERED";
	public static final String ACCEPTED = "ACCEPTED";
	public static final String REJECTED = "REJECTED";	
		
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
	public long   timestamp; 		// in milliseconds, android.text.format.Time
	public String status;			// either DELIVERED, ACCEPTED or REJECTED
	
	
	
	
	public RequestModel(Resources resources, int request_id, int user_id, String userName, int group_id, String groupName, 
						String type, String email, String sms, long timestamp, String status) {
		super(request_id);
		
		this.user_id = user_id;
		this.userName = userName;
		this.group_id = group_id;
		this.groupName = groupName;
		this.type = type;
		this.email = email;		
		this.sms = sms;
		this.timestamp = timestamp;
		this.status = status;
		
		this.finalDescription = getFinalDescription(resources);
	}
	
	public boolean isInvite() {
		return type.equals(INVITE);
	}
	
	public boolean isMembershipRequest() {
		return type.equals(REQUEST_MEMBERSHIP);
	}
	
	public boolean isDelivered() {
		return status.equals(DELIVERED);
	}
	
	public boolean isAccepted() {
		return status.equals(ACCEPTED);
	}
	
	public boolean isRejected() {
		return status.equals(REJECTED);
	}
	
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", " + userName + "," + group_id + ", " + groupName + 
				", " + email + ", " + sms + "," + finalDescription + "," + timestamp + "," + status;
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
			paramUserName = "???";
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
			paramGroupName = "???";
		}
		
		if (isDelivered() && isInvite()) {
			finalDescription = resources.getString(R.string.invite_request_description, paramUserName, paramGroupName);
		} else if (isDelivered() && isMembershipRequest()) {
			finalDescription = resources.getString(R.string.membership_request_description, paramUserName, paramGroupName);
		} else if (isAccepted() && isInvite()) {
			finalDescription = resources.getString(R.string.invite_request_accepted_description, paramUserName, paramGroupName);
		} else if (isAccepted() && isMembershipRequest()) {
			finalDescription = resources.getString(R.string.membership_request_accepted_description, paramGroupName);
		} else if (isRejected() && isInvite()) {
			finalDescription = resources.getString(R.string.invite_request_rejected_description, paramUserName, paramGroupName);
		} else if (isRejected() && isMembershipRequest()) {
			finalDescription = resources.getString(R.string.membership_request_rejected_description, paramGroupName);				
		} else {
			finalDescription = "<" + paramUserName + " " + this.type + " " + this.status + " " + paramGroupName + ">";
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
			json.optString("sms"),
			json.optLong("updated_at", 0L) * 1000, // convert seconds from server into millis
			json.optString("status", "DELIVERED")
		);
	
	}	

}
