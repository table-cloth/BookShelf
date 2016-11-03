package com.tablecloth.bookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.Util;

/**
 * Data accessor for Settings within the app
 *
 * Created by shnomura on 2015/03/29.
 */
public class SettingsDao extends DaoBase {

    /**
     * Constructor
     *
     * @param context context
     */
    public SettingsDao(@NonNull Context context) {
        super(context);
    }

    /**
     * Save or Update new setting
     *
     * @param key Key for setting to be saved
     * @param value Value for setting to be saved
     * @return isSaveSuccess
     */
    public boolean save(@NonNull String key, @NonNull String value) {
        // Set save contents
        ContentValues cv = new ContentValues();
        cv.put(Const.DB.Settings.SettingsTable.KEY, key);
        cv.put(Const.DB.Settings.SettingsTable.VALUE, value);

        long affectRowCount = 0;
        SQLiteDatabase sqLiteDatabase = DB.getDB(mContext).getSQLiteDatabase(mContext);

        // Setting with the given key already exists
        if(!Util.isEmpty(load(key, ""))) {
            affectRowCount = sqLiteDatabase.update(Const.DB.Settings.SettingsTable.TABLE_NAME, cv, Const.DB.Settings.SettingsTable.KEY + " = ? ", new String[]{key});
        // Setting with the given key does not exist
        } else {
            affectRowCount = sqLiteDatabase.insert(Const.DB.Settings.SettingsTable.TABLE_NAME, null, cv);
        }

        return affectRowCount > 0;
    }

    /**
     * Load setting with given key
     *
     * @param key Key for setting to load
     * @param defValue Default value to be used if setting with given key is not registered
     * @return Value paired with given key, or defValue
     */
    public String load(String key, String defValue) {
        // Get cursor with given key
        SQLiteDatabase sqLiteDatabase = DB.getDB(mContext).getSQLiteDatabase(mContext);
        Cursor cursor = sqLiteDatabase.rawQuery(SqlText.loadSettingsSQL(key), null);
        // return if cursor not found
        if(cursor == null) {
            return defValue;
        }

        String value = defValue;
        int max = cursor.getCount();

        // Read value param from cursor
        try {
            for (int i = 0 ; i < max ; i++) {
                if (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex(Const.DB.Settings.SettingsTable.VALUE);
                    value = cursor.getString(columnIndex);
                    if (!Util.isEmpty(value)) {
                        break;
                    }
                }
            }
        } finally {
            cursor.close();
        }

        return value;
    }
}
