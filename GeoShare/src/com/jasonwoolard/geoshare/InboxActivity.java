package com.jasonwoolard.geoshare;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class InboxActivity extends Activity {

	TextView mMessagesAmount;
	ListView mMessagesList;
	ParseUser mCurrentUser;
	ProgressDialog mProgressDialog;
	String mTAG = "InboxActivity";
	String mMessages;
	String mFinalDate;
	List<Map<String, String>> mData;

	
	private class PullUserMessagesFromParse extends AsyncTask<Void, Integer, Void> {
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
			// Running query against Parse DB to obtain Messages for currently logged in user to display in a ListView.
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Messages");
			query.orderByDescending("createdAt");
			query.whereEqualTo("receiver", mCurrentUser.getUsername());
			query.findInBackground(new FindCallback<ParseObject>() 
			{
				@SuppressLint("SimpleDateFormat")
				@Override
				public void done(List<ParseObject> objects, ParseException e) 
				{
					if (e == null) 
					{
						if (objects.toArray().length > 0)
						{
							mData = new ArrayList<Map<String, String>>();
						    mMessages = Integer.toString(objects.toArray().length);
						    mMessagesAmount.setText("(" + mMessages + ")");
						    for (int i=0; i < objects.toArray().length; i++)
							{			
								ParseObject messages = objects.get(i);
								Date date = messages.getCreatedAt();
								
								SimpleDateFormat normalFormat = new SimpleDateFormat("MM/dd/yyyy"); 
								
								mFinalDate = normalFormat.format(date);
								
								
								Map<String, String> map = new HashMap<String, String>(2);
								map.put("subject", messages.getString("subject"));
								map.put("date", mFinalDate);
								map.put("message", messages.getString("message"));
								map.put("receiver", messages.getString("receiver"));
								map.put("sender", messages.getString("sender"));
								map.put("oid", messages.getObjectId());
								
								mData.add(map);
							}
							// Defining ListAdapter to use custom xml layout
							ListAdapter adapter = new SimpleAdapter(InboxActivity.this,
							mData, R.layout.listview_messages_cell,new String[] {"subject", "date", "sender", "receiver", "message", "oid"}, new int[] {
									R.id.textView_listView_messageSubject, R.id.textView_listView_messageDate, R.id.textView_listView_messageSender });

							mMessagesList.setAdapter(adapter);
							mMessagesList.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
									@SuppressWarnings("unchecked")
									HashMap<String, String> hMap = (HashMap<String, String>) mMessagesList.getItemAtPosition(pos);
									
									Intent intent = new Intent(getApplicationContext(), InboxMessageActivity.class);
									
									intent.putExtra("subject", hMap.get("subject"));
									intent.putExtra("date", hMap.get("date"));
									intent.putExtra("message", hMap.get("message"));
									intent.putExtra("receiver", hMap.get("receiver"));
									intent.putExtra("sender", hMap.get("sender"));
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
			mProgressDialog = new ProgressDialog(InboxActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(100);
			mProgressDialog.setTitle("Loading...");
			mProgressDialog.setMessage("Please wait while we obtain any recevied messages.");
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
		setContentView(R.layout.activity_inbox);
		mMessagesAmount = (TextView) findViewById(R.id.textView_messages_amount);
		mMessagesList = (ListView) findViewById(R.id.listView_messages);
		mCurrentUser = ParseUser.getCurrentUser();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Executing Async Task to Obtain User's Messages from Parse DB
		new PullUserMessagesFromParse().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inbox, menu);
		return true;
	}

}
