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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class LoginActivity extends Activity {

	// Member Variables 
	public TextView mResetPwTV;
	public EditText mUserName;
	public EditText mUserPassword;
	public Button mLogInBtn; 
	public Button mLogInAsGuestBtn;
	public Button mSignUpBtn;
	public CheckBox mSaveUserName;
	SharedPreferences mSharedPref;
	public static String mUserData = "UserSavedData";
	ProgressDialog mProgressDialog;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Initializing all UI Elements to be used
		initializeUIElements();
		mSignUpBtn.setOnClickListener(new View.OnClickListener() {
			
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
				
			    final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.alertdialog_resetpw);
                dialog.setTitle("Reset Password");
                dialog.show();

                final EditText resetEmail = (EditText) dialog.findViewById(R.id.editText_resetEmailAddress);
                Button btnReset = (Button)dialog.findViewById(R.id.button_resetPW);
                btnReset.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(final View v) {
						// Setting inputedString to the current text in resetEmail field
		                String inputedString = resetEmail.getText().toString();
		                
		                // Running Parse Request Password Reset in Background method which doesn't boggle up the main thread.
		                ParseUser.requestPasswordResetInBackground(inputedString, new RequestPasswordResetCallback() 
		                {
		                	public void done(ParseException e) 
		                	{
		                		if (e == null) 
		                		{
		                			alertUser(R.string.success_message_reset_pw, R.string.success_title_reset_pw);
		                			
		                		} 
		                		else
		                		{
		                			alertUserException(e.getMessage().toString(), R.string.error_title_sign_up_verify_pw);
		                		}
		                	}
		                });
						
					}
				});
                Button btnCancel = (Button)dialog.findViewById(R.id.button_cancelResetPw);
                btnCancel.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
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
		mLogInAsGuestBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ParseAnonymousUtils.logIn(new LogInCallback() {
					  @Override
					  public void done(ParseUser user, ParseException e) {
					    if (e != null) {
					      Log.d("MyApp", "Anonymous login failed.");
					    } else {
					      Log.d("MyApp", "Anonymous user logged in.");
					      Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(i);
					    }
					  }
					});
			}
		});
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
					alertUser(R.string.error_message_sign_up_missing_field, R.string.error_title_sign_up_missing_field);
				} 
				// Checking if the verified entered password matches the entered password, if not alert the user.
				else
				{
					progressDialogShow();
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
								progressDialogHide();
								Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);
							}
							else
							{
								alertUserException(e.getMessage().toString(), R.string.error_title_sign_up_verify_pw);
								progressDialogHide();
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
		mSignUpBtn = (Button) findViewById(R.id.button_signUp);
		mResetPwTV = (TextView) findViewById(R.id.textView_forgot_pw);
		mLogInAsGuestBtn = (Button) findViewById(R.id.button_browse_as_guest);
	}
	private void alertUser(int message, int title) {
		Resources re = getResources();
	    String fTitle = re.getString(title);
	    String fMessage = re.getString(message);
		AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
		b.setMessage(fMessage);
		b.setTitle(fTitle);
		b.setPositiveButton(android.R.string.ok, null);
		AlertDialog d = b.create();
		d.show();
	}
	private void alertUserException(String message, int title) {
		Resources re = getResources();
	    String fTitle = re.getString(title);
		AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
		b.setMessage(message);
		b.setTitle(fTitle);
		b.setPositiveButton(android.R.string.ok, null);
		AlertDialog d = b.create();
		d.show();
	}
	private void progressDialogShow() {
	    mProgressDialog = new ProgressDialog(this);
	    mProgressDialog.setTitle("Logging In...");
	    mProgressDialog.setMessage("Attempting credentials, please wait.");
	    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    mProgressDialog.setCancelable(false);
	    mProgressDialog.show();
	}
	private void progressDialogHide() {
		if(mProgressDialog.isShowing())
		{
		mProgressDialog.dismiss();
		mProgressDialog = null;
		}
	}
}
