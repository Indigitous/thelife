package com.p2c.thelife;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;

public class Utilities {
	
	private static final String TAG = "Utilities";
	
	public static String fillTemplateString(FriendModel friend, String template_string) {
		return template_string.replace("$f", friend.first_name);
	}
	
	public static String fillTemplateString(UserModel user, FriendModel friend, String template_string) {
		String s = template_string.replace("$u", (user == null) ? "user" : user.first_name); // TODO translation		
		return s.replace("$f", (friend == null) ? "friend" : friend.first_name); // TODO translation
	}
	
	public static Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		} else {
			throw new IllegalArgumentException();
		}
			
	}

}
