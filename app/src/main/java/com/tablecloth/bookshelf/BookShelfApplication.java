package com.tablecloth.bookshelf;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.tablecloth.bookshelf.util.Util;

/**
 * Application class of BookShelf
 * Initialize tracking info here
 *
 * Created by Minami on 2015/02/19.
 */
public class BookShelfApplication extends Application {
    Tracker mGoogleAnalyticsTracker;

    public synchronized  Tracker getGoogleAnalyticsTracker() {
        if(mGoogleAnalyticsTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mGoogleAnalyticsTracker = analytics.newTracker(getString(R.string.id_google_analytics_tracking));
            mGoogleAnalyticsTracker.enableAutoActivityTracking(true);
            mGoogleAnalyticsTracker.enableAdvertisingIdCollection(true);
            mGoogleAnalyticsTracker.enableExceptionReporting(true);
        }
        return mGoogleAnalyticsTracker;
    }
}
