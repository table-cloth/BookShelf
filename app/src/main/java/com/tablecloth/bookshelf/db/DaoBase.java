package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Data access object Base class
 * All DaoXx class should override this class to manage same DB usage
 *
 * Created by shnomura on 2015/03/29.
 */
public class DaoBase {

    /**
     * Context
     */
    protected final Context mContext;

    /**
     * Constructor
     *
     * @param context context
     */
    public DaoBase(@NonNull Context context) {
        mContext = context;
    }
}
