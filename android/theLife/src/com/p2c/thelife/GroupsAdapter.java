package com.p2c.thelife;

import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.p2c.thelife.model.DataStoreListener;
import com.p2c.thelife.model.GroupModel;

public class GroupsAdapter extends ArrayAdapter<GroupModel> implements DataStoreListener {
		
	public GroupsAdapter(Context context, int mode) {
		super(context, mode);
		
		query();
	}
	
	// TODO see ApiDemos List14.java for other (maybe better?) ways for this
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View groupView = convertView;
		if (groupView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			groupView = inflator.inflate(R.layout.group_cell, null);
		}
		
		GroupModel group = getItem(position);
			
		// TODO this could be done with a simple string array adapter, instead of this custom class
		TextView nameView = (TextView)groupView.findViewById(R.id.group_name);
		nameView.setTextSize(20.0f);
		nameView.setText(group.name);
		
		groupView.setTag(group);  		
							
		return groupView;     
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
		
		// get all the Groups for the current user
		Collection<GroupModel> Groups = TheLifeConfiguration.getGroupsDS().findAll();
		for (GroupModel f:Groups) {
			add(f);
		}		

	}
	

}
