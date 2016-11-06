package com.tablecloth.bookshelf.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Basic data management for things related with books
 * All static checking / converting method should be declared in this class
 *
 * Created by Minami on 2016/11/06.
 */
public class BookData {

    public final static int BOOK_SERIES_ERROR_VALUE = -1;
    public final static int BOOK_VOLUME_ERROR_VALUE = -1;

    private final static String TAGS_SEPARATOR_SYMBOL = "\n";

    /**
     * Converts tags list into raw text tags data
     *
     * @param tagsList tags list
     * @return raw text tags data
     */
    @NonNull
    public static String convertTagsList2TagsRawText(@Nullable ArrayList<String> tagsList) {
        StringBuilder rawTagsText = new StringBuilder();
        boolean isFirstValue = true;

        if(tagsList != null && !tagsList.isEmpty()) {
            for (String tag : tagsList) {
                if (isFirstValue) {
                    isFirstValue = false;
                } else {
                    rawTagsText.append(TAGS_SEPARATOR_SYMBOL);
                }
                rawTagsText.append(tag);
            }
        }
        return rawTagsText.toString();
    }

    /**
     * Converts raw text tags data into tags list
     *
     * @param rawTextTagsData raw text tags data
     * @return tags list
     */
    @NonNull
    public static ArrayList<String> convertTagsRawText2TagsList(String rawTextTagsData) {
        // return if tags is empty
        if(Util.isEmpty(rawTextTagsData)) {
            return new ArrayList<>();
        }

        String[] tagsArray = rawTextTagsData.contains(TAGS_SEPARATOR_SYMBOL)
                ? rawTextTagsData.split(TAGS_SEPARATOR_SYMBOL)
                : new String[] {rawTextTagsData};
        return new ArrayList<>(Arrays.asList(tagsArray));
    }

    /**
     * Checks whether given book seriesId is valid
     * This does not check if data exists in DB
     * This only checks whether the given seriesId is legal value or not
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return true if valid seriesId
     */
    public static boolean isValidBookSeriesId(int seriesId) {
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
    public static boolean isValidBookVolume(int bookVolume) {
        return bookVolume >= 0;
    }

}
