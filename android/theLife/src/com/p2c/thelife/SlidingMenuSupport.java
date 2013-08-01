package com.p2c.thelife;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.p2c.thelife.model.OwnerDS;
import com.p2c.thelife.model.RequestsDS;
import com.p2c.thelife.model.UserModel;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Support for SlidingMenu. 
 * SlidingMenu is from https://github.com/jfeinstein10/SlidingMenu, Apache 2.0 license
 * @author clarence
 *
 */ 
public class SlidingMenuSupport implements OwnerDS.DSChangedListener, RequestsDS.DSChangedListener, SlidingMenu.OnOpenListener, SlidingMenu.OnCloseListener {
	
	protected Activity    m_activity;
	protected SlidingMenu m_slidingMenu;
	protected int         m_slidingMenuPosition;
	protected View		  m_appMenu;
	
	public static final int NO_POSITION = -2;
	public static final int REQUESTS_POSITION = -1;		
	public static final int COMMUNITY_POSITION = 0;
	public static final int FRIENDS_POSITION = 1;
	public static final int GROUPS_POSITION = 2;
	public static final int RESOURCES_POSITION = 3;
	public static final int HELP_POSITION = 4;
	public static final int SETTINGS_POSITION = 5;
	public static final int TEST_POSITION = 6;
	
	public SlidingMenuSupport(Activity activity, int slidingMenuPosition) {
		
		m_activity = activity;
		m_slidingMenuPosition = slidingMenuPosition;
		
		// set up the sliding menu
		m_slidingMenu = new SlidingMenu(m_activity, SlidingMenu.SLIDING_CONTENT);
        m_slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        m_slidingMenu.setMode(SlidingMenu.LEFT);  // LEFT_RIGHT crashes on Nexus 4
        m_slidingMenu.setFadeDegree(0.50f);
        
        // set the behind width to 
        // 250 pixels on Samsung Galaxy Q 320x480 180ppi, 400 pixels on Nexus 4 1280x768 320ppi
        m_slidingMenu.setBehindWidth((int)(250 * m_activity.getResources().getDisplayMetrics().density));
        
        // set the layout and listeners for the application menu
        m_slidingMenu.setMenu(R.layout.app_menu);
        m_appMenu = m_slidingMenu.getMenu();
        LinearLayout appMenuNotificationNumber = (LinearLayout)m_appMenu.findViewById(R.id.app_menu_notification);
        appMenuNotificationNumber.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_activity.startActivity(new Intent("com.p2c.thelife.Requests"));
			}
		});
        m_slidingMenu.setOnOpenListener(this);
        m_slidingMenu.setOnCloseListener(this);
        
        showOwner();
        showNotificationNumber();
        
        // add the commands to the sliding menu using an adapter
        ListView commandsView = (ListView)m_appMenu.findViewById(R.id.app_menu_command_list);
        String[] commandList = m_activity.getResources().getStringArray(R.array.app_menu_commands);
        SlidingMenuSupportAdapter commands = new SlidingMenuSupportAdapter(m_activity, android.R.layout.simple_list_item_1);
        for (String s: commandList) {
            commands.add(s);
        }
