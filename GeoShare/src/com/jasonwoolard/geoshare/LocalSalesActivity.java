/*
 * Project		GeoShare
 * 
 * Package		com.jasonwoolard.geoshare
 * 
 * @author		Jason Woolard
 * 
 * Date			Mar 20, 2014
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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LocalSalesActivity extends Activity {
	ListView mLocalSales;
	List<Map<String, String>> mData;
	TextView mLocalSaleSubject;
	TextView mLocalSaleDescription;
	TextView mLocalSalePrice;
	TextView mPostedSalesAmount;
	ProgressDialog mProgressDialog;
	ParseUser mCurrentUser;
	String mTAG = "LocalSalesActivity";
	String mPostedSales;
	
	private void obtainLocalSales() 
	{
		progressDialogShow();
		// Running query vs Parse DB to obtain Posted Sales from currently logged in user to display in ListView
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Sales");
		query.orderByDescending("createdAt");
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
							map.put("postedBy", sales.getString("postedBy"));
							
							mData.add(map);
							
						}
						// Defining ListAdapter to use custom xml layout
						ListAdapter adapter = new SimpleAdapter(LocalSalesActivity.this,
						mData, R.layout.listview_local_sales_cell,new String[] {"title", "description", "price", "location", "oid", "postedBy"}, new int[] {
								R.id.textView_listView_localSalesSubject, R.id.textView_listView_localSalesDescription, R.id.textView_listView_localSalesPrice });

						mLocalSales.setAdapter(adapter);
						progressDialogHide();
						mLocalSales.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
								@SuppressWarnings("unchecked")
								HashMap<String, String> hMap = (HashMap<String, String>) mLocalSales.getItemAtPosition(pos);
								
								Intent intent = new Intent(getApplicationContext(), LocalSalesDetailActivity.class);
								
								intent.putExtra("title", hMap.get("title"));
								intent.putExtra("price",hMap.get("price"));
								intent.putExtra("location", hMap.get("location"));
								intent.putExtra("description", hMap.get("description"));
								intent.putExtra("oid", hMap.get("oid"));
								intent.putExtra("postedBy", hMap.get("postedBy"));

								startActivity(intent);
							}
						});
					}
					else
					{
						progressDialogHide();
					}
				}
				else
				{
					Log.i(mTAG, e.getMessage());
					progressDialogHide();

				}
			}
		});
	}
			
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_sales);
		
		mLocalSales = (ListView) findViewById(R.id.listView_localSales);
		mLocalSaleSubject = (TextView) findViewById(R.id.textView_listView_localSalesSubject);
		mLocalSaleDescription = (TextView) findViewById(R.id.textView_listView_localSalesDescription);
		mLocalSalePrice = (TextView) findViewById(R.id.textView_listView_localSalesPrice);
		mPostedSalesAmount = (TextView) findViewById(R.id.textView_localSalesAmount);
		mCurrentUser = ParseUser.getCurrentUser();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Executing method to Obtain Posted Sales from Parse DB
		obtainLocalSales();
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
		getMenuInflater().inflate(R.menu.local_sales, menu);
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
		case R.id.action_profile_activity:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	private void progressDialogShow() {
	    mProgressDialog = new ProgressDialog(this);
	    mProgressDialog.setTitle("Loading...");
	    mProgressDialog.setMessage("Please wait while we obtain sales within your local area.");
	    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    mProgressDialog.setCancelable(false);
	    mProgressDialog.show();
	}
	private void progressDialogHide() {
		mProgressDialog.dismiss();
		mProgressDialog = null;
	}
}
