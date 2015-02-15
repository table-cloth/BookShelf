package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shnomura on 2014/08/16.
 */
public class DB {
    // DB名
    public static final String DB_NAME = "book_shelf";

    private final SQLiteDatabase mSqLiteDatabase;
    private final Context mContext;
    private static DB mDb;

    // コラム情報
    public static class BookSeriesTable {
        // テーブル名
        public static final String TABLE_NAME = "book_series_table";
        // この作品のID
        // BookDetailと紐づけるため必要
        public static final String SERIES_ID = "series_id";
        // タイトル
        public static final String TITLE_NAME  = "title_name";
        // タイトル読み
        public static final String TITLE_PRONUNCIATION = "title_pronunciation";
        // 作者
        public static final String AUTHOR_NAME = "author_name";
        // 作者読み
        public static final String AUTHOR_PRONUNCIATION = "author_pronunciation";
        // 出版社
        public static final String COMPANY_NAME = "company_name";
        // 出版社読み
        public static final String COMPANY_PRONUNCIATION = "company_pronunciation";
        // 掲載雑誌名
        public static final String MAGAZINE_NAME = "magazine_name";
        // 掲載雑誌名
        public static final String MAGAZINE_PRONUNCIATION = "magazine_pronunciation";
        // 写真情報（バイト配列）
        // http://d.hatena.ne.jp/suusuke/20080127/1201417192
        // 型はBLOBを使う
        // 配列の長さ的にバイト配列では無理があったため、パスを保存させる
        public static final String IMAGE_PATH = "image_path";
        // タグ名|||タグ名
        // 上の用なデータを文字列として記録。（複数必須なので）
        public static final String TAGS = "tags";
        // 補足的なメモ
        public static final String MEMO = "memo";
        // 作品が完結済みか
        // 0(false) or 1(true)
        public static final String SERIES_IS_FINISH = "series_is_finish";
        // 登録日時
        public static final String INIT_UPDATE_UNIX = "init_update_unix";
        // 更新日時
        public static final String LAST_UPDATE_UNIX = "last_update_unix";
    }

    // BookTable作成SQL
    private final static String CREATE_SERIES_TABLE_SQL = "create table "
            + BookSeriesTable.TABLE_NAME
            + " ( "
            + BookSeriesTable.SERIES_ID
            + " integer not null primary key autoincrement,"
            + BookSeriesTable.TITLE_NAME
            + " text not null default '',"
            + BookSeriesTable.TITLE_PRONUNCIATION
            + " text, "
            + BookSeriesTable.AUTHOR_NAME
            + " text, "
            + BookSeriesTable.AUTHOR_PRONUNCIATION
            + " text, "
            + BookSeriesTable.COMPANY_NAME
            + " text, "
            + BookSeriesTable.COMPANY_PRONUNCIATION
            + " text, "
            + BookSeriesTable.MAGAZINE_NAME
            + " text, "
            + BookSeriesTable.MAGAZINE_PRONUNCIATION
            + " text, "
            + BookSeriesTable.IMAGE_PATH
            + " text, "
            + BookSeriesTable.TAGS
            + " text, "
            + BookSeriesTable.MEMO
            + " memo, "
            + BookSeriesTable.SERIES_IS_FINISH
            + " integer, "
            + BookSeriesTable.INIT_UPDATE_UNIX
            + " integer, "
            + BookSeriesTable.LAST_UPDATE_UNIX
            + " integer "
            + ")";


    // 各作品の１巻ごとに必要な情報
    public static class BookDetail {
        // テーブル名
        public static final String TABLE_NAME = "book_detail";
        // この作品のID
        // BookTableと紐づけるため必要
        public static final String SERIES_ID = "series_id";
        // 巻数情報
        public static final String SERIES_VOLUME = "series_volume";
        // 登録日時
        public static final String INIT_UPDATE_UNIX = "init_update_unix";
        // 更新日時
        public static final String LAST_UPDATE_UNIX = "last_update_unix";
    }

    // BookDetail作成SQL
    private final static String CREATE_BOOK_DETAIL_SQL = "create table "
            + BookDetail.TABLE_NAME
            + " ( "
            + BookDetail.SERIES_ID
            + " integer not null,"
            + BookDetail.SERIES_VOLUME
            + " integer not null, "
            + BookDetail.INIT_UPDATE_UNIX
            + " integer, "
            + BookDetail.LAST_UPDATE_UNIX
            + " integer "
            + ")";


    /**
     * DB生成処理など
     */
    public DB(final Context context) {
        mContext = context;
        final OpenHelper openHelper = new OpenHelper(context);
        mSqLiteDatabase = openHelper.getWritableDatabase();
//          mSqLiteDatabase = null;
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        private static final int NEWEST_VERSION = 1;

        public OpenHelper(Context context) {
            super(context, DB_NAME, null, NEWEST_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SERIES_TABLE_SQL);
            db.execSQL(CREATE_BOOK_DETAIL_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public void close() {
        mSqLiteDatabase.close();
        closeDB();
    }
    public Context getContext() {
        return mContext;

    }

    public SQLiteDatabase getSQLiteDatabase() {
        return mSqLiteDatabase;
    }

    public static synchronized void ensureOpenedDB(final Context context) {
        if (mDb == null) {
            mDb = new DB(context);
        }
    }

    /**
     * DB自体を閉じ、インスタンスも破棄する
     */
    private void closeDB() {
        if(mDb != null) {
            final DB opened = mDb;
            mDb = null;
            if(opened != null) opened.close();
        }
    }

    public static DB getInstance(Context context) {
        if (mDb == null) {
            ensureOpenedDB(context);
        }
        return mDb;
    }
}
