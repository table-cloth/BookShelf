package com.tablecloth.bookshelf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tablecloth.bookshelf.db.TagHistoryDao;
import com.tablecloth.bookshelf.util.Util;

/**
 * Container view for tags
 *
 * Created by Minami on 2015/04/10.
 */
public class TagContainerLayout extends RelativeLayout {

    protected Context mContext = null;
    private boolean mNeedsReDraw = true;
    private OnClickListener mOnTagClickListener = null;

    public TagContainerLayout(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public TagContainerLayout(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public TagContainerLayout(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
     * Initialize
     *
     * @param context context
     */
    private void initialize(@NonNull Context context) {
        mContext = context;
        setWillNotDraw(false);
        setWillNotCacheDrawing(false);
    }

    /**
     * Set on click listener
     * This listener will be set to all tag views in container
     *
     * @param listener OnClickListener instance
     */
    public void setOnTagClickListener(@NonNull OnClickListener listener) {
        mOnTagClickListener = listener;
    }

    /**
     * Sets flag to re-draw
     *
     * @param flag whether to redraw
     */
    public void setReDrawFlag(boolean flag) {
        mNeedsReDraw = flag;
    }

    /**
     * OnDraw
     *
     * @param canvas canvas to draw
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if(!mNeedsReDraw) {
            super.onDraw(canvas);
            return;
        }
        mNeedsReDraw = false;

        // boundary info
        int maxWidth = this.getWidth();
        int maxChild = this.getChildCount();
        int lineStartIndex = 0;
        int currentWidth = 0;
        int currentHeight = 0;

        ViewGroup tagChild = null;
        for(int i = 0 ; i < maxChild ; i ++) {
            tagChild = (ViewGroup)this.getChildAt(i);
            tagChild.setOnClickListener(mOnTagClickListener);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tagChild.getLayoutParams();

            // If this child view is not the first one in horizontal line,
            // and if this child view does not fit in the remaining space
            // start new line
            if(lineStartIndex != i && currentWidth + tagChild.getWidth() >= maxWidth) {
                lineStartIndex = i;
                currentWidth = 0;
                currentHeight += tagChild.getHeight() + Util.convertDp2Px(mContext, 10);
            }

            params.leftMargin = currentWidth;
            params.topMargin = currentHeight;

            tagChild.setLayoutParams(params);

            // update starting point x
            currentWidth += tagChild.getWidth();
        }

        // Update layout params, since height & other params may have changed
        ViewGroup.LayoutParams parentParams = this.getLayoutParams();
        if(tagChild != null) {
            parentParams.height = currentHeight + tagChild.getHeight() + Util.convertDp2Px(mContext, 10);
        }
        this.setLayoutParams(parentParams);

        // Set the max height as 100dp
        // This 100dp is just set as temporary value
        if(parentParams.height > Util.convertDp2Px(mContext, 100)) {
            ViewGroup ScrollParent = (ViewGroup)(getParent()).getParent();
            ViewGroup.LayoutParams param = ScrollParent.getLayoutParams();
            param.height = Util.convertDp2Px(mContext, 100);
            ScrollParent.setLayoutParams(param);
            ScrollParent.invalidate();
        }
    }
}