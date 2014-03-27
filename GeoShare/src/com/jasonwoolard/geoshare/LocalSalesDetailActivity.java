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
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
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

public class LocalSalesDetailActivity extends ActionBarActivity {

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
	ProgressDialog mProgressDialog;
	
	private ShareActionProvider mProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_sales_detail);
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
								ImageView saleImage = (ImageView) findViewById(R.id.imageView_localSalesDetailPhoto);
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
		mWatchItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialogShow();
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
									progressDialogHide();
									// Displaying Toast Notification that the sale was successfully added to Watch List
									Toast.makeText(getApplicationContext(), "You have successfully added the item " +  "'" + mDataTitle + "'" + " to your Watch List!", Toast.LENGTH_LONG).show();

								}
								else
								{
									Log.e(mTAG, e.getMessage());
									progressDialogHide();
								}
							}
						});

				    } else {
				    	// Not added to watch list - something went wrong...
						  Log.e("done fired", e.toString());
						  progressDialogHide();
				    }
				  }
				});
			}
		});
		mContactSeller.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ContactSellerLSActivity.class);
				
				intent.putExtra("title", mDataTitle);
				intent.putExtra("oid", mDataObjectId);
				intent.putExtra("postedBy", mDataPostedBy);
				intent.putExtra("location", mDataLocation);
				intent.putExtra("price", mDataPrice);
				intent.putExtra("description", mDataDetails);

				startActivityForResult(intent, 0);
			}
		});
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data); 
		Log.i(mTAG, "onActivityResult called");
		switch(requestCode) { 
		case 0: { 
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
	private void initializeUIElements() {
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
		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.action_share_others_item);
		mProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
		if(mProvider != null )
		{
			 Intent intent = new Intent(Intent.ACTION_SEND);
			 intent.setType("text/plain");
			 intent.putExtra(Intent.EXTRA_TEXT, "'"+ mDataTitle + "'" +  " has been posted via Android's GeoShare app for only a whopping " + mDataPrice + " bucks!" + "\n" + "Get it while you can by downloading the app today!");
			 mProvider.setShareIntent(intent);
		}
		return true;
	}
	private void progressDialogShow() {
	    mProgressDialog = new ProgressDialog(this);
	    mProgressDialog.setTitle("One Moment...");
	    mProgressDialog.setMessage("Adding sale to your watch list!");
	    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    mProgressDialog.setCancelable(false);
	    mProgressDialog.show();
	}
	
	private void progressDialogHide() {
		if (mProgressDialog != null && mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
}
