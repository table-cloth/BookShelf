package com.tablecloth.bookshelf.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.Const;

/**
 * Book Series catalog screen in Grid format
 *
 * Created by Minami on 2015/03/15.
 */
public class BookSeriesGridCatalogActivity extends BookSeriesCatalogBaseActivity {

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return R.layout.activity_grid;
    }

    /**
     * OnCreate
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((GridView)findViewById(R.id.grid_view)).setAdapter(mCatalogAdapter);
    }

    /**
     * Check if current catalog is shown in Grid format
     *
     * @return whether is Grid format
     */
    @Override
    protected boolean isGridCatalog() {
        return true;
    }

    /**
     * Get book series catalog view
     *
     * @return BookSeries catalog view
     */
    @Override
    protected View getBookSeriesCatalogView() {
        return findViewById(R.id.grid_view);
    }
}
