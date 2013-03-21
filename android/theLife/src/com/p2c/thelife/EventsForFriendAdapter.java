package com.p2c.thelife;

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

import com.p2c.thelife.model.DataStoreListener;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;

public class EventsForFriendAdapter extends ArrayAdapter<EventModel> implements DataStoreListener {
	
	private static final String TAG = "DeedsDS"; 
	
	private FriendModel m_friend = null;
	
	public EventsForFriendAdapter(Context context, int mode, FriendModel friend) {
		super(context, mode);
		
		m_friend = friend;
		
		query();
	}
	
	// see ApiDemos List14.java for other (maybe better?) ways for this
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
		UserModel user = TheLifeConfiguration.getUsersDS().findById(event.user_id);
		FriendModel friend = TheLifeConfiguration.getFriendsDS().findById(event.friend_id);
		
		TextView textViewDescription = (TextView)eventView.findViewById(R.id.textViewDescription);
		String eventDescription = Utilities.fillTemplateString(user, friend, event.description);
		textViewDescription.setText(Html.fromHtml(eventDescription));
		
		ImageView imageView1 = (ImageView)eventView.findViewById(R.id.imageView1);
		imageView1.setImageBitmap(user.thumbnail);
		ImageView imageView2 = (ImageView)eventView.findViewById(R.id.imageView2);
		imageView2.setImageBitmap(friend.thumbnail);		
		
		// only show the pledge view if the event requests it
		CheckBox pledgeView = (CheckBox)eventView.findViewById(R.id.pledgeView);				
		if (event.isPledge) {
			pledgeView.setVisibility(View.VISIBLE);
			String pledgeDescription = Utilities.fillTemplateString(user, friend, "Pray for $u and $f."); // TODO translated
			pledgeView.setText(pledgeDescription);			
		} else {
			pledgeView.setVisibility(View.GONE);
		}
		
		return eventView;
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
		// get all the Events for the current user
		Collection<EventModel> events = TheLifeConfiguration.getEventsDS().findByFriend(m_friend.friend_id);
		for (EventModel m:events) {
			add(m);
		}
		Log.d(TAG, "FOUND EVENTS FOR FRIEND " + m_friend + ": " + getCount());
	}	

}
