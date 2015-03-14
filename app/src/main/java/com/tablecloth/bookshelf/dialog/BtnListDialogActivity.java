package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.G;

/**
 * 縦にボタンが並んだダイアログ
 * ボタンの数は可変に変更したい（現状は内容も個数も固定＠2015/02/21）
 * Created by shnomura on 2015/02/21.
 */
public class BtnListDialogActivity extends DialogBaseActivity {

    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        // ラジオボタンの取得・初期設定
        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        radioGroup.check(R.id.radio_0);

        // テキスト設定
        ((TextView)findViewById(R.id.title)).setText(intent.getStringExtra(KEY_TITLE));
        ((TextView)findViewById(R.id.message)).setText(intent.getStringExtra(KEY_MESSAGE));
        ((TextView)findViewById(R.id.btn_positive)).setText(intent.getStringExtra(KEY_BTN_POSITIVE));
        ((TextView)findViewById(R.id.btn_negative)).setText(intent.getStringExtra(KEY_BTN_NEGATIVE));

        ((TextView)findViewById(R.id.btn_positive)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                int selectedBtn = 0;
                switch (selectedId) {
                    // ISBN検索
                    default:
                    case R.id.radio_0:
                        selectedBtn = G.RESULT_DATA_SELECTED_BTN_SEARCH;
                        break;
                    // 手動検索
                    case R.id.radio_1:
                        selectedBtn = G.RESULT_DATA_SELECTED_BTN_MANUAL;
                        break;
                }
                Intent data = new Intent();
                data.putExtra(G.RESULT_DATA_SELECTED_ID, selectedBtn);
                BtnListDialogActivity.this.setResult(G.RESULT_POSITIVE, data);
                BtnListDialogActivity.this.finish();
            }
        });
        ((TextView)findViewById(R.id.btn_negative)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnListDialogActivity.this.setResult(G.RESULT_NEGATIVE);
                BtnListDialogActivity.this.finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_btn_list_dialog;
    }

    public static Intent getIntent(Context context, String title, String message, String btnPositive, String btnNegative) {
        Intent intent = new Intent(context, BtnListDialogActivity.class);

        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_MESSAGE, message);
        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);
        intent.putExtra(KEY_BTN_NEGATIVE, btnNegative);

        return intent;
    }
}
