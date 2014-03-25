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

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
/* This class extends Application class which is the first class loaded when the app is launched. Perfect for initial setup. */

public class GeoShareApplication extends Application {

	@Override
	public void onCreate()
	{
		super.onCreate();
		ParseObject.registerSubclass(Sale.class);
		// Setting up and Initializing the Parse Backend Database to be used throughout the application
		Parse.initialize(this, "wvDUFjibQFXplnigPzaT46QtKxLqOiBqIg45AzGB", "AnY3ZX8m3xJpLG1NmfA3Jk9iNsb76RTjrnjBkB25");
	}
}
