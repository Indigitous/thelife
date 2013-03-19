package com.p2c.thelife;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.p2c.thelife.model.DataStoreListener;
import com.p2c.thelife.model.GroupModel;
import com.p2c.thelife.model.UserModel;

/**
 * Show the user's groups and their members in an expandable list.
 */
public class ExpandableGroupsAdapter extends BaseExpandableListAdapter implements DataStoreListener {
	
	private TheLifeApplication m_app = null;
	private Context m_context = null;
	private ArrayList<GroupModel> m_groups = null;
	
	public ExpandableGroupsAdapter(Context context, int mode, TheLifeApplication app) {
		super();
		
		m_app = app;
		m_context = context;
		
		query();
	}
	
	
	@Override
	public void notifyDataChanged() {
		
		// redo query
		query();
		
		// redisplay
		notifyDataSetChanged();
	}
	
	private void query() {
		if (m_groups != null) {
			m_groups.clear();
		}
		m_groups = m_app.getGroupsDS().findAll();	
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
	
	

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		int userId = m_groups.get(groupPosition).member_ids.get(childPosition);
		return m_app.getUsersDS().findById(userId);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return m_groups.get(groupPosition).member_ids.get(childPosition);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		View childView = convertView;
		if (childView == null) {
			LayoutInflater inflator = LayoutInflater.from(m_context);
			childView = inflator.inflate(R.layout.group_member_cell, null);
		}	
		
		UserModel user = (UserModel)getChild(groupPosition, childPosition);
		
		ImageView imageView = (ImageView)childView.findViewById(R.id.group_member_image);
		imageView.setImageBitmap(user.image);		
		
		TextView nameView = (TextView)childView.findViewById(R.id.group_member_name);
		nameView.setText(user.get_full_name());
		
		return childView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return m_groups.get(groupPosition).member_ids.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return m_groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return m_groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return m_groups.get(groupPosition).id;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		View groupView = convertView;
		if (groupView == null) {
			LayoutInflater inflator = LayoutInflater.from(m_context);
			groupView = inflator.inflate(R.layout.group_cell, null);
		}
		
		GroupModel group = m_groups.get(groupPosition);
			
		TextView nameView = (TextView)groupView.findViewById(R.id.group_name);
		nameView.setText(group.name);
		
		// groupView.setTag(group);		
							
		return groupView;		
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}				

}
