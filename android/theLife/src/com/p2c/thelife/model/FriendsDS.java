package com.p2c.thelife.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeApplication;


public class FriendsDS extends AbstractDS<FriendModel> {
	
	public FriendsDS(Context context) {
		
		super(
				context, 
				"FriendsDS", 
				context.getCacheDir().getAbsolutePath() + "/friends.json",
				"refresh_friends_timestamp_key",
				"http://thelife.ballistiq.com/friends.json",
				"refresh_friends_delta_key",
				TheLifeApplication.REFRESH_FRIENDS_DELTA
			);		
		
		m_data.add(new FriendModel(1, "1Thomas", "Smith", context.getResources().getDrawable(R.drawable.hardy), FriendModel.Threshold.Entering));
		m_data.add(new FriendModel(2, "2Abraham", "Jones", context.getResources().getDrawable(R.drawable.lincoln), FriendModel.Threshold.New_Contact));
		m_data.add(new FriendModel(3, "3Clarence", "Robertson", context.getResources().getDrawable(R.drawable.clarence), FriendModel.Threshold.Open));
		m_data.add(new FriendModel(4, "4Nola", "Johnson", context.getResources().getDrawable(R.drawable.treeswater), FriendModel.Threshold.Seeking));
		m_data.add(new FriendModel(5, "5Charles", "Martin", context.getResources().getDrawable(R.drawable.darwin), FriendModel.Threshold.New_Contact));
		m_data.add(new FriendModel(6, "6Thomas", "Trudeau", context.getResources().getDrawable(R.drawable.hardy), FriendModel.Threshold.Christian));
		m_data.add(new FriendModel(7, "7Abraham", "Harper", context.getResources().getDrawable(R.drawable.lincoln), FriendModel.Threshold.Seeking));
		m_data.add(new FriendModel(8, "8Clarence", "Findlay", context.getResources().getDrawable(R.drawable.clarence), FriendModel.Threshold.Christian));
		m_data.add(new FriendModel(9, "9Charles", "Garneau", context.getResources().getDrawable(R.drawable.darwin), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(10, "10Abraham", "Gagnon", context.getResources().getDrawable(R.drawable.lincoln), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(11, "11Charles", "Teo", context.getResources().getDrawable(R.drawable.darwin), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(12, "12Henry", "Strike", context.getResources().getDrawable(R.drawable.henry4), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(13, "13Nola", "Martinez", context.getResources().getDrawable(R.drawable.treeswater), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(14, "14Charles", "Kerry", context.getResources().getDrawable(R.drawable.darwin), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(15, "15Abraham", "Palin", context.getResources().getDrawable(R.drawable.lincoln), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(16, "16Thomas", "Brown", context.getResources().getDrawable(R.drawable.hardy), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(17, "17Clarence", "Strombolopolous", context.getResources().getDrawable(R.drawable.clarence), FriendModel.Threshold.Trusting));
		m_data.add(new FriendModel(18, "18Nola", "Woo", context.getResources().getDrawable(R.drawable.treeswater), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(19, "19Henry", "Chow", context.getResources().getDrawable(R.drawable.henry4), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(20, "20Clarence", "Wang", context.getResources().getDrawable(R.drawable.clarence), FriendModel.Threshold.Curious));		
	}
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected FriendModel createFromJSON(Context context, JSONObject json) throws JSONException {
		return null; // EventModel.fromJSON(json);
	}	

}
