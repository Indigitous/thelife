package com.p2c.thelife;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.p2c.thelife.model.AbstractDS;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.GroupUsersDS;
import com.p2c.thelife.model.UserModel;

public class GroupAdapter extends ArrayAdapter<UserModel> implements AbstractDS.DSChangedListener {
	
	private GroupModel m_group;
	private GroupUsersDS m_groupUsersDS;
	
	public GroupAdapter(Context context, int mode, GroupModel group, GroupUsersDS groupUsersDS) {
		super(context, mode);
		
		m_group = group;
		m_groupUsersDS = groupUsersDS;
		
		query();
	}
	
	// TODO: see ApiDemos List14.java for other (maybe better?) ways for this
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View userView = convertView;
		if (userView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			userView = inflator.inflate(R.layout.user_cell, null);
		}
		
		UserModel user = getItem(position);
		userView.setTag(user);
		
		ImageView imageView = (ImageView)userView.findViewById(R.id.user_image);
		imageView.setImageBitmap((user != null) ? user.image : TheLifeConfiguration.getGenericPersonImage());
		
		TextView textView = (TextView)userView.findViewById(R.id.user_name);
		textView.setText((user != null) ? user.getFullName() : "?");
		
		// show the group leader in bold and italics
		Typeface typeface = textView.getTypeface();
		textView.setTypeface(typeface, (user != null && m_group.leader_id == user.id) ? 3 : 0);
	
		return userView;
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
		
		ArrayList<UserModel> users = m_groupUsersDS.findAll();
		
		for (UserModel m:users) {
			add(m);
		}
	}	

}
