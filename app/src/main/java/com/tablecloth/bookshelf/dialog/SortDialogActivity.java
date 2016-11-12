package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.R;

/**
 * 設定ボタンタップ後に表示されるダイアログ
 * Created by Minami on 2014/11/24.
 */
public class SortDialogActivity extends DialogBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        // テキスト設定
        ((TextView)findViewById(R.id.title)).setText(intent.getStringExtra(KEY_TITLE_STR_ID));
        ((TextView)findViewById(R.id.message)).setText(intent.getStringExtra(KEY_MESSAGE_STR_ID));
        ((TextView)findViewById(R.id.btn_positive)).setText(intent.getStringExtra(KEY_BTN_POSITIVE_STR_ID));
        ((TextView)findViewById(R.id.btn_negative)).setText(intent.getStringExtra(KEY_BTN_NEGATIVE_STR_ID));

        ((TextView)findViewById(R.id.btn_positive)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortDialogActivity.this.setResult(G.RESULT_POSITIVE);
                SortDialogActivity.this.finish();
            }
        });
        ((TextView)findViewById(R.id.btn_negative)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortDialogActivity.this.setResult(G.RESULT_NEGATIVE);
                SortDialogActivity.this.finish();
            }
        });
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_simple_dialog;
    }

    public static Intent getIntent(Context context, String title, String message, String btnPositive, String btnNegative) {
        Intent intent = new Intent(context, SortDialogActivity.class);

        intent.putExtra(KEY_TITLE_STR_ID, title);
        intent.putExtra(KEY_MESSAGE_STR_ID, message);
        intent.putExtra(KEY_BTN_POSITIVE_STR_ID, btnPositive);
        intent.putExtra(KEY_BTN_NEGATIVE_STR_ID, btnNegative);

        return intent;
    }
}
