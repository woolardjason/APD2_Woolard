package com.jasonwoolard.geoshare;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
		
		initializeUIElements();
		
		mItemName.setText(mDataTitle);
		mItemPrice.setText(mDataPrice);
		mItemLocation.setText(mDataLocation);
		mItemDetails.setText(mDataDetails);
		mWatchItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		mContactSeller.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.local_sales_detail, menu);
		return true;
	}

}
