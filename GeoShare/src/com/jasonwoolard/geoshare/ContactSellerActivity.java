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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ContactSellerActivity extends Activity {
	TextView mSellerName;
	TextView mItemTitle;
	EditText mMessageDetails;
	String mDataItemTitle;
	String mDataItemOid;
	String mDataSellerName;
	String mDataItemDescription;
	String mDataItemPrice;
	String mDataItemLocation;
    ParseUser mCurrentUser;
	String mTAG = "ContactSellerActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_seller);
		
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
		mSellerName = (TextView) findViewById(R.id.textView_contactSellerSellersName);
		mItemTitle = (TextView) findViewById(R.id.textView_contactSellerSubject);
		mMessageDetails = (EditText) findViewById(R.id.editText_contactSellerMessageField);
		
		mCurrentUser = ParseUser.getCurrentUser();
		
		Intent intent = getIntent();
		mDataItemTitle = intent.getStringExtra("title");
		mDataItemOid = intent.getStringExtra("oid");
		mDataSellerName = intent.getStringExtra("postedBy");
		mDataItemDescription = intent.getStringExtra("description");
		mDataItemPrice = intent.getStringExtra("price");
		mDataItemLocation = intent.getStringExtra("location");
		
		mSellerName.setText(mDataSellerName);
		mItemTitle.setText("(Item Inquiry)  " + mDataItemTitle );
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_seller, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
		case R.id.action_send_message:
			// Creating a new message to be saved to the backend
			ParseObject sale = new ParseObject("Messages");
			sale.put("subject", "Item Inquiry - " + mDataItemTitle);
			sale.put("message", mMessageDetails.getText().toString());
			sale.put("receiver", mDataSellerName);
			sale.put("sender", mCurrentUser.getUsername());

			// Saving sale out to the backend
			sale.saveInBackground(new SaveCallback()
			{

				@Override
				public void done(ParseException e) {
					if (e == null) 
					{
						// Closing Activity and displaying Toast Notification that the message was sent successfully.
						finish();
						Toast.makeText(getApplicationContext(), "Your message was successfully sent to " + mDataSellerName, Toast.LENGTH_LONG).show();
					} 
					else 
					{
						// Not closing the Activity, and displaying the error message via a toast message to the user.
						Toast.makeText(getApplicationContext(), "An error has occured: " + e +  "\n" + "Please try posting again shortly.", Toast.LENGTH_LONG).show();
					}
				}
			});
			break;
		  case android.R.id.home:
			  finish();
			  return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {
		Log.i(mTAG, "Finish fired!");
		Intent data = new Intent();
		data.putExtra("title", mDataItemTitle);
		data.putExtra("description", mDataItemDescription);
		data.putExtra("location", mDataItemLocation);
		data.putExtra("price", mDataItemPrice);
		data.putExtra("oid", mDataItemOid);
		data.putExtra("postedBy", mDataSellerName);

		setResult(RESULT_OK, data);
		super.finish();
	}
}
