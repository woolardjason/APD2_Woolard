package com.jasonwoolard.geoshare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PostSaleActivity extends Activity {

	Button mPostSaleBtn;
	EditText mItemTitle;
	EditText mItemPrice;
	EditText mItemLocation;
	EditText mItemDescription;
	EditText mCaptchaInput;
	TextView mCaptchaCode;
	ParseUser user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_sale);
		// Initializing UI Elements defined as Member Variables and in the XML
		initializeUIElements();
		// Setting on click Listener for the Post Sale Button
		mPostSaleBtn.setOnClickListener(new View.OnClickListener() {
		
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// Obtaining inputed information from user, removing any whitespace in the process.
				String itemTitle = mItemTitle.getText().toString().trim();
				String itemPrice = mItemPrice.getText().toString().trim();
				String itemLocation = mItemLocation.getText().toString().trim();
				String itemDescription = mItemDescription.getText().toString().trim();
				String captchaInput = mCaptchaInput.getText().toString().trim();
				
				// Checking EditText fields for empty values, if they are return an alert dialog for the user.
				if (itemTitle.isEmpty() || itemPrice.isEmpty() || itemLocation.isEmpty() || itemDescription.isEmpty() || captchaInput.isEmpty())
				{
					AlertDialog.Builder b = new AlertDialog.Builder(PostSaleActivity.this);
					b.setMessage(R.string.error_message_sign_up_missing_field);
					b.setTitle(R.string.error_title_sign_up_missing_field);
					b.setPositiveButton(android.R.string.ok, null);
					AlertDialog d = b.create();
					d.show();
				}
				else
				{
					// Creating a new sale to be saved to the backend
					ParseObject sale = new ParseObject("Sales");
					sale.put("title", itemTitle);
					sale.put("price", itemPrice);
					sale.put("location", itemLocation);
					sale.put("description", itemDescription);

					sale.put("postedBy", user);
					// Saving sale out to the backend
					sale.saveInBackground(new SaveCallback()
					{

						@Override
						public void done(ParseException e) {
							if (e == null) 
							{
								// Closing Activity and displaying Toast Notification that the sale was posted successfully.
								finish();
								Toast.makeText(getApplicationContext(), "Your item has been posted for sale successfully!", Toast.LENGTH_LONG).show();
							} 
							else 
							{
								// Not closing the Activity, and displaying the error message via a toast message to the user.
								Toast.makeText(getApplicationContext(), "An error has occured: " + e +  "\n" + "Please try posting again shortly.", Toast.LENGTH_LONG).show();
							}
						}
					});
				}
			}
		});
		
		
	}

	public void initializeUIElements() {
		
		user = ParseUser.getCurrentUser();
		
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
