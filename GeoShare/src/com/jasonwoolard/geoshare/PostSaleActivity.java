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

import java.io.File;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PostSaleActivity extends Activity {

	Button mPostSaleBtn;
	Button mSnapPhotoBtn;
	ProgressDialog mProgressDialog;
	EditText mItemTitle;
	EditText mItemPrice;
	EditText mItemLocation;
	EditText mItemDescription;
	EditText mCaptchaInput;
	TextView mCaptchaCode;
	ImageView mItemImage;
	ParseUser mUser;
	String mCurrentUser;
	
	public Uri inputedImageUri;
	Uri sImage;
	private Sale sale;


	final static int mData = 1;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sale = new Sale();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_sale);
		// Initializing UI Elements defined as Member Variables and in the XML
		initializeUIElements();
		mSnapPhotoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				File photoTaken = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoTaken));
				inputedImageUri = Uri.fromFile(photoTaken);
				startActivityForResult(intent, mData);
				
			}
		});
		// Setting on click Listener for the Post Sale Button
		mPostSaleBtn.setOnClickListener(new View.OnClickListener() {
		
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				progressDialogShow();

				// Obtaining inputed information from user, removing any whitespace in the process.
				String itemTitle = mItemTitle.getText().toString().trim();
				String itemPrice = mItemPrice.getText().toString().trim();
				String itemLocation = mItemLocation.getText().toString().trim();
				String itemDescription = mItemDescription.getText().toString().trim();
				String captchaInput = mCaptchaInput.getText().toString().trim();
				
				// Checking EditText fields for empty values, if they are return an alert dialog for the user.
				if (itemTitle.isEmpty() || itemPrice.isEmpty() || itemLocation.isEmpty() || itemDescription.isEmpty() || captchaInput.isEmpty())
				{
					progressDialogHide();

					AlertDialog.Builder b = new AlertDialog.Builder(PostSaleActivity.this);
					b.setMessage(R.string.error_message_sign_up_missing_field);
					b.setTitle(R.string.error_title_sign_up_missing_field);
					b.setPositiveButton(android.R.string.ok, null);
					AlertDialog d = b.create();
					d.show();
				}
				else
				{	
					sale.setSaleTitle(itemTitle);
					sale.setSalePrice("$"+itemPrice);
					sale.setSaleDescription(itemDescription);
					sale.setSaleLocation(itemLocation);
					sale.setSalePoster(mUser);
					
					if (sImage != null)
					{
						byte[] fileBytes = FileHelper.getByteArrayFromFile(PostSaleActivity.this, sImage);
						if (fileBytes == null)
						{
							// FileBytes is null, something went wrong, no image found?
						}
						else
						{
							// Utilizing library to reduceImageSize for Uploading to Parse
							fileBytes = FileHelper.reduceImageForUpload(fileBytes);
							String fileName = FileHelper.getFileName(PostSaleActivity.this, sImage);
							ParseFile file = new ParseFile(fileName, fileBytes);
							sale.setSalePhotoFile(file);
						}
					}
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
								progressDialogHide();
							} 
							else 
							{
								// Not closing the Activity, and displaying the error message via a toast message to the user.
								Toast.makeText(getApplicationContext(), "An error has occured: " + e +  "\n" + "Please try posting again shortly.", Toast.LENGTH_LONG).show();
								progressDialogHide();
							}
						}
					});
				}
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Operation succeeded? If so:
		switch (requestCode) {
		case mData:
			if (resultCode == RESULT_OK)
			{
				sImage = inputedImageUri;
				getContentResolver().notifyChange(sImage, null);
				ContentResolver contentResolver = getContentResolver();
				Bitmap bmp = null;
				try {
					bmp = android.provider.MediaStore.Images.Media
							.getBitmap(contentResolver, sImage);
					mItemImage.setImageBitmap(bmp);
					Toast.makeText(this, sImage.toString(),
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
					.show();
					Log.e("Camera", e.toString());
				}
			}
			break;
		}
	}
	public void initializeUIElements() { 

		
		mPostSaleBtn = (Button) findViewById(R.id.button_post_sale);
		mSnapPhotoBtn = (Button) findViewById(R.id.button_snapPhoto);
		mItemTitle = (EditText) findViewById(R.id.editText_item_title);
		mItemPrice = (EditText) findViewById(R.id.editText_item_price);
		mItemLocation = (EditText) findViewById(R.id.editText_item_location);
		mItemDescription = (EditText) findViewById(R.id.editText_item_description);
		mCaptchaInput = (EditText) findViewById(R.id.editText_captcha_input);
		mItemImage = (ImageView) findViewById(R.id.imageView_saleImage);
		mCaptchaCode = (TextView) findViewById(R.id.textView_captcha_code);
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void presentUserWithLogin() {
		// Displaying the Login Activity to the user
		Intent i = new Intent(this, LoginActivity.class);
		// Logging in =  New Task, Old Task = Clear so back button cannot be used to go back into Profile Activity if logged out.
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		
		mUser = ParseUser.getCurrentUser();
		mCurrentUser =  mUser.getUsername();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_sale, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
		case R.id.action_log_out:
			// Logging out the current user and presenting them with the login activity.
			ParseUser.logOut();
			presentUserWithLogin();

			break;
		case R.id.action_profile_activity:
			finish();
			break;
		case R.id.action_local_sales:
			Intent intent = new Intent(this, LocalSalesActivity.class);
			startActivity(intent);
		}
		
		return super.onOptionsItemSelected(item);
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
		if (mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
}
