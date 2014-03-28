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

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InboxMessageReplyActivity extends Activity {
	TextView mSellerName;
	EditText mMessageDetails;
	EditText mSubjectDetails;
	String mDataItemOid;
	String mDataSellerName;
	ParseUser mCurrentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox_message_reply);
	    getActionBar().setDisplayHomeAsUpEnabled(true);

		mSellerName = (TextView) findViewById(R.id.textView_inboxMsgReply_receiversName);
		
		mMessageDetails = (EditText) findViewById(R.id.editText_inboxMsgReply_message);
		mSubjectDetails = (EditText) findViewById(R.id.editText_inboxMsgReply_subject);
		
		
		mCurrentUser = ParseUser.getCurrentUser();
		
		Intent intent = getIntent();
		mDataItemOid = intent.getStringExtra("oid");
		mDataSellerName = intent.getStringExtra("sender");
		
		mSellerName.setText(mDataSellerName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inbox_message_reply, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
		case R.id.action_reply_to_message:
			// Creating a new message to be saved to the backend
			ParseObject sale = new ParseObject("Messages");
			sale.put("subject", mSubjectDetails.getText().toString());
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
}
