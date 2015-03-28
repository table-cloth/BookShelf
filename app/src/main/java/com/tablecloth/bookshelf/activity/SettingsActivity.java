package com.tablecloth.bookshelf.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.Util;

/**
 * Created by shnomura on 2015/03/15.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 広告の初期化処理
        Util.initAdview(this, (ViewGroup) findViewById(R.id.banner));

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });

    }
    @Override
    protected int getContentViewID() {
        return R.layout.activity_settings;
    }
}
