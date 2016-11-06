package com.tablecloth.bookshelf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tablecloth.bookshelf.db.TagHistoryDao;
import com.tablecloth.bookshelf.util.Util;

/**
 * Created by Minami on 2015/04/10.
 */
public class BaseTagRelativeLayout extends RelativeLayout {
    private String mTagData = null;
    protected Context mContext = null;
    protected boolean mNeedsReLayout = true;
    protected OnCurrentTagUpdateListener mOnCurrentTagUpdateListener = null;
    protected TagHistoryDao mTagHistoryDao;

    public BaseTagRelativeLayout(Context context) {
        super(context);
        initialize(context);
        setWillNotDraw(false);
        setWillNotCacheDrawing(false);
    }

    public BaseTagRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
        setWillNotDraw(false);
        setWillNotCacheDrawing(false);
    }

    public BaseTagRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
        setWillNotDraw(false);
        setWillNotCacheDrawing(false);
    }

    private void initialize(Context context) {
        mContext = context;
        mTagHistoryDao = new TagHistoryDao(mContext);
    }


    public void setTagData(String tagData) {
        mTagData = tagData;
    }

    public String getTagData() {
        if (mTagData == null) return "";
        return mTagData;
    }

    // 描画関数
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setReLayoutFlag(boolean flag) {
        mNeedsReLayout = flag;
    }

    public interface OnCurrentTagUpdateListener {
        void onUpdate(String currentTags);
    }

    public void setTagUpdateListener(OnCurrentTagUpdateListener listener) {
        mOnCurrentTagUpdateListener = listener;
    }

}