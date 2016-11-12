package com.tablecloth.bookshelf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
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
public class CurrentTagContainerLayout extends BaseTagContainerLayout {

    public CurrentTagContainerLayout(Context context) {
        super(context);
    }
    public CurrentTagContainerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CurrentTagContainerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // get tag to delete
            String deleteTag = (String) view.findViewById(R.id.tag_name).getTag();

            ArrayList<String> tagsTmp = BookData.convertTagsRawText2TagsList(getTagData());
            if(!tagsTmp.contains(deleteTag)) {
                ToastUtil.show(mContext, R.string.tag_error_failed_2_delete);
                return;
            }

            // delete tag
            tagsTmp.remove(deleteTag);
            setTagData(BookData.convertTagsList2TagsRawText(tagsTmp));

            // update view
            if(mOnCurrentTagUpdateListener != null) mOnCurrentTagUpdateListener.onUpdate(getTagData());
        }
    };

}
