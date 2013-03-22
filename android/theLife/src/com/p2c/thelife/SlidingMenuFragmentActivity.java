package com.p2c.thelife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Superclass of all FragmentActivities that use the sliding menu. 
 * SlidingMenu is from https://github.com/jfeinstein10/SlidingMenu, Apache 2.0 license TODO license notice
 * @author clarence
 *
 */
public class SlidingMenuFragmentActivity extends FragmentActivity {
	
	protected SlidingMenuSupport m_support = null;
		
	protected void onCreate(Bundle savedInstanceState, int layout_res, int slidingMenuPosition) {
		super.onCreate(savedInstanceState);
		setContentView(layout_res);
		
		m_support = new SlidingMenuSupport(this, slidingMenuPosition);
	}

}
