package com.p2c.thelife.model;

import android.graphics.drawable.Drawable;



// POJO - plain old java object
// TODO need the group id?
public class UserModel {
	
	public int      group_id;
	public int      user_id;
	public String   first_name;
	public String   last_name;
	public Drawable image;  // TODO is this an image id, image or what?
							// TODO do we need a bigger image and a thumbnail?
	public String   email;
	public String   phone;
	
	public UserModel(int group_id, int user_id, String first_name, String last_name, Drawable image, String email, String phone) {
		this.group_id = group_id;
		this.user_id = user_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.image = image;
		this.email = email;
		this.phone = phone;
	}
	
	public String get_full_name() {
		return first_name + " " + last_name;
	}
	
	@Override
	public String toString() {
		return group_id + ", " + user_id + ", " + first_name + ", " + last_name + ", " + email + ", " + phone;
	}

}
