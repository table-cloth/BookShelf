package com.tablecloth.bookshelf.util;

import android.support.annotation.NonNull;

import com.tablecloth.bookshelf.data.BookSeriesData;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Sort related util class
 *
 * Created on 2016/11/20.
 */
public class SortUtil {

    private final static int SORT_TYPE_ID = 0;
    private final static int SORT_TYPE_TITLE = 1;
    private final static int SORT_TYPE_AUTHOR = 2;
    private final static int SORT_TYPE_MAGAZINE = 3;
    private final static int SORT_TYPE_COMPANY = 4;

    /**
     * Get list sorted with Id
     *
     * @param seriesDataList List to sort
     * @return Sorted list
     */
    public static ArrayList<BookSeriesData> sortById(
            @NonNull ArrayList<BookSeriesData> seriesDataList) {
        return sort(seriesDataList, SORT_TYPE_ID);
    }

    /**
     * Get list sorted with Title
     *
     * @param seriesDataList List to sort
     * @return Sorted list
     */
    public static ArrayList<BookSeriesData> sortByTitle(
            @NonNull ArrayList<BookSeriesData> seriesDataList) {
        return sort(seriesDataList, SORT_TYPE_TITLE);
    }

    /**
     * Get list sorted with Author
     *
     * @param seriesDataList List to sort
     * @return Sorted list
     */
    public static ArrayList<BookSeriesData> sortByAuthor(
            @NonNull ArrayList<BookSeriesData> seriesDataList) {
        return sort(seriesDataList, SORT_TYPE_AUTHOR);
    }

    /**
     * Get list sorted with Magazine
     *
     * @param seriesDataList List to sort
     * @return Sorted list
     */
    public static ArrayList<BookSeriesData> sortByMagazine(
            @NonNull ArrayList<BookSeriesData> seriesDataList) {
        return sort(seriesDataList, SORT_TYPE_MAGAZINE);
    }

    /**
     * Get list sorted with Company
     *
     * @param seriesDataList List to sort
     * @return Sorted list
     */
    public static ArrayList<BookSeriesData> sortByCompany(
            @NonNull ArrayList<BookSeriesData> seriesDataList) {
        return sort(seriesDataList, SORT_TYPE_COMPANY);
    }

    /**
     * Get list sorted with given type
     *
     * @param seriesDataList List to sort
     * @param sortType Type to sort. Put SORT_TYPE_xxx
     * @return Sorted list
     */
    private static ArrayList<BookSeriesData> sort(
            @NonNull ArrayList<BookSeriesData> seriesDataList,
            final int sortType) {

        final Collator jaCollator = Collator.getInstance(Locale.JAPANESE);;
        Collections.sort(seriesDataList, new Comparator<BookSeriesData>() {
            @Override
            public int compare(BookSeriesData data1, BookSeriesData data2) {

                String value1;
                String value2;

                switch (sortType) {
                    case SORT_TYPE_ID:
                        value1 = String.valueOf(data1.getSeriesId());
                        value2 = String.valueOf(data2.getSeriesId());
                        break;

                    case SORT_TYPE_TITLE:
                        value1 = String.valueOf(data1.getTitlePronunciation());
                        value2 = String.valueOf(data2.getTitlePronunciation());
                        break;

                    case SORT_TYPE_AUTHOR:
                        value1 = String.valueOf(data1.getAuthorPronunciation());
                        value2 = String.valueOf(data2.getAuthorPronunciation());
                        break;

                    case SORT_TYPE_MAGAZINE:
                        value1 = String.valueOf(data1.getMagazinePronunciation());
                        value2 = String.valueOf(data2.getMagazinePronunciation());
                        break;

                    case SORT_TYPE_COMPANY:
                        value1 = String.valueOf(data1.getCompanyPronunciation());
                        value2 = String.valueOf(data2.getCompanyPronunciation());
                        break;

                    default:
                        value1 = String.valueOf(data1.getSeriesId());
                        value2 = String.valueOf(data2.getSeriesId());
                        break;
                }

                // ones with empty text goes last
                if(Util.isEmpty(value1)) {
                    return 1;
                }
                if(Util.isEmpty(value2)) {
                    return -1;
                }

                // do compare
                return jaCollator.compare(value1, value2);
            }
        });
        return seriesDataList;
    }
}
