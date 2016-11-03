package com.tablecloth.bookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tablecloth.bookshelf.util.Util;

/**
 * Created by shnomura on 2015/03/29.
 * 各種設定項目用のdao
 */
public class SettingsDao extends DaoBase {

    // 設定項目はこちらのクラスにて管理する
    public class KEY {
        final public static String SERIES_SHOW_TYPE = "series_show_type"; // 作品一覧の表示タイプ
    }
    // 設定値ははこちらのクラスにて管理する
    public class VALUE {
        final public static String SERIES_SHOW_TYPE_GRID = "grid"; // 作品一覧の表示タイプ
        final public static String SERIES_SHOW_TYPE_LIST = "list"; // 作品一覧の表示タイプ
    }

    public SettingsDao(Context context) {
        super(context);
    }

    /**
     * 設定値を保存する
     * @param key
     * @param value
     * @return
     */
    public boolean save(String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(DB.Settings.KEY, key);
        cv.put(DB.Settings.VALUE, value);

        long result = 0;
        // 値が存在する場合
        if(!Util.isEmpty(load(key, ""))) {
            result = mSqlDb.update(DB.Settings.TABLE_NAME, cv, DB.Settings.KEY + " = ? ", new String[]{key});
        // 値が存在しない場合
        } else {
            result = mSqlDb.insert(DB.Settings.TABLE_NAME, null, cv);
        }
        if(result > 0) return true;
        return false;
    }

    /**
     * 設定値を読み込む
     * 未設定の場合は空文字を返す
     * @param key
     * @return
     */
    public String load(String key, String defValue) {
        initDB(mContext);

        Cursor cursor = mDb.getSQLiteDatabase().rawQuery(getLoadSqlString(key), null);
        String value = defValue;

        // cursorからSeriesDataを生成
        if(cursor != null) {
            try {
                int max = cursor.getCount();
                for (int i = 0; i < max; i++) {
                    if (cursor.moveToNext()) {
                        int columnIndex = cursor.getColumnIndex(DB.Settings.VALUE);
                        value = cursor.getString(columnIndex);
                        if (!Util.isEmpty(value)) {
                            break;
                        }
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return value;
    }

    /**
     * ロード用のSQL文を取得する
     * @param key
     * @return
     */
    private String getLoadSqlString(String key) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM ");
        sql.append(DB.Settings.TABLE_NAME);
        sql.append(" WHERE " + DB.Settings.KEY + " = " + "'" + key + "'");
        return sql.toString();
    }
}
