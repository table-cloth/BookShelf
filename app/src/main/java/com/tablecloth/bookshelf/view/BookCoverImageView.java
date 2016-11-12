package com.tablecloth.bookshelf.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Extended image view for book series cover
 * Ratio must be always 3:2
 *
 * Created by Minami on 2015/03/15.
 */
public class BookCoverImageView extends ImageView {

    public BookCoverImageView(@NonNull Context context) {
        super(context);
    }

    public BookCoverImageView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public BookCoverImageView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Measure size and adjust to 3:2 ratio
     * @param widthMeasureSpec width measure spec
     * @param heightMeasureSpec height measure spec
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, (int)(width * 1.5f));
    }
}
