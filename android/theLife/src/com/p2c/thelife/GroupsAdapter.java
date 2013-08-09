package com.p2c.thelife;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.p2c.thelife.config.TheLifeConfiguration;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.AbstractDS;


/**
 * Show the owner's groups in a list.
 * @author clarence
 *
 */
public class GroupsAdapter extends ArrayAdapter<GroupModel> implements AbstractDS.DSChangedListener {
		
	public GroupsAdapter(Context context, int mode) {
		super(context, mode);
		
		query();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View groupView = convertView;
		if (groupView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			groupView = inflator.inflate(R.layout.group_cell, null);
		}
		
		GroupModel group = getItem(position);
			
		TextView nameView = (TextView)groupView.findViewById(R.id.group_name);
		nameView.setTextSize(20.0f);
		nameView.setText(group.name);
		TextView descriptionView = (TextView)groupView.findViewById(R.id.group_description);
		descriptionView.setText(group.description);		
		
		groupView.setTag(group);  		
							
		return groupView;     
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
		
		// get all the Groups for the current user
		Collection<GroupModel> Groups = TheLifeConfiguration.getGroupsDS().findAll();
		for (GroupModel g:Groups) {
			add(g);
		}		

	}
	

}
