package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivitiesForFriendAdapter extends ArrayAdapter<ActivityModel> implements DataStoreListener {
	
	private TheLifeApplication m_app;
	private FriendModel m_friend;
	
	public ActivitiesForFriendAdapter(Context context, int mode, TheLifeApplication app, FriendModel friend) {
		super(context, mode);
		
		m_app = app;
		m_friend = friend;
		
		query();
	}
	
	// see ApiDemos List14.java for other (maybe better?) ways for this
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View activityView = convertView;
		if (activityView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			activityView = inflator.inflate(R.layout.activity_cell, null);
		}
		
		ActivityModel activity = getItem(position);
		
		ImageView imageView = (ImageView)activityView.findViewById(R.id.activity_image);
		imageView.setImageDrawable(activity.image);
		
		TextView textView = (TextView)activityView.findViewById(R.id.activity_title);
		textView.setText(Utilities.fill_template_string(m_friend, activity.title));
		
		activityView.setTag(activity);		
					
		return activityView;
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
		// get all the Activities for the current user		
		Collection<ActivityModel> Activities = m_app.getActivitiesDS().findByThreshold(m_friend.threshold);
		for (ActivityModel m:Activities) {
			add(m);
		}		
	}

}
