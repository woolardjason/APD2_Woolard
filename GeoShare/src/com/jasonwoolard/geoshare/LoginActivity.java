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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	// Member Variables
	public TextView mSignUpTV;
	public TextView mResetPwTV;
	public EditText mUserName;
	public EditText mUserPassword;
	public Button mLogInBtn; 
	public CheckBox mSaveUserName;
	SharedPreferences mSharedPref;
	public static String mUserData = "UserSavedData";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Initializing all UI Elements to be used
		initializeUIElements();

		mSignUpTV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Setting intent for the SignUpActivity to be launched, then starting the intent.
				Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(i);

			}
		});
		mResetPwTV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Create the fragment and show it as a dialog.
				ResetPasswordDialogFragment newFragment = ResetPasswordDialogFragment.newInstance();
				newFragment.show(getFragmentManager(), "dialog");
			}
		});
		// Obtaining Shared Preferences Data
		mSharedPref = getSharedPreferences(mUserData, 0);

		String savedUserName = mSharedPref.getString("username", "");
		if (!savedUserName.isEmpty())
		{
			mUserName.setText(savedUserName);
			mSaveUserName.setChecked(true);
		}		

		// Setting on click listener for the Sign Up Button
		mLogInBtn.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// Obtaining user entered information and trimming any white space.
				String userName = mUserName.getText().toString().trim();
				String password = mUserPassword.getText().toString().trim();

				// Checking if fields are empty, if so alert the user.
				if (userName.isEmpty() || password.isEmpty())
				{
					AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
					b.setMessage(R.string.error_message_sign_up_missing_field);
					b.setTitle(R.string.error_title_sign_up_missing_field);
					b.setPositiveButton(android.R.string.ok, null);
					AlertDialog d = b.create();
					d.show();
				} 
				// Checking if the verified entered password matches the entered password, if not alert the user.
				else
				{
					ParseUser.logInInBackground(userName, password, new LogInCallback() {

						@Override
						public void done(ParseUser user, ParseException e) {
							if (e == null)
							{ 
								// Saving inputed email address to SharedPreferences
								if (mSaveUserName.isChecked() == true)
								{
									// Referencing string preference editor
									SharedPreferences.Editor editor = mSharedPref.edit();

									// Obtaining user's username from EditText
									String usersName = mUserName.getText().toString();
									// Putting string in username key
									editor.putString("username", usersName);

									// Committing / Saving out data
									editor.commit();			 
								}
								else
								{
									// User rather not save their user name, so if they previously saved it empty the username key in SharedPref
									SharedPreferences.Editor editor = mSharedPref.edit();

									// Putting string in username key
									editor.putString("username", "");

									// Committing / Saving out data
									editor.commit();			 
								}
								Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);
							}
							else
							{
								AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
								b.setMessage(e.getMessage());
								b.setTitle(R.string.error_title_sign_up_verify_pw);
								b.setPositiveButton(android.R.string.ok, null);
								AlertDialog d = b.create();
								d.show();
							}
						}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void initializeUIElements() {
		mUserName = (EditText) findViewById(R.id.editText_username);
		mUserPassword = (EditText) findViewById(R.id.editText_password);
		// Setting the Type Face to Default as well as setting the edittext's transformation method to Password. 
		// This overrides the font face fixing the off-font from the standard Edit Texts when using Password type EditTexts.
		mUserPassword.setTypeface(Typeface.DEFAULT);
		mUserPassword.setTransformationMethod(new PasswordTransformationMethod());
		mLogInBtn = (Button) findViewById(R.id.button_login);
		mSaveUserName = (CheckBox) findViewById(R.id.checkBox_saveUsername);
		mSignUpTV = (TextView)findViewById(R.id.textView_sign_up);
		mResetPwTV = (TextView) findViewById(R.id.textView_forgot_pw);
	}
}
