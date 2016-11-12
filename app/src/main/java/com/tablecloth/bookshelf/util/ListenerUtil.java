package com.tablecloth.bookshelf.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Util class for custom listner
 *
 * Created by Minami on 2015/03/14.
 */
public class ListenerUtil {

    /**
     * Listener for loading bitmap
     */
    public interface LoadBitmapListener {

        /**
         * Called on loading bitmap finished successfully
         *
         * @param bitmap bitmap
         */
        public void onFinish(@NonNull Bitmap bitmap);

        /**
         * Called when loading finished with error
         */
        public void onError();
    }
}
