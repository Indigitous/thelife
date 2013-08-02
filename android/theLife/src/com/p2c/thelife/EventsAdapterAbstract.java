package com.p2c.thelife;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.p2c.thelife.model.AbstractDS;
import com.p2c.thelife.model.EventModel;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;
import com.p2c.thelife.view.EventUserFriendView;


/**
 * Superclass for showing Event Cells in a list.
 * @author clarence
 *
 */
public abstract class EventsAdapterAbstract 
	extends ArrayAdapter<EventModel> 
	implements AbstractDS.DSChangedListener, BitmapNotifierHandler.UserBitmapListener, BitmapNotifierHandler.FriendBitmapListener {
	
	private static final String TAG = "EventsAdapterAbstract";
	
	private Bitmap m_deedTalkBitmap = null;
	private Bitmap m_deedMoveBitmap = null;
	
		
	public EventsAdapterAbstract(Context context, int mode) {
		super(context, mode);
		
		m_deedTalkBitmap = Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.activity_talk));
		m_deedMoveBitmap = Utilities.getBitmapFromDrawable(context.getResources().getDrawable(R.drawable.activity_move));		
	}
	
	
	/**
	 * Query the data store for the events.
	 */
	protected abstract void query();

	
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
		textViewTime.setText(eventTime);		
		
		EventUserFriendView eventUserFriendView = (EventUserFriendView)eventView.findViewById(R.id.event_images);
		eventUserFriendView.setBitmaps(UserModel.getThumbnail(event.user_id), 
			(event.threshold != null) ? m_deedMoveBitmap : m_deedTalkBitmap, 
			FriendModel.getThumbnail(event.friend_id));
		
		// only show the pledge view if the event requests it
		ToggleButton pledgeView = (ToggleButton)eventView.findViewById(R.id.event_pledge);
		TextView peoplePrayedView = (TextView)eventView.findViewById(R.id.event_people_prayed);
		if (event.isPrayerRequested) {
			
			// only show the pledge icon if not the event is not from the owner
			pledgeView.setVisibility((event.user_id == TheLifeConfiguration.getOwnerDS().getId()) ? View.GONE : View.VISIBLE);
			
			pledgeView.setChecked(event.hasPledged);
			if (pledgeView.isChecked()) {
				pledgeView.setClickable(false);
				pledgeView.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.pray_active), null, null, null);
			} else {
				pledgeView.setClickable(true);
				pledgeView.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.pray_normal), null, null, null);				
			}
			
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
	
	
	@Override
	public void notifyDSChanged(ArrayList<Integer> oldModelIds, ArrayList<Integer> newModelIds) {
		
		// clear data and redo query
		clear();		
		query();
		
		// redisplay
		notifyDataSetChanged();
	}	
	
	
	@Override
	public void notifyUserBitmap(int userId) {
		// redisplay
		notifyDataSetChanged();
	}
	
	
	@Override
	public void notifyFriendBitmap(int friendId) {
		// redisplay
		notifyDataSetChanged();
	}	
}
