package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;

/**
 * 作品の情報追加用のダイアログ
 * Created by shnomura on 2014/08/17.
 */
public class DataAddDialogActivity extends DialogBaseActivity {

//    int mSelectData = 0;
//    Button mSelectTypeButton;
//    EditText mTextSingle;
//    EditText mTextFrom;
//    EditText mTextTo;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        Intent intent = getIntent();
//
//        // テキスト設定
//        ((TextView) findViewById(R.id.title)).setText(intent.getStringExtra(KEY_TITLE));
//        ((TextView) findViewById(R.id.btn_positive)).setText(intent.getStringExtra(KEY_BTN_POSITIVE));
//        ((TextView) findViewById(R.id.btn_negative)).setText(intent.getStringExtra(KEY_BTN_NEGATIVE));
//
//        mSelectTypeButton = (Button) findViewById(R.id.data_type_btn);
//        mTextSingle = (EditText) findViewById(R.id.data_detail_normal);
//        mTextFrom = (EditText) findViewById(R.id.data_detail_from);
//        mTextTo = (EditText) findViewById(R.id.data_detail_to);
//
//        ((TextView) findViewById(R.id.btn_positive)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveToData();
//                DataAddDialogActivity.this.finish();
//            }
//        });
//        ((TextView) findViewById(R.id.btn_negative)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DataAddDialogActivity.this.finish();
//            }
//        });
//
//        if (EditSeriesDialogActivity.sSeriesData == null) {
//            ToastUtil.show(DataAddDialogActivity.this, "作品のデータが不正です。画面を閉じ、再度登録をお願いします");
//            finish();
//        }
//
//        if(Util.isEmpty(EditSeriesDialogActivity.sSeriesData.mTitle)) {
//            mSelectData = G.SELECT_DATA_TITLE;
//        } else if(Util.isEmpty(EditSeriesDialogActivity.sSeriesData.mAuthor)) {
//            mSelectData = G.SELECT_DATA_AUTHOR;
//        } else if(Util.isEmpty(EditSeriesDialogActivity.sSeriesData.mCompany)) {
//            mSelectData = G.SELECT_DATA_COMPANY;
////        } else if(NewSeriesDialogActivity.sSeriesData.mImage == null) {
////            mSelectData = G.SELECT_DATA_IMAGE;
//        } else if(EditSeriesDialogActivity.sSeriesData.mVolumeList == null || EditSeriesDialogActivity.sSeriesData.mVolumeList.size() <= 0) {
//            mSelectData = G.SELECT_DATA_VOLUME;
//        } else if(EditSeriesDialogActivity.sSeriesData.mTagsList == null || EditSeriesDialogActivity.sSeriesData.mTagsList.size() <= 0) {
//            mSelectData = G.SELECT_DATA_TAGS;
//        } else if(Util.isEmpty(EditSeriesDialogActivity.sSeriesData.mTitlePronunciation)) {
//            mSelectData = G.SELECT_DATA_TITLE_PRONUNCIATION;
//        } else if(Util.isEmpty(EditSeriesDialogActivity.sSeriesData.mAuthorPronunciation)) {
//            mSelectData = G.SELECT_DATA_AUTHOR_PRONUNCIATION;
//        } else if(Util.isEmpty(EditSeriesDialogActivity.sSeriesData.mCompanyPronunciation)) {
//            mSelectData = G.SELECT_DATA_COMPANY_PRONUNCIATION;
//        } else {
//            mSelectData = G.SELECT_DATA_TAGS;
//        }
//
//        setLayout();
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_data_add_dialog;
    }
