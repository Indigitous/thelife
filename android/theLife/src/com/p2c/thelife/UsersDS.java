package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;


// TODO need to know current user?
public class UsersDS {
	
	private ArrayList<UserModel> m_data = new ArrayList<UserModel>();
	
	public UsersDS(Context context) {
		m_data.add(new UserModel(1, 1, "Geoff", "Moore", context.getResources().getDrawable(R.drawable.moore), "john@martin@p2c.com", "555-123-4567"));
		m_data.add(new UserModel(1, 2, "Mark", "Twain", context.getResources().getDrawable(R.drawable.twain),"john@martin@p2c.com", "555-123-4567"));
		m_data.add(new UserModel(1, 3, "Robert", "Rashdall", context.getResources().getDrawable(R.drawable.rashdall), "john@martin@p2c.com", "555-123-4567"));
		m_data.add(new UserModel(2, 1, "Tak", "Inouye", context.getResources().getDrawable(R.drawable.inouye), "john@martin@p2c.com", "555-123-4567"));
		m_data.add(new UserModel(2, 2, "Nelson", "Schock", context.getResources().getDrawable(R.drawable.schock), "john@martin@p2c.com", "555-123-4567"));	
	}
	
	public Collection<UserModel> findAll() {
		return m_data;
	}

	public UserModel findById(int groupId, int userId) {
		
		for (UserModel m:m_data) {
			if (m.group_id == groupId && m.user_id == userId) {
				return m;
			}		
		}
		
		return null;
	}

}
