package com.tablecloth.bookshelf.data;

import android.support.annotation.NonNull;

/**
 * Simple data with a singe value & id
 * Commonly used for filtering or sorting
 *
 * Created on 2016/11/13.
 */
public class SimpleData {

    private final int mId;
    private final String mValue;

    /**
     * Constructor
     *
     * @param id id
     * @param value text value
     */
    public SimpleData(int id, @NonNull String value) {
        mId = id;
        mValue =value;
    }

    /**
     * Gets id
     *
     * @return id
     */
    public int getId() {
        return mId;
    }

    /**
     * Gets value
     *
     * @return value
     */
    public String getValue() {
        return mValue;
    }
}
