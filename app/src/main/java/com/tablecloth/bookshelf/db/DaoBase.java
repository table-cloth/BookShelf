package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.math.MathContext;

/**
 * Created by shnomura on 2015/03/29.
 */
public class DaoBase {

    protected DB mDb = null; // DBクラス
    protected SQLiteDatabase mSqlDb = null; // 実際にDBを操作するためのSQLiteDatabaseクラス
    protected Context mContext = null;

    public DaoBase(Context context) {
        mContext = context;
        instantiateDB(context);
    }

    /**
     * DBの初期化処理
     * @param context
     */
    protected void instantiateDB(Context context) {
        if(mDb == null) {
            mDb = DB.getInstance(context);
            mSqlDb = mDb.getSQLiteDatabase();
        }
    }

    protected void closeDB() {
        mDb.close();
        mSqlDb = null;
        mDb = null;
    }
}
