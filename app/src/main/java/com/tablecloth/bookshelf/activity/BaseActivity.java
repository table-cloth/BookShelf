package com.tablecloth.bookshelf.activity;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tablecloth.bookshelf.BookShelfApplication;
import com.tablecloth.bookshelf.R;


/**
 * Created by shnomura on 2015/02/19.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GoogleAnalyticsを初期化
        getGoogleAnalyticsTracker();
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
     * @param category
     * @param action
     * @param label
     */
    protected void sendGoogleAnalyticsEvent(String category, String action, String label) {
        getGoogleAnalyticsTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

}
