package com.jasonwoolard.geoshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LocalSalesDetailActivity extends Activity {

	TextView mItemName;
	TextView mItemPrice;
	TextView mItemLocation;
	TextView mItemDetails;
	Button mWatchItem;
	Button mContactSeller;
    ActionBar mActionBar;
	String mDataObjectId;
	String mDataTitle;
	String mDataPrice;
	String mDataLocation;
	String mDataDetails;
	String mDataPostedBy;
	ParseRelation<ParseObject> mWatchingRelation;
	ParseUser mCurrentUser;
	String mTAG ="LocalSalesDetailActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_sales_detail);
		
		Intent intent = getIntent();
		
		mDataTitle = intent.getStringExtra("title");
		mDataPrice = intent.getStringExtra("price");
		mDataLocation = intent.getStringExtra("location");
		mDataDetails = intent.getStringExtra("description");
		mDataObjectId = intent.getStringExtra("oid");
		mDataPostedBy = intent.getStringExtra("postedBy");

		initializeUIElements();
		
		mItemName.setText(mDataTitle);
		mItemPrice.setText(mDataPrice);
		mItemLocation.setText(mDataLocation);
		mItemDetails.setText(mDataDetails);
		mWatchItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("WatchIteMBtn", "was clicked");
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Sales");
				query.getInBackground(mDataObjectId, new GetCallback<ParseObject>() {
				  public void done(ParseObject object, ParseException e) {
					  Log.i("done fired", "done fired");
				    if (e == null) {
						mWatchingRelation.add(object);
						mCurrentUser.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(ParseException e) {
								if (e == null)
								{
									// Closing Activity and displaying Toast Notification that the sale was posted successfully.
									finish();
									Toast.makeText(getApplicationContext(), "You have successfully added the item " +  "'" + mDataTitle + "'" + " to your Watch List!", Toast.LENGTH_LONG).show();
									
								}
								else
								{
									Log.e(mTAG, e.getMessage());
								}
							}
						});

				    } else {
				    	// Not added to watch list - something went wrong...
						  Log.e("done fired", e.toString());
				    }
				  }
				});
			}
		});
		mContactSeller.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ContactSellerActivity.class);
				
				intent.putExtra("title", mDataTitle);
				intent.putExtra("oid", mDataObjectId);
				intent.putExtra("postedBy", mDataPostedBy);

				startActivity(intent);
			}
		});
	}

	public void initializeUIElements() {
		mItemName = (TextView) findViewById(R.id.textView_localSalesDetailTitle);
		mItemPrice = (TextView) findViewById(R.id.textView_localSalesDetailPrice);
		mItemLocation = (TextView) findViewById(R.id.textView_localSalesDetailLocation);
		mItemDetails = (TextView) findViewById(R.id.textView_localSalesDetailDetails);
		mWatchItem = (Button) findViewById(R.id.button_localSalesDetailWatchItem);
		mContactSeller = (Button) findViewById(R.id.button_localSalesDetailContactSeller);
	}

	
	@Override
	protected void onResume() {
		mCurrentUser = ParseUser.getCurrentUser();
		mWatchingRelation = mCurrentUser.getRelation("Watching");
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.local_sales_detail, menu);
		return true;
	}

}
