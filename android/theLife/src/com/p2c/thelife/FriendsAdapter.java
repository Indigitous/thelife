package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.AbstractDS;

public class FriendsAdapter extends ArrayAdapter<FriendModel> implements AbstractDS.DSChangedListener {
	
	public FriendsAdapter(Context context, int mode) {
		super(context, mode);
				
		query();
	}
	
	// see ApiDemos List14.java for other (maybe better?) ways for this
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View friendView = convertView;
		if (friendView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			friendView = inflator.inflate(R.layout.friend_cell, null);
		}
		
		FriendModel friend = getItem(position);
		
		ImageView imageView = (ImageView)friendView.findViewById(R.id.friend_image);
		imageView.setImageBitmap(friend.image);
		
		TextView nameView = (TextView)friendView.findViewById(R.id.friend_name);
		nameView.setText(friend.firstName);
		
		TextView thresholdView = (TextView)friendView.findViewById(R.id.friend_threshold);
		thresholdView.setText(friend.getThresholdShortString(getContext().getResources()));
		
		friendView.setTag(friend);
			
		return friendView;
	}
	
	@Override
	public void notifyDSChanged(ArrayList<Integer> modelIds) {
		
		// clear data and redo query
		clear();		
		query();
		
		// redisplay
		notifyDataSetChanged();
	}
	
	private void query() {
		// get all the friends for the current user
		Collection<FriendModel> friends = TheLifeConfiguration.getFriendsDS().findAll();
		for (FriendModel f:friends) {
			add(f);
		}
	}		

}
