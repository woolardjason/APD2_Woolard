package com.jasonwoolard.geoshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InboxMessageActivity extends Activity {

	TextView mSenderName;
	TextView mSenderDate;
	TextView mSenderSubject;
	TextView mSenderMessage;
	Button mReplyToSender;
	Button mDeleteMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox_message);
		
		Intent intent = getIntent();
		
		String messageSubject = intent.getStringExtra("subject");
		String messageSender = intent.getStringExtra("sender");
		String messageDate = intent.getStringExtra("date");
		String messageMessage = intent.getStringExtra("message");
		String messageOid = intent.getStringExtra("oid");
		
		mSenderName = (TextView) findViewById(R.id.textView_receivedMessageSentBy);
		mSenderDate = (TextView) findViewById(R.id.textView_receivedMessageSentOn);
		mSenderSubject = (TextView) findViewById(R.id.textView_receivedMessageTitle);
		mSenderMessage = (TextView) findViewById(R.id.textView_receivedMessageMessage);
		mReplyToSender = (Button) findViewById(R.id.button_replyToSender);
		mDeleteMessage = (Button) findViewById(R.id.button_deleteMessage);
		mReplyToSender.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		mDeleteMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mSenderName.setText(messageSender);
		mSenderDate.setText(messageDate);
		mSenderSubject.setText(messageSubject);
		mSenderMessage.setText(messageMessage);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inbox_message, menu);
		return true;
	}

}
