package com.tablecloth.bookshelf.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by Minami on 2015/03/14.
 */
public class ListenerUtil {

    public interface LoadBitmapListener {
        public void onFinish(@NonNull Bitmap bitmap);
        public void onError();
    }
}
