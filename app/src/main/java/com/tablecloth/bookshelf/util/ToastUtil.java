package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Util class for toast
 *
 * Created by Minami on 2014/08/17.
 */
public class ToastUtil {

	private static Toast sToast;
	final public static int GRAVITY_NONE = -1;

	/**
	 * Show toast
	 *
	 * @param context context
	 * @param text toast string
	 * @param gravity gravity
     */
	public static void show(Context context, String text, int gravity) {
		show(context, text, Toast.LENGTH_SHORT, gravity);
	}

	/**
	 * Show toast
	 *
	 * @param context context
	 * @param strId toast string id
	 * @param gravity gravity
	 */
	public static void show(Context context, int strId, int gravity) {
		show(context, strId, Toast.LENGTH_SHORT, gravity);
	}

	/**
	 * Show toast
	 *
	 * @param context context
	 * @param text toast string
     */
	public static void show(Context context, String text) {
		show(context, text, Toast.LENGTH_SHORT, GRAVITY_NONE);
	}

	 /**
	 * Show toast
	 *
	 * @param context context
	 * @param strId toast string id
     */
	public static void show(Context context, int strId) {
		show(context, strId, Toast.LENGTH_SHORT, GRAVITY_NONE);
	}

	/**
	 * Show toast
	 *
	 * @param context context
	 * @param strId toast string id
	 * @param duration toast show duration
     * @param gravity gravity
     */
	public static void show(Context context, int strId, int duration, int gravity) {
		show(context, context.getString(strId), duration, gravity);
	}

	/**
	 * Show toast
	 *
	 * @param context context
	 * @param text toast string
	 * @param duration toast show duration
	 * @param gravity gravity
     */
	public static void show(Context context, String text, int duration, int gravity) {
		try {
			if (sToast == null) {
				sToast = Toast.makeText(context, text, duration);
			} else {
				sToast.setText(text);
				sToast.setDuration(duration);
			}
			if (gravity != GRAVITY_NONE) sToast.setGravity(gravity, 0, 0);
			sToast.show();
		} catch (RuntimeException e) {
			e.printStackTrace();
			Log.e("ToastUtil", "Trying to show toast using dead context, causing runtime exception with \"Can't create handler inside thread that has not called Looper.preapare()\"");
		}
	}
}
