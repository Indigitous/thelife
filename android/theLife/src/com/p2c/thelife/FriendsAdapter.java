package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.p2c.thelife.model.AbstractDS;
import com.p2c.thelife.model.FriendModel;


/**
 * Show the friends of the owner.
 * @author clarence
 *
 */
public class FriendsAdapter 
	extends ArrayAdapter<FriendModel> 
	implements AbstractDS.DSChangedListener, BitmapNotifierHandler.FriendBitmapListener {
	
	public FriendsAdapter(Context context, int mode) {
		super(context, mode);
				
		query();
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View friendView = convertView;
		if (friendView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			friendView = inflator.inflate(R.layout.friend_cell, null);
		}
		
		// set up the view for the friend
		FriendModel friend = getItem(position);
		FriendImageView friendImageView = (FriendImageView)friendView.findViewById(R.id.friend_image);
		friendImageView.setData(FriendModel.getImage(friend.id), friend.getFullName(), friend.getThresholdMediumString(getContext().getResources()));
		
		friendView.setTag(friend);
			
		return friendView;
	}
	
	
	@Override
	public void notifyDSChanged(ArrayList<Integer> oldModelIds, ArrayList<Integer> newModelIds) {
		
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

	
	@Override
	public void notifyFriendBitmap(int friendId) {
		// redisplay
		notifyDataSetChanged();		
	}		

}
