package com.tablecloth.bookshelf.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Minami on 2015/03/14.
 */
public class ProgressUtil {
    final private Context mContext;
    private ProgressDialog mProgressDialog = null;

    public ProgressUtil(Context context) {
        mContext = context;
    }

    public void start() {
        start(null,  null);
    }

    public void start(String message, DialogInterface.OnDismissListener listener) {
        start(message, listener, ProgressDialog.STYLE_SPINNER);
    }

    public void start(String message, DialogInterface.OnDismissListener listener, int progressStype) {
        close();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(false);
        if(message != null) mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(progressStype);
        if(listener != null) mProgressDialog.setOnDismissListener(listener);
        mProgressDialog.show();

    }

    public void close() {
        if((mProgressDialog != null ) && mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }
}
