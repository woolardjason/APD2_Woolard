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
import java.util.Random;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.parse.ParseGeoPoint;
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
	LocationManager mLocationManager;
	LocationListener mLocationListener;
	double mLatitude;
	double mLongitude;
	public Uri inputedImageUri;
	Uri sImage;
	private Sale sale;
	String mTAG = "PostSaleActivity";
	String[] captchaCodes;

	final static int mData = 1;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sale = new Sale();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_sale);

		// Initializing UI Elements defined as Member Variables and in the XML
		initializeUIElements();

		// Captcha Codes
		captchaCodes = new String[]{"2002", "5642", "3123", "5468", "9824", "6029", "2094", "1001", "2093", "3097", "8209", "2989", "0092", "1208"};

		mItemImage.setImageResource(R.drawable.placeholder);
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
				// getting network status
				mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
				if(userIsOnline())
				{
					mLocationListener = new UserLocation();
					mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
				}
				else
				{
					Toast.makeText(getApplicationContext(), "It appears you have no network connectivity, please try again!", Toast.LENGTH_LONG).show();
				}

			}
		});
	}
	public boolean userIsOnline() {
		Context context = this;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnectedOrConnecting()) 
		{
			return true;
		}
		return false;
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
					Toast.makeText(this, "Image also saved to your Photo Album!",
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
	
	@Override
	protected void onResume() {

		mUser = ParseUser.getCurrentUser();
		mCurrentUser =  mUser.getUsername();
		// Defining Random Class to use in Captcha logic
		Random randomGen = new Random();
		// Taking length of string array and obtaining a random number from the array then setting the textview.
		int randomCaptcha = randomGen.nextInt(captchaCodes.length);
		String captcha = captchaCodes[randomCaptcha];
		mCaptchaCode.setText(captcha);

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
		mProgressDialog.setTitle("Posting Sale");
		mProgressDialog.setMessage("One moment please while we post your sale...");
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
	public class UserLocation implements LocationListener {
		@Override
		public void onLocationChanged(Location loc) {

			if (loc != null)
			{
				mLatitude = loc.getLatitude();
				mLongitude = loc.getLongitude();
		
				// Obtaining inputed information from user, removing any whitespace in the process.
				String itemTitle = mItemTitle.getText().toString().trim();
				String itemPrice = mItemPrice.getText().toString().trim();
				String itemLocation = mItemLocation.getText().toString().trim();
				String itemDescription = mItemDescription.getText().toString().trim();
				String captchaInput = mCaptchaInput.getText().toString().trim();
				ParseGeoPoint itemGeoLocation = new ParseGeoPoint(mLatitude, mLongitude);

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
				} else if (!captchaInput.equals(mCaptchaCode.getText())) {
					progressDialogHide();

					AlertDialog.Builder b = new AlertDialog.Builder(PostSaleActivity.this);
					b.setMessage(R.string.error_message_captcha_incorrect_field);
					b.setTitle(R.string.error_title_captcha_incorrect_field);
					b.setPositiveButton(android.R.string.ok, null);
					AlertDialog d = b.create();
					d.show();
				}

				else
				{	
					progressDialogShow();
					sale.setSaleTitle(itemTitle);
					sale.setSalePrice("$"+itemPrice);
					sale.setSaleDescription(itemDescription);
					sale.setSaleLocation(itemLocation);
					sale.setSalePoster(mUser);
					sale.setSaleGeoLocation(itemGeoLocation);

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
								progressDialogHide();
								Toast.makeText(getApplicationContext(), "Your item has been posted for sale successfully!", Toast.LENGTH_LONG).show();
								finish();
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
			else
			{
				progressDialogHide();
				Log.i(mTAG, "No location found, hrmp?");
			}
			// Stopping Location Updates
			mLocationManager.removeUpdates(mLocationListener);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}
}
