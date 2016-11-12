package com.tablecloth.bookshelf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.view.CustomListView;
import com.tablecloth.bookshelf.R;

/**
 * Book Series catalog screen in Grid format
 *
 * Created by Minami on 2014/08/16.
 */
public class BookSeriesListCatalogActivity extends BookSeriesCatalogBaseActivity {

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return R.layout.activity_list;
    }

    /**
     * OnCreate
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ListView)findViewById(R.id.list_view)).setAdapter(mCatalogAdapter);
    }

    /**
     * Check if current catalog is shown in Grid format
     *
     * @return whether is Grid format
     */
    @Override
    protected boolean isGridCatalog() {
        return false;
    }
}
