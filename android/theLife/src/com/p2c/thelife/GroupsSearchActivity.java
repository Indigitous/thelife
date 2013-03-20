package com.p2c.thelife;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class GroupsSearchActivity extends Activity implements OnEditorActionListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups_search);
				
		// Get the main application
		TheLifeApplication app = (TheLifeApplication)getApplication();		
		
		// listen for a search
		EditText editText = (EditText)findViewById(R.id.search_groups_text);
		editText.setOnEditorActionListener(this);
		
		// attach the event list view
		ListView listView = (ListView)findViewById(R.id.search_groups_list);
		GroupsSearchAdapter adapter = new GroupsSearchAdapter(this, android.R.layout.simple_list_item_1, app, queryString);
		listView.setAdapter(adapter);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.groups_search, menu);
		return true;
	}

	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			String queryString = (String)view.getText();
		}
		return true;
	}

}
