package com.tablecloth.bookshelf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.tablecloth.bookshelf.R;


public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my);


        // バージョン情報関連




        // 次のActivityを開始
        startActivity(new Intent(this, ListActivity.class));
        SplashActivity.this.finish();
    }

}
