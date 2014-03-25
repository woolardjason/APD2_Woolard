package com.jasonwoolard.geoshare;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
// ParseQueryAdapter Documentation :
// Class used for Sales Parse Object to get / set different key/value data sets on Parse backend.

// Specifying Parse Class
@ParseClassName("Sales")
public class Sale extends ParseObject 
{
	public Sale() 
	{
		// Required Constructor Method
	}
	// Obtaining Sale Title from Parse
	public String getSaleTitle() 
	{
		return getString("title");
	}
	// Setting Sale Title to Parse
	public void setSaleTitle(String title) 
	{
		put("title", title);
	}
	// Obtaining Sale Poster from Parse
	public String getSalePoster() 
	{
		return getString("postedBy");
	}
	// Setting Sale Poster to Parse
	public void setSalePoster(ParseUser user) 
	{
		String poster = user.getUsername();
		put("postedBy", poster);
	}
	// Obtaining Sale Location from Parse
	public String getSaleLocation() 
	{
		return getString("location");
	}
	// Setting Sale Location to Parse
	public void setSaleLocation(String location) 
	{
		put("location", location);
	}
	// Obtaining Sale Price from Parse
	public String getSalePrice() 
	{
		return getString("price");
	}
	// Setting Sale Price to Parse
	public void setSalePrice(String price) 
	{
		put("price", price);
	}
	// Obtaining Sale Description from Parse
	public String getSaleDescription() 
	{
		return getString("description");
	}
	// Setting Sale Description to Parse
	public void setSaleDescription(String description) 
	{
		put("description", description);
	}
	// Obtaining Sale Photo from Parse
	public ParseFile getSalePhotoFile() 
	{
		return getParseFile("photo");
	}
	// Setting Sale Photo File to Parse
	public void setSalePhotoFile(ParseFile file) 
	{
		put("photo", file);
	}
}
