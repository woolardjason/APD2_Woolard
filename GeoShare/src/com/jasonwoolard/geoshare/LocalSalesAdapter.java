package com.jasonwoolard.geoshare;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;


public class LocalSalesAdapter extends ParseQueryAdapter<Sale> {

	static String mTAG = "LocalSalesAdapter";
	
	public LocalSalesAdapter(final Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Sale>() {
			@SuppressWarnings("unchecked")
			public ParseQuery<Sale> create() {
				@SuppressWarnings("rawtypes")
				ParseQuery query = new ParseQuery("Sales"); 
				query.orderByDescending("createdAt");
				int userInputedMiles = LocalSalesActivity.getMiles();
				Log.i(mTAG, Integer.toString(userInputedMiles));
				ParseGeoPoint userLoc = LocalSalesActivity.getUserCoords();
				query.whereWithinMiles("saleLocation", userLoc, userInputedMiles);
				try {
					int salesAmount = query.count();
					LocalSalesActivity.updateUserWithSaleInfo(context, salesAmount);
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
				return query;
			}
		});
	}

	@Override
	public View getItemView(Sale sale, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.listview_local_sales_cell, null);
		}

		super.getItemView(sale, v, parent);

		ParseImageView saleImage = (ParseImageView) v.findViewById(R.id.icon);
		ParseFile photoFile = sale.getParseFile("photo");
		if (photoFile != null) {
			saleImage.setParseFile(photoFile);
			saleImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {

				}
			});
		} else { 
			// Clear ParseImageView if the object has no photo, set placeholder.
	        saleImage.setImageResource(R.drawable.placeholder); 
	    }
		TextView titleTextView = (TextView) v.findViewById(R.id.textView_listView_localSalesSubject);
		titleTextView.setText(sale.getSaleTitle());
		TextView descriptionTextView = (TextView) v.findViewById(R.id.textView_listView_localSalesDescription);
		descriptionTextView.setText(sale.getSaleDescription());
		TextView priceTextView = (TextView) v.findViewById(R.id.textView_listView_localSalesPrice);
		priceTextView.setText(sale.getSalePrice());
		return v;
	}

}