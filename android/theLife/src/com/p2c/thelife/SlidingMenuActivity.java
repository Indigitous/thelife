package com.p2c.thelife;

import android.app.Activity;
import android.os.Bundle;

/**
 * Superclass of all Activities that use the sliding menu. 
 * SlidingMenu is from https://github.com/jfeinstein10/SlidingMenu, Apache 2.0 license TODO license notice
 * @author clarence
 *
 */
public class SlidingMenuActivity extends Activity {
	
	protected SlidingMenuSupport m_support = null;
		
	protected void onCreate(Bundle savedInstanceState, int layout_res, int slidingMenuPosition) {
		super.onCreate(savedInstanceState);
		setContentView(layout_res);
		
		m_support = new SlidingMenuSupport(this, slidingMenuPosition);
	}

}
