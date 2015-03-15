package com.tablecloth.bookshelf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.dialog.EditSeriesDialogActivity;
import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.IntentUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;

/**
 * Created by shnomura on 2015/03/15.
 */
public class GridActivity extends MainBaseActivity {

    private GridView mGridView;
    private GridAdapter mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // グリッド部分の生成・設定処理
        mGridView = (GridView)findViewById(R.id.grid_view);
        mGridAdapter = new GridAdapter();
        mGridView.setAdapter(mGridAdapter);

    }

    private class GridAdapter extends BaseAdapter {
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
                v = inflater.inflate(R.layout.book_grid_item, null);
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
                    series.getImage(mHandler, GridActivity.this, new ListenerUtil.LoadBitmapListener() {
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
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // リストのセルをタップで
                                startActivity(IntentUtil.getSeriesDetailIntent(GridActivity.this, series.mSeriesId));
                            }
                        });
                        // WebAPIの検索結果表示時の場合は一部処理をかえる
                    } else {
                        // リストの各要素のタッチイベント
                        v.findViewById(R.id.delete_btn).setVisibility(View.GONE);

                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = EditSeriesDialogActivity.getIntent(GridActivity.this, "この作品を追加しますか？", "追加", series);
//                                Intent intent = SimpleDialogActivity.getIntent(ListActivity.this, "確認", "この作品を本棚に追加しますか？", "はい", "いいえ");
                                GridActivity.this.startActivityForResult(intent, G.REQUEST_CODE_LIST_ADD_SERIES);
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

    protected int getContentViewID() {
        return R.layout.activity_grid;
    }

    @Override
    protected void notifyDataSetChanged() {
        mGridAdapter.notifyDataSetChanged();
    }

}