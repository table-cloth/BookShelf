package com.tablecloth.bookshelf.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Util class for Progress Dialog
 *
 * Created by Minami on 2015/03/14.
 */
public class ProgressDialogUtil {

    final private ProgressDialog mProgressDialog;

    /**
     * Calls constructor and gets instance of ProgressDialogUtil
     *
     * @param context Context
     * @return ProgressDialogUtil instance
     */
    public static ProgressDialogUtil getInstance(@NonNull Context context) {
        return new ProgressDialogUtil(context);
    }

    /**
     * Constructor
     *
     * @param context Context
     */
    private ProgressDialogUtil(@NonNull Context context) {
        mProgressDialog = new ProgressDialog(context);
    }

    /**
     * Starts progress dialog in spinner style
     * If the progress dialog is already open, it will close what is already open
     * then will re-open new progress dialog
     *
     * @param message Message to show in progress dialog
     * @param listener OnDismissListener instance
     */
    public void show(Handler handler, @Nullable String message, @Nullable DialogInterface.OnDismissListener listener) {
        show(handler, message, listener, ProgressDialog.STYLE_SPINNER);
    }

    /**
     * Starts progress dialog in spinner style
     * If the progress dialog is already open, it will close what is already open
     * then will re-open new progress dialog
     *
     * @param message Message to show in progress dialog
     * @param listener OnDismissListener instance
     * @param progressDialogStyle Style of progress dialog. Set ProgresDialog.xxx
     */
    public void show(Handler handler, @Nullable final String message, @Nullable final DialogInterface.OnDismissListener listener, final int progressDialogStyle) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // ensure progress dialog is dismissed before showing
                dismiss();

                // init progress dialog
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgressStyle(progressDialogStyle);
                mProgressDialog.setMessage(Util.isEmpty(message)
                        ? "" // set empty text if message is null
                        : message);
                mProgressDialog.setOnDismissListener(listener); // listener may be null

                // show dialog
                mProgressDialog.show();
            }
        });
    }

    /**
     * Close prog
     */
    public void dismiss() {
        if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }
}
