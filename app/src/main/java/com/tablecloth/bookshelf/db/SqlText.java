package com.tablecloth.bookshelf.db;

import com.tablecloth.bookshelf.util.Const;

/**
 * Created by nomura on 2016/11/04.
 */

public class SqlText {

    // Create SQLite text for registering Book Series table
    public final static String CREATE_SERIES_TABLE_SQL = "create table "
            + Const.DB.BookSeriesTable.TABLE_NAME
            + " ( "
            + Const.DB.BookSeriesTable.SERIES_ID
            + " integer not null primary key autoincrement,"
            + Const.DB.BookSeriesTable.TITLE_NAME
            + " text not null default '',"
            + Const.DB.BookSeriesTable.TITLE_PRONUNCIATION
            + " text, "
            + Const.DB.BookSeriesTable.AUTHOR_NAME
            + " text, "
            + Const.DB.BookSeriesTable.AUTHOR_PRONUNCIATION
            + " text, "
            + Const.DB.BookSeriesTable.COMPANY_NAME
            + " text, "
            + Const.DB.BookSeriesTable.COMPANY_PRONUNCIATION
            + " text, "
            + Const.DB.BookSeriesTable.MAGAZINE_NAME
            + " text, "
            + Const.DB.BookSeriesTable.MAGAZINE_PRONUNCIATION
            + " text, "
            + Const.DB.BookSeriesTable.IMAGE_PATH
            + " text, "
            + Const.DB.BookSeriesTable.TAGS
            + " text, "
            + Const.DB.BookSeriesTable.MEMO
            + " text, "
            + Const.DB.BookSeriesTable.SERIES_IS_FINISH
            + " integer, "
            + Const.DB.BookSeriesTable.INIT_UPDATE_UNIX
            + " integer, "
            + Const.DB.BookSeriesTable.LAST_UPDATE_UNIX
            + " integer "
            + ")";

    // Create SQLite text for registering Book Detail table
    public final static String CREATE_BOOK_DETAIL_SQL = "create table "
            + Const.DB.BookVolumeDetailTable.TABLE_NAME
            + " ( "
            + Const.DB.BookVolumeDetailTable.SERIES_ID
            + " integer not null,"
            + Const.DB.BookVolumeDetailTable.SERIES_VOLUME
            + " integer not null, "
            + Const.DB.BookVolumeDetailTable.INIT_UPDATE_UNIX
            + " integer, "
            + Const.DB.BookVolumeDetailTable.LAST_UPDATE_UNIX
            + " integer "
            + ")";

    // Create SQLite text for registering Book Series Tags table
    public static final String CREATE_TAGS_SQL = "create table "
            + Const.DB.BookSeriesTagsTable.TABLE_NAME
            + " ( "
            + Const.DB.BookSeriesTagsTable.TAG_NAME
            + " text not null primary key, "
            + Const.DB.BookSeriesTagsTable.LAST_UPDATE
            + " integer not null "
            + " ) ";

    // Create SQLite text for Application Settings table
    public final static String CREATE_SETTINGS_SQL = "create table "
            + Const.DB.Settings.SettingsTable.TABLE_NAME
            + " ( "
            + Const.DB.Settings.SettingsTable.KEY
            + " text not null,"
            + Const.DB.Settings.SettingsTable.VALUE
            + " text "
            + ")";

    /**
     * Create SQLite text for loading application settings
     *
     * @param key Key of setting
     * @return SQLite text
     */
    public static String loadSettingsSQL(String key) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM ");
        sql.append(Const.DB.Settings.SettingsTable.TABLE_NAME);
        sql.append(" WHERE " + Const.DB.Settings.SettingsTable.KEY + " = " + "'" + key + "'");
        return sql.toString();
    }

}
