package com.p2c.thelife;

import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;

public class Utilities {
	
	public static String fill_template_string(FriendModel friend, String template_string) {
		return template_string.replace("$f", friend.first_name);
	}
	
	public static String fill_template_string(UserModel user, FriendModel friend, String template_string) {
		String s = template_string.replace("$u", user.first_name);
		return s.replace("$f", friend.first_name);
	}	

}
