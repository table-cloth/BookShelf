package com.tablecloth.bookshelf.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.text.format.DateFormat;

/**
 * Created by shnomura on 2014/08/17.
 */
public class ImageUtil {

    public static Bitmap convertByte2Bitmap(byte[] byteData) {
    	if(byteData != null) {
    		return BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
    	}
    	return null;
    }
    public static byte[] convertBitmap2Byte(Bitmap bitmap) {
    	if(bitmap != null) {
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	bitmap.compress(CompressFormat.PNG, 100, baos);
	    	return baos.toByteArray();
    	}
    	return null;
    }
    
    public static String saveBitmap2LocalStorage(Activity activity, Bitmap bitmap) {
    	String ret = "";
    	if(bitmap == null) return ret;
		ret = createFileNameString();
		try {
    		//ローカルファイルへ保存  
			final FileOutputStream out = activity.openFileOutput(ret, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return ret;
    }
    
    public static Bitmap getBitmapFromPath(Activity activity, String path) {
    	 if(activity == null || Util.isEmpty(path)) return null;
    	Bitmap ret = null;
    	InputStream is;
    	try {
    		is = activity.openFileInput(path);
    		ret = BitmapFactory.decodeStream(is);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return ret;
    }

    private static String createFileNameString() {
    	return (String) DateFormat.format("yyyyMMddkkmmss", Calendar.getInstance()) + ".jpg";
    }
    
    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
          Matrix matrix = new Matrix();
          matrix.postRotate(angle);
          return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
          //bitmap.recycle();
          //bitmap = null;
          //return ret;
    }
    
}
