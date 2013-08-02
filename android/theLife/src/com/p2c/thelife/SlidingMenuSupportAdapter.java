package com.p2c.thelife;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Set up the menu cell views in the sliding menu list view.
 * @author clarence
 *
 */
public class SlidingMenuSupportAdapter extends ArrayAdapter<String> {
	
	private static final String TAG = "SlidingMenuSupportAdapter"; 	
	
	
	public SlidingMenuSupportAdapter(Context context, int mode) {
		super(context, mode);	
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// get the view
		View appMenuCellView = (View)convertView;
		if (appMenuCellView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			appMenuCellView = inflator.inflate(R.layout.app_menu_cell, null);			
		}
		
		// set the text 
		TextView textViewDescription = (TextView)appMenuCellView.findViewById(R.id.app_menu_title);
		String description = getItem(position);
		textViewDescription.setText(Html.fromHtml(description));
		
		// set the image
		ImageView imageView1 = (ImageView)appMenuCellView.findViewById(R.id.app_menu_image);			
		switch (position) {
			case SlidingMenuSupport.COMMUNITY_POSITION:
				imageView1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.menu_community));
				break;
			case SlidingMenuSupport.FRIENDS_POSITION:
				imageView1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.menu_friends));
				break;
			case SlidingMenuSupport.GROUPS_POSITION:
				imageView1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.menu_groups));
				break;
			case SlidingMenuSupport.RESOURCES_POSITION:
				imageView1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.menu_resources));
				break;				
			case SlidingMenuSupport.HELP_POSITION:
				imageView1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.menu_help));
				break;	
			case SlidingMenuSupport.SETTINGS_POSITION:
				imageView1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.menu_settings));
				break;
			case SlidingMenuSupport.TEST_POSITION:
				imageView1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.action_search));
				break;				
		}
		
		return appMenuCellView;
	}

}
