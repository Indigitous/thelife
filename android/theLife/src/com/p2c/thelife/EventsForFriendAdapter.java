package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.p2c.thelife.model.AbstractDS;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;

public class EventsForFriendAdapter extends ArrayAdapter<EventModel> implements AbstractDS.DSChangedListener {
	
	private static final String TAG = "DeedsDS"; 
	
	private FriendModel m_friend = null;
	
	public EventsForFriendAdapter(Context context, int mode, FriendModel friend) {
		super(context, mode);
		
		m_friend = friend;
		
		query();
	}
	
	// TODO this routine is duplicated
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// get the view
		View eventView = convertView;
		if (eventView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			eventView = inflater.inflate(R.layout.event_cell, null);
		}
		
		// get the event for this view
		EventModel event = getItem(position);		
		
		TextView textViewDescription = (TextView)eventView.findViewById(R.id.event_description);
		String eventDescription = event.description; // Utilities.fillTemplateString(getContext().getResources(), user, friend, event.description);
		textViewDescription.setText(Html.fromHtml(eventDescription));
		
		TextView textViewTime = (TextView)eventView.findViewById(R.id.event_time);
		String eventTime = DateUtils.getRelativeDateTimeString(getContext(), event.timestamp, 
			DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();		
		textViewTime.setText(Html.fromHtml(eventTime));		
		
		ImageView imageView1 = (ImageView)eventView.findViewById(R.id.event_image1);
		imageView1.setImageBitmap(UserModel.getThumbnail(event.user_id, false));
		ImageView imageView2 = (ImageView)eventView.findViewById(R.id.event_image2);
		imageView2.setImageBitmap(FriendModel.getThumbnail(event.friend_id, false));		
		
		// only show the pledge view if the event requests it
		ToggleButton pledgeView = (ToggleButton)eventView.findViewById(R.id.event_pledge);
		TextView peoplePrayedView = (TextView)eventView.findViewById(R.id.event_people_prayed);
		if (event.isPrayerRequested) {
			pledgeView.setVisibility(View.VISIBLE);
			peoplePrayedView.setVisibility(View.VISIBLE);
//			String pledgeDescription = getContext().getResources().getString(R.string.pray) + " " + event.pledgeCount;
//			pledgeView.setText(pledgeDescription);	
			pledgeView.setTag(event);
		} else {
			pledgeView.setVisibility(View.GONE);
			peoplePrayedView.setVisibility(View.GONE);			
		}
		
		return eventView;
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
		// get all the Events for the current user
		Collection<EventModel> events = TheLifeConfiguration.getEventsDS().findByFriend(m_friend.id);
		for (EventModel m:events) {
			add(m);
		}
	}	

}
