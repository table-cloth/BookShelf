package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;

import java.util.ArrayList;

/**
 * View related Util class.
 * Class for setting up & generating views
 * 
 * Created by Minami on 2015/04/05.
 */
public class ViewUtil {

    public static final int TAGS_LAYOUT_TYPE_SMALL = 0;
    public static final int TAGS_LAYOUT_TYPE_NORMAL = 1;
    public static final int TAGS_LAYOUT_TYPE_LARGE = 2;
    public static final int TAGS_LAYOUT_TYPE_LARGE_WITH_DELETE = 3;

    /**
     * Get tags view list from given tags text list
     *
     * @param context context
     * @param tags list of tag strings
     * @param tagLayoutType TAG_LAYOUT_TYPE_xxx
     * @return list of tag views
     */
    @NonNull
    public static ArrayList<ViewGroup> getTagViewList(@NonNull Context context, @Nullable ArrayList<String> tags, int tagLayoutType) {
        ArrayList<ViewGroup> tagViewList = new ArrayList<>();
        if(Util.isEmpty(tags)) {
            return tagViewList;
        }

        LayoutInflater inflater =  getLayoutInflater(context);
        int layoutId = getLayoutId4TagView(tagLayoutType);

        for(String tag : tags) {
            ViewGroup tagView = (ViewGroup)inflater.inflate(layoutId, null);

            TextView tagTextView = (TextView)tagView.findViewById(R.id.tag_name);
            tagTextView.setText(tag);
            tagTextView.setTag(tag);

            tagViewList.add(tagView);
        }
    }

    /**
     * Get Layout Inflater instance from given context
     *
     * @param context context
     * @return LayoutInflater instance
     */
    private static LayoutInflater getLayoutInflater(@NonNull Context context) {
        return (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Get layoutId for given tag view type
     *
     * @param tagViewType TAG_LAYOUT_TYPE_xxx
     * @return layoutId for given tag type
     */
    private static int getLayoutId4TagView(int tagViewType) {
        switch (tagViewType) {
            case TAGS_LAYOUT_TYPE_SMALL:
                return R.layout.tag_layout_small;
            case TAGS_LAYOUT_TYPE_LARGE:
                return R.layout.tag_layout_large;
            case TAGS_LAYOUT_TYPE_LARGE_WITH_DELETE:
                return R.layout.tag_layout_large_delete;
            case TAGS_LAYOUT_TYPE_NORMAL:
            default:
                return R.layout.tag_layout_normal;

        }
    }
}
