package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.support.annotation.NonNull;

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
        return isValidBookSeriesId(seriesId)
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
        return isValidBookSeriesId(seriesId)
                && isValidBookVolume(volume)
                && isDataAvailable(SqlText.createLoadBookVolumeSQL(seriesId, volume));
    }

    /**
     * Checks whether given book seriesId is valid
     * This does not check if data exists in DB
     * This only checks whether the given seriesId is legal value or not
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return true if valid seriesId
     */
    protected boolean isValidBookSeriesId(int seriesId) {
        return seriesId >= 0;
    }

    /**
     * Checks whether given book volume is valid
     * This does not check if data exists in DB
     * This only checks whether the given volume is legal value or not
     *
     * @param bookVolume volume of a book in a series. Invalid if < 0.
     * @return true if valid book volume
     */
    protected boolean isValidBookVolume(int bookVolume) {
        return bookVolume >= 0;
    }




}
