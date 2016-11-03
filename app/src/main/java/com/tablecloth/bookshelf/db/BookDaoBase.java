package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Data accessor for getting information of Book Series
 * This class should only read data, and not set data
 *
 * Created by nomura on 2016/11/04.
 */
public class BookDaoBase extends DaoBase {

    /**
     * Constructor
     *
     * @param context context
     */
    public BookSeriesDao(@NonNull Context context) {
        super(context);
    }
}
