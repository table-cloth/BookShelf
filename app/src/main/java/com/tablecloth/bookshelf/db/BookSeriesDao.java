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

    private BookVolumeDao mBookVolumeDao;

    /**
     * Constructor
     *
     * @param context context
     */
    public BookSeriesDao(@NonNull Context context) {
        super(context);

        mBookVolumeDao = new BookVolumeDao(context);
    }

    /**
     * Load series data, related to given seriesId
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return SeriesData related with seriesId, or null if not found
     */
    @Nullable
    public SeriesData loadBookSeriesData(int seriesId) {
        if(!isValidBookSeriesId(seriesId)) {
            return null;
        }

        Cursor cursor = openCursor(SqlText.createLoadBookSeriesSQL(seriesId));
        if(cursor == null) {
            return null;
        }

        try {
            SeriesData data = createSeriesDataFromCursor(cursor);
            data.setVolumeList(mBookVolumeDao.loadBookVolumes(data.getSeriesId()));
            return data;
        } finally {
            closeCursor(cursor);
        }
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

        SeriesData data = new SeriesData(mContext);
        // basic info
        data.setTitle(getStringFromCursor(cursor, Const.DB.BookSeriesTable.TITLE_NAME));
        data.setAuthor(getStringFromCursor(cursor, Const.DB.BookSeriesTable.AUTHOR_NAME));
        data.setMagazine(getStringFromCursor(cursor, Const.DB.BookSeriesTable.MAGAZINE_NAME));
        data.setCompany(getStringFromCursor(cursor, Const.DB.BookSeriesTable.COMPANY_NAME));
        data.setImagePath(getStringFromCursor(cursor, Const.DB.BookSeriesTable.IMAGE_PATH));

        // pronounciation info
        data.setTitlePronunciation(getStringFromCursor(cursor, Const.DB.BookSeriesTable.TITLE_PRONUNCIATION));
        data.setAuthorPronunciation(getStringFromCursor(cursor, Const.DB.BookSeriesTable.AUTHOR_PRONUNCIATION));
        data.setMagazinePronunciation(getStringFromCursor(cursor, Const.DB.BookSeriesTable.MAGAZINE_PRONUNCIATION));
        data.setCompanyPronunciation(getStringFromCursor(cursor, Const.DB.BookSeriesTable.COMPANY_PRONUNCIATION));

        // additional info
        data.setMemo(getStringFromCursor(cursor, Const.DB.BookSeriesTable.MEMO));
        data.setRawTags(getStringFromCursor(cursor, Const.DB.BookSeriesTable.TAGS));
        // series not complete if value is 0
        data.setSeriesComplete(!(0== getIntFromCursor(cursor, Const.DB.BookSeriesTable.SERIES_IS_FINISH)));
        data.setInitUpdateUnix(getLongFromCursor(cursor, Const.DB.BookSeriesTable.INIT_UPDATE_UNIX));
        data.setLastUpdateUnix(getLongFromCursor(cursor, Const.DB.BookSeriesTable.LAST_UPDATE_UNIX));

        return data;
    }

}