//        commands.add("TEST"); // TODO debugging
        commandsView.setAdapter(commands);
        
        // listen for a sliding menu selection
        commandsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		
        		if (position == m_slidingMenuPosition) {
   					m_slidingMenu.showContent();
        		} else {

        			Intent intent = null;
	        		switch (position) {
	        		
		    			case REQUESTS_POSITION:
							intent = new Intent("com.p2c.thelife.Requests");	        				
		    				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    				m_activity.startActivity(intent);
		    				break;		        		
	        		
	    				case COMMUNITY_POSITION:
	    					intent = new Intent("com.p2c.thelife.EventsForCommunity");
	    					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    					m_activity.startActivity(intent);
	    					break;
	    					
	        			case FRIENDS_POSITION:
	    					intent = new Intent("com.p2c.thelife.Friends");	        				
	        				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        				m_activity.startActivity(intent);
	        				break;
	        				
	        			case GROUPS_POSITION:
	    					intent = new Intent("com.p2c.thelife.Groups");	        				
	        				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        				m_activity.startActivity(intent);
	        				break;
	        				
	        			case RESOURCES_POSITION:
	    					intent = new Intent("com.p2c.thelife.Resources");	        				
	        				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        				m_activity.startActivity(intent);
	        				break;         				
	        				
	        			case HELP_POSITION:
	    					intent = new Intent("com.p2c.thelife.HelpCentral");	        				
	        				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        				m_activity.startActivity(intent);
	        				break; 
	        				
	        			case SETTINGS_POSITION:
	    					intent = new Intent("com.p2c.thelife.Settings");	        				
	        				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        				m_activity.startActivity(new Intent("com.p2c.thelife.Settings"));
	        				break;
	        				
	        			// debugging
//	        			case TEST_POSITION: 
//	        				m_activity.startActivity(new Intent("com.p2c.thelife.Test"));
//	        				break;  	        				
	        		}
        		}
			}
		});   
	}

	@Override
	public void notifyOwnerDSChanged() {
		showOwner();
	}
	
	
	private void showOwner() {
        // show the app user
        if (TheLifeConfiguration.getOwnerDS().isValidOwner()) {
	        ImageView imageView = (ImageView)m_appMenu.findViewById(R.id.app_menu_user_image);
	        imageView.setImageBitmap(UserModel.getImage(TheLifeConfiguration.getOwnerDS().getId()));
	        imageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent("com.p2c.thelife.Settings");	        				
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					m_activity.startActivity(new Intent("com.p2c.thelife.Settings"));	
				}
			});
        
	        TextView textView = (TextView)m_appMenu.findViewById(R.id.app_menu_user_name);
	        textView.setText(TheLifeConfiguration.getOwnerDS().getOwner().getFullName());
	        textView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent("com.p2c.thelife.Settings");	        				
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					m_activity.startActivity(new Intent("com.p2c.thelife.Settings"));	
				}
			});
        }		
	}
	
	
	/**
	 * Change in the Requests data store.
	 */
	@Override
	public void notifyDSChanged(ArrayList<Integer> oldModelIds, ArrayList<Integer> newModelIds) {
		showNotificationNumber();
	}	
	
	
	private void showNotificationNumber() {
        if (TheLifeConfiguration.getOwnerDS().isValidOwner()) {
    		int numNotifications = TheLifeConfiguration.getRequestsDS().count();
    		
	        TextView textViewNum = (TextView)m_appMenu.findViewById(R.id.app_menu_notification_number);
	        textViewNum.setText(String.valueOf(TheLifeConfiguration.getRequestsDS().count()));
	        textViewNum.setBackgroundDrawable(numNotifications > 0 ? 
	        								  	m_activity.getResources().getDrawable(R.drawable.round_red) : 
	        								  	m_activity.getResources().getDrawable(R.drawable.round_black));
	        TextView textViewLabel = (TextView)m_appMenu.findViewById(R.id.app_menu_notification_label);
	        textViewLabel.setText(numNotifications == 1 ? R.string.notification_singular : R.string.notification_plural);
        }	
	}
	
	
	/**
	 * Show the sliding menu.
	 */
	public void slideOpen() {
		m_slidingMenu.showMenu();
	}
	

	/**
	 * The sliding menu is being closed.
	 */
	@Override
	public void onClose() {
		// nasty code because of the interaction of third party libs
		if (m_activity instanceof SherlockActivity) {
			((SherlockActivity)m_activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else if (m_activity instanceof SherlockFragmentActivity) {
			((SherlockFragmentActivity)m_activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	
	/**
	 * The sliding menu is being opened.
	 */
	@Override
	public void onOpen() {
		// nasty code because of the interaction of third party libs
		if (m_activity instanceof SherlockActivity) {
			((SherlockActivity)m_activity).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		} else if (m_activity instanceof SherlockFragmentActivity) {
			((SherlockFragmentActivity)m_activity).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		}		
	}

}
