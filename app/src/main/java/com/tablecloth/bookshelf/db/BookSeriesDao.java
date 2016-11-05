package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.util.Const;

/**
 * Data accessor for book series
 *
 * Created by nomura on 2016/11/05.
 */
public class BookSeriesDao extends BookDaoBase {

    /**
     * Constructor
     *
     * @param context context
     */
    public BookSeriesDao(@NonNull Context context) {
        super(context);
    }

    /**
     * Load series data, related to given seriesId
     *
     * @param seriesId id for book series. Invalid if <= 0.
     * @return SeriesData related with seriesId, or null if not found
     */
    @Nullable
    public SeriesData loadSeriesData(int seriesId) {
        if(!isValidBookSeriesId(seriesId)) {
            return null;
        }

        Cursor cursor = openCursor(SqlText.createLoadBookSeriesSQL(seriesId));
        if(cursor == null) {
            return null;
        }

        // TODO return value
        return null;
//        SeriesData data =
    }

    /**
     * Create SeriesData from cursor's current position
     * Cursor will NOT be closed within this method
     *
     * @param cursor cursor
     * @return SeriesData instance, or if cursor is invalid
     */
    @Nullable
    private SeriesData createSeriesDataFromCursor(@Nullable Cursor cursor) {
        if(cursor == null || cursor.isClosed()) {
            return null;
        }

        SeriesData seriesData = new SeriesData(getStringFromCursor(cursor, Const.DB.BookSeriesTable.TITLE_NAME));
        seriesData.mSe
    }

}
