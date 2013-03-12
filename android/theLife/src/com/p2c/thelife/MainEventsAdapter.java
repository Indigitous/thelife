package com.p2c.thelife;

import java.util.Collection;

import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MainEventsAdapter extends ArrayAdapter<EventModel> {
	
	private TheLifeApplication m_app = null;
	
	public MainEventsAdapter(Context context, int mode, TheLifeApplication app) {
		super(context, mode);
		
		m_app = app;
		
		// get all the events
		Collection<EventModel> events = m_app.getEventsDS().findAll();
		for (EventModel m:events) {
			add(m);
		}		
	}
	
	// see ApiDemos List14.java for other (maybe better?) ways for this
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// convertView is null only once, at position 0
		if (convertView != null) {
			System.out.println("AT POSITION " + Integer.toString(position) + ":  convertView is " + convertView.getClass().toString());
		}
		else {
			System.out.println("AT POSITION " + Integer.toString(position) + ":  convertView is NULL");
		}

		// get the view
		View eventView = convertView;
		if (eventView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			eventView = inflator.inflate(R.layout.event_cell, null);
		}
		
		// get the event for this view
		EventModel event = getItem(position);
		UserModel user = m_app.getUsersDS().findById(event.group_id, event.user_id);
		FriendModel friend = m_app.getFriendsDS().findById(event.group_id, event.friend_id);
		
		TextView textViewDescription = (TextView)eventView.findViewById(R.id.textViewDescription);
		String eventDescription = Utilities.fill_template_string(user, friend, event.description);
		textViewDescription.setText(Html.fromHtml(eventDescription));
		
		ImageView imageView1 = (ImageView)eventView.findViewById(R.id.imageView1);
		imageView1.setImageDrawable(user.image);
		ImageView imageView2 = (ImageView)eventView.findViewById(R.id.imageView2);
		imageView2.setImageDrawable(friend.image);		
		
		// only show the pledge view if the event requests it
		CheckBox pledgeView = (CheckBox)eventView.findViewById(R.id.pledgeView);				
		if (event.isPledge) {
			pledgeView.setVisibility(View.VISIBLE);
			String pledgeDescription = Utilities.fill_template_string(user, friend, "Pray for $u and $f."); // TODO translated
			pledgeView.setText(pledgeDescription);			
		} else {
			pledgeView.setVisibility(View.GONE);
		}
		
		return eventView;
	}

}
