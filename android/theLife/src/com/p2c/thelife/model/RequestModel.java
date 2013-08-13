package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.p2c.thelife.R;



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
		
	public int    request_id;
	public int    user_id;			// user making the request
	public String userName;			// the name of the user making the original request
	public int    recipient_id;		// the id of the user receiving the original request
	public String recipientName;    // the name of the user receiving the original request; will be empty if the person never registered
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
	
	
	
	
	public RequestModel(Resources resources, int request_id, 
						int user_id, String userName, int recipient_id, String recipientName, int group_id, String groupName, 
						String type, String email, String sms, long timestamp, String status) {
		super(request_id);
		
		this.user_id = user_id;
		this.userName = userName;
		this.recipient_id = recipient_id;
		this.recipientName = recipientName;
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
	
	// return the id of the user that authored this object
	public int getAuthorId() {
		return isDelivered() ? user_id : recipient_id;
	}
	
	@Override
	public String toString() {
		return id + ", " + user_id + ", [from] " + userName + ", [to] " + recipientName + "," + group_id + ", " + groupName + 
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
			paramGroupName = "???";
		}
		
		if (isDelivered() && isInvite()) {
			finalDescription = resources.getString(R.string.invite_request_description, paramUserName, paramGroupName);
		} else if (isDelivered() && isMembershipRequest()) {
			finalDescription = resources.getString(R.string.membership_request_description, paramUserName, paramGroupName);
		} else if (isAccepted() && isInvite()) {
			finalDescription = resources.getString(R.string.invite_request_accepted_description, this.recipientName, paramGroupName);
		} else if (isAccepted() && isMembershipRequest()) {
			finalDescription = resources.getString(R.string.membership_request_accepted_description, paramGroupName);
		} else if (isRejected() && isInvite()) {
			finalDescription = resources.getString(R.string.invite_request_rejected_description, this.recipientName, paramGroupName);
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
			json.optString("sender_full_name", null),
			json.optInt("recipient_id", 0),
			json.optString("recipient_full_name", null),
			json.getInt("group_id"),
			json.optString("group_name", null),		
			json.getString("type"),
			json.optString("email"),
			json.optString("sms"),
			json.optLong("updated_at", 0L) * 1000, // convert seconds from server into millis
			json.optString("status", "DELIVERED")
		);
	
	}
	
	
	public static RequestModel fromBundle(Resources resources, Bundle bundle) {
		Log.d(TAG, "fromBundle");
		
		return new RequestModel(
			resources,
			Integer.valueOf(bundle.getString("id")),
			Integer.valueOf(bundle.getString("user_id")),
			bundle.getString("sender_full_name", null),
			Integer.valueOf(bundle.getString("recipient_id", "0")),
			bundle.getString("recipient_full_name", null),
			Integer.valueOf(bundle.getString("group_id")),
			bundle.getString("group_name", null),		
			bundle.getString("type"),
			bundle.getString("email"),
			bundle.getString("sms"),
			Long.valueOf(bundle.getString("updated_at", "0")) * 1000, // convert seconds from server into millis
			bundle.getString("status", "DELIVERED")
		);
	}

}
