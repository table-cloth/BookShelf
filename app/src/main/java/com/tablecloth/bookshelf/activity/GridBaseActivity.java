package com.tablecloth.bookshelf.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.Const;

/**
 * Created by Minami on 2015/03/15.
 */
public class GridBaseActivity extends BookSeriesCatalogBaseActivity {

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
            return mBookSeriesDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBookSeriesDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return initializeBookSeriesItemView(position, convertView, parent,
                    mBookSeriesDataList.get(position), true);
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


    protected int getContentViewID() {
        return R.layout.activity_grid;
    }

    @Override
    protected void notifyDataSetChanged() {
        mGridAdapter.notifyDataSetChanged();
    }

    /**
     * 現在の表示形式設定と、表示されている画面が一致するかを確認
     * @return
     */
    protected boolean isCurrentShowTypeSameWithSetting() {
        String value = mSettings.load(Const.DB.Settings.KEY.SERIES_SHOW_TYPE, Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID);
        if(Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID.equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * 現在表示している画面の種類を返す
     */
    protected String getCurrentShowType() {
        return Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID;
    }

}
