package com.jasonwoolard.geoshare;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InboxMessageActivity extends Activity {

	TextView mSenderName;
	TextView mSenderDate;
	TextView mSenderSubject;
	TextView mSenderMessage;
	Button mReplyToSender;
	Button mDeleteMessage;
	String mMessageOid;
	String mMessageMessage;
	String mMessageDate;
	String mMessageSender;
	String mMessageSubject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox_message);
		
		Intent intent = getIntent();
		
		mMessageSubject = intent.getStringExtra("subject");
		mMessageSender = intent.getStringExtra("sender");
		mMessageDate = intent.getStringExtra("date");
		mMessageMessage = intent.getStringExtra("message");
		mMessageOid = intent.getStringExtra("oid");
		
		mSenderName = (TextView) findViewById(R.id.textView_receivedMessageSentBy);
		mSenderDate = (TextView) findViewById(R.id.textView_receivedMessageSentOn);
		mSenderSubject = (TextView) findViewById(R.id.textView_receivedMessageTitle);
		mSenderMessage = (TextView) findViewById(R.id.textView_receivedMessageMessage);
		mReplyToSender = (Button) findViewById(R.id.button_replyToSender);
		mDeleteMessage = (Button) findViewById(R.id.button_deleteMessage);
		mReplyToSender.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), InboxMessageReplyActivity.class);
				
				intent.putExtra("oid", mMessageOid);
				intent.putExtra("sender", mMessageSender);

				startActivity(intent);
			}
		});
		mDeleteMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Deleting the object from parse backend
            	ParseObject.createWithoutData("Messages", mMessageOid).deleteInBackground(new DeleteCallback() 
            	{
					@Override
					public void done(ParseException e) 
					{
						if (e == null)
						{
							finish();
							Toast.makeText(getApplicationContext(), "You have successfully deleted the message.", Toast.LENGTH_LONG).show();

						}
					}
            	});
			}
		});
		
		mSenderName.setText(mMessageSender);
		mSenderDate.setText(mMessageDate);
		mSenderSubject.setText(mMessageSubject);
		mSenderMessage.setText(mMessageMessage);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inbox_message, menu);
		return true;
	}

}
