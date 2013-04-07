package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Application;
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
	
	private Application m_app = null;
		
	public RequestsAdapter(Context context, int mode, Application app) {
		super(context, mode);
	
		m_app = app;
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
	public void notifyDSChanged(ArrayList<Integer> modelIds) {
		
		// clear data and redo query
		clear();		
		query();
		
		// redisplay
		notifyDataSetChanged();
		
		// create notifications for each of the new requests
		// TODO finish this -- but don't overwhelm Android with a full reset notification list each time
//		for (Integer id:modelIds) {
//			NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
//			builder.setSmallIcon(R.drawable.ic_launcher); // TODO real icon
//			builder.setContentTitle("theLife");
//			builder.setContentText("A request to join your group has been received.");
//			
//			NotificationManager notificationManager = (NotificationManager)m_app.getSystemService(Context.NOTIFICATION_SERVICE);
//			notificationManager.notify(id, builder.build());
//		}
	}
	
	private void query() {
		
		// get all the events
		Collection<RequestModel> requests = TheLifeConfiguration.getRequestsDS().findAll();
		for (RequestModel m:requests) {
			add(m);
		}				

	}			

}
