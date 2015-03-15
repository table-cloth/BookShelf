package com.tablecloth.bookshelf.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.tablecloth.bookshelf.R;

/**
 * Created by shnomura on 2015/03/15.
 */
public class GridActivity extends MainBaseActivity {

    private GridView mGridView;
    private GridAdapter mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGridView = (GridView)findViewById(R.id.grid_view);
        mGridView.setAdapter(mGridAdapter);
    }

    /**
     * 基底クラスなのでContentViewIDを指定しない
     * @return
     */
    protected int getContentViewID() {
        return R.layout.activity_grid;
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
            return null;
        }
    }

    @Override
    protected void notifyDataSetChanged() {

    }

}
