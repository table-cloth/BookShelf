package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.R;

/**
 * タイトル・メッセージ・YES/NOボタンの要素を持ったダイアログ拡張クラス
 * Created by shnomura on 2014/08/17.
 */
public class SimpleDialogActivity extends DialogBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        // テキスト設定
        ((TextView)findViewById(R.id.title)).setText(intent.getStringExtra(KEY_TITLE));
        ((TextView)findViewById(R.id.message)).setText(intent.getStringExtra(KEY_MESSAGE));
        ((TextView)findViewById(R.id.btn_positive)).setText(intent.getStringExtra(KEY_BTN_POSITIVE));
        ((TextView)findViewById(R.id.btn_negative)).setText(intent.getStringExtra(KEY_BTN_NEGATIVE));

        ((TextView)findViewById(R.id.btn_positive)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialogActivity.this.setResult(G.RESULT_POSITIVE);
                SimpleDialogActivity.this.finish();
            }
        });
        ((TextView)findViewById(R.id.btn_negative)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialogActivity.this.setResult(G.RESULT_NEGATIVE);
                SimpleDialogActivity.this.finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_dialog;
    }

    public static Intent getIntent(Context context, String title, String message, String btnPositive, String btnNegative) {
        Intent intent = new Intent(context, SimpleDialogActivity.class);

        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_MESSAGE, message);
        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);
        intent.putExtra(KEY_BTN_NEGATIVE, btnNegative);

        return intent;
    }
}
