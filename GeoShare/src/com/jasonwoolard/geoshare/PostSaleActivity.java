package com.jasonwoolard.geoshare;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PostSaleActivity extends Activity {

	Button mPostSaleBtn;
	EditText mItemTitle;
	EditText mItemPrice;
	EditText mItemLocation;
	EditText mItemDescription;
	EditText mCaptchaInput;
	TextView mCaptchaCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_sale);
		// Initializing UI Elements defined as Member Variables and in the XML
		initializeUIElements();
		// Setting on click Listener for the Post Sale Button
		mPostSaleBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
	}

	public void initializeUIElements() {
		mPostSaleBtn = (Button) findViewById(R.id.button_post_sale);

		mItemTitle = (EditText) findViewById(R.id.editText_item_title);
		mItemPrice = (EditText) findViewById(R.id.editText_item_price);
		mItemLocation = (EditText) findViewById(R.id.editText_item_location);
		mItemDescription = (EditText) findViewById(R.id.editText_item_description);
		mCaptchaInput = (EditText) findViewById(R.id.editText_captcha_input);
		
		mCaptchaCode = (TextView) findViewById(R.id.textView_captcha_code);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_sale, menu);
		return true;
	}

}
