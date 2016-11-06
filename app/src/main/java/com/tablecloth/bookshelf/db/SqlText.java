package com.tablecloth.bookshelf.db;

import android.text.TextUtils;

import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.Util;

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

    // Create SQLite text for registering Tags History table
    public static final String CREATE_TAGS_SQL = "create table "
            + Const.DB.TagHistoryTable.TABLE_NAME
            + " ( "
            + Const.DB.TagHistoryTable.TAG_NAME
            + " text not null primary key, "
            + Const.DB.TagHistoryTable.LAST_UPDATE
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
    public static String createLoadSettingsSQL(String key) {
        return " SELECT * FROM "
                + Const.DB.Settings.SettingsTable.TABLE_NAME
                + " WHERE "
                + Const.DB.Settings.SettingsTable.KEY
                + " = '"
                + key
                + "'";
    }

    /**
     * Create SQLite text for loading book series for given seriesId
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return SQLite text
     */
    public static String createLoadBookSeriesSQL(int seriesId) {
        return "SELECT * FROM "
                + Const.DB.BookSeriesTable.TABLE_NAME
                + " WHERE "
                + Const.DB.BookSeriesTable.SERIES_ID
                + " = "
                + seriesId;
    }

    /**
     * Create SQLite text for loading book series that matches given data
     *
     * @param searchMode search mode
     * @param searchContent search text content
     * @return SQLite text
     */
    public static String createSearchBookSeriesSQL(int[] searchMode, String[] searchContent) {
        StringBuilder sql = new StringBuilder()
                .append("SELECT * FROM ")
                .append(Const.DB.BookSeriesTable.TABLE_NAME);

        // if no search content is given, do not add where statements
        if(searchContent == null
                || searchContent.length <= 0) {
            return sql.toString();
        }

        // loop each search word
        boolean isFirstSearchWord = true;
        for(String searchWord : searchContent) {
            // if search word is invalid, continue without any action
            if(Util.isEmpty(searchWord)) {
                continue;
            }

            // add AND clause after first search word
            if(isFirstSearchWord) {
                isFirstSearchWord = false;
                sql.append(" WHERE ");
            } else {
                sql.append(" AND ");
            }

            // start next search word block
            sql.append(" ( ");

            // loop each search mode
            boolean isFirstMode = true;
            for(int mode : searchMode) {
                // add OR clause after first mode
                if(isFirstMode) {
                    isFirstMode = false;
                } else {
                    sql.append(" OR ");
                }

                switch(mode) {
                    case G.SEARCH_MODE_TITLE:
                        sql.append(Const.DB.BookSeriesTable.TITLE_NAME);
                        break;
                    case G.SEARCH_MODE_AUTHOR:
                        sql.append(Const.DB.BookSeriesTable.AUTHOR_NAME);
                        break;
                    case G.SEARCH_MODE_COMPANY:
                        sql.append(Const.DB.BookSeriesTable.COMPANY_NAME);
                        break;
                    case G.SEARCH_MODE_MAGAZINES:
                        sql.append(Const.DB.BookSeriesTable.MAGAZINE_NAME);
                        break;
                    case G.SEARCH_MODE_TAG:
                        sql.append(Const.DB.BookSeriesTable.TAGS);
                        break;
                    // safety net to avoid illegal sql
                    // will check title, since it is the most basic search mode
                    default:
                        sql.append(Const.DB.BookSeriesTable.TITLE_NAME);
                        break;
                }

                sql.append(" LIKE '%");
                sql.append(searchWord);
                sql.append("%' ");
            }

            // close search word block
            sql.append(" ) ");
        }
        return sql.toString();
    }



    /**
     * Create SQLite text for loading book volume in given seriesId
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return SQLite text
     */
    public static String createLoadBookVolumeSQL(int seriesId) {
        return "SELECT * FROM "
                + Const.DB.BookVolumeDetailTable.TABLE_NAME
                + " WHERE "
                + Const.DB.BookVolumeDetailTable.SERIES_ID
                + " = "
                + seriesId;
    }


    /**
     * Create SQLite text for loading book volume in given seriesId
     *
     * @param seriesId id for book series. Invalid if <x 0.
     * @param volume volume index for a book. Invalid if < 0.
     * @return SQLite text
     */
    public static String createLoadBookVolumeSQL(int seriesId, int volume) {
        return "SELECT * FROM "
                + Const.DB.BookVolumeDetailTable.TABLE_NAME
                + " WHERE "
                + Const.DB.BookVolumeDetailTable.SERIES_ID
                + " = "
                + seriesId
                + " AND "
                + Const.DB.BookVolumeDetailTable.SERIES_VOLUME
                + " = "
                + volume;
    }

    /**
     * Create SQLite text for loading given tag
     *
     * @param tag tag
     * @return SQLite text
     */
    public static String createLoadTagSQL(String tag) {
        return "SELECT * FROM "
                + Const.DB.TagHistoryTable.TABLE_NAME
                + " WHERE "
                + Const.DB.TagHistoryTable.TAG_NAME
                + " = "
                + "'"
                + tag
                + "'";
    }

    /**
     * Create SQLite text for loading all tags
     *
     * @return SQLite text
     */
    public static String createLoadAllTagsSQL() {
        return "SELECT * FROM "
                + Const.DB.TagHistoryTable.TABLE_NAME
                + " ORDER BY "
                + Const.DB.TagHistoryTable.LAST_UPDATE
                + " DESC ";
    }

    /**
     * Create where clause for updating book series
     * Expected where args are
     * 1. seriesId
     *
     * @return WhereClause
     */
    public static String createWhereClause4UpdateBookSeries() {
        return Const.DB.BookSeriesTable.SERIES_ID
                + " = ? ";
    }

    /**
     * Create where clause for updating book volume
     * Expected where args are
     * 1. seriesId
     * 2. bookVolume
     *
     * @return WhereClause
     */
    public static String createWhereClause4UpdateBookVolume() {
        return Const.DB.BookVolumeDetailTable.SERIES_ID
                + " = ? "
                + " AND "
                + Const.DB.BookVolumeDetailTable.SERIES_VOLUME
                + " = ? ";
    }

    /**
     * Create where clause for updating tag
     * Expected where args are
     * 1. tag
     *
     * @return WhereClause
     */
    public static String createWhereClause4UpdateTag() {
        return Const.DB.TagHistoryTable.TAG_NAME
                + " = ? ";
    }
}
