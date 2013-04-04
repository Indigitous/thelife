package com.p2c.thelife.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



// POJO - plain old java object
public class GroupModel extends AbstractModel {
	
	private static final String TAG = "GroupModel";
	
	public String   name;
	public String   description;
	public int      leader_id; // user_id in that group
	public ArrayList<Integer> member_ids; // user_ids in the group, including the leader 
	
	public GroupModel(int group_id, String name, String description, int leader_id, ArrayList<Integer> member_ids) {
		
		super(group_id);

		this.name = name;
		this.description = description;
		this.leader_id = leader_id;
		this.member_ids = (ArrayList<Integer>)member_ids.clone();
	}
	
	@Override
	public String toString() {
		return id + ", " + name + ", " + description + ", " + leader_id + ", " + member_ids;
	}
	
	public static GroupModel fromJSON(JSONObject json, boolean useServer) throws JSONException {
		
		Log.d(TAG, "IN GROUP MODEL from JSON");
		
		// get the member ids
		JSONArray jsonMemberIds = json.getJSONArray("member_ids");
		ArrayList<Integer> memberIds = new ArrayList<Integer>(jsonMemberIds.length());
		for (int i = 0; i < jsonMemberIds.length(); i++) {
			memberIds.add(jsonMemberIds.getInt(i));
		}
		
		// create the group
		return new GroupModel(
			json.getInt("id"),
			json.getString("name"),
			json.getString("description"),
			json.getInt("leader_id"),
			memberIds
		);
	}	


}
