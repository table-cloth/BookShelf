package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.BookSeriesDao;
import com.tablecloth.bookshelf.data.BookSeriesData;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;

import java.util.ArrayList;

/**
 * タイトル・メッセージ・YES/NOボタンの要素を持ったダイアログ拡張クラス
 * Created by Minami on 2014/08/17.
 */
public class EditSeriesDialogActivity extends DialogBaseActivity {

    private BookSeriesData mBookSeriesData = null;
    private int mSeriesId = -1;
    private static BookSeriesData sTmpBookSeriesData = null;
    ViewGroup tagContainer;

    BookSeriesDao mBookSeriesDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        mSeriesId = intent.getIntExtra(KEY_ID, -1);
        mBookSeriesDao = new BookSeriesDao(this);

        // SeriesIDが登録されていない場合、tmpSeriesDataに一部情報が存在するかを確認する
        if(mSeriesId <= -1 && sTmpBookSeriesData != null) {
            mSeriesId = sTmpBookSeriesData.getSeriesId();
            mBookSeriesData = sTmpBookSeriesData;
        } else {
            // 作品IDが取得できれば、情報をDBから読み取る
            if(mSeriesId >= 0) {
                mBookSeriesData = mBookSeriesDao.loadBookSeriesData(mSeriesId);
                if(mBookSeriesData != null) {
                    // 削除ボタンを表示する
                    findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
                }
            }
        }
        // 取得に失敗した場合、新規登録の場合は空のデータを作成
        if(mBookSeriesData == null) mBookSeriesData = new BookSeriesData(this);
        sTmpBookSeriesData = null;

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
        setRowContents(findViewById(R.id.data_detail_row_title), "タイトル", mBookSeriesData.getTitle());
        setRowContents(findViewById(R.id.data_detail_row_title_pronunciation), "タイトル（カナ）", mBookSeriesData.getTitlePronunciation());
        // 作者
        setRowContents(findViewById(R.id.data_detail_row_author), "作者", mBookSeriesData.getAuthor());
        setRowContents(findViewById(R.id.data_detail_row_author_pronunciation), "作者（カナ）", mBookSeriesData.getAuthorPronunciation());
        // 掲載誌
        setRowContents(findViewById(R.id.data_detail_row_magazine), "掲載誌", mBookSeriesData.getMagazine());
        setRowContents(findViewById(R.id.data_detail_row_magazine_pronunctaion), "掲載誌（カナ）", mBookSeriesData.getMagazinePronunciation());
        // 出版社
        setRowContents(findViewById(R.id.data_detail_row_company), "出版社", mBookSeriesData.getCompany());
        // メモ
        setRowContents(findViewById(R.id.data_detail_row_memo), "メモ", mBookSeriesData.getCompanyPronunciation());
        // タグ
        updateTags();

        findViewById(R.id.btn_tag_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = TagsEditDialogActivity.getIntent(
                        EditSeriesDialogActivity.this, "タグを編集",
                        mBookSeriesData.getTagsAsList(), "完了");
                if(intent != null) startActivityForResult(intent, G.REQUEST_CODE_TAGS_EDIT);
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
                        mBookSeriesData.setTitle(title);
                        mBookSeriesData.setTitlePronunciation(getRowContents(findViewById(R.id.data_detail_row_title_pronunciation)));
                        // 作者
                        mBookSeriesData.setAuthor(getRowContents(findViewById(R.id.data_detail_row_author)));
                        mBookSeriesData.setAuthorPronunciation(getRowContents(findViewById(R.id.data_detail_row_author_pronunciation)));
                        // 掲載誌
                        mBookSeriesData.setMagazine(getRowContents(findViewById(R.id.data_detail_row_magazine)));
                        mBookSeriesData.setMagazinePronunciation(getRowContents(findViewById(R.id.data_detail_row_magazine_pronunctaion)));
                        // 出版社
                        mBookSeriesData.setCompany(getRowContents(findViewById(R.id.data_detail_row_company)));
                        // メモ
                        mBookSeriesData.setMemo(getRowContents(findViewById(R.id.data_detail_row_memo)));
                        // タグ（初期は実装しない）
//                setRowContents(findViewById(R.id.data_detail_row_title), "タイトル", mBookSeriesData.);

                        mBookSeriesDao.saveSeries(mBookSeriesData);

                        setResult(G.RESULT_POSITIVE, G.RESULT_DATA_KEY_EDIT_SERIES, G.RESULT_DATA_VALUE_EDIT_SERIES_EDIT);
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

    public static Intent getIntent(Context context, String title, String btnPositive, BookSeriesData bookSeriesData) {
        Intent intent = new Intent(context, EditSeriesDialogActivity.class);

        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);
        sTmpBookSeriesData = bookSeriesData;

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
                    if(mBookSeriesDao.deleteBookSeries(mSeriesId)) {
                        ToastUtil.show(EditSeriesDialogActivity.this, "作品の情報を削除しました");
                        setResult(G.RESULT_POSITIVE, G.RESULT_DATA_KEY_EDIT_SERIES, G.RESULT_DATA_VALUE_EDIT_SERIES_DELETE);
                        finish();
                    } else {
                        ToastUtil.show(EditSeriesDialogActivity.this, "作品の情報の削除に失敗しました");
                    }
                }
                break;
            case G.REQUEST_CODE_TAGS_EDIT:
                if(data != null) {
                    String tagsStr = data.getStringExtra(TagsEditDialogActivity.KEY_TAGS);
                    mBookSeriesData.setRawTags(tagsStr);
                    updateTags();
                }
                break;
        }
    }

    private void updateTags() {
        // タグ
        tagContainer.removeAllViews();
        ArrayList<ViewGroup> tagViewList = ViewUtil.getTagViewList(this, mBookSeriesData.getTagsAsList(), ViewUtil.TAGS_LAYOUT_TYPE_NORMAL);
        for(ViewGroup tagView : tagViewList) {
            tagContainer.addView(tagView);
        }
        tagContainer.invalidate();
    }
}