//
//    public static Intent getIntent(Context context, String title, String btnPositive, String btnNegative) {
//        Intent intent = new Intent(context, DataAddDialogActivity.class);
//
//        intent.putExtra(KEY_TITLE, title);
//        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);
//        intent.putExtra(KEY_BTN_NEGATIVE, btnNegative);
//
//        return intent;
//    }
//
//    private void setLayout() {
//        switch (mSelectData) {
//            case G.SELECT_DATA_TITLE:
//                findViewById(R.id.data_detail_type_normal).setVisibility(View.VISIBLE);
//                findViewById(R.id.data_detail_type_volume).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_tag).setVisibility(View.GONE);
//                ((EditText)findViewById(R.id.data_detail_normal)).setText("");
//                mSelectTypeButton.setText("タイトル");
//                break;
//            case G.SELECT_DATA_TITLE_PRONUNCIATION:
//                findViewById(R.id.data_detail_type_normal).setVisibility(View.VISIBLE);
//                findViewById(R.id.data_detail_type_volume).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_tag).setVisibility(View.GONE);
//                ((EditText)findViewById(R.id.data_detail_normal)).setText("");
//                mSelectTypeButton.setText("タイトル（読み）");
//                break;
//            case G.SELECT_DATA_AUTHOR:
//                findViewById(R.id.data_detail_type_normal).setVisibility(View.VISIBLE);
//                findViewById(R.id.data_detail_type_volume).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_tag).setVisibility(View.GONE);
//                ((EditText)findViewById(R.id.data_detail_normal)).setText("");
//                mSelectTypeButton.setText("作者");
//                break;
//            case G.SELECT_DATA_AUTHOR_PRONUNCIATION:
//                findViewById(R.id.data_detail_type_normal).setVisibility(View.VISIBLE);
//                findViewById(R.id.data_detail_type_volume).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_tag).setVisibility(View.GONE);
//                ((EditText)findViewById(R.id.data_detail_normal)).setText("");
//                mSelectTypeButton.setText("作者（読み）");
//                break;
//            case G.SELECT_DATA_COMPANY:
//                findViewById(R.id.data_detail_type_normal).setVisibility(View.VISIBLE);
//                findViewById(R.id.data_detail_type_volume).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_tag).setVisibility(View.GONE);
//                ((EditText)findViewById(R.id.data_detail_normal)).setText("");
//                mSelectTypeButton.setText("出版社");
//                break;
//            case G.SELECT_DATA_COMPANY_PRONUNCIATION:
//                findViewById(R.id.data_detail_type_normal).setVisibility(View.VISIBLE);
//                findViewById(R.id.data_detail_type_volume).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_tag).setVisibility(View.GONE);
//                ((EditText)findViewById(R.id.data_detail_normal)).setText("");
//                mSelectTypeButton.setText("タイトル");
//                break;
//            case G.SELECT_DATA_IMAGE:
//                findViewById(R.id.data_detail_type_normal).setVisibility(View.VISIBLE);
//                findViewById(R.id.data_detail_type_volume).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_tag).setVisibility(View.GONE);
//                ((EditText)findViewById(R.id.data_detail_normal)).setText("");
//                mSelectTypeButton.setText("写真");
//                break;
//            case G.SELECT_DATA_VOLUME:
//                findViewById(R.id.data_detail_type_normal).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_volume).setVisibility(View.VISIBLE);
//                findViewById(R.id.data_detail_type_tag).setVisibility(View.GONE);
//                ((EditText)findViewById(R.id.data_detail_from)).setText("");
//                ((EditText)findViewById(R.id.data_detail_to)).setText("");
//                mSelectTypeButton.setText("所持巻数");
//                break;
//            case G.SELECT_DATA_TAGS:
//                findViewById(R.id.data_detail_type_normal).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_volume).setVisibility(View.GONE);
//                findViewById(R.id.data_detail_type_tag).setVisibility(View.VISIBLE);
//                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.data_detail_type_tag_container);
//                linearLayout.removeAllViews();
//                mSelectTypeButton.setText("タグ");
//                break;
//        }
//    }
//    private void saveToData() {
//        switch (mSelectData) {
//            case G.SELECT_DATA_TITLE:
//                EditSeriesDialogActivity.sSeriesData.mTitle = mTextSingle.getText().toString();
//                break;
//            case G.SELECT_DATA_TITLE_PRONUNCIATION:
//                EditSeriesDialogActivity.sSeriesData.mTitlePronunciation = mTextSingle.getText().toString();
//                break;
//            case G.SELECT_DATA_AUTHOR:
//                EditSeriesDialogActivity.sSeriesData.mAuthor = mTextSingle.getText().toString();
//                break;
//            case G.SELECT_DATA_AUTHOR_PRONUNCIATION:
//                EditSeriesDialogActivity.sSeriesData.mAuthorPronunciation = mTextSingle.getText().toString();
//                break;
//            case G.SELECT_DATA_COMPANY:
//                EditSeriesDialogActivity.sSeriesData.mCompany = mTextSingle.getText().toString();
//                break;
//            case G.SELECT_DATA_COMPANY_PRONUNCIATION:
//                EditSeriesDialogActivity.sSeriesData.mCompanyPronunciation = mTextSingle.getText().toString();
//                break;
//            case G.SELECT_DATA_IMAGE:
//                ///TODO イメージ保存
//                break;
//            case G.SELECT_DATA_VOLUME:
//                String fromStr = mTextFrom.getText().toString();
//                String toStr = mTextTo.getText().toString();
//
//                int from = 0;
//                if(!Util.isEmpty(fromStr)) from = Integer.valueOf(fromStr);
//                int to = 0;
//                if(!Util.isEmpty(toStr)) to = Integer.valueOf(toStr);
//
//                for(int i = from ; i < to ; i ++) {
//                    EditSeriesDialogActivity.sSeriesData.mVolumeList.add(i);
//                }
//
//                break;
//            case G.SELECT_DATA_TAGS:
//                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.data_detail_type_tag_container);
//                int count = linearLayout.getChildCount();
//                if(count > 0) {
//                    for(int i = 0 ; i < count ; i ++) {
//                        String str = ((EditText)linearLayout.getChildAt(i)).getText().toString();
//                        EditSeriesDialogActivity.sSeriesData.mTagsList.add(str);
//                    }
//                }
//                break;
//        }
//        setResult(mSelectData);
//        finish();
//    }
}
