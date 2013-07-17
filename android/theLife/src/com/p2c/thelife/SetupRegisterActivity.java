package com.p2c.thelife;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class SetupRegisterActivity extends Activity {
	
	private Account[] m_googleAccounts = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_register);
		
		// look for Google accounts, and add them to the layout
		LinearLayout layout = (LinearLayout)findViewById(R.id.register_layout);		
		AccountManager accountManager = AccountManager.get(this);
		m_googleAccounts = accountManager.getAccountsByType("com.google");
		for (int index= 0; index < m_googleAccounts.length; index++) {
			Button button = new Button(this);
			button.setId(index);
			button.setText("Google " + m_googleAccounts[index].name);
			button.setOnClickListener(new Button.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					registerViaGoogle(v.getId());
				}

			});
			layout.addView(button);
		}
		
		// add the manual register button
		Button button = new Button(this);
		button.setText(getResources().getString(R.string.manually));
		button.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				registerManually();
			}
			
		});
		layout.addView(button);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup_register, menu);
		return true;
	}
	
	
	private void registerViaGoogle(int index) {
		System.out.println("REGISTER GOOGLE index " + index);
		System.out.println("REGISTER WITH GOOGLE ACCOUNT " + m_googleAccounts[index].name);
	}	
	
	public void registerManually() {
		// go to the register manually screen
		Intent intent = new Intent("com.p2c.thelife.SetupRegisterManually");
		startActivity(intent);		
	}

}
