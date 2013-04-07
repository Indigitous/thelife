package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.p2c.thelife.model.AbstractDS;
import com.p2c.thelife.model.RequestModel;
import com.p2c.thelife.model.UserModel;


public class RequestsAdapter extends ArrayAdapter<RequestModel> implements AbstractDS.DSChangedListener {
	
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
		UserModel user = TheLifeConfiguration.getUsersDS().findById(request.user_id);

		TextView textViewDescription = (TextView)requestView.findViewById(R.id.textViewDescription);
		String description = request.description;
		textViewDescription.setText(Html.fromHtml(description));
		
		ImageView imageView1 = (ImageView)requestView.findViewById(R.id.imageView1);
		imageView1.setImageBitmap((user == null) ? TheLifeConfiguration.getMissingDataThumbnail() : (user != null) ? user.thumbnail : TheLifeConfiguration.getGenericPersonThumbnail());
		
		requestView.setTag(request);		
		
		return requestView;
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
		Collection<RequestModel> requests = TheLifeConfiguration.getRequestsDS().findAll();
		for (RequestModel m:requests) {
			add(m);
		}				

	}			

}