package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tablecloth.bookshelf.util.Const;

/**
 * Created by shnomura on 2014/08/16.
 */
public class DB {

    private final Context mContext;
    private SQLiteDatabase mSqLiteDatabase;
    private static DB mDb;

    /**
     * Gets the instance of DB class
     * If the instance is not initialized, DB is returned after initialize
     *
     * @param context context
     * @return DB instance
     */
    static DB getDB(Context context) {
        if (mDb == null) {
            ensureOpenedDB(context);
        }
        return mDb;
    }

    /**
     * Returns SQLiteDatabase instance
     * If SQLiteDatabase is not initialized, it will be initialized
     *
     * @return SQLiteDatabase
     */
    SQLiteDatabase getSQLiteDatabase() {
        ensureOpenedSQLiteDatabase();
        return mSqLiteDatabase;
    }

    /**
     * Constructor
     */
    private DB(final Context context) {
        mContext = context;
        ensureOpenedSQLiteDatabase();
    }

    /**
     * Initialize DB if not initialized
     * @param context
     */
    private static synchronized void ensureOpenedDB(final Context context) {
        // return if DB instance is already set
        if (mDb != null) {
            return;
        }
        mDb = new DB(context);
    }

    /**
     * Initialize SQLite if not initialized
     * @return
     */
    private void ensureOpenedSQLiteDatabase() {
        // return if already open
        if(mSqLiteDatabase != null
                && mSqLiteDatabase.isOpen()) {
            return;
        }
        final OpenHelper openHelper = new OpenHelper(mContext);
        mSqLiteDatabase = openHelper.getWritableDatabase();
    }

    /**
     * Close SQLiteDatabase & resets all instance in DB class
     */
    private void close() {
        if(mSqLiteDatabase != null) {
            mSqLiteDatabase.close();
            mSqLiteDatabase = null;
        }
        if(mDb != null) {
            mDb = null;
        }
    }

    /**
     * Initialization of DB table are to be stated in this class
     * When DB tables are changed due to update, they are also to be stated in this class
     */
    private static class OpenHelper extends SQLiteOpenHelper {

        /**
         * Version of the DB
         * Needs to be incremented every time DB table needs to be changed
         *
         * [Version update logs]
         * 1. Initial release with "BookSeriesTable" & "BookDetailTable"
         * 2. "SettingsTable" & "BookSeriesTagsTable" added
         */
        private static final int NEWEST_VERSION = 2;

        /**
         * Constructor
         *
         * @param context context
         */
        OpenHelper(Context context) {
            super(context, Const.DB.DB_NAME, null, NEWEST_VERSION);
        }

        /**
         * This action is to be called for new users
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SqlText.CREATE_SERIES_TABLE_SQL);
            db.execSQL(SqlText.CREATE_BOOK_DETAIL_SQL);
            db.execSQL(SqlText.CREATE_SETTINGS_SQL);
            db.execSQL(SqlText.CREATE_TAGS_SQL);
        }

        /**
         * This action is to be called for update users
         *
         * @param db SQLiteDatabase
         * @param oldVersion OldVersion
         * @param newVersion NewVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(oldVersion < 2) {
                db.execSQL(SqlText.CREATE_SETTINGS_SQL);
                db.execSQL(SqlText.CREATE_TAGS_SQL);
            }
        }
    }
}
