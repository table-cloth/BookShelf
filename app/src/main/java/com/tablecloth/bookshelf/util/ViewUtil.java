package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;

import java.util.ArrayList;

/**
 * Created by Minami on 2015/04/05.
 */
public class ViewUtil {

    public static ViewGroup setTagInfoNormal(Context context, ArrayList<String> tags, ViewGroup tagContainerView) {
        if(tags == null || tags.size() <= 0) return tagContainerView;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i = 0 ; i < tags.size() ; i ++) {
            ViewGroup tagView = (ViewGroup) layoutInflater.inflate(R.layout.tag_layout_normal, null);
            ((TextView) tagView.findViewById(R.id.tag_name)).setText(tags.get(i));
            tagContainerView.addView(tagView);
        }

        return tagContainerView;
    }
    public static ViewGroup setTagInfoSmall(Context context, ArrayList<String> tags, ViewGroup tagContainerView) {
        if(tags == null || tags.size() <= 0) return tagContainerView;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i = 0 ; i < tags.size() ; i ++) {
            ViewGroup tagView = (ViewGroup) layoutInflater.inflate(R.layout.tag_layout_small, null);
            ((TextView) tagView.findViewById(R.id.tag_name)).setText(tags.get(i));
            tagContainerView.addView(tagView);
        }

        return tagContainerView;
    }

    public static ViewGroup setTagInfoLarge(Context context, ArrayList<String> tags, ViewGroup tagContainerView, int visibility) {
        if(tags == null || tags.size() <= 0) return tagContainerView;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i = 0 ; i < tags.size() ; i ++) {
            ViewGroup tagView = (ViewGroup) layoutInflater.inflate(R.layout.tag_layout_large, null);
            tagView.setVisibility(visibility);
            ((TextView) tagView.findViewById(R.id.tag_name)).setText(tags.get(i));
            tagContainerView.addView(tagView);
        }

        return tagContainerView;
    }
    public static ViewGroup setTagInfoLargeDelete(Context context, ArrayList<String> tags, ViewGroup tagContainerView, int visibility) {
        if(tags == null || tags.size() <= 0) return tagContainerView;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i = 0 ; i < tags.size() ; i ++) {
            ViewGroup tagView = (ViewGroup) layoutInflater.inflate(R.layout.tag_layout_large_delete, null);
            tagView.setVisibility(visibility);
            ((TextView) tagView.findViewById(R.id.tag_name)).setText(tags.get(i));
            tagContainerView.addView(tagView);
        }

        return tagContainerView;
    }

    public static ArrayList<ViewGroup> getTagViewNormal(Context context, ArrayList<String> tags, ViewGroup tagContainerView) {
        ArrayList<ViewGroup> ret = new ArrayList<>();
        if(tags == null || tags.size() <= 0) return ret;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i = 0 ; i < tags.size() ; i ++) {
            ViewGroup tagView = (ViewGroup) layoutInflater.inflate(R.layout.tag_layout_normal, null);
            ((TextView) tagView.findViewById(R.id.tag_name)).setText(tags.get(i));
            ret.add(tagView);
        }
        return ret;
    }
}
