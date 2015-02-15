package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.content.Intent;

import com.tablecloth.bookshelf.activity.SeriesDetailActivity;

/**
 * Created by minami on 14/09/07.
 */
public class IntentUtil {

    public static Intent getSeriesDetailIntent(Context context, int seriesId) {
        Intent intent = new Intent(context, SeriesDetailActivity.class);
        intent.putExtra(G.INTENT_SERIES_ID,seriesId);
        return intent;
    }
}
