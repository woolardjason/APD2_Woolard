/*
 * Project		GeoShare
 * 
 * Package		com.jasonwoolard.geoshare
 * 
 * @author		Jason Woolard
 * 
 * Date			Mar 11, 2014
 */
package com.jasonwoolard.geoshare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class ProfileActivity extends Activity {

	public static final String mTAG = "Profile Acitivty";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		// Parse Analytics - (User data)
		ParseAnalytics.trackAppOpened(getIntent());
		
		// Setting up currentUser to current logged in user
		ParseUser currentUser = ParseUser.getCurrentUser();
		// If user is not logged in, present them with Login Activity
		if (currentUser == null)
		{
			presentUserWithLogin();
		}
		else
		{
			// Print their username to LogCat for debugging purposes
			Log.i(mTAG, currentUser.getUsername());
		}
		
	}

	@SuppressLint("InlinedApi")
	public void presentUserWithLogin() {
		// Displaying the Login Activity to the user
		Intent i = new Intent(this, LoginActivity.class);
		// Logging in =  New Task, Old Task = Clear so back button cannot be used to go back into Profile Activity if logged out.
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
		case R.id.action_log_out:
			// Logging out the current user and presenting them with the login activity.
			ParseUser.logOut();
			presentUserWithLogin();
			break;
		case R.id.action_post_sale:
			// Launching new intent to start Post Sale Activity
			Intent i = new Intent(this, PostSaleActivity.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
