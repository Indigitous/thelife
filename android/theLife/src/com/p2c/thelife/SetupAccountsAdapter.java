package com.p2c.thelife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Adapter for the accounts available for login/register.
 * @author clarence
 *
 */
public class SetupAccountsAdapter extends ArrayAdapter<String> {
	
	private int m_numGoogleAccounts = 0;
	
	public SetupAccountsAdapter(Context context, int mode, int numGoogleAccounts) {
		super(context, mode);
		
		m_numGoogleAccounts = numGoogleAccounts;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View accountView = convertView;
		if (accountView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			accountView = inflator.inflate(R.layout.account_cell, null);
		}
		
		// set the name of the account
		String accountName = getItem(position);
		TextView accountTextView = (TextView)accountView.findViewById(R.id.account_name);
		accountTextView.setText(accountName);

		// set the icon of the account		
		ImageView accountImageView = (ImageView)accountView.findViewById(R.id.account_image);
		if (position < m_numGoogleAccounts) {
			accountImageView.setImageResource(R.drawable.common_signin_btn_icon_dark);
		} else if (position < m_numGoogleAccounts + 1) {
			accountImageView.setImageResource(R.drawable.facebook_144);
		} else {
			accountImageView.setImageResource(R.drawable.ic_launcher);
		}
			
		return accountView;
	}	

}
