package com.p2c.thelife.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.p2c.thelife.R;
import com.p2c.thelife.TheLifeApplication;
import com.p2c.thelife.Utilities;


public class FriendsDS extends AbstractDS<FriendModel> {
	
	public FriendsDS(Context context) {
		
		super(
				context, 
				"FriendsDS", 
				context.getCacheDir().getAbsolutePath() + "/friends.json",
				"refresh_friends_timestamp_key",
				TheLifeApplication.SERVER_URL + "friends.json",
				"refresh_friends_delta_key",
				TheLifeApplication.REFRESH_FRIENDS_DELTA
			);		
		
		m_data.add(new FriendModel(1, "1Thomas", "Smith", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.hardy)), FriendModel.Threshold.Entering));
		m_data.add(new FriendModel(2, "2Abraham", "Jones", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.lincoln)), FriendModel.Threshold.New_Contact));
		m_data.add(new FriendModel(3, "3Clarence", "Robertson", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.clarence)), FriendModel.Threshold.Open));
		m_data.add(new FriendModel(4, "4Nola", "Johnson", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.treeswater)), FriendModel.Threshold.Seeking));
		m_data.add(new FriendModel(5, "5Charles", "Martin", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.darwin)), FriendModel.Threshold.New_Contact));
		m_data.add(new FriendModel(6, "6Thomas", "Trudeau", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.hardy)), FriendModel.Threshold.Christian));
		m_data.add(new FriendModel(7, "7Abraham", "Harper", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.lincoln)), FriendModel.Threshold.Seeking));
		m_data.add(new FriendModel(8, "8Clarence", "Findlay", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.clarence)), FriendModel.Threshold.Christian));
		m_data.add(new FriendModel(9, "9Charles", "Garneau", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.darwin)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(10, "10Abraham", "Gagnon", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.lincoln)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(11, "11Charles", "Teo", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.darwin)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(12, "12Henry", "Strike", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.henry4)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(13, "13Nola", "Martinez", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.treeswater)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(14, "14Charles", "Kerry", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.darwin)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(15, "15Abraham", "Palin", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.lincoln)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(16, "16Thomas", "Brown", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.hardy)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(17, "17Clarence", "Strombolopolous", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.clarence)), FriendModel.Threshold.Trusting));
		m_data.add(new FriendModel(18, "18Nola", "Woo", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.treeswater)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(19, "19Henry", "Chow", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.henry4)), FriendModel.Threshold.Curious));
		m_data.add(new FriendModel(20, "20Clarence", "Wang", Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.clarence)), FriendModel.Threshold.Curious));		
	}
	
	/**
	 * Needed by the abstract superclass.
	 */
	protected FriendModel createFromJSON(JSONObject json, boolean useServer) throws JSONException {
		return null; // EventModel.fromJSON(json);
	}	

}
