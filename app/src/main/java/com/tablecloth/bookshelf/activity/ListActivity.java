package com.tablecloth.bookshelf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.CustomListView;
import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.dialog.EditSeriesDialogActivity;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ImageUtil;
import com.tablecloth.bookshelf.util.IntentUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ViewUtil;
/**
 * Created by Minami on 2014/08/16.
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
            ViewGroup tagContainer;

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
                    tagContainer = (ViewGroup) v.findViewById(R.id.tag_container);

                    final ImageView image = (ImageView) v.findViewById(R.id.image);

                    title.setText(series.getTitle());
                    author.setText(series.getAuthor());
                    if(mMode == G.MODE_API_SEARCH_RESULT) {
                        volume.setVisibility(View.GONE);
                    } else {
                        volume.setText(series.getVolumeText());
                        volume.setVisibility(View.VISIBLE);
                    }
                    image.setImageResource(R.drawable.no_image);

                    Bitmap cacheImage = ImageUtil.getImageCache(series.getSeriesId());
                    if(cacheImage != null) {
                        image.setImageBitmap(cacheImage);
                    } else {
                        final int seriesId = series.getSeriesId();
                        series.loadImage(mHandler, ListActivity.this, new ListenerUtil.LoadBitmapListener() {
                            @Override
                            public void onFinish(Bitmap bitmap) {
                                if (bitmap != null) {
                                    image.setImageBitmap(bitmap);
                                    ImageUtil.setImageCache(seriesId, bitmap);
                                } else {
                                    image.setImageResource(R.drawable.no_image);
                                }
                            }

                            @Override
                            public void onError() {
                                image.setImageResource(R.drawable.no_image);
                            }
                        });
                    }

                    // タグ
                    tagContainer.removeAllViews();
                    tagContainer = ViewUtil.setTagInfoNormal(ListActivity.this, series.getTagsAsList(), tagContainer);
                    tagContainer.invalidate();

                    // WebAPIの検索結果時以外は先品詳細画面へ
                    if(mMode != G.MODE_API_SEARCH_RESULT) {
                        // リストの各要素のタッチイベント
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // リストのセルをタップで
                                startActivity(IntentUtil.getSeriesDetailIntent(ListActivity.this, series.getSeriesId()));
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
        String value = mSettings.load(Const.DB.Settings.KEY.SERIES_SHOW_TYPE, Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID);
        if(Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_LIST.equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * 現在表示している画面の種類を返す
     */
    protected String getShowType() {
        return Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_LIST;
    }

}
