package com.p2c.thelife;

import java.util.ArrayList;



// POJO - plain old java object
public class GroupModel {
	
	public int      group_id;
	public String   name;
	public int      leader_id; // user_id in that group
	public ArrayList<Integer> member_ids; // user_ids in the group, including the leader 
	
	public GroupModel(int group_id, String name, int leader_id, ArrayList<Integer> member_ids) {
		this.group_id = group_id;
		this.name = name;
		this.leader_id = leader_id;
		this.member_ids = member_ids;
	}
	
	@Override
	public String toString() {
		return group_id + ", " + name + ", " + leader_id + ", " + member_ids;
	}

}
