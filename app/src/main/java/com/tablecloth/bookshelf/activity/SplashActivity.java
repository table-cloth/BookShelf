package com.tablecloth.bookshelf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.SettingsDao;


public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // バージョン情報関連


        // 次のActivityを開始
        // 設定に合っている画面を開き、この画面を閉じる
        String value = mSettings.load(SettingsDao.KEY.SERIES_SHOW_TYPE, SettingsDao.VALUE.SERIES_SHOW_TYPE_GRID);
        if(SettingsDao.VALUE.SERIES_SHOW_TYPE_LIST.equals(value)) {
            startActivity(new Intent(this, ListActivity.class));
        } else {
            startActivity(new Intent(this, GridActivity.class));
        }
        SplashActivity.this.finish();
    }

    @Override
    protected int getContentViewID() {
        return CONTENT_VIEW_ID_NONE;
    }

}
