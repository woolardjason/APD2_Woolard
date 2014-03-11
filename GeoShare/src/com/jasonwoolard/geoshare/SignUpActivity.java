/*
 * Project		GeoShare
 * 
 * Package		com.jasonwoolard.geoshare
 * 
 * @author		Jason Woolard
 * 
 * Date			Mar 11, 2014
 */
package com.jasonwoolard.geoshare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends Activity {
	
	public EditText mUserName;
	public EditText mUserPassword;
	public EditText mUserVerifyPassword;
	public EditText mUserEmailAddress;
	public Button mSignUpBtn; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		// Initializing all UI Elements to be used
		initializeUIElements();
		
		// Setting on click listener for the Sign Up Button
		mSignUpBtn.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				String userName = mUserName.getText().toString().trim();
				String password = mUserPassword.getText().toString().trim();
				String verifyPassword = mUserVerifyPassword.getText().toString().trim();
				String emailAddress = mUserEmailAddress.getText().toString().trim();
				
				if (userName.isEmpty() || password.isEmpty() || verifyPassword.isEmpty() || emailAddress.isEmpty())
				{
					AlertDialog.Builder b = new AlertDialog.Builder(SignUpActivity.this);
					b.setMessage(R.string.error_message_sign_up_missing_field);
					b.setTitle(R.string.error_title_sign_up_missing_field);
					b.setPositiveButton(android.R.string.ok, null);
					AlertDialog d = b.create();
					d.show();
				} 
				else if (!verifyPassword.matches(password))
				{
					AlertDialog.Builder b = new AlertDialog.Builder(SignUpActivity.this);
					b.setMessage(R.string.error_message_sign_up_verify_pw);
					b.setTitle(R.string.error_title_sign_up_verify_pw);
					b.setPositiveButton(android.R.string.ok, null);
					AlertDialog d = b.create();
					d.show();
				}
				else
				{
					// Actually sign up...
				}
			}
		});
		
	}

	public void initializeUIElements() {
		mUserName = (EditText) findViewById(R.id.editText_username);
		mUserPassword = (EditText) findViewById(R.id.editText_password);
		mUserVerifyPassword = (EditText) findViewById(R.id.editText_verifyPassword);
		mUserEmailAddress = (EditText) findViewById(R.id.editText_emailAddress);
		mSignUpBtn = (Button) findViewById(R.id.button_sign_up);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

}
