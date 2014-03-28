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

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class LocalSalesActivity extends ListActivity {
	ListView mLocalSales;
	static TextView mPostedSalesAmount;
	ProgressDialog mProgressDialog;
	ParseUser mCurrentUser;
	String mTAG = "LocalSalesActivity";
	static EditText mMileInput;
	Button mSearchSales;
	
	LocationManager mLocationManager;
	LocationListener mLocationListener;
	
	static double mLatitude;
	static double mLongitude;
	
	private LocalSalesAdapter localSalesAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_sales);
		// Hiding SoftKeyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		mLocalSales = (ListView) findViewById(android.R.id.list);
		mPostedSalesAmount = (TextView) findViewById(R.id.textView_localSalesAmount);
		mMileInput = (EditText) findViewById(R.id.editText_localSalesSearch_mileInput);
		mSearchSales = (Button) findViewById(R.id.button_localSalesSearch_searchLocalSales);
		localSalesAdapter = new LocalSalesAdapter(this);

		mSearchSales.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
		
		mLocalSales.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				ParseObject sale = localSalesAdapter.getItem(pos);
				String objectID = sale.getObjectId().toString();
				Log.i(mTAG, objectID);
				Intent intent = new Intent(getApplicationContext(), LocalSalesDetailActivity.class);
				intent.putExtra("title", sale.getString("title"));
				intent.putExtra("price",sale.getString("price"));
				intent.putExtra("location", sale.getString("location"));
				intent.putExtra("description", sale.getString("description"));
				intent.putExtra("oid", objectID);
				intent.putExtra("postedBy", sale.getString("postedBy"));
				startActivity(intent);
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
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
	public boolean userIsOnline() {
	    Context context = this;
	    ConnectivityManager cm = (ConnectivityManager) context
	        .getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	public static void updateUserWithSaleInfo(Context context, int amount)
	{
		String finalString = Integer.toString(amount);
		mPostedSalesAmount.setText(finalString);
		Toast.makeText(context, amount + " local sales have been found!", Toast.LENGTH_LONG).show();
	}
	public static int getMiles()
	{
		String miles = mMileInput.getText().toString();
		if (miles.matches(""))
		{
			return 30;
		}
		else
		{
			int mileCount = Integer.parseInt(miles);
			return mileCount;
		}
	}
	@SuppressLint("InlinedApi")
	public void presentUserWithLogin() {
		// Displaying the Login Activity to the user
		Intent i = new Intent(this, LoginActivity.class);
		// Logging in =  New Task, Old Task = Clear so back button cannot be used to go back into Profile Activity if logged out.
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.local_sales, menu);
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
		case R.id.action_post_sale:
			// Launching new intent to start Post Sale Activity
			Intent i = new Intent(this, PostSaleActivity.class);
			startActivity(i);
			break; 
		case R.id.action_profile_activity:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public static ParseGeoPoint getUserCoords()
	{
		ParseGeoPoint usersPoints = new ParseGeoPoint(mLatitude, mLongitude);
		return usersPoints;
	}
	public class UserLocation implements LocationListener{
	    @Override
	  public void onLocationChanged(Location loc) {
	    	
	    	
	    	if (loc != null)
	    	{
	    		mLatitude = loc.getLatitude();
		    	mLongitude = loc.getLongitude();
		    	localSalesAdapter.loadObjects();
				setListAdapter(localSalesAdapter);
	    	}
	    	else
	    	{
	    		
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
