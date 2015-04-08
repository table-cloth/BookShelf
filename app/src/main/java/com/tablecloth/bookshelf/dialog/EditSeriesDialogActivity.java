package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.FilterDao;
import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;

/**
 * タイトル・メッセージ・YES/NOボタンの要素を持ったダイアログ拡張クラス
 * Created by shnomura on 2014/08/17.
 */
public class EditSeriesDialogActivity extends DialogBaseActivity {

    private SeriesData sSeriesData = null;
    private int mSeriesId = -1;
    private static SeriesData sTmpSeriesData = null;
    ViewGroup tagContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        mSeriesId = intent.getIntExtra(KEY_ID, -1);

        // SeriesIDが登録されていない場合、tmpSeriesDataに一部情報が存在するかを確認する
        if(mSeriesId <= -1 && sTmpSeriesData != null) {
            mSeriesId = sTmpSeriesData.mSeriesId;
            sSeriesData = sTmpSeriesData;
        } else {
            // 作品IDが取得できれば、情報をDBから読み取る
            if(mSeriesId >= 0) {
                sSeriesData = FilterDao.loadSeries(EditSeriesDialogActivity.this, mSeriesId);
                if(sSeriesData != null) {
                    // 削除ボタンを表示する
                    findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
                }
            }
        }
        // 取得に失敗した場合、新規登録の場合は空のデータを作成
        if(sSeriesData == null) sSeriesData = new SeriesData();
        sTmpSeriesData = null;

        tagContainer = (ViewGroup)findViewById(R.id.tag_container);


        // テキスト設定
        ((TextView)findViewById(R.id.title)).setText(intent.getStringExtra(KEY_TITLE));
        ((TextView)findViewById(R.id.btn_positive)).setText(intent.getStringExtra(KEY_BTN_POSITIVE));
//        ((TextView)findViewById(R.id.btn_negative)).setText(intent.getStringExtra(KEY_BTN_NEGATIVE));

        // ヒント設定
        View view = findViewById(R.id.data_detail_row_title);
        ((EditText)view.findViewById(R.id.data_content)).setHint("※必須");
        
        // 内容設定
        // タイトル
        setRowContents(findViewById(R.id.data_detail_row_title), "タイトル", sSeriesData.mTitle);
        setRowContents(findViewById(R.id.data_detail_row_title_pronunciation), "タイトル（カナ）", sSeriesData.mTitlePronunciation);
        // 作者
        setRowContents(findViewById(R.id.data_detail_row_author), "作者", sSeriesData.mAuthor);
        setRowContents(findViewById(R.id.data_detail_row_author_pronunciation), "作者（カナ）", sSeriesData.mAuthorPronunciation);
        // 掲載誌
        setRowContents(findViewById(R.id.data_detail_row_magazine), "掲載誌", sSeriesData.mMagazine);
        setRowContents(findViewById(R.id.data_detail_row_magazine_pronunctaion), "掲載誌（カナ）", sSeriesData.mMagazinePronunciation);
        // 出版社
        setRowContents(findViewById(R.id.data_detail_row_company), "出版社", sSeriesData.mCompany);
        // メモ
        setRowContents(findViewById(R.id.data_detail_row_memo), "メモ", sSeriesData.mMemo);
        // タグ
        updateTags();

