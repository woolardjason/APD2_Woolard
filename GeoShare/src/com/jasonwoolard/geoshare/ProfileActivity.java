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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileActivity extends Activity {

	public static final String mTAG = "Profile Acitivty";
	List<Map<String, String>> mData;
	ListView mUserSales;
	ParseUser mCurrentUser;
	ProgressDialog mProgressDialog;
	TextView mInboxAmount;
	TextView mWatchingAmount;
	TextView mGBuxAmount;
	String mPostedSales;
	TextView mPostedSalesAmount;
	TextView mUsersName;
	
	private class PullUserSalesFromParse extends AsyncTask<Void, Integer, Void> {
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < 20; i++)
			{
				publishProgress(5);
				try {
					Thread.sleep(45);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// Running query vs Parse DB to obtain Posted Sales from currently logged in user to display in ListView
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Sales");
			query.orderByDescending("createdAt");
			query.whereEqualTo("postedBy", mCurrentUser);
			query.findInBackground(new FindCallback<ParseObject>() 
			{
				@Override
				public void done(List<ParseObject> objects, ParseException e) 
				{
					if (e == null) 
					{
						if (objects.toArray().length > 0)
						{
							mData = new ArrayList<Map<String, String>>();
						    mPostedSales = Integer.toString(objects.toArray().length);
						    mPostedSalesAmount.setText("(" + mPostedSales + ")");
						    for (int i=0; i < objects.toArray().length; i++)
							{			
								ParseObject sales = objects.get(i);
								Map<String, String> map = new HashMap<String, String>(2);
								map.put("title", sales.getString("title"));
								map.put("price", sales.getString("price"));
								map.put("location", sales.getString("location"));
								map.put("description", sales.getString("description"));
								map.put("oid", sales.getObjectId());
								
								mData.add(map);
								
							}
							// Defining ListAdapter to use custom xml layout
							ListAdapter adapter = new SimpleAdapter(ProfileActivity.this,
							mData, R.layout.listview_cell,new String[] {"title", "price", "location", "description", "oid"}, new int[] {
									R.id.textView_listView_saleTitle, R.id.textView_listView_salePrice });

							mUserSales.setAdapter(adapter);
							mUserSales.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
									@SuppressWarnings("unchecked")
									HashMap<String, String> hMap = (HashMap<String, String>) mUserSales.getItemAtPosition(pos);
									
									Intent intent = new Intent(getApplicationContext(), MyPostedSalesDetailActivity.class);
									
									intent.putExtra("title", hMap.get("title"));
									intent.putExtra("price",hMap.get("price"));
									intent.putExtra("location", hMap.get("location"));
									intent.putExtra("description", hMap.get("description"));
									intent.putExtra("oid", hMap.get("oid"));


									startActivity(intent);
								}
							});
						}
					}
					else
					{
						Log.i(mTAG, e.getMessage());
					}
				}
			});
		
			return null;
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(ProfileActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(100);
			mProgressDialog.setTitle("Loading...");
			mProgressDialog.setMessage("Please wait while we obtain your posted sales.");
			mProgressDialog.show(); 
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgressDialog.incrementProgressBy(values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();

		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		mUserSales = (ListView) findViewById(R.id.listView1);
		mInboxAmount = (TextView) findViewById(R.id.textView_inbox_amount);
		mWatchingAmount = (TextView) findViewById(R.id.textView_watching_amount);
		mGBuxAmount = (TextView) findViewById(R.id.textView_gBux_amount);
		mPostedSalesAmount = (TextView) findViewById(R.id.textView_posted_sales_amount);
		mUsersName = (TextView) findViewById(R.id.textView_users_name);
		mInboxAmount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), InboxActivity.class);
				startActivity(intent);
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		// Parse Analytics - (User data)
		ParseAnalytics.trackAppOpened(getIntent());
		
		// Setting up currentUser to current logged in user
		// If user is not logged in, present them with Login Activity
		if (mCurrentUser == null)
		{
			presentUserWithLogin();
		}
		else
		{
			// Print their username to LogCat for debugging purposes
			Log.i(mTAG, mCurrentUser.getUsername());
			// Setting users username to the associated UI Element for Welcome Text
			mUsersName.setText(mCurrentUser.getUsername() + "!");
			String eBuxAmount = Integer.toString(mCurrentUser.getInt("gBux")); 
			Log.i(mTAG, eBuxAmount); 
			// Refreshing Users g-Bux Currency
			mGBuxAmount.setText(eBuxAmount);
			// Executing Async Task to Obtain User's Posted Sales to Parse DB
			new PullUserSalesFromParse().execute();
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
