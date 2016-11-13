package com.tablecloth.bookshelf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tablecloth.bookshelf.BookShelfApplication;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.VersionUtil;


/**
 * Base class for ALL activity
 * Handles basic feature all activity may use
 *
 * Created by Minami on 2015/02/19.
 */
public abstract class BaseActivity extends Activity {

    // Key values for Intent extras
    final protected static String KEY_TITLE_STR_ID = "title";
    final protected static String KEY_MESSAGE_STR_ID = "message";
    final protected static String KEY_BTN_POSITIVE_STR_ID = "btn_positive";
    final protected static String KEY_BTN_NEGATIVE_STR_ID = "btn_negative";
    final protected static String KEY_DATA_TYPE_STR_ID = "data_type";
    final protected static String KEY_BOOK_SERIES_ID = "series_id";
    final protected static String KEY_RAW_TAGS = "raw_tags";

    // Default values for Intent extras
    final protected static int VALUE_DEFAULT_STR_ID = -1;

    protected Handler mHandler;
    final protected int CONTENT_VIEW_ID_NONE = -1;

    // Whether to check version updates
    // If version update check is not expected
    // set this value to true before calling onCreate of base class
    protected boolean doCheckVersionUpdates = true;

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    protected abstract int getContentViewID();

    /**
     * OnCreate
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // set layout ID if valid
        int contentViewID = getContentViewID();
        if(contentViewID != CONTENT_VIEW_ID_NONE) {
            setContentView(contentViewID);
        }

        // init handler
        Looper looper = getMainLooper();
        mHandler = new Handler(looper);

        // init Google Analytics Tracker
        // This initialize needs to be called in every Activity
        if(!Util.isDebugMode(BaseActivity.this)) {
            getGoogleAnalyticsTracker();
        }

        // Check for version updates
        // Show update dialog if needed
        if(doCheckVersionUpdates) {
            Context appContext = getApplicationContext();
            VersionUtil versionUtil =VersionUtil.getInstance(appContext);
            if(versionUtil.isVersionUpdated(appContext)) {
                Intent updateDialogIntent = versionUtil.getUpdateDialogIntent(appContext);
                if(updateDialogIntent != null) {
                    startActivityForResult(updateDialogIntent, Const.REQUEST_CODE.UPDATE_DIALOG);
                    versionUtil.updateVersionInfo(appContext);
                }
            }
        }
    }

    /**
     * Called when activity starts
     */
    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    /**
     * Called when activity stops
     */
    @Override
    public void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    /**
     * Get instance of Tracker for sending values to Google Analytics
     * Create Tracker instance if not already made
     *
     * @return Tracker instance
     */
    private Tracker getGoogleAnalyticsTracker() {
        return ((BookShelfApplication)getApplication()).getGoogleAnalyticsTracker();
    }

    /**
     * Send tracking event to GoogleAnalytics
     *
     * @param eventCategory Category of the event. This must be valid value
     * @param eventAction Action of the event. This must be valid value
     */
    protected void sendGoogleAnalyticsEvent(@NonNull String eventCategory, @NonNull String eventAction) {
        sendGoogleAnalyticsEvent(eventCategory, eventAction, null);
    }

    /**
     * Send tracking event to GoogleAnalytics
     *
     * @param eventCategory Category of the event. This must be valid value
     * @param eventAction Action of the event. This must be valid value
     * @param eventLabel Label (additional info) of the event. This may be null
     */
    protected void sendGoogleAnalyticsEvent(@NonNull String eventCategory, @NonNull String eventAction, @Nullable String eventLabel) {
        // do not track if not release mode
        if(Util.isDebugMode(this)) return;
        // return if category or action is invalid
        if(Util.isEmpty(eventCategory)
                || Util.isEmpty(eventAction)) return;

        // set data for EventBuilder
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder()
                .setCategory(eventCategory)
                .setAction(eventAction);
        // set label if given param is not empty
        if(!Util.isEmpty(eventLabel)) {
            eventBuilder.setLabel(eventLabel);
        }

        // do send data to GoogleAnalytics
        getGoogleAnalyticsTracker().send(eventBuilder.build());
    }

    /**
     * Checks whether given resultCode is positive
     *
     * @param resultCode resultCode
     * @return is resultCode positive
     */
    protected boolean isResultPositive(int resultCode) {
        return resultCode == Const.RESULT_CODE.POSITIVE
                || resultCode == RESULT_OK;
    }

    /**
     * Checks whether given resultCode is negative
     *
     * @param resultCode resultCode
     * @return is resultCode negative
     */
    protected boolean isResultNegative(int resultCode) {
        return resultCode == Const.RESULT_CODE.NEGATIVE;
    }

    /**
     * Sets result to activity
     *
     * @param resultCode Result Code
     * @param resultDataKey Result Data Key, put in Intent.purExtra(...)
     * @param resultDataValue Result Data Value, put in Intent.putExtra(...)
     */
    protected void setResult(int resultCode, String resultDataKey, String resultDataValue) {
        Intent data = new Intent();
        data.putExtra(resultDataKey, resultDataValue);
        setResult(resultCode, data);
    }

    /**
     * Sets result to activity
     *
     * @param resultCode Result Code
     * @param resultDataKey Result Data Key, put in Intent.purExtra(...)
     * @param resultDataValue Result Data Value, put in Intent.putExtra(...)
     */
    protected void setResult(int resultCode, String resultDataKey, int resultDataValue) {
        Intent data = new Intent();
        data.putExtra(resultDataKey, resultDataValue);
        setResult(resultCode, data);
    }

    /**
     * Gets result data set in given Intent
     *
     * @param data Intent with data
     * @param resultDataKey Result Data Key, put in Intent.purExtra(...)
     * @return resultDataValue
     */
    @Nullable
    protected String getIntentExtraStr(Intent data, String resultDataKey) {
        return data.getStringExtra(resultDataKey);
    }

    /**
     * Gets result data set in given Intent
     *
     * @param data Intent with data
     * @param resultDataKey Result Data Key, put in Intent.purExtra(...)
     * @return resultDataValue
     */
    @Nullable
    protected int getIntentExtraInt(Intent data, String resultDataKey) {
        return data.getIntExtra(resultDataKey, Const.INTENT_EXTRA.VALUE_DEFAULT_ERROR);
    }

    /**
     * Finish activity with result
     *
     * @param result Result to set
     */
    protected void finishWithResult(int result) {
        setResult(result);
        finish();
    }

    /**
     * Finish activity with result
     *
     * @param result Result to set
     * @param data Intent data to set
     */
    protected void finishWithResult(int result, Intent data) {
        setResult(result, data);
        finish();
    }
}
