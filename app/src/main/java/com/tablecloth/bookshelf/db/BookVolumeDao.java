package com.tablecloth.bookshelf.db;

import android.content.Context;
import android.support.annotation.NonNull;

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
}
