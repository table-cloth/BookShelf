package com.tablecloth.bookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.data.BookData;
import com.tablecloth.bookshelf.data.BookSeriesData;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.Util;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Data accessor for book series
 *
 * Created by Minami on 2016/11/05.
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
     * @return BookSeriesData related with seriesId, or null if not found
     */
    @Nullable
    public BookSeriesData loadBookSeriesData(int seriesId) {
        if(!BookData.isValidBookSeriesId(seriesId)) {
            return null;
        }

        Cursor cursor = openCursor(SqlText.createLoadBookSeriesSQL(seriesId));
        if(cursor == null) {
            return null;
        }

        try {
            if(cursor.moveToFirst()) {
                BookSeriesData data = createSeriesDataFromCursor(cursor);
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
     * @return list of BookSeriesData found, or null if not found
     */
    @Nullable
    public ArrayList<BookSeriesData> loadAllBookSeriesDataList() {
        return loadBookSeriesDataList(Const.SEARCH_MODE.ALL, "");
    }

    /**
     * Load all series data related with search mode & search content
     *
     * @param rawSearchMode search mode
     * @param rawSearchText search text
     * @return list of BookSeriesData found, or null if not found
     */
    @Nullable
    public ArrayList<BookSeriesData> loadBookSeriesDataList(int rawSearchMode, String rawSearchText) {
        // init search content if invalid
        if(Util.isEmpty(rawSearchText)) {
            rawSearchText = "";
        }

        // split search content by space & double byte space
        String[] searchContent = rawSearchText.split("[ 　]");
        if(Util.isEmpty(searchContent)) {
            searchContent = new String[] {rawSearchText};
        }

        int[] searchMode = rawSearchMode == Const.SEARCH_MODE.ALL
                ? new int[] {Const.SEARCH_MODE.TITLE, Const.SEARCH_MODE.AUTHOR, Const.SEARCH_MODE.COMPANY, Const.SEARCH_MODE.MAGAZINE , Const.SEARCH_MODE.TAG}
                : new int[] {rawSearchMode};

        Cursor cursor = openCursor(SqlText.createSearchBookSeriesSQL(searchMode, searchContent));
        if(cursor == null) {
            return null;
        }

        ArrayList<BookSeriesData> bookSeriesDataList = new ArrayList<>();
        try {
            for (boolean nextIsAvailable = cursor.moveToFirst(); nextIsAvailable; nextIsAvailable = cursor.moveToNext()) {
                BookSeriesData data = createSeriesDataFromCursor(cursor);
                if(data != null) {
                    data.setVolumeList(mBookVolumeDao.loadBookVolumes(data.getSeriesId()));
                    bookSeriesDataList.add(data);
                }
            }
        } finally {
            closeCursor(cursor);
        }
        return bookSeriesDataList;
    }

    /**
     * Saves book series data
     *
     * @param bookSeriesData series data to saveInt.
     * @return is saveInt success
     */
    public boolean saveSeries(BookSeriesData bookSeriesData) {
        if(bookSeriesData == null) {
            return false;
        }

        boolean isUpdate = isBookSeriesRegistered(bookSeriesData.getSeriesId());
        ContentValues contentValues = createContentValues4BookSeries(bookSeriesData, isUpdate);
        if(contentValues == null) {
            return false;
        }

        if(isUpdate) {
            int result = DB.getDB(mContext).getSQLiteDatabase(mContext).update(
                    Const.DB.BookSeriesTable.TABLE_NAME,
                    contentValues,
                    SqlText.createWhereClause4UpdateBookSeries(),
                    new String[]{Integer.toString(bookSeriesData.getSeriesId())});
            return result >= 0;
        } else {
            long result = DB.getDB(mContext).getSQLiteDatabase(mContext).insert(
                    Const.DB.BookSeriesTable.TABLE_NAME, null, contentValues);
            return result != -1L;
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
     * Create BookSeriesData from cursor's current position
     * Cursor will NOT be closed within this method
     *
     * @param cursor cursor
     * @return BookSeriesData instance, or if cursor is invalid
     */
    @Nullable
    private BookSeriesData createSeriesDataFromCursor(@Nullable Cursor cursor) {
        if(cursor == null || cursor.isClosed()) {
            return null;
        }

        BookSeriesData data = new BookSeriesData(mContext);
        // basic info
        data.setSeriesId(getIntFromCursor(cursor, Const.DB.BookSeriesTable.SERIES_ID));
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
     * Create ContentValues for given BookSeriesData
     *
     * @param bookSeriesData BookSeriesData instance.
     * @param isUpdate whether is updating, or newly adding
     * @return ContentValues instance or null if invalid data is given
     */
    @Nullable
    private ContentValues createContentValues4BookSeries(BookSeriesData bookSeriesData, boolean isUpdate) {
        // return null if is update & invalid seriesId is given
        if(isUpdate && !BookData.isValidBookSeriesId(bookSeriesData.getSeriesId())) {
            return null;
        }

        Calendar now = Calendar.getInstance();
        ContentValues contentValues = new ContentValues();
        // basic info
        if(isUpdate) {
            // add seriesId only when updating, since seriesId is given only after registered to DB
            contentValues.put(Const.DB.BookSeriesTable.SERIES_ID, bookSeriesData.getSeriesId());
        }
        contentValues.put(Const.DB.BookSeriesTable.TITLE_NAME, bookSeriesData.getTitle());
        contentValues.put(Const.DB.BookSeriesTable.AUTHOR_NAME, bookSeriesData.getAuthor());
        contentValues.put(Const.DB.BookSeriesTable.MAGAZINE_NAME, bookSeriesData.getMagazine());
        contentValues.put(Const.DB.BookSeriesTable.COMPANY_NAME, bookSeriesData.getCompany());
        contentValues.put(Const.DB.BookSeriesTable.IMAGE_PATH, bookSeriesData.getImagePath());

        // pronunciation info
        contentValues.put(Const.DB.BookSeriesTable.TITLE_PRONUNCIATION, bookSeriesData.getTitlePronunciation());
        contentValues.put(Const.DB.BookSeriesTable.AUTHOR_PRONUNCIATION, bookSeriesData.getAuthorPronunciation());
        contentValues.put(Const.DB.BookSeriesTable.MAGAZINE_PRONUNCIATION, bookSeriesData.getMagazinePronunciation());
        contentValues.put(Const.DB.BookSeriesTable.COMPANY_PRONUNCIATION, bookSeriesData.getCompanyPronunciation());

        contentValues.put(Const.DB.BookSeriesTable.MEMO, bookSeriesData.getMemo());
        contentValues.put(Const.DB.BookSeriesTable.TAGS, bookSeriesData.getRawTags());
        contentValues.put(Const.DB.BookSeriesTable.SERIES_IS_FINISH, bookSeriesData.isSeriesComplete());
        contentValues.put(Const.DB.BookSeriesTable.LAST_UPDATE_UNIX, now.getTimeInMillis());
        if(!isUpdate) {
            contentValues.put(Const.DB.BookSeriesTable.INIT_UPDATE_UNIX, now.getTimeInMillis());
        }

        return contentValues;
    }
}
