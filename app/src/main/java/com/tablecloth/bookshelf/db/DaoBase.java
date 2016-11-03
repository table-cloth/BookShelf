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
     * DB instance
     * All DB access are to be done with this instance only
     */
    private DB mDB = null; // DB instance

    /**
     * SQLite instance
     * For to control DB instance
     */
    private  SQLiteDatabase mSqliteDatabase = null; // 実際にDBを操作するためのSQLiteDatabaseクラス

    /**
     * Context
     */
    private final Context mContext;

    /**
     * Constructor
     *
     * @param context context
     */
    public DaoBase(@NonNull Context context) {
        mContext = context;
        initDB();
    }

    public static synchronized void ensureOpenedDB(final Context context)
    {
        if(m)
    }

    /**
     * Initialize DB related variables
     * Inits DB & SQLiteDatabase
     */
    protected void initDB() {
        if(mDB == null) {
            mDB = DB.getDB(mContext);
            mSqliteDatabase = mDB.getSQLiteDatabase();
        }
    }

    /**
     * close DB & resets all DB related variables
     */
    protected void closeDB() {
        if(mSqliteDatabase != null) {
            mSqliteDatabase.close();
            mSqliteDatabase = null;
        }
        if(mDB != null) {
            final DB openedDB = mDB;
            mDB = null;
            if(openedDB != null) {
                openedDB.close();
            }
        }
    }

    /**
     * Get DB instance
     *
     * @return DB
     */
    protected DB getDB() {
        return mDB;
    }

    /**
     * Get SQLiteDatabase instance
     *
     * @return SQLiteDatabase
     */
    protected SQLiteDatabase getSQLiteDatabase() {
        return mSqliteDatabase;
    }

}
