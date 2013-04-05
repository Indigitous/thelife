package com.p2c.thelife;

import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;

public class GroupsSearchActivity extends SlidingMenuActivity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_groups_search, SlidingMenuSupport.GROUPS_POSITION);
		
		EditText editText = (EditText)findViewById(R.id.search_groups_text);
		
		// attach the event list view
		ListView listView = (ListView)findViewById(R.id.search_groups_list);
		GroupsSearchAdapter adapter = new GroupsSearchAdapter(this, android.R.layout.simple_list_item_1, editText);
		listView.setAdapter(adapter);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.groups_search, menu);
		return true;
	}



}
