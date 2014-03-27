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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class WatchingSalesDetailActivity extends ActionBarActivity {
	TextView mItemName;
	TextView mItemPrice;
	TextView mItemLocation;
	TextView mItemDetails;
	Button mContactSeller;
	Button mDoNotWatchItem;
	ActionBar mActionBar;
	String mDataObjectId;
	String mDataTitle;
	String mDataPrice;
	String mDataLocation;
	String mDataDetails;
	String mDataPostedBy;
	ParseRelation<ParseObject> mWatchingRelation;
	ParseUser mCurrentUser;
	String mTAG ="WatchingSalesDetailActivity";

	private ShareActionProvider mProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watching_sales_detail);
		mActionBar = getSupportActionBar();

		Intent intent = getIntent();

		mDataTitle = intent.getStringExtra("title");
		mDataPrice = intent.getStringExtra("price");
		mDataLocation = intent.getStringExtra("location");
		mDataDetails = intent.getStringExtra("description");
		mDataObjectId = intent.getStringExtra("oid");
		mDataPostedBy = intent.getStringExtra("postedBy");

		initializeUIElements();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Sales");
		query.getInBackground(mDataObjectId, new GetCallback<ParseObject>() {
			public void done(ParseObject object,ParseException e) {

				ParseFile fileObject = (ParseFile) object.get("photo");
				if (fileObject != null)
				{
					fileObject.getDataInBackground(new GetDataCallback() {
						public void done(byte[] data, ParseException e) {
							if (e == null) 
							{
								// Decoding the Byte Array into a Bitmap
								Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
								ImageView saleImage = (ImageView) findViewById(R.id.imageView_watchingSalesDetailPhoto);
								// Setting the Bitmap into the ImageView
								saleImage.setImageBitmap(bmp);
							} else {
								Log.i(mTAG, "Error with downloading the data: " + e.toString());
							}
						}
					});
				}
				else
				{
					// Set ImageView to Placeholder
				}
			}
		});
		mItemName.setText(mDataTitle);
		mItemPrice.setText(mDataPrice);
		mItemLocation.setText(mDataLocation);
		mItemDetails.setText(mDataDetails);

		mContactSeller.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ContactSellerActivity.class);

				intent.putExtra("title", mDataTitle);
				intent.putExtra("oid", mDataObjectId);
				intent.putExtra("postedBy", mDataPostedBy);
				intent.putExtra("location", mDataLocation);
				intent.putExtra("price", mDataPrice);
				intent.putExtra("description", mDataDetails);
				
				startActivityForResult(intent, 0);
			}
		});
		mDoNotWatchItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(mTAG, "DoNotWatchMeBtn was clicked");
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Sales");
				query.getInBackground(mDataObjectId, new GetCallback<ParseObject>() 
				{
					public void done(ParseObject object, ParseException e) 
					{
						if (e == null) 
						{
							mWatchingRelation.remove(object);
							mCurrentUser.saveInBackground(new SaveCallback() 
							{
								@Override
								public void done(ParseException e) 
								{
									if (e == null)
									{
										// Closing Activity and displaying Toast Notification that the sale was removed successfully.
										finish();
										Toast.makeText(getApplicationContext(), "You have successfully removed the item " +  "'" + mDataTitle + "'" + " from your Watch List!", Toast.LENGTH_LONG).show();
									}
									else
									{
										Log.e(mTAG, e.getMessage());
									}
								}
							});
						} else {
							// Not remove from watch list - something went wrong...
							Log.e(mTAG, e.toString());
						}
					}
				});
			}
		});				
	}

	private void initializeUIElements() {
		mItemName = (TextView) findViewById(R.id.textView_watchingSalesDetailTitle);
		mItemPrice = (TextView) findViewById(R.id.textView_watchingSalesDetailPrice);
		mItemLocation = (TextView) findViewById(R.id.textView_watchingSalesDetailLocation);
		mItemDetails = (TextView) findViewById(R.id.textView_watchingSalesDetailDetails);
		mDoNotWatchItem = (Button) findViewById(R.id.button_watchingSalesDetailUnwatchItem);
		mContactSeller = (Button) findViewById(R.id.button_watchingSalesDetailContactSeller);
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
		getMenuInflater().inflate(R.menu.watching_sales_detail, menu);
		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.action_share_watching_item);
		mProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
		if(mProvider != null )
		{
			mProvider.setShareIntent(customShareIntent());
			mProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {
		        @Override
		        public boolean onShareTargetSelected(ShareActionProvider actionProvider, Intent intent) {
		            rewardUser();
		            return false;
		        }
		    });
		}
		return true;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data); 
		Log.i(mTAG, "onActivityResult Fired");

		switch(requestCode) { 
		case 0 : { 
			if (resultCode == Activity.RESULT_OK) { 
				mDataTitle = data.getStringExtra("title");
				mDataPrice = data.getStringExtra("price");
				mDataLocation = data.getStringExtra("location");
				mDataDetails = data.getStringExtra("description");
				mDataObjectId = data.getStringExtra("oid");
				mDataPostedBy = data.getStringExtra("postedBy");

				mItemName.setText(mDataTitle);
				mItemPrice.setText(mDataPrice);
				mItemLocation.setText(mDataLocation);
				mItemDetails.setText(mDataDetails);
			} 
			break; 
		} 
		} 
	}
	public void rewardUser(){
		int gBuxAmount = mCurrentUser.getInt("gBux");
		int gBuxAmountModified = gBuxAmount + 5;
		mCurrentUser.put("gBux", gBuxAmountModified);
		mCurrentUser.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null)
				{
					Toast.makeText(getApplicationContext(), "You have been rewarded 5 gBux for sharing the item " + "'" + mDataTitle + "'" + " !", Toast.LENGTH_LONG).show();

				}
			}
		});
	}
	private Intent customShareIntent()
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "'" + mDataTitle + "'" + " has been posted via Android's GeoShare app for only a whopping " + mDataPrice + " bucks!" + "\n" + "Get it while you can by downloading the app today!");
		return intent;
	}
}
