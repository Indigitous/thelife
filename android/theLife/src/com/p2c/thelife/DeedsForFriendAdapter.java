package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.AbstractDS;

public class DeedsForFriendAdapter extends ArrayAdapter<DeedModel> implements AbstractDS.DSListener {
	
	private FriendModel m_friend;
	
	public DeedsForFriendAdapter(Context context, int mode, FriendModel friend) {
		super(context, mode);
		
		m_friend = friend;
		
		query();
	}
	
	// see ApiDemos List14.java for other (maybe better?) ways for this
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View deedView = convertView;
		if (deedView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			deedView = inflator.inflate(R.layout.deed_cell, null);
		}
		
		DeedModel deed = getItem(position);

		ImageView imageView = (ImageView)deedView.findViewById(R.id.deed_image);
		imageView.setImageBitmap(deed.image);
		
		TextView textView = (TextView)deedView.findViewById(R.id.deed_title);
		textView.setText(Utilities.fillTemplateString(getContext().getResources(), m_friend, deed.title));
		
		deedView.setTag(deed);		
					
		return deedView;
	}

	@Override
	public void notifyDSChanged() {
		
		// clear data and redo local query
		clear();		
		query();
		
		// redisplay
		notifyDataSetChanged();
	}
	
	/**
	 * local query
	 */
	private void query() {
		// get all the Deeds for the current user		
		Collection<DeedModel> Activities = TheLifeConfiguration.getDeedsDS().findByThreshold(m_friend.threshold);
		for (DeedModel m:Activities) {
			add(m);
		}		
	}

}
