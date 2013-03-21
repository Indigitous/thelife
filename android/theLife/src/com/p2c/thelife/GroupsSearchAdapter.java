package com.p2c.thelife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.p2c.thelife.model.GroupModel;

public class GroupsSearchAdapter extends ArrayAdapter<GroupModel> {
	
	private static final String TAG = "GroupsSearchAdapter"; 
		
	public GroupsSearchAdapter(Context context, int mode) {
		super(context, mode);
				
		query();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			
		// get the group for this view
		GroupModel group = getItem(position);

		View groupView = convertView;
		if (groupView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			groupView = inflator.inflate(R.layout.group_cell, null);
		}
				
		// TODO this could be done with a simple string array adapter, instead of this custom class
		TextView nameView = (TextView)groupView.findViewById(R.id.group_name);
		nameView.setTextSize(20.0f);
		nameView.setText(group.name);
		
		groupView.setTag(group);  		
							
		return groupView;     		
	}
	
	
	private void query() {

		clear();
		// dummy data for now
		add(TheLifeConfiguration.getGroupsDS().findById(1));
		add(TheLifeConfiguration.getGroupsDS().findById(2));

	}	

}
