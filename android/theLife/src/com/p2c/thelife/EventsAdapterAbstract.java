package com.p2c.thelife;

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

public abstract class EventsAdapterAbstract extends ArrayAdapter<EventModel> implements AbstractDS.DSChangedListener {
	
	private static final String TAG = "EventsAdapterAbstract"; 	
		
	public EventsAdapterAbstract(Context context, int mode) {
		super(context, mode);
	}		

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// get the view
		View eventView = convertView;
		if (eventView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			eventView = inflator.inflate(R.layout.event_cell, null);
		}
		
		// get the event for this view
		EventModel event = getItem(position);

		TextView textViewDescription = (TextView)eventView.findViewById(R.id.event_description);
		String eventDescription = event.finalDescription;
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
			
			// show the pledge count
			if (event.pledgeCount == 1) {
				peoplePrayedView.setText(getContext().getResources().getString(R.string.people_prayed_singular));
			} else {
				String prayedText = getContext().getResources().getString(R.string.people_prayed_plural, event.pledgeCount);
				peoplePrayedView.setText(prayedText);
			}

			pledgeView.setTag(event);
		} else {
			pledgeView.setVisibility(View.GONE);
			peoplePrayedView.setVisibility(View.GONE);			
		}
		
		return eventView;
	}
}
