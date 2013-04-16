package com.p2c.thelife;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.p2c.thelife.model.UserModel;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Support for SlidingMenu. 
 * SlidingMenu is from https://github.com/jfeinstein10/SlidingMenu, Apache 2.0 license TODO license notice
 * @author clarence
 *
 */ 
public class SlidingMenuSupport {
	
	protected Activity    m_activity;
	protected SlidingMenu m_slidingMenu;
	protected int         m_slidingMenuPosition;
	
	public static final int NO_POSITION = -1;
	public static final int COMMUNITY_POSITION = 0;
	public static final int FRIENDS_POSITION = 1;
	public static final int GROUPS_POSITION = 2;
	public static final int REQUESTS_POSITION = 3;	
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
        
        m_slidingMenu.setMenu(R.layout.app_menu);
        View appMenu = m_slidingMenu.getMenu();
        appMenu.setBackgroundColor(android.graphics.Color.LTGRAY);
        
        // show the app user
        if (TheLifeConfiguration.isValidUser()) {
	        ImageView imageView = (ImageView)appMenu.findViewById(R.id.app_menu_user_image);
	        imageView.setImageBitmap(UserModel.getImage(TheLifeConfiguration.getUserId(), false));
	        TextView textView = (TextView)appMenu.findViewById(R.id.app_menu_user_name);
	        textView.setText(TheLifeConfiguration.getUser().getFullName());
        }
        
        // add the commands to the sliding menu using an adapter
        ListView commandsView = (ListView)appMenu.findViewById(R.id.app_menu_command_list);
        String[] commandList = m_activity.getResources().getStringArray(R.array.app_menu_commands);
        SlidingMenuSupportAdapter commands = new SlidingMenuSupportAdapter(m_activity, android.R.layout.simple_list_item_1);
        for (String s: commandList) {
            commands.add(s);
        }      
        commandsView.setAdapter(commands);
        
        // listen for a sliding menu selection
        commandsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		
        		if (position == m_slidingMenuPosition) {
   					m_slidingMenu.showContent();
        		} else {

	        		switch (position) {
	        		
	    				case COMMUNITY_POSITION: 
	    					m_activity.startActivity(new Intent("com.p2c.thelife.EventsForCommunity"));
	    					break;
	    					
	        			case FRIENDS_POSITION: 
	        				m_activity.startActivity(new Intent("com.p2c.thelife.Friends"));
	        				break;
	        				
	        			case GROUPS_POSITION: 
	        				m_activity.startActivity(new Intent("com.p2c.thelife.Groups"));
	        				break;
	        				
	        			case REQUESTS_POSITION: 
	        				m_activity.startActivity(new Intent("com.p2c.thelife.Requests"));
	        				break;	        				
	        				
	        			case HELP_POSITION: 
	        				m_activity.startActivity(new Intent("com.p2c.thelife.GlobalHelp"));
	        				break; 
	        				
	        			case SETTINGS_POSITION: 
	        				m_activity.startActivity(new Intent("com.p2c.thelife.Settings"));
	        				break;  	        				
	        		}
        		}
			}
		});   
	}		
	
}
