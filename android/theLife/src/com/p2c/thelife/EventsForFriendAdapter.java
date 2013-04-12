package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.text.Html;
import android.util.Log;
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

		TextView textViewDescription = (TextView)eventView.findViewById(R.id.textViewDescription);
		String eventDescription = event.description; // Utilities.fillTemplateString(getContext().getResources(), user, friend, event.description);
		textViewDescription.setText(Html.fromHtml(eventDescription));
		
		ImageView imageView1 = (ImageView)eventView.findViewById(R.id.imageView1);
		imageView1.setImageBitmap(UserModel.getThumbnail(event.user_id, false));
		ImageView imageView2 = (ImageView)eventView.findViewById(R.id.imageView2);
		imageView2.setImageBitmap(FriendModel.getThumbnail(event.friend_id, false));			
		
		// only show the pledge view if the event requests it
		CheckBox pledgeView = (CheckBox)eventView.findViewById(R.id.pledgeView);				
		if (event.isPrayerRequested) {
			pledgeView.setVisibility(View.VISIBLE);
			String pledgeDescription = getContext().getResources().getString(R.string.pray) + " " + event.pledgeCount;			
			pledgeView.setText(pledgeDescription);			
		} else {
			pledgeView.setVisibility(View.GONE);
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
