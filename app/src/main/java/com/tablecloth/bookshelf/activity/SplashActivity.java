package com.tablecloth.bookshelf.activity;

import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.tablecloth.bookshelf.db.SettingsDao;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.Util;

import io.fabric.sdk.android.Fabric;

/**
 * Splash screen shown right after boot
 * Will switch to next activity immediately after activate
 *
 * Created by Minami on 2014/08/16.
 */
public class SplashActivity extends BaseActivity {

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return CONTENT_VIEW_ID_NONE;
    }

    /**
     * Constructor
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(!Util.isDebugMode(this)) {
            Fabric.with(this, new Crashlytics());
        }

        // Do NOT check version updates in splash screen
        doCheckVersionUpdates =false;

        super.onCreate(savedInstanceState);

        // Start next Activity according to the setting
        String nextActivity = new SettingsDao(this).load(
                Const.DB.Settings.KEY.SERIES_SHOW_TYPE,
                Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID);
        startActivity(new Intent(this,
                Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID.equals(nextActivity)
                        ? BookSeriesGridCatalogActivity.class
                        : BookSeriesListCatalogActivity.class));

        // Finish this activity after starting next activity
        SplashActivity.this.finish();
    }
}
