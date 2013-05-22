package com.p2c.thelife.test;

import android.test.AndroidTestCase;

import com.p2c.thelife.model.EventModel;

public class UtilitiesTest extends AndroidTestCase {
	
	public UtilitiesTest() {
		super();
	}
	
	@Override
	public void setUp() {
		
	}
	
	@Override
	public void tearDown() {
		
	}
	
	public void testFillTemplateString() {

		// test with good data part one
		EventModel event1 = new EventModel(getContext().getResources(), 1, 1, "UName", 1, "FName", 1, 0, "$u shared with $f", 0, false, 0, false, 0);
		assertEquals("<b>UName</b> shared with <b>FName</b>", event1.finalDescription);
		
		// test with good data part two
		EventModel event2 = new EventModel(getContext().getResources(), 2, 2, "UName", 2, "FName", 2, 0, "$u moved $f to $t", 0, false, 0, false, 2);
		assertEquals("<b>UName</b> moved <b>FName</b> to <b>Build Trust</b>", event2.finalDescription);	
	}

}
