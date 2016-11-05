package com.tablecloth.bookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.util.Const;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Data accessor for book volume
 *
 * Created by nomura on 2016/11/05.
 */
public class BookVolumeDao extends BookDaoBase {

    /**
     * Constructor
     *
     * @param context context
     */
    public BookVolumeDao(@NonNull Context context) {
        super(context);
    }

    /**
     * Load volume list registered with given seriesId
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return volume list in given seriesId
     */
    @Nullable
    public ArrayList<Integer> loadBookVolumes(int seriesId) {
        if(!isValidBookSeriesId(seriesId)) {
            return null;
        }

        Cursor cursor = openCursor(SqlText.createLoadBookVolumeSQL(seriesId));
        if(cursor == null) {
            return null;
        }

        try {
            ArrayList<Integer> bookVolumeList = new ArrayList<>();
            for (boolean next = cursor.moveToFirst(); next; next = cursor.moveToNext()) {
                bookVolumeList.add(getIntFromCursor(cursor, Const.DB.BookVolumeDetailTable.SERIES_VOLUME));
            }
            return bookVolumeList;
        } finally {
            closeCursor(cursor);
        }
    }

    /**
     * Saves book volume data
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return volume list in given seriesId
     * @return is save success
     */
    public boolean saveBookVolume(int seriesId, int bookVolume) {
        boolean isUpdate = isBookVolumeRegistered(seriesId, bookVolume);
        ContentValues contentValues = createContentValues4BookVolume(seriesId, bookVolume, isUpdate);

        if(isUpdate) {
            int result = DB.getDB(mContext).getSQLiteDatabase(mContext).update(
                    Const.DB.BookVolumeDetailTable.TABLE_NAME,
                    contentValues,
                    SqlText.createWhereClause4UpdateBookVolume(),
                    new String[] {Integer.toString(seriesId), Integer.toString(bookVolume)});
            return result >= 0;
        } else {
            long result = DB.getDB(mContext).getSQLiteDatabase(mContext).insert(
                    Const.DB.BookVolumeDetailTable.TABLE_NAME, null, contentValues);
            return result != -1L;
        }
    }

    /**
     * Deletes book volume data
     *
     * @param seriesId id for book series. Invalid if <x 0.
     * @param bookVolume volume index for a book. Invalid if < 0.
     * @return is delete success
     */
    public boolean deleteBookVolume(int seriesId, int bookVolume) {
        int result = DB.getDB(mContext).getSQLiteDatabase(mContext).delete(
                Const.DB.BookVolumeDetailTable.TABLE_NAME,
                SqlText.createWhereClause4UpdateBookVolume(),
                new String[] {Integer.toString(seriesId), Integer.toString(bookVolume)});
        return result >= 0;
    }

    /**
     * Create ContentValues for given book volume, in given series id
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @param bookVolume volume index for a book. Invalid if < 0.
     * @param isUpdate whether is updating, or newly adding
     * @return ContentValues instance or null if invalid data is given
     */
    @Nullable
    private ContentValues createContentValues4BookVolume(int seriesId, int bookVolume, boolean isUpdate) {
        // return null if invalid value is given
        if(!isValidBookSeriesId(seriesId)
                || !isValidBookVolume(bookVolume)) {
            return null;
        }

        Calendar now =Calendar.getInstance();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.DB.BookVolumeDetailTable.SERIES_ID, seriesId);
        contentValues.put(Const.DB.BookVolumeDetailTable.SERIES_VOLUME, bookVolume);
        contentValues.put(Const.DB.BookVolumeDetailTable.LAST_UPDATE_UNIX, now.getTimeInMillis());
        if(!isUpdate) {
            contentValues.put(Const.DB.BookVolumeDetailTable.INIT_UPDATE_UNIX, now.getTimeInMillis());
        }
        return contentValues;
    }
}
