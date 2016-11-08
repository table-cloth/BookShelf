package com.tablecloth.bookshelf.activity;

import android.app.Activity;
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
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.VersionUtil;


/**
 * Base class for ALL activity
 * Handles basic feature all activity may use
 *
 * Created by Minami on 2015/02/19.
 */
public abstract class BaseActivity extends Activity {

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
            VersionUtil versionUtil = new VersionUtil(this);
            versionUtil.showUpdateDialog();
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
}
