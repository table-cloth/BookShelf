package com.tablecloth.bookshelf.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.data.BookSeriesData;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Util clas for sorting collections
 *
 * Created on 2016/11/13.
 */

public class SortUtil {

    @NonNull
    public static void sort(@Nullable ArrayList<BookSeriesData> sortTarget, int sortType) {
        if(Util.isEmpty(sortTarget)) {
            return;
        }

        final Collator collator = Collator.getInstance(Locale.JAPANESE);
        Collections.sort(sortTarget, new Comparator<BookSeriesData>() {
            @Override
            public int compare(BookSeriesData data1, BookSeriesData data2) {
                return collator.compare(data1.getTitle(), data2.getTitle());
            }
        });

    }
}
