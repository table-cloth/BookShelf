package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	private static Toast sToast;
	final public static int GRAVITY_NONE = -1;
	
	public static void show(Context context, String text, int gravity) {
		show(context, text, Toast.LENGTH_SHORT, gravity);
	}
	public static void show(Context context, int strId, int gravity) {
		show(context, strId, Toast.LENGTH_SHORT, gravity);
	}
	
	public static void show(Context context, String text) {
		show(context, text, Toast.LENGTH_SHORT, GRAVITY_NONE);
	}
	public static void show(Context context, int strId) {
		show(context, strId, Toast.LENGTH_SHORT, GRAVITY_NONE);
	}
	
	public static void show(Context context, int strId, int duration, int gravity) {
		show(context, context.getString(strId), duration);
	}
	public static void show(Context context, String text, int duration, int gravity) {
		if(sToast == null) {
			sToast = Toast.makeText(context, text, duration);
		} else {
			sToast.setText(text);
			sToast.setDuration(duration);
		}
		if(gravity != GRAVITY_NONE) sToast.setGravity(gravity, 0, 0);
		sToast.show();
	}
}
