package com.tablecloth.bookshelf.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.view.Window;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tablecloth.bookshelf.BookShelfApplication;
import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.DB;
import com.tablecloth.bookshelf.db.SettingsDao;
import com.tablecloth.bookshelf.util.Util;


/**
 * Created by shnomura on 2015/02/19.
 */
public abstract class BaseActivity extends Activity {

    protected Handler mHandler;
    final protected int CONTENT_VIEW_ID_NONE = -1;
    protected SettingsDao mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int contentViewID = getContentViewID();
        if(contentViewID != CONTENT_VIEW_ID_NONE) setContentView(contentViewID);

        Looper looper = getMainLooper();
        mHandler = new Handler(looper);

        // GoogleAnalyticsを初期化
        if(!Util.isDebuggable(BaseActivity.this)) {
            getGoogleAnalyticsTracker();
        }

        mSettings = new SettingsDao(BaseActivity.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    /**
     * GoogleAnalytics集計用インスタンスを取得・存在しなければ作成する
     * @return
     */
    private Tracker getGoogleAnalyticsTracker() {
        return ((BookShelfApplication)getApplication()).getGoogleAnalyticsTracker();
    }

    /**
     * GoogleAnalyticsに個別のイベントを送信する関数
     * @param type
     * @param event
     * @param param
     */
    protected void sendGoogleAnalyticsEvent(String type, String event, String param) {
        if(Util.isDebuggable(BaseActivity.this)) return;
        if(Util.isEmpty(type)) return;
        if(Util.isEmpty(param)) {
            getGoogleAnalyticsTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(type)
                    .setAction(event)
                    .build());
        } else {
            getGoogleAnalyticsTracker().send(new HitBuilders.EventBuilder()
                    .setCategory(type)
                    .setAction(event)
                    .setLabel(param)
                    .build());

        }
    }
    /**
     * GoogleAnalyticsに個別のイベントを送信する関数
     * @param type
     * @param event
     */
    protected void sendGoogleAnalyticsEvent(String type, String event) {
        if(Util.isDebuggable(BaseActivity.this)) return;
        if(Util.isEmpty(type)) return;
        getGoogleAnalyticsTracker().send(new HitBuilders.EventBuilder()
                .setCategory(type)
                .setAction(event)
                .build());
    }

    protected abstract int getContentViewID();

}
