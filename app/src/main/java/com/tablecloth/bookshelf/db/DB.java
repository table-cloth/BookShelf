package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.tablecloth.bookshelf.util.Const;

/**
 * DB singleton class
 * All access to DB, with SQLiteDatabase must be done using this class
 *
 * Created by Minami on 2014/08/16.
 */
public class DB {

    private SQLiteDatabase mSqLiteDatabase;
    private static DB mDb;

    /**
     * Gets the instance of DB class
     * If the instance is not initialized, DB is returned after initialize
     *
     * @param context context
     * @return DB instance
     */
    @NonNull
    static DB getDB(@NonNull Context context) {
        if (mDb == null) {
            ensureOpenedDB(context);
        }
        return mDb;
    }

    /**
     * Returns SQLiteDatabase instance
     * If SQLiteDatabase is not initialized, it will be initialized
     *
     * @param context context
     * @return SQLiteDatabase
     */
    @NonNull
    SQLiteDatabase getSQLiteDatabase(Context context) {
        ensureOpenedSQLiteDatabase(context);
        return mSqLiteDatabase;
    }

    /**
     * Close SQLiteDatabase & resets all instance in DB class
     */
    void close() {
        if(mSqLiteDatabase != null) {
            mSqLiteDatabase.close();
            mSqLiteDatabase = null;
        }
        if(mDb != null) {
            mDb = null;
        }
    }


    /**
     * Constructor
     */
    private DB(@NonNull Context context) {
        ensureOpenedSQLiteDatabase(context);
    }

    /**
     * Initialize DB if not initialized
     * @param context context
     */
    private static void ensureOpenedDB(@NonNull Context context) {
        // return if DB instance is already set
        if (mDb != null) {
            return;
        }
        mDb = new DB(context);
    }

    /**
     * Initialize SQLiteDatabase if not initialized
     */
    private void ensureOpenedSQLiteDatabase(@NonNull Context context) {
        // return if already open
        if(mSqLiteDatabase != null
                && mSqLiteDatabase.isOpen()) {
            return;
        }
        final OpenHelper openHelper = new OpenHelper(context);
        mSqLiteDatabase = openHelper.getWritableDatabase();
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
        OpenHelper(@NonNull Context context) {
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