        findViewById(R.id.btn_tag_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = TagsEditDialogActivity.getIntent(EditSeriesDialogActivity.this, "タグを編集", sSeriesData.mTagsList, "完了");
                if(intent != null) startActivityForResult(intent, G.REQUEST_CODE_TAGS_EDIT);
                ToastUtil.show(EditSeriesDialogActivity.this, "タグ編集画面を起動");
            }
        });
        

        // 保存ボタン
                ((TextView) findViewById(R.id.btn_positive)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = findViewById(R.id.data_detail_row_title);
                        // タイトル
                        String title = getRowContents(findViewById(R.id.data_detail_row_title));
                        // タイトルは必須情報なので、無い場合は登録させない
                        if (title == null || title.length() <= 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(EditSeriesDialogActivity.this, "タイトルが未入力です");
                                }
                            });
                            return;
                        }
                        sSeriesData.mTitle = title;
                        sSeriesData.mTitlePronunciation = getRowContents(findViewById(R.id.data_detail_row_title_pronunciation));
                        // 作者
                        sSeriesData.mAuthor = getRowContents(findViewById(R.id.data_detail_row_author));
                        sSeriesData.mAuthorPronunciation = getRowContents(findViewById(R.id.data_detail_row_author_pronunciation));
                        // 掲載誌
                        sSeriesData.mMagazine = getRowContents(findViewById(R.id.data_detail_row_magazine));
                        sSeriesData.mMagazinePronunciation = getRowContents(findViewById(R.id.data_detail_row_magazine_pronunctaion));
                        // 出版社
                        sSeriesData.mCompany = getRowContents(findViewById(R.id.data_detail_row_company));
                        // メモ
                        sSeriesData.mMemo = getRowContents(findViewById(R.id.data_detail_row_memo));
                        // タグ（初期は実装しない）
//                setRowContents(findViewById(R.id.data_detail_row_title), "タイトル", sSeriesData.);

                        FilterDao.saveSeries(sSeriesData);
                        setResult(G.RESULT_POSITIVE);
                        EditSeriesDialogActivity.this.finish();
                    }
                });
        ((TextView)findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(G.RESULT_NONE);
                EditSeriesDialogActivity.this.finish();
            }
        });

        ((TextView)findViewById(R.id.btn_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 削除確認ダイアログ
                Intent intent = SimpleDialogActivity.getIntent(EditSeriesDialogActivity.this, "作品を削除します", "削除した作品は復元することが出来ません。\n本当に削除してもよろしいでしょうか？", "削除", "キャンセル");
                startActivityForResult(intent, G.REQUEST_CODE_SIMPLE_CHECK);
            }
        });
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_edit_series_dialog;
    }

    public static Intent getIntent(Context context, String title, String btnPositive, int seriesId) {
        Intent intent = new Intent(context, EditSeriesDialogActivity.class);

        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);
        intent.putExtra(KEY_ID, seriesId);

        return intent;
    }

    public static Intent getIntent(Context context, String title, String btnPositive, SeriesData seriesData) {
        Intent intent = new Intent(context, EditSeriesDialogActivity.class);

        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);
        sTmpSeriesData = seriesData;

        return intent;

    }

    private void setRowContents(View rowView, String dataName, String dataContent) {
    	// 内容設定
        ((TextView)rowView.findViewById(R.id.data_name)).setText(dataName);
        if(!Util.isEmpty(dataContent)) ((EditText)rowView.findViewById(R.id.data_content)).setText(dataContent);
    }
    
    private String getRowContents(View rowView) {
    	return ((TextView)rowView.findViewById(R.id.data_content)).getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case G.REQUEST_CODE_SIMPLE_CHECK:
                if(resultCode == G.RESULT_POSITIVE) {
                    if(FilterDao.deleteSeries(mSeriesId)) {
                        ToastUtil.show(EditSeriesDialogActivity.this, "作品の情報を削除しました");
                        EditSeriesDialogActivity.this.setResult(G.RESULT_SPECIAL);
                        EditSeriesDialogActivity.this.finish();
                    } else {
                        ToastUtil.show(EditSeriesDialogActivity.this, "作品の情報の削除に失敗しました");
                    }
                }
                break;
            case G.REQUEST_CODE_TAGS_EDIT:
                if(data != null) {
                    String tagsStr = data.getStringExtra(TagsEditDialogActivity.KEY_TAGS);
                    sSeriesData.mTagsList = FilterDao.getTagsData(tagsStr);
                    updateTags();
                }
                break;
        }
    }

    private void updateTags() {
        // タグ
        tagContainer.removeAllViews();
        tagContainer = ViewUtil.setTagInfoNormal(EditSeriesDialogActivity.this, sSeriesData.mTagsList, tagContainer);
        tagContainer.invalidate();
    }
}
