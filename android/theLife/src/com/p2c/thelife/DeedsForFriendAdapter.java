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
import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

public class DeedsForFriendAdapter extends ArrayAdapter<DeedModel> implements DataStoreListener {
	
	private TheLifeApplication m_app;
	private FriendModel m_friend;
	
	public DeedsForFriendAdapter(Context context, int mode, TheLifeApplication app, FriendModel friend) {
		super(context, mode);
		
		m_app = app;
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
		textView.setText(Utilities.fillTemplateString(m_friend, deed.title));
		
		deedView.setTag(deed);		
					
		return deedView;
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
		// get all the Deeds for the current user		
		Collection<DeedModel> Activities = m_app.getDeedsDS().findByThreshold(m_friend.threshold);
		for (DeedModel m:Activities) {
			add(m);
		}		
	}

}
