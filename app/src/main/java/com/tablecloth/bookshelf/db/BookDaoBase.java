package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tablecloth.bookshelf.data.BookData;

/**
 * Data accessor for getting information of Books
 * This class should only read data, and not set data
 *
 * Created by Minami on 2016/11/04.
 */
public class BookDaoBase extends DaoBase {

    /**
     * Constructor
     *
     * @param context context
     */
    public BookDaoBase(@NonNull Context context) {
        super(context);
    }

    /**
     * Checks whether book series with given seriesId is already registered
     *
     * @param seriesId id for book series. Invalid id if <= 0.
     * @return true if already registered
     */
    public boolean isBookSeriesRegistered(int seriesId) {
        return BookData.isValidBookSeriesId(seriesId)
                && isDataAvailable(SqlText.createLoadBookSeriesSQL(seriesId));
    }

    /**
     * Checks whether book volume with  given seriesId is already registered
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @param volume volume index for a book. Invalid if < 0.
     * @return true if already registered
     */
    public boolean isBookVolumeRegistered(int seriesId, int volume)
    {
        return BookData.isValidBookSeriesId(seriesId)
                && BookData.isValidBookVolume(volume)
                && isDataAvailable(SqlText.createLoadBookVolumeSQL(seriesId, volume));
    }





}
