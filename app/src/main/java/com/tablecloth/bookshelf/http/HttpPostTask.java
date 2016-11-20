package com.tablecloth.bookshelf.http;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Async Task for Http Post Request
 *
 * Created on 2016/11/18.
 */
public class HttpPostTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "HttpPostTask";
    private static final String ENCODING = "UTF-8";

    public static final String HTTP_POST_IS_SUCCESS = "http_post_is_success";
    public static final String HTTP_RESPONSE_TEXT = "http_response";

    private final String mPostUrl;
    private final List<NameValuePair> mPostParams = new ArrayList<>();
    private final HashMap<String, String> mPostHeader = new HashMap<>();

    private final Context mContext;
    private final Handler mUIHandler;
    private ResponseHandler<Void> mResponseHandler;

    // for http request result
    private String mHttpResponseMessage = null;
    private boolean mHttpResponseIsSuccess = false;

    /**
     * Constructor
     *
     * @param context Context
     * @param postUrl Post request URL
     * @param handler Handler for Activity
     */
    public HttpPostTask(@NonNull Context context, @NonNull String postUrl, @NonNull HttpPostHandler handler) {
        mContext = context;
        mPostUrl = postUrl;
        mUIHandler = handler;
    }

    /**
     * Add parameter for post request
     *
     * @param key Key of parameter
     * @param value Value of parameter
     */
    public void addPostParam(@NonNull String key, @NonNull String value) {
        mPostParams.add(new BasicNameValuePair(key, value));
    }

    /**
     * Add Header for post request
     *
     * @param key Key of parameter
     * @param value Value of parameter
     */
    public void addPostHeader(@NonNull String key, @NonNull String value) {
        mPostHeader.put(key, value);
    }

    /**
     * Called before execution
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mResponseHandler = new ResponseHandler<Void>() {
            @Override
            public Void handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

                int responseCode =
                        httpResponse == null || httpResponse.getStatusLine() == null
                                ? HttpStatus.SC_NOT_FOUND
                                : httpResponse.getStatusLine().getStatusCode();

                // Set response message according to response code
                switch (responseCode) {
                    case HttpStatus.SC_OK: // success
                        // get response data
                        setResponseMessage(
                                EntityUtils.toString(
                                        httpResponse.getEntity(),
                                        HttpPostTask.ENCODING),
                                true);
                        break;

                    case HttpStatus.SC_NOT_FOUND: // 404 not found
                        setResponseMessage(
                                mContext.getString(R.string.http_result_sc_not_found),
                                false);
                        break;

                    default: // other errors
                        setResponseMessage(
                                mContext.getString(R.string.http_result_sc_error_default),
                                false);
                        break;
                }
                return null;
            }
        };
    }

    /**
     * Main execution
     *
     * @param unused Unused void params
     * @return null
     */
    @Override
    protected Void doInBackground(Void... unused) {
        URI url = getUri(mPostUrl);
        if(url == null) {
            setResponseMessage(
                    mContext.getString(R.string.http_result_sc_error_invalid_url),
                    false);
            return null;
        }

        HttpPost httpPostRequest = new HttpPost(url);

        // Set parameters with encoding
        try {
            httpPostRequest.setEntity(
                    new UrlEncodedFormEntity(mPostParams, ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            setResponseMessage(
                    mContext.getString(R.string.http_result_sc_error_invalid_encode),
                    false);
            return null;
        }

        // Set header
        if(Util.isEmpty(mPostHeader)) {
            for(String key : mPostHeader.keySet()) {
                if(Util.isEmpty(key)) {
                    continue;
                }
                String value = mPostHeader.get(key);
                if(Util.isEmpty(value)) {
                    continue;
                }
                httpPostRequest.setHeader(key, value);
            }
        }

        // Post request
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.execute(httpPostRequest, mResponseHandler);
        } catch(ClientProtocolException e) {
            e.printStackTrace();
            setResponseMessage(
                    mContext.getString(R.string.http_result_sc_error_invalid_protocol),
                    false);
        } catch(IOException e) {
            e.printStackTrace();
            setResponseMessage(
                    mContext.getString(R.string.http_result_sc_error_invalid_io),
                    false);
        } finally {
            if(httpClient.getConnectionManager() != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }

        return null;
    }

    /**
     * Called after execution
     *
     * @param unused Unused void
     */
    @Override
    protected void onPostExecute(Void unused) {
        Message message = getResponseMessage();
        mUIHandler.sendMessage(message);
    }


    /**
     * Set response message and whether is success
     *
     * @param message message to set
     * @param isSuccess whether response is success
     */
    private void setResponseMessage(String message, boolean isSuccess) {
        mHttpResponseMessage = message;
        mHttpResponseIsSuccess = isSuccess;
    }

    /**
     * Get parsed URI or null
     *
     * @param uriText Uri
     * @return URI instance or null
     */
    private URI getUri(String uriText) {
        if(Util.isEmpty(uriText)) {
            return null;
        }
        try {
            return new URI(uriText);
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Message getResponseMessage() {
        Message message = new Message();
        Bundle bundle = new Bundle();

        bundle.putBoolean(HTTP_POST_IS_SUCCESS, mHttpResponseIsSuccess);
        bundle.putString(HTTP_RESPONSE_TEXT, mHttpResponseMessage);

        message.setData(bundle);
        return message;
    }
}
