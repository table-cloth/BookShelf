package com.tablecloth.bookshelf.util;

import android.graphics.Bitmap;

/**
 * Created by Minami on 2015/03/14.
 */
public class ListenerUtil {

    public interface LoadBitmapListener {
        public void onFinish(Bitmap bitmap);
        public void onError();
    }
}
