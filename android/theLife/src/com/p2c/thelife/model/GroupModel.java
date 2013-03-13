package com.p2c.thelife.model;

import java.util.ArrayList;



// POJO - plain old java object
public class GroupModel extends AbstractModel {
	
	public String   name;
	public int      leader_id; // user_id in that group
	public ArrayList<Integer> member_ids; // user_ids in the group, including the leader 
	
	public GroupModel(int group_id, String name, int leader_id, ArrayList<Integer> member_ids) {
		
		super(group_id);

		this.name = name;
		this.leader_id = leader_id;
		this.member_ids = (ArrayList<Integer>)member_ids.clone();
	}
	
	@Override
	public String toString() {
		return id + ", " + name + ", " + leader_id + ", " + member_ids;
	}

}
