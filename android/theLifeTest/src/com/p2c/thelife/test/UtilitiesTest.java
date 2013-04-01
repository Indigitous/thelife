package com.p2c.thelife.test;

import android.test.AndroidTestCase;

import com.p2c.thelife.Utilities;
import com.p2c.thelife.model.FriendModel;
import com.p2c.thelife.model.UserModel;

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
		
		String templateString1 = "This is a test for first name: $f in here.";
		String templateString2 = "This is a test for first name: $f and $u in here.";

		// test with good data
		FriendModel friend1 = new FriendModel(1, "FFirst", "FLast", null, FriendModel.Threshold.Open);
		UserModel user1 = new UserModel(1, "UFirst", "ULast", null, "test@example.com", "555-5555");
		String answer1 = Utilities.fillTemplateString(getContext().getResources(), friend1, templateString1);
		assertEquals("This is a test for first name: FFirst in here.", answer1);
		String answer2 = Utilities.fillTemplateString(getContext().getResources(), user1, friend1, templateString2);
		assertEquals("This is a test for first name: FFirst and UFirst in here.", answer2);				
		
		// test with missing data
		FriendModel friend2 = new FriendModel(2, null, null, null, FriendModel.Threshold.Open);
		UserModel user2 = new UserModel(2, null, null, null, "test@example.com", "555-5555");		
		String answer3 = Utilities.fillTemplateString(getContext().getResources(), friend2, templateString1);
		assertEquals("This is a test for first name: friend in here.", answer3);
		String answer4 = Utilities.fillTemplateString(getContext().getResources(), user2, friend2, templateString2);
		assertEquals("This is a test for first name: friend and user in here.", answer4);		

	}

}
