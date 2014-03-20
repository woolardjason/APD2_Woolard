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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class WatchingSalesActivity extends Activity {
	
	List<Map<String, String>> mData;
	TextView mWatchingAmount;
	ListView mWatchingList;
	ParseUser mCurrentUser;
	ProgressDialog mProgressDialog;
	String mWatching;
	ParseRelation<ParseObject> mWatchingRelation;
	String mTAG = "WatchingSalesActivity";
	
	private class PullUserWatchingSalesFromParse extends AsyncTask<Void, Integer, Void> {
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
			// Running query against Parse DB to obtain Watching Items for currently logged in user to display in a ListView.
			mWatchingRelation.getQuery().findInBackground(new FindCallback<ParseObject>() 
			{
				@Override
				public void done(List<ParseObject> objects, ParseException e) 
				{
					if (e == null) 
					{
						if (objects.toArray().length > 0)
						{
							mData = new ArrayList<Map<String, String>>();
						    mWatching = Integer.toString(objects.toArray().length);
						    mWatchingAmount.setText("(" + mWatching + ")");
						    for (int i=0; i < objects.toArray().length; i++)
							{			
								ParseObject watches = objects.get(i);
								
								Map<String, String> map = new HashMap<String, String>(2);
								map.put("title", watches.getString("title"));
								map.put("price", watches.getString("price"));
								map.put("location", watches.getString("location"));
								map.put("description", watches.getString("description"));
								map.put("postedBy", watches.getString("postedBy"));
								map.put("oid", watches.getObjectId());
								
								mData.add(map);
							}
							// Defining ListAdapter to use custom xml layout
							ListAdapter adapter = new SimpleAdapter(WatchingSalesActivity.this,
							mData, R.layout.listview_watching_cell,new String[] {"title", "description", "price", "oid", "postedBy"}, new int[] {
									R.id.textView_listView_watchingSubject, R.id.textView_listView_watchingDescription, R.id.textView_listView_watchingPrice });

							mWatchingList.setAdapter(adapter);
							mWatchingList.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
									@SuppressWarnings("unchecked")
									HashMap<String, String> hMap = (HashMap<String, String>) mWatchingList.getItemAtPosition(pos);
									
									Intent intent = new Intent(getApplicationContext(), WatchingSalesDetailActivity.class);
									
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
			mProgressDialog = new ProgressDialog(WatchingSalesActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(100);
			mProgressDialog.setTitle("Loading...");
			mProgressDialog.setMessage("Please wait while we look up your watching list.");
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
		setContentView(R.layout.activity_watching_sales);
		
		mWatchingList = (ListView) findViewById(R.id.listView_watchingSales);
		mWatchingAmount = (TextView) findViewById(R.id.textView_watching_amount);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		mWatchingRelation = mCurrentUser.getRelation("Watching");
		// Executing Async Task to Obtain User's Watching List from Parse DB
		new PullUserWatchingSalesFromParse().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.watching_sales, menu);
		return true;
	}

}
