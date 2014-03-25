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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;


public class MyPostedSalesDetailActivity extends ActionBarActivity  {
	
	TextView mItemName;
	TextView mItemPrice;
	TextView mItemLocation;
	TextView mItemDetails;
	Button mDeleteBtn;
    ActionBar mActionBar;
	String mObjectId;
	String mPassedItemsName;
	String mPassedItemsPrice;

	private ShareActionProvider mProvider;
	
	String mTAG = "MyPostedSalesDetailActivity";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_posted_sales_detail);
		mActionBar = getSupportActionBar();
		mItemName = (TextView) findViewById(R.id.textView_mypost_title);
		mItemPrice = (TextView) findViewById(R.id.textView_mypost_asking_price);
		mItemLocation = (TextView) findViewById(R.id.textView_mypost_location);
		mItemDetails = (TextView) findViewById(R.id.textView_mypost_item_details);
		mDeleteBtn = (Button) findViewById(R.id.button_mypost_delete_btn);
		Intent intent = getIntent();

		mPassedItemsName = intent.getStringExtra("title");
		mPassedItemsPrice = intent.getStringExtra("price");
		String itemLocation = intent.getStringExtra("location");
		String itemDetails = intent.getStringExtra("description");

		mObjectId = intent.getStringExtra("oid");

		mDeleteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				// Add the buttons
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {
						// Deleting the object from parse backend
						ParseObject.createWithoutData("Sales", mObjectId).deleteInBackground(new DeleteCallback() 
						{
							@Override
							public void done(ParseException e) 
							{
								if (e == null)
								{
									dialog.dismiss();
									finish();
									Toast.makeText(getApplicationContext(), "You have successfully deleted your posted sale.", Toast.LENGTH_LONG).show();

								}
							}
						});
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

				builder.setMessage("Are you sure you want to delete your sale permanently?").setTitle("Delete Confirmation");

				// Creating the AlertDialog
				AlertDialog dialog = builder.create();
				// Showing The AlertDialog
				dialog.show();
			}
		});	
		
		mItemName.setText(mPassedItemsName);
		mItemPrice.setText(mPassedItemsPrice);
		mItemLocation.setText(itemLocation);
		mItemDetails.setText(itemDetails); 
		
	}

	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_posted_sales_detail, menu);
		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.action_share_own_item);
		mProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
		if(mProvider != null )
		{
			 Intent intent = new Intent(Intent.ACTION_SEND);
			 intent.setType("text/plain");
			 intent.putExtra(Intent.EXTRA_TEXT, "'" + mPassedItemsName + "'" + " has been posted via Android's GeoShare app for only a whopping " + mPassedItemsPrice + " bucks!" + "\n" + "Get it while you can by downloading the app today!");
			  mProvider.setShareIntent(intent);
		}
		return true;
	}
}
