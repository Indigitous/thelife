package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.p2c.thelife.model.DataStoreListener;
import com.p2c.thelife.model.FriendModel;

public class FriendsAdapter extends ArrayAdapter<FriendModel> implements DataStoreListener {
	
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
		nameView.setText(friend.first_name);
		
		TextView thresholdView = (TextView)friendView.findViewById(R.id.friend_threshold);
		thresholdView.setText(friend.get_threshold_short_string(getContext().getResources()));
		
		friendView.setTag(friend);
			
		return friendView;
	}
	
	@Override
	public void notifyDataChanged() {
		
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
