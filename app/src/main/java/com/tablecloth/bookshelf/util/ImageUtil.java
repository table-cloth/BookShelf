package com.tablecloth.bookshelf.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;

/**
 * Image related util
 *
 * Created by Minami on 2014/08/17.
 */
public class ImageUtil {

    private static HashMap<Integer, Bitmap> mImageCache;

    /**
     * Convert byte array to Bitmap instance
     * @param byteData byte data
     * @return Bitmap instance
     */
    @NonNull
    public static Bitmap convertByte2Bitmap(@NonNull byte[] byteData) {
        return BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
    }

    /**
     * Convert Bitmap to byte array
     *
     * @param bitmap bitmap
     * @return bitmap data in byte array
     */
    @NonNull
    public static byte[] convertBitmap2Byte(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Save Bitmap to local storage
     * Returns saved file path
     *
     * @param activity activity
     * @param bitmap bitmap
     * @return saved file path
     */
    @Nullable
    public static String saveBitmap2LocalStorage(@NonNull Activity activity, @NonNull Bitmap bitmap) {
    	String ret = createFileNameString();;
		try {
			final FileOutputStream out = activity.openFileOutput(ret, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		}catch(IOException e) {
			e.printStackTrace();
            return null;
		}
		return ret;
    }

    /**
     * Get image bitmap from file path
     *
     * @param activity activity
     * @param path file path
     * @return bitmap
     */
    @Nullable
    public static Bitmap getBitmapFromPath(@NonNull Activity activity, @NonNull String path) {
        if(Util.isEmpty(path)) {
            return null;
        }
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

    /**
     * Generate file name with current date
     *
     * @return file name in "yyyyMMddkkmmss.jpg" format
     */
    @NonNull
    private static String createFileNameString() {
    	return (String) DateFormat.format("yyyyMMddkkmmss", Calendar.getInstance()) + ".jpg";
    }

    /**
     * Rotates bitmap
     *
     * @param bitmap bitmao to rotate
     * @param angle rotate angle
     * @return rotated bitmap
     */
    @NonNull
    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
          Matrix matrix = new Matrix();
          matrix.postRotate(angle);
          return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Checks whether image cache for given seriesId exists
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return true if cache exists
     */
    public static boolean hasImageCache(int seriesId) {
        if(mImageCache == null) {
            mImageCache = new HashMap<>();
        }
        return mImageCache.containsKey(seriesId);
    }

    /**
     * Get image cache if exist
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return bitmap if cache exists
     */
    @Nullable
    public static Bitmap getImageCache(int seriesId) {
        if(mImageCache == null) {
            mImageCache = new HashMap<>();
        }
        if(mImageCache.containsKey(seriesId)) {
            return mImageCache.get(seriesId);
        }
        return null;
    }

    /**
     * Set image cache with given seriesId
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @param image bitmap image to saveInt
     */
    public static void setImageCache(int seriesId, @NonNull Bitmap image) {
        if(mImageCache == null) {
            mImageCache = new HashMap<>();
        }
        if(mImageCache.containsKey(seriesId)) {
            mImageCache.remove(seriesId);
        }
        mImageCache.put(seriesId, image);
    }

    /**
     * Clear all cache
     */
    public static void clearCache() {
        if(mImageCache != null) {
            mImageCache.clear();
        }
    }
    
}
