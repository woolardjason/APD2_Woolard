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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends Activity {

	// Constants
	public TextView mSignUpTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mSignUpTV = (TextView)findViewById(R.id.textView_sign_up);
		mSignUpTV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Setting intent for the SignUpActivity to be launched, then starting the intent.
				Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(i);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
