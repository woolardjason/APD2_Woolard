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

import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class WatchingSalesActivity extends ListActivity {
	
	List<Map<String, String>> mData;
	static TextView mWatchingAmount;
	ListView mWatchingList;
	ParseUser mCurrentUser;
	ProgressDialog mProgressDialog;
	String mWatching;
	ParseRelation<ParseObject> mWatchingRelation;
	String mTAG = "WatchingSalesActivity";
	
	private WatchingSalesAdapter watchingSalesAdapter;


	private void obtainWatchingSales()
	{
		progressDialogShow();
		watchingSalesAdapter = new WatchingSalesAdapter(this);
		watchingSalesAdapter.loadObjects();
		setListAdapter(watchingSalesAdapter);
		progressDialogHide();

//						mWatchingList.setOnItemClickListener(new OnItemClickListener() {
//
//							@Override
//							public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
//								@SuppressWarnings("unchecked")
//								HashMap<String, String> hMap = (HashMap<String, String>) mWatchingList.getItemAtPosition(pos);
//
//								Intent intent = new Intent(getApplicationContext(), WatchingSalesDetailActivity.class);
//
//								intent.putExtra("title", hMap.get("title"));
//								intent.putExtra("price",hMap.get("price"));
//								intent.putExtra("location", hMap.get("location"));
//								intent.putExtra("description", hMap.get("description"));
//								intent.putExtra("oid", hMap.get("oid"));
//								intent.putExtra("postedBy", hMap.get("postedBy"));
//
//
//								startActivity(intent);
//							}
//						});
				
	}

			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watching_sales);
		mWatchingList = (ListView) findViewById(android.R.id.list);
		mWatchingAmount = (TextView) findViewById(R.id.textView_watching_amount);
		mWatchingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				ParseObject sale = watchingSalesAdapter.getItem(pos);
				String objectID = sale.getObjectId().toString();
				Log.i(mTAG, objectID);
				Intent intent = new Intent(getApplicationContext(), WatchingSalesDetailActivity.class);
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

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		mWatchingRelation = mCurrentUser.getRelation("Watching");
		// Obtaining User's Watching List from Parse DB
		obtainWatchingSales();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.watching_sales, menu);
		return true;
	}
	public static void setTextView(int amount)
	{
		String finalString = Integer.toString(amount);
		mWatchingAmount.setText(finalString);
	}
	private void progressDialogShow() {
	    mProgressDialog = new ProgressDialog(this);
	    mProgressDialog.setTitle("Loading...");
	    mProgressDialog.setMessage("Please wait while we obtain your currently watching sales.");
	    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    mProgressDialog.setCancelable(false);
	    mProgressDialog.show();
	}
	private void progressDialogHide() {
		mProgressDialog.dismiss();
		mProgressDialog = null;
	}
}
