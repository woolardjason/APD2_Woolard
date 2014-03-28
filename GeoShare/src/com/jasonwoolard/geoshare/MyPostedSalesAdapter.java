package com.jasonwoolard.geoshare;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class MyPostedSalesAdapter extends ParseQueryAdapter<Sale> {

	static String mTAG = "LocalSalesAdapter";
	static ParseUser mCurrentUser = ParseUser.getCurrentUser();
	
	public MyPostedSalesAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Sale>() {
			@SuppressWarnings("unchecked")
			public ParseQuery<Sale> create() {
				@SuppressWarnings("rawtypes")
				ParseQuery query = new ParseQuery("Sales"); 
				query.orderByDescending("createdAt");
				query.whereEqualTo("postedBy", mCurrentUser.getUsername());
				try {
					int salesAmount = query.count();
					 ProfileActivity.setTextView(salesAmount);
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
			v = View.inflate(getContext(), R.layout.listview_cell, null);
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
		TextView titleTextView = (TextView) v.findViewById(R.id.textView_listView_saleTitle);
		titleTextView.setText(sale.getSaleTitle());
		
		TextView priceTextView = (TextView) v.findViewById(R.id.textView_listView_salePrice);
		priceTextView.setText(sale.getSalePrice());
		return v;
	}

}