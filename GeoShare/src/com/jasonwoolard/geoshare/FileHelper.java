package com.jasonwoolard.geoshare;

// CLASS IS USED AS A LIBRARY OBTAINED FROM AUTHOR https://github.com/treehouse/treehouse_android_utilities
// SOLELY USED FOR CONVERTING IMAGE TO BYTE ARRAY TO BE USED WITH PARSE, MODIFIED ONLY TO USE NEEDED ITEMS FOR BETTER APP FUNCTIONALITY (IE. IMAGE LOAD TIMES)

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class FileHelper {
	
	public static final String TAG = FileHelper.class.getSimpleName();
	public static final int SHORT_SIDE_TARGET = 1280;
	public static byte[] getByteArrayFromFile(Context context, Uri uri) {
		byte[] fileBytes = null;
        	try {
	        	File file = new File(uri.getPath());
	        	FileInputStream fileInput = new FileInputStream(file);
	        	fileBytes = IOUtils.toByteArray(fileInput);
        	}
        	catch (IOException e) {
        		Log.e(TAG, e.getMessage());
        	}
    
        return fileBytes;
	}
	public static byte[] reduceImageForUpload(byte[] imageData) {
		Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(imageData, SHORT_SIDE_TARGET);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		byte[] reducedData = outputStream.toByteArray();
		try {
			outputStream.close();
		}
		catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		return reducedData;
	}
	public static String getFileName(Context context, Uri uri) {
		String fileName = "sale_image.";
		fileName += "png";
		return fileName;
	}
}