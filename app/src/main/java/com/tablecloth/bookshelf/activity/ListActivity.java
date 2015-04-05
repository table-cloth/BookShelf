package com.tablecloth.bookshelf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.db.SettingsDao;
import com.tablecloth.bookshelf.dialog.BtnListDialogActivity;
import com.tablecloth.bookshelf.dialog.SearchDialogActivity;
import com.tablecloth.bookshelf.util.CustomListView;
import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.FilterDao;
import com.tablecloth.bookshelf.dialog.EditSeriesDialogActivity;
import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;
import com.tablecloth.bookshelf.util.GAEvent;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.IntentUtil;
import com.tablecloth.bookshelf.util.JsonUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ProgressUtil;
import com.tablecloth.bookshelf.util.Rakuten;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.VersionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by shnomura on 2014/08/16.
 */
public class ListActivity extends MainBaseActivity {

    private CustomListView mListView;
    ListAdapter mListAdapter;

    
    // http://shogogg.hatenablog.jp/entry/20110118/1295326773
    int mDraggingPosition = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // リスト部分の生成・設定処理
        mListView = (CustomListView)findViewById(R.id.list_view);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        mListView.setSortable(true);

    }


    /**
     * ListView用のAdapterクラス
     */
    private class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mDataArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView title;
            TextView author;
            TextView volume;
            View v = convertView;

            if(v==null){
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.book_list_row, null);
            }
            if(mDataArrayList != null) {
                final SeriesData series = (SeriesData) mDataArrayList.get(position);
                if (series != null) {
                    // データの設定
                    title = (TextView) v.findViewById(R.id.title);
                    author = (TextView) v.findViewById(R.id.author);
                    volume = (TextView) v.findViewById(R.id.volume);
                    final ImageView image = (ImageView) v.findViewById(R.id.image);

                    title.setText(series.mTitle);
                    author.setText(series.mAuthor);
                    if(mMode == G.MODE_API_SEARCH_RESULT) {
                        volume.setVisibility(View.GONE);
                    } else {
                        volume.setText(series.getVolumeString());
                        volume.setVisibility(View.VISIBLE);
                    }
                    image.setImageResource(R.drawable.no_image);
                    series.getImage(mHandler, ListActivity.this, new ListenerUtil.LoadBitmapListener() {
                        @Override
                        public void onFinish(Bitmap bitmap) {
                            if(bitmap != null) {
                                image.setImageBitmap(bitmap);
                            }
                            else {
                                image.setImageResource(R.drawable.no_image);
                            }
                        }

                        @Override
                        public void onError() {
                            image.setImageResource(R.drawable.no_image);
                        }
                    });

                    // WebAPIの検索結果時以外は先品詳細画面へ
                    if(mMode != G.MODE_API_SEARCH_RESULT) {
                        // リストの各要素のタッチイベント
//                        v.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mSelectSeriesIds = new int[]{series.mSeriesId};
//                                Intent intent = SimpleDialogActivity.getIntent(ListActivity.this, "削除しますか？", "登録された作品の情報を削除しますか？\n\n復元は出来ませんのでご注意ください", "はい", "いいえ");
//                                startActivityForResult(intent, G.REQUEST_CODE_LIST_ROW_DELETE_SERIES);
//                            }
//                        });
//                        v.findViewById(R.id.delete_btn).setVisibility(View.VISIBLE);

                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // リストのセルをタップで
                                startActivity(IntentUtil.getSeriesDetailIntent(ListActivity.this, series.mSeriesId));
                            }
                        });
                    // WebAPIの検索結果表示時の場合は一部処理をかえる
                    } else {
                        // リストの各要素のタッチイベント
                        v.findViewById(R.id.delete_btn).setVisibility(View.GONE);

                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = EditSeriesDialogActivity.getIntent(ListActivity.this, "この作品を追加しますか？", "追加", series);
//                                Intent intent = SimpleDialogActivity.getIntent(ListActivity.this, "確認", "この作品を本棚に追加しますか？", "はい", "いいえ");
                                ListActivity.this.startActivityForResult(intent, G.REQUEST_CODE_LIST_ADD_SERIES);
                            }
                        });
                    }
                }
            }
            return v;
        }
    }

    /**
     * タッチイベントを設定
     */
    protected void setClickListener() {
        super.setClickListener();
    }
    
    /**
     * その他各種リスナー登録
     */
    protected void setOtherListener() {
        super.setOtherListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_list;
    }

    /**
     * データに変動があり、notifyDataSetChanged()が呼び出される必要があるときに呼ばれる
     */
    @Override
    protected void notifyDataSetChanged() {
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * 現在の表示形式設定と、表示されている画面が一致するかを確認
     * @return
     */
    protected boolean isShowTypeCorrect() {
        String value = mSettings.load(SettingsDao.KEY.SERIES_SHOW_TYPE, SettingsDao.VALUE.SERIES_SHOW_TYPE_GRID);
        if(SettingsDao.VALUE.SERIES_SHOW_TYPE_LIST.equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * 現在表示している画面の種類を返す
     */
    protected String getShowType() {
        return SettingsDao.VALUE.SERIES_SHOW_TYPE_GRID;
    }

}
