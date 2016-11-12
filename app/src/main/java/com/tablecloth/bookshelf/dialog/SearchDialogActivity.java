package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.Rakuten;

/**
 * タイトル・メッセージ・YES/NOボタンの要素を持ったダイアログ拡張クラス
 * Created by Minami on 2014/08/17.
 */
public class SearchDialogActivity extends DialogBaseActivity {

    Spinner mSpinnerView;
    int mSpinnerSelectedItemId = 0;

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
                Intent data = new Intent();
                String key = "";
                switch (mSpinnerSelectedItemId) {
                    case 0: // タイトル名
                    default:
                        key = Rakuten.Key.TITLE_NAME;
                        break;
                    case 1: // タイトル名読み
                        key = Rakuten.Key.TITLE_NAME_KANA;
                        break;
                    case 2: // 作者名
                        key = Rakuten.Key.AUTHOR_NAME;
                        break;
                    case 3: // 作者名読み
                        key = Rakuten.Key.AUTHOR_NAME_KANA;
                        break;
                    case 4: // 掲載誌名
                        key = Rakuten.Key.MAGAZINE_NAME;
                        break;
                    case 5: // 掲載誌名読み
                        key = Rakuten.Key.MAGAZINE_NAME_KANA;
                        break;
                    case 6: // 出版社名
                        key = Rakuten.Key.COMPANY_NAME;
                        break;
                    case 7: // ISBN
                        key = Rakuten.Key.ISBN;
                        break;
                }
                EditText editText = (EditText)findViewById(R.id.data_content);
                String value = editText.getText().toString();
                data.putExtra(G.RESULT_DATA_SELECTED_KEY, key);
                data.putExtra(G.RESULT_DATA_SELECTED_VALUE, value);
                SearchDialogActivity.this.setResult(G.RESULT_POSITIVE, data);
                SearchDialogActivity.this.finish();
            }
        });
        ((TextView)findViewById(R.id.btn_negative)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialogActivity.this.setResult(G.RESULT_NEGATIVE);
                SearchDialogActivity.this.finish();
            }
        });

        // 検索対象選択スピナーボタン
        mSpinnerView = ((Spinner)findViewById(R.id.spinner_search_content));
        mSpinnerView.setAdapter(getSpinnerAdapter());
        mSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerSelectedItemId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ArrayAdapter<String> getSpinnerAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchDialogActivity.this, android.R.layout.simple_spinner_item);

        // 検索用Spinner選択肢
        for(int i = 0 ; i < Rakuten.SEARCH_CONTENT_LIST.length ; i ++) {
            adapter.add(Rakuten.SEARCH_CONTENT_LIST[i]);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_search_dialog;
    }

    public static Intent getIntent(Context context, String title, String message, String btnPositive, String btnNegative) {
        Intent intent = new Intent(context, SearchDialogActivity.class);

        intent.putExtra(KEY_TITLE_STR_ID, title);
        intent.putExtra(KEY_MESSAGE_STR_ID, message);
        intent.putExtra(KEY_BTN_POSITIVE_STR_ID, btnPositive);
        intent.putExtra(KEY_BTN_NEGATIVE_STR_ID, btnNegative);

        return intent;
    }
}
