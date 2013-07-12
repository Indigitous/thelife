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

import com.p2c.thelife.model.AbstractDS;
import com.p2c.thelife.model.RequestModel;
import com.p2c.thelife.model.UserModel;


/**
 * Manage the list of requests/notifications for the RequestsActivity screen.
 * @author clarence
 *
 */
public class RequestsAdapter extends ArrayAdapter<RequestModel> implements AbstractDS.DSChangedListener, BitmapNotifierHandler.UserBitmapListener {
	
	private static final String TAG = "RequestsAdapter"; 	
	
	
	public RequestsAdapter(Context context, int mode) {
		super(context, mode);
	
		query();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// get the view
		View requestView = convertView;
		if (requestView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			requestView = inflator.inflate(R.layout.request_cell, null);
		}
		
		// get the request for this view
		RequestModel request = getItem(position);

		TextView textViewDescription = (TextView)requestView.findViewById(R.id.textViewDescription);
		String description = request.finalDescription;
		textViewDescription.setText(Html.fromHtml(description));
		
		TextView textViewTime = (TextView)requestView.findViewById(R.id.request_time);
		String eventTime = DateUtils.getRelativeDateTimeString(getContext(), request.timestamp, 
			DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();		
		textViewTime.setText(eventTime);				
		
		ImageView imageView1 = (ImageView)requestView.findViewById(R.id.imageView1);
		imageView1.setImageBitmap(UserModel.getThumbnail(request.getAuthorId()));
		
		requestView.setTag(request);		
		
		return requestView;
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
		
		// get all the events
		Collection<RequestModel> requests = TheLifeConfiguration.getRequestsDS().findAll();
		for (RequestModel m:requests) {
			add(m);
		}				

	}

	@Override
	public void notifyUserBitmap(int userId) {
		// redisplay
		notifyDataSetChanged();		
	}			

}
