package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;


public class FriendsDS {
	
	private ArrayList<FriendModel> m_data = new ArrayList<FriendModel>();
	
	public FriendsDS(Context context) {
		m_data.add(new FriendModel(1, 1, "1Thomas", "Smith", context.getResources().getDrawable(R.drawable.hardy), FriendModel.Threshold.Entering));
		m_data.add(new FriendModel(1, 2, "2Abraham", "Jones", context.getResources().getDrawable(R.drawable.lincoln), FriendModel.Threshold.New_Contact));
		m_data.add(new FriendModel(1, 3, "3Clarence", "Robertson", context.getResources().getDrawable(R.drawable.clarence), FriendModel.Threshold.Open));
		m_data.add(new FriendModel(1, 4, "4Nola", "Johnson", context.getResources().getDrawable(R.drawable.treeswater), FriendModel.Threshold.Seeking));
		m_data.add(new FriendModel(1, 5, "5Charles", "Martin", context.getResources().getDrawable(R.drawable.darwin), FriendModel.Threshold.New_Contact));
		m_data.add(new FriendModel(1, 6, "6Thomas", "Trudeau", context.getResources().getDrawable(R.drawable.hardy), FriendModel.Threshold.Christian));
		m_data.add(new FriendModel(1, 7, "7Abraham", "Harper", context.getResources().getDrawable(R.drawable.lincoln), FriendModel.Threshold.Seeking));
		m_data.add(new FriendModel(1, 8, "8Clarence", "Findlay", context.getResources().getDrawable(R.drawable.clarence), FriendModel.Threshold.Christian));
		m_data.add(new FriendModel(1, 9, "9Charles", "Garneau", context.getResources().getDrawable(R.drawable.darwin), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(1, 10, "10Abraham", "Gagnon", context.getResources().getDrawable(R.drawable.lincoln), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(1, 11, "11Charles", "Teo", context.getResources().getDrawable(R.drawable.darwin), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(1, 12, "12Henry", "Strike", context.getResources().getDrawable(R.drawable.henry4), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(2, 1, "13Nola", "Martinez", context.getResources().getDrawable(R.drawable.treeswater), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(2, 2, "14Charles", "Kerry", context.getResources().getDrawable(R.drawable.darwin), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(2, 3, "15Abraham", "Palin", context.getResources().getDrawable(R.drawable.lincoln), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(2, 4, "16Thomas", "Brown", context.getResources().getDrawable(R.drawable.hardy), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(2, 5, "17Clarence", "Strombolopolous", context.getResources().getDrawable(R.drawable.clarence), FriendModel.Threshold.Trusting));
		m_data.add(new FriendModel(2, 6, "18Nola", "Woo", context.getResources().getDrawable(R.drawable.treeswater), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(2, 7, "19Henry", "Chow", context.getResources().getDrawable(R.drawable.henry4), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(2, 8, "20Clarence", "Wang", context.getResources().getDrawable(R.drawable.clarence), FriendModel.Threshold.Curious));		
	}
	
	public Collection<FriendModel> findAll() {
		return m_data;
	}

	public FriendModel findById(int groupId, int friendId) {
		
		for (FriendModel f:m_data) {
			if (f.group_id == groupId && f.friend_id == friendId) {
				return f;
			}		
		}
		
		return null;
	}

}
