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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class ProfileActivity extends ListActivity {

	public static final String mTAG = "Profile Acitivty";
	List<Map<String, String>> mData;
	ListView mUserSales;
	ParseUser mCurrentUser;
	ProgressDialog mProgressDialog;
	TextView mInboxAmount;
	TextView mWatchingAmount;
	TextView mGBuxAmount;
	String mPostedSales;
	static TextView mPostedSalesAmount;
	TextView mUsersName;
	ParseRelation<ParseObject> mWatchingRelation;
	
	private MyPostedSalesAdapter mOwnSalesAdapter;

	private void obtainPostedSales ()
	{
		progressDialogShow();
		mOwnSalesAdapter = new MyPostedSalesAdapter(this);
		mOwnSalesAdapter.loadObjects();
	
						mUserSales.setAdapter(mOwnSalesAdapter);
						progressDialogHide();

//						mUserSales.setOnItemClickListener(new OnItemClickListener() {
//
//							@Override
//							public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
//								@SuppressWarnings("unchecked")
//								HashMap<String, String> hMap = (HashMap<String, String>) mUserSales.getItemAtPosition(pos);
//								
//								Intent intent = new Intent(getApplicationContext(), MyPostedSalesDetailActivity.class);
//								
//								intent.putExtra("title", hMap.get("title"));
//								intent.putExtra("price",hMap.get("price"));
//								intent.putExtra("location", hMap.get("location"));
//								intent.putExtra("description", hMap.get("description"));
//								intent.putExtra("oid", hMap.get("oid"));
//
//								startActivity(intent);
//							}
//						});
				
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		initializeUIElements();
		mWatchingAmount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), WatchingSalesActivity.class);
				startActivity(intent);				
			}
		});
		mInboxAmount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), InboxActivity.class);
				startActivity(intent);
			}
		});
	}

	public void initializeUIElements() {
		mUserSales = (ListView) findViewById(android.R.id.list);
		mInboxAmount = (TextView) findViewById(R.id.textView_inbox_amount);
		mWatchingAmount = (TextView) findViewById(R.id.textView_watching_amount);
		mGBuxAmount = (TextView) findViewById(R.id.textView_gBux_amount);
		mPostedSalesAmount = (TextView) findViewById(R.id.textView_posted_sales_amount);
		mUsersName = (TextView) findViewById(R.id.textView_users_name);
	}
	public static void setTextView(int amount)
	{
		String finalString = Integer.toString(amount);
		mPostedSalesAmount.setText(finalString);
	}
	@Override
	protected void onResume() {
		super.onResume();
		
		mCurrentUser = ParseUser.getCurrentUser();
		ParseAnalytics.trackAppOpened(getIntent());
		
		// Setting up currentUser to current logged in user
		// If user is not logged in, present them with Login Activity
		if (mCurrentUser == null)
		{
			presentUserWithLogin();
		}
		else
		{
			// Obtaining User's Posted Sales from the Parse Backend
			obtainPostedSales();
			// Setting the User's Relation Class to the Member Variable mWatchingRelation
			mWatchingRelation = mCurrentUser.getRelation("Watching");
			
			// Print their username to LogCat for debugging purposes
			// Log.i(mTAG, mCurrentUser.getUsername());
			
			// Setting users username to the associated UI Element for Welcome Text
			mUsersName.setText(mCurrentUser.getUsername() + "!");
			
			// Obtaining & Refreshing Users g-Bux Currency
			String eBuxAmount = Integer.toString(mCurrentUser.getInt("gBux")); 
			Log.i(mTAG, eBuxAmount); 
			mGBuxAmount.setText(eBuxAmount);
			
			// Obtaining User Messages Stored via Parse Backend
			obtainUserMessages();
			// Obtaining User's Watching Sales from Parse DB
			obtainWatchingSales();			
		}
	
	}

	public void obtainWatchingSales() {
		mWatchingRelation.getQuery().findInBackground(new FindCallback<ParseObject>() 
		{
			@Override
			public void done(List<ParseObject> objects, ParseException e) 
			{
				if (e == null) 
				{
				mWatchingAmount.setText(Integer.toString(objects.size()));
		        Log.i(mTAG, "Retrieved " + objects.size() + " watching items!");

				}
				else
				{
					Log.i(mTAG, e.getMessage());
				}
			}
		});
	}

	public void obtainUserMessages() {
		// Running Query to obtain Messages for user & updating necessary UI element
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
		query.whereEqualTo("receiver", mCurrentUser.getUsername());
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> messages, ParseException e) 
		    {
		        if (e == null) 
		        {
		        	// Logging the amount of messages received, as well as setting the text for the TextView
		            Log.i(mTAG, "Retrieved " + messages.size() + " messages!");
		            mInboxAmount.setText(Integer.toString(messages.size()));
		        } 
		        else 
		        {
		            Log.i(mTAG, "Error has occured: " + e.getMessage());
		        }
		    }
		});
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
		case R.id.action_local_sales:
			Intent intent = new Intent(this, LocalSalesActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	private void progressDialogShow() {
	    mProgressDialog = new ProgressDialog(this);
	    mProgressDialog.setTitle("Loading...");
	    mProgressDialog.setMessage("Please wait while we obtain your posted sales.");
	    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    mProgressDialog.setCancelable(false);
	    mProgressDialog.show();
	}
	private void progressDialogHide() {
		if (mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

}
