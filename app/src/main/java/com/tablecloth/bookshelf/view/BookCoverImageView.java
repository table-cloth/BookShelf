package com.tablecloth.bookshelf.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 書籍の表紙用のImageView。
 * 縦横比率を3:2とする
 * Created by shnomura on 2015/03/15.
 */
public class BookCoverImageView extends ImageView {

    public BookCoverImageView(Context context) {
        super(context);
    }

    public BookCoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookCoverImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, (int)(width * 1.5f));
    }
}
