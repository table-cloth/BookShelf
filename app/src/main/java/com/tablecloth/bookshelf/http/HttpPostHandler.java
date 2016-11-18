package com.tablecloth.bookshelf.http;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Handler for Http Post Request
 *
 * Created on 2016/11/18.
 */
public abstract class HttpPostHandler extends Handler {

    /**
     * Handles post message result
     *
     * @param msg Result message
     */
    @Override
    public void handleMessage(Message msg) {
        Bundle bundleData = msg.getData();
        boolean isSuccess = bundleData != null
                &&  bundleData.getBoolean(HttpPostTask.HTTP_POST_IS_SUCCESS);
        Object responseObj = msg.getData().get(HttpPostTask.HTTP_RESPONSE_TEXT);
        String httpResponse = responseObj == null
                ? ""
                : responseObj.toString();

        if(isSuccess) {
            onPostSuccess(httpResponse);
        } else {
            onPostFail(httpResponse);
        }
    }

    /**
     * Called when post succeeded
     *
     * @param response Response text
     */
    public abstract void onPostSuccess(String response);

    /**
     * Called when post failed
     *
     * @param response Response text
     */
    public abstract void onPostFail(String response);
}
