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
}
