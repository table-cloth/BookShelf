package com.tablecloth.bookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.Util;

import java.util.ArrayList;
import java.util.Calendar;

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
            if(cursor.moveToFirst()) {
                SeriesData data = createSeriesDataFromCursor(cursor);
                if(data != null) {
                    data.setVolumeList(mBookVolumeDao.loadBookVolumes(data.getSeriesId()));
                    return data;
                }
            }
        } finally {
            closeCursor(cursor);
        }
        return null;
    }

    /**
     * Load all series data
     *
     * @return list of SeriesData found, or null if not found
     */
    @Nullable
    public ArrayList<SeriesData> loadAllBookSeriesDataList() {
        return loadBookSeriesDataList(G.SEARCH_MODE_ALL, "");
    }

    /**
     * Load all series data related with search mode & search content
     *
     * @param rawSearchMode search mode
     * @param rawSearchText search text
     * @return list of SeriesData found, or null if not found
     */
    @Nullable
    public ArrayList<SeriesData> loadBookSeriesDataList(int rawSearchMode, String rawSearchText) {
        // init search content if invalid
        if(Util.isEmpty(rawSearchText)) {
            rawSearchText = "";
        }

        // split search content by space & double byte space
        String[] searchContent = rawSearchText.split("[ 　]");
        if(Util.isEmpty(searchContent)) {
            searchContent = new String[] {rawSearchText};
        }

        int[] searchMode = rawSearchMode == G.SEARCH_MODE_ALL
                ? new int[] {G.SEARCH_MODE_TITLE, G.SEARCH_MODE_AUTHOR, G.SEARCH_MODE_COMPANY, G.SEARCH_MODE_MAGAZINES , G.SEARCH_MODE_TAG}
                : new int[] {rawSearchMode};

        Cursor cursor = openCursor(SqlText.createSearchBookSeriesSQL(searchMode, searchContent));
        if(cursor == null) {
            return null;
        }

        ArrayList<SeriesData> seriesDataList = new ArrayList<>();
        try {
            for (boolean nextIsAvailable = cursor.moveToFirst(); nextIsAvailable; nextIsAvailable = cursor.moveToNext()) {
                SeriesData data = createSeriesDataFromCursor(cursor);
                if(data != null) {
                    data.setVolumeList(mBookVolumeDao.loadBookVolumes(data.getSeriesId()));
                    seriesDataList.add(data);
                }
            }
        } finally {
            closeCursor(cursor);
        }
        return seriesDataList;
    }

    /**
     * Saves book series data
     *
     * @param seriesData series data to save.
     * @return is save success
     */
    public boolean saveSeries(SeriesData seriesData) {
        if(seriesData == null) {
            return false;
        }

        boolean isUpdate = isBookSeriesRegistered(seriesData.getSeriesId());
        ContentValues contentValues = createContentValues4BookSeries(seriesData, isUpdate);
        if(contentValues == null) {
            return false;
        }

        if(isUpdate) {
            int result = DB.getDB(mContext).getSQLiteDatabase(mContext).update(
                    Const.DB.BookSeriesTable.TABLE_NAME,
                    contentValues,
                    SqlText.createWhereClause4UpdateBookSeries(),
                    new String[]{Integer.toString(seriesData.getSeriesId())});
            return result >= 0;
        } else {
            try {
                long result = DB.getDB(mContext).getSQLiteDatabase(mContext).insertOrThrow(
                        Const.DB.BookSeriesTable.TABLE_NAME, null, contentValues);
                return result != -1L;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Deletes book series data
     *
     * @param seriesId id for book series. Invalid if <x 0.
     * @return is delete success
     */
    public boolean deleteBookSeries(int seriesId) {
        int result = DB.getDB(mContext).getSQLiteDatabase(mContext).delete(
                Const.DB.BookSeriesTable.TABLE_NAME,
                SqlText.createWhereClause4UpdateBookSeries(),
                new String[] {Integer.toString(seriesId)});
        return result >= 0;
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

        // pronunciation info
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

    /**
     * Create ContentValues for given SeriesData
     *
     * @param seriesData SeriesData instance.
     * @param isUpdate whether is updating, or newly adding
     * @return ContentValues instance or null if invalid data is given
     */
    @Nullable
    private ContentValues createContentValues4BookSeries(SeriesData seriesData, boolean isUpdate) {
        // return null if is update & invalid seriesId is given
        if(isUpdate && !isValidBookSeriesId(seriesData.getSeriesId())) {
            return null;
        }

        Calendar now = Calendar.getInstance();
        ContentValues contentValues = new ContentValues();
        // basic info
        if(isUpdate) {
            // add seriesId only when updating, since seriesId is given only after registered to DB
            contentValues.put(Const.DB.BookSeriesTable.SERIES_ID, seriesData.getSeriesId());
        }
        contentValues.put(Const.DB.BookSeriesTable.TITLE_NAME, seriesData.getTitle());
        contentValues.put(Const.DB.BookSeriesTable.AUTHOR_NAME, seriesData.getAuthor());
        contentValues.put(Const.DB.BookSeriesTable.MAGAZINE_NAME, seriesData.getMagazine());
        contentValues.put(Const.DB.BookSeriesTable.COMPANY_NAME, seriesData.getCompany());
        contentValues.put(Const.DB.BookSeriesTable.IMAGE_PATH, seriesData.getImagePath());

        // pronunciation info
        contentValues.put(Const.DB.BookSeriesTable.TITLE_PRONUNCIATION, seriesData.getTitlePronunciation());
        contentValues.put(Const.DB.BookSeriesTable.AUTHOR_PRONUNCIATION, seriesData.getAuthorPronunciation());
        contentValues.put(Const.DB.BookSeriesTable.MAGAZINE_PRONUNCIATION, seriesData.getMagazinePronunciation());
        contentValues.put(Const.DB.BookSeriesTable.COMPANY_PRONUNCIATION, seriesData.getCompanyPronunciation());

        contentValues.put(Const.DB.BookSeriesTable.MEMO, seriesData.getMemo());
        contentValues.put(Const.DB.BookSeriesTable.TAGS, seriesData.getRawTags());
        contentValues.put(Const.DB.BookSeriesTable.SERIES_IS_FINISH, seriesData.isSeriesComplete());
        contentValues.put(Const.DB.BookSeriesTable.LAST_UPDATE_UNIX, now.getTimeInMillis());
        if(!isUpdate) {
            contentValues.put(Const.DB.BookSeriesTable.INIT_UPDATE_UNIX, now.getTimeInMillis());
        }

        return contentValues;
    }
}
