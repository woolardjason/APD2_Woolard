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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {
	
	public EditText mUserName;
	public EditText mUserPassword;
	public EditText mUserVerifyPassword;
	public EditText mUserEmailAddress;
	public Button mSignUpBtn; 
	ProgressDialog mProgressDialog;
	String mTAG = "SignUpActivity";
	
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
				// Obtaining user entered information and trimming any white space.
				String userName = mUserName.getText().toString().trim();
				String password = mUserPassword.getText().toString().trim();
				String verifyPassword = mUserVerifyPassword.getText().toString().trim();
				String emailAddress = mUserEmailAddress.getText().toString().trim();
				// Checking if fields are empty, if so alert the user.
				if (userName.isEmpty() || password.isEmpty() || verifyPassword.isEmpty() || emailAddress.isEmpty())
				{
					alertUser(R.string.error_message_sign_up_missing_field, R.string.error_title_sign_up_missing_field);
					
				} 
				// Checking if the verified entered password matches the entered password, if not alert the user.
				else if (!verifyPassword.matches(password))
				{
					alertUser(R.string.error_message_sign_up_verify_pw, R.string.error_title_sign_up_verify_pw);
				
				}
				else
				{
					progressDialogShow();
					// Actually sign up...
					ParseUser newGeoShareUser = new ParseUser();
					newGeoShareUser.setUsername(userName);
					newGeoShareUser.setPassword(password);
					newGeoShareUser.setEmail(emailAddress);
					// Rewarding the user with 100 gBux to be used in the applications future virtual market
					newGeoShareUser.put("gBux", 100);
					newGeoShareUser.signUpInBackground(new SignUpCallback() {
						
						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							if (e == null)
							{
								progressDialogHide();

								// TODO: User has successfully created an account, reward the user with g-Bux and return user
								// TODO: to the profile activity
								Intent i = new Intent(SignUpActivity.this, ProfileActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);
								
							}
							else
							{
								progressDialogHide();
							}
						}
					});
				}
			}
		});
		
	}
	public void initializeUIElements() {
		mUserName = (EditText) findViewById(R.id.editText_username);
		mUserPassword = (EditText) findViewById(R.id.editText_password);
		// Setting the Type Face to Default as well as setting the edittext's transformation method to Password. 
		// This overrides the font face fixing the off-font from the standard Edit Texts when using Password type EditTexts.
		mUserPassword.setTypeface(Typeface.DEFAULT);
		mUserPassword.setTransformationMethod(new PasswordTransformationMethod());
		mUserVerifyPassword = (EditText) findViewById(R.id.editText_verifyPassword);
		// Setting the Type Face to Default as well as setting the edittext's transformation method to Password. 
		// This overrides the font face fixing the off-font from the standard Edit Texts when using Password type EditTexts.
		mUserVerifyPassword.setTypeface(Typeface.DEFAULT);
		mUserVerifyPassword.setTransformationMethod(new PasswordTransformationMethod());
		mUserEmailAddress = (EditText) findViewById(R.id.editText_emailAddress);
		mSignUpBtn = (Button) findViewById(R.id.button_sign_up);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}
	
	private void alertUser(int message, int title)
	{
		Resources re = getResources();
	    String fTitle = re.getString(title);
	    String fMessage = re.getString(message);
		AlertDialog.Builder b = new AlertDialog.Builder(SignUpActivity.this);
		b.setMessage(fMessage);
		b.setTitle(fTitle);
		b.setPositiveButton(android.R.string.ok, null);
		AlertDialog d = b.create();
		d.show();
	}
	private void progressDialogShow() {
	    mProgressDialog = new ProgressDialog(this);
	    mProgressDialog.setTitle("Signing Up...");
	    mProgressDialog.setMessage("Please wait while we sign you up!");
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
