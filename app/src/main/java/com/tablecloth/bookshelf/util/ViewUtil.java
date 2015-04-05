package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;

/**
 * Created by shnomura on 2015/04/05.
 */
public class ViewUtil {

    public static ViewGroup setTagInfo(Context context, String[] tags, ViewGroup tagContainerView) {
        if(tags == null || tags.length <= 0) return tagContainerView;

        LayoutInflater layoutnflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i = 0 ; i < tags.length ; i ++) {
            ViewGroup tagView = (ViewGroup) layoutnflater.inflate(R.layout.tag_layout, null);
            ((TextView) tagView.findViewById(R.id.tag_name)).setText(tags[i]);
            tagContainerView.addView(tagView);
        }

        return tagContainerView;
    }
}
