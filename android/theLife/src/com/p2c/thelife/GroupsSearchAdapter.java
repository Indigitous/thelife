package com.p2c.thelife;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.p2c.thelife.model.GroupModel;

public class GroupsSearchAdapter extends ArrayAdapter<GroupModel> implements OnEditorActionListener, Server.ServerListener {
	
	private static final String TAG = "GroupsSearchAdapter"; 
	
	private ProgressDialog m_progressDialog = null;	
	
		
	public GroupsSearchAdapter(Context context, int mode, EditText searchView) {
		super(context, mode);
		
		// listen for a search
		searchView.setOnEditorActionListener(this);		
				
		// initial query is for everything
		query("");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			
		// get the group for this view
		GroupModel group = getItem(position);

		View groupView = convertView;
		if (groupView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			groupView = inflator.inflate(R.layout.group_cell, null);
		}
				
		TextView nameView = (TextView)groupView.findViewById(R.id.group_name);
		nameView.setTextSize(20.0f);
		nameView.setText(group.name);
		TextView descriptionView = (TextView)groupView.findViewById(R.id.group_description);
		descriptionView.setText(group.description);
		
		groupView.setTag(group);  		
							
		return groupView;     		
	}
	
	
	private void query(String queryString) {
		
		// send the query to the server; server will call back with notifyServerResponseAvailable
		notifyAttemptingServerAccess("queryGroups");
		Server server = new Server(getContext());
		server.queryGroups(queryString, this, "queryGroups");
	}	
	
	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			String queryString = (String)view.getText().toString();
			query(queryString);
		}
		return true;
	}
	
	public void notifyAttemptingServerAccess(String indicator) {
		m_progressDialog = ProgressDialog.show(getContext(), 
			getContext().getResources().getString(R.string.waiting), 
			getContext().getResources().getString(R.string.querying), true);
				
	}	

	@Override
	public void notifyServerResponseAvailable(String indicator,	int httpCode, JSONObject jsonObject) {
	
		if (jsonObject != null) {
			
			// look for a JSONArray wrapped in an JSON object under key "a"
			JSONArray jsonArray = jsonObject.optJSONArray("a");
			if (jsonArray != null) {
				clear();
				
				try {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject json = jsonArray.getJSONObject(i);
						GroupModel group = GroupModel.fromJSON(json, false);
						add(group);
					}
				} catch (Exception e) {
					Log.wtf(TAG, "notifyServerResponseAvailable()", e);
				}
			}
		}
		
		// redisplay
		notifyDataSetChanged();		
		
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}				
	}

}
