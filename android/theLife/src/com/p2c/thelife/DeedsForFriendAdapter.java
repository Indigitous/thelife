package com.p2c.thelife;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.p2c.thelife.model.AbstractDS;
import com.p2c.thelife.model.CategoryModel;
import com.p2c.thelife.model.DeedModel;
import com.p2c.thelife.model.FriendModel;

public class DeedsForFriendAdapter extends BaseExpandableListAdapter implements AbstractDS.DSChangedListener {
	
	private static final String TAG = "DeedsForFriendAdapter"; 
	
	private Context m_context = null;
	private FriendModel m_friend = null;
	private ArrayList<CategoryModel> m_categories = null;
	
	public DeedsForFriendAdapter(Context context, FriendModel friend) {
		
		m_context = context;
		m_friend = friend;
		m_categories = new ArrayList<CategoryModel>();
		
		query();
	}
	

	@Override
	public void notifyDSChanged(ArrayList<Integer> oldModelIds, ArrayList<Integer> newModelIds) {
		
		// clear data and redo local query
		query();
		
		// redisplay
		notifyDataSetChanged();
	}
	
	
	/**
	 * local query
	 * Find all the deeds/activities applicable to the friend's threshold, and group them by their categories.
	 */
	private void query() {
		
		// clear the category list
		m_categories.clear();
		
		// add a null category to represent those deeds without a category
		CategoryModel nullCategory = new CategoryModel(0, 
				m_context.getResources().getString(R.string.null_category_name),
				m_context.getResources().getString(R.string.null_category_description));
		m_categories.add(nullCategory);		
		
		// find all the deeds/activities applicable to the friend's threshold	
		ArrayList<DeedModel> deeds = TheLifeConfiguration.getDeedsDS().findByThreshold(m_friend.threshold);
		
		// Add each deed to the categories list
		// Since only a few categories are likely to exist, this should not be too inefficient.
		for (DeedModel deed:deeds) {
			addDeedToCategories(deed);
		}
		
		// Now see if the null category was needed after all.
		// If needed, move to the end. If not needed, remove it. 
		if (m_categories.get(0).deed_ids != null) {
			m_categories.remove(0);
			m_categories.add(nullCategory);
		} else {
			m_categories.remove(0);
		}
	}
	
	/**
	 * Add the deed to the category list.
	 * @param deed
	 */
	private void addDeedToCategories(DeedModel deed) {
		
		for (CategoryModel category:m_categories) {
			
			// look for a category already present, matching this deed
			// if found -- add the deed
			if (category.id == deed.category_id) {
				category.addDeed(deed.id);
				return;
			}
		}
		
		// did not find a category for this deed, so add the category and then the deed
		CategoryModel category = TheLifeConfiguration.getCategoriesDS().findById(deed.category_id);
		if (category != null) {
			m_categories.add(category);
			category.clearDeeds();
			category.addDeed(deed.id);
		} else {
			Log.e(TAG, "addDeedToCategories(): category missing for deed " + deed);
		}
	}
	
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
	
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		int deedId = m_categories.get(groupPosition).deed_ids.get(childPosition);
		return TheLifeConfiguration.getDeedsDS().findById(deedId);
	}
	

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return m_categories.get(groupPosition).deed_ids.get(childPosition);
	}
	

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		View deedView = convertView;
		if (deedView == null) {
			LayoutInflater inflator = LayoutInflater.from(m_context);
			deedView = inflator.inflate(R.layout.deed_cell, null);
		}	
		
		DeedModel deed = (DeedModel)getChild(groupPosition, childPosition);
		
//		ImageView imageView = (ImageView)deedView.findViewById(R.id.deed_image);
//		imageView.setImageBitmap(deed.image); // TODO when deed/activity images are supported
		
		TextView textView = (TextView)deedView.findViewById(R.id.deed_title);
		textView.setText(deed.title); // TODO: templates for deed title?
		
		deedView.setTag(deed);
		
		return deedView;
	}
	

	@Override
	public int getChildrenCount(int groupPosition) {
		return m_categories.get(groupPosition).deed_ids.size();
	}
	

	@Override
	public Object getGroup(int groupPosition) {
		return m_categories.get(groupPosition);
	}
	

	@Override
	public int getGroupCount() {
		return m_categories.size();
	}
	

	@Override
	public long getGroupId(int groupPosition) {
		return m_categories.get(groupPosition).id;
	}
	

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		View categoryView = convertView;
		if (categoryView == null) {
			LayoutInflater inflator = LayoutInflater.from(m_context);
			categoryView = inflator.inflate(R.layout.category_cell, null);
		}
		
		CategoryModel category = m_categories.get(groupPosition);
			
		TextView nameView = (TextView)categoryView.findViewById(R.id.category_title);
		nameView.setText(category.name);
		TextView descriptionView = (TextView)categoryView.findViewById(R.id.category_description);
		descriptionView.setText(category.description);		
		
		categoryView.setTag(category);		
							
		return categoryView;		
	}
	

	@Override
	public boolean hasStableIds() {
		return true;
	}

	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}				
	
}
