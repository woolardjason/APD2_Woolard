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
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class LocalSalesActivity extends ListActivity {
	ListView mLocalSales;
	static TextView mPostedSalesAmount;
	ProgressDialog mProgressDialog;
	ParseUser mCurrentUser;
	String mTAG = "LocalSalesActivity";
	
	private LocalSalesAdapter localSalesAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_sales);
		mLocalSales = (ListView) findViewById(android.R.id.list);
		mPostedSalesAmount = (TextView) findViewById(R.id.textView_localSalesAmount);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		
		localSalesAdapter = new LocalSalesAdapter(this);
		localSalesAdapter.loadObjects();
		setListAdapter(localSalesAdapter);
		mLocalSales.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				ParseObject sale = localSalesAdapter.getItem(pos);
				String objectID = sale.getObjectId().toString();
				Log.i(mTAG, objectID);
				Intent intent = new Intent(getApplicationContext(), LocalSalesDetailActivity.class);
				intent.putExtra("title", sale.getString("title"));
				intent.putExtra("price",sale.getString("price"));
				intent.putExtra("location", sale.getString("location"));
				intent.putExtra("description", sale.getString("description"));
				intent.putExtra("oid", objectID);
				intent.putExtra("postedBy", sale.getString("postedBy"));
				startActivity(intent);
			}
		});
		
	}
	
	public static void setTextView(int amount)
	{
		String finalString = Integer.toString(amount);
		mPostedSalesAmount.setText(finalString);
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
}
