package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;
import com.p2c.thelife.model.AbstractDS;

public class MainEventsAdapter extends ArrayAdapter<EventModel> implements AbstractDS.DSChangedListener {
	
	private static final String TAG = "MainEventsAdapter"; 	
		
	public MainEventsAdapter(Context context, int mode) {
		super(context, mode);
				
		query();
	}
	
	// see ApiDemos List14.java for other (maybe better?) ways for this
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// convertView is null only once, at position 0

		// get the view
		View eventView = convertView;
		if (eventView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			eventView = inflator.inflate(R.layout.event_cell, null);
		}
		
		// get the event for this view
		EventModel event = getItem(position);
		UserModel user = TheLifeConfiguration.getUsersDS().findById(event.user_id);
		FriendModel friend = TheLifeConfiguration.getFriendsDS().findById(event.friend_id);

		TextView textViewDescription = (TextView)eventView.findViewById(R.id.textViewDescription);
		String eventDescription = event.description; // Utilities.fillTemplateString(getContext().getResources(), user, friend, event.description);
		textViewDescription.setText(Html.fromHtml(eventDescription));
		
		ImageView imageView1 = (ImageView)eventView.findViewById(R.id.imageView1);
		imageView1.setImageBitmap((user == null) ? TheLifeConfiguration.getMissingDataThumbnail() : (user != null) ? user.thumbnail : TheLifeConfiguration.getGenericPersonThumbnail());
		ImageView imageView2 = (ImageView)eventView.findViewById(R.id.imageView2);
		imageView2.setImageBitmap((friend == null) ? TheLifeConfiguration.getMissingDataThumbnail() : (friend != null) ? friend.thumbnail : TheLifeConfiguration.getGenericPersonThumbnail());		
		
		// only show the pledge view if the event requests it
		CheckBox pledgeView = (CheckBox)eventView.findViewById(R.id.pledgeView);				
		if (event.isPrayerRequested) {
			pledgeView.setVisibility(View.VISIBLE);
			String pledgeDescription = "Pray"; // TODO translated
			pledgeView.setText(pledgeDescription);			
		} else {
			pledgeView.setVisibility(View.GONE);
		}
		
		return eventView;
	}
	
	@Override
	public void notifyDSChanged() {
		
		// clear data and redo query
		clear();		
		query();
		
		// redisplay
		notifyDataSetChanged();
	}
	
	private void query() {
		
		// get all the events
		Collection<EventModel> events = TheLifeConfiguration.getEventsDS().findAll();
		for (EventModel m:events) {
			add(m);
		}				

	}			

}
