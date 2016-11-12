package com.tablecloth.bookshelf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookData;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;

import java.util.ArrayList;

/**
 * Created by Minami on 2015/04/10.
 */
public class RecentTagContainerLayout extends BaseTagContainerLayout {

    public RecentTagContainerLayout(Context context) {
        super(context);
    }

    public RecentTagContainerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecentTagContainerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCurrentTagData(String currentTagData) {
        setTagData(currentTagData);
    }

    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String newTag = (String) view.findViewById(R.id.tag_name).getTag();
            // invalid tag
            if(Util.isEmpty(newTag)) {
                ToastUtil.show(mContext, R.string.tag_error_enter_tag_2_add);
                return;
            }

            ArrayList<String> tagInList = BookData.convertTagsRawText2TagsList(getTagData());


            // tag is already added
            if(tagInList.contains(newTag)) {
                ToastUtil.show(mContext, R.string.tag_error_already_added);
                return;
            }

            // add new tag and update view
            tagInList.add(newTag);
            setTagData(BookData.convertTagsList2TagsRawText(tagInList));
            if(mOnCurrentTagUpdateListener != null) mOnCurrentTagUpdateListener.onUpdate(getTagData());

            // save in DB
            mTagHistoryDao.saveTag(newTag);
        }
    };

    /**
     * Handles all click event within this Activity
     *
     * @param view clicked view
     */
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
        }
    }
}
