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
 * Base class for tag container view
 *
 * Created by Minami on 2015/04/10.
 */
public class BaseTagContainerLayout extends RelativeLayout implements View.OnClickListener {

    protected Context mContext = null;
    protected OnCurrentTagUpdateListener mOnCurrentTagUpdateListener = null;
    protected TagHistoryDao mTagHistoryDao;

    private String mTagData = null;
    private boolean mNeedsReDraw = true;

    public BaseTagContainerLayout(Context context) {
        super(context);
        initialize(context);
    }

    public BaseTagContainerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public BaseTagContainerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(@NonNull Context context) {
        mContext = context;
        mTagHistoryDao = new TagHistoryDao(mContext);

        setWillNotDraw(false);
        setWillNotCacheDrawing(false);
    }

    public void setTagData(@Nullable String tagData) {
        mTagData = tagData;
    }

    @Nullable
    public String getTagData() {
        return mTagData;
    }

    /**
     * Sets flag to re-draw
     *
     * @param flag whether to redraw
     */
    public void setReDrawFlag(boolean flag) {
        mNeedsReDraw = flag;
    }


    // 描画関数
    @Override
    protected void onDraw(Canvas canvas) {
        if(!mNeedsReDraw) {
            super.onDraw(canvas);
            return;
        }

        // 描画領域の情報を取得
        int maxWidth = this.getWidth();
        int maxChild = this.getChildCount();
        int lineStartIndex = 0;
        int currentWidth = 0;
        int measuredWidth = 0;
        int currentHeight = 0;
        ViewGroup tagChild = null;

        for(int i = 0 ; i < maxChild ; i ++) {
            try {
                tagChild = (ViewGroup)this.getChildAt(i);
                tagChild.setVisibility(View.VISIBLE);

                // タップによるタグの登録処理
//                tagChild.setOnClickListener(this);

                // ビューの設定
                measuredWidth += tagChild.getWidth();

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tagChild.getLayoutParams();
                // その行の１つ目 or 横幅に余裕がある場合
                if(lineStartIndex != i && measuredWidth + tagChild.getWidth() >= maxWidth) {
                    lineStartIndex = i;
                    currentWidth = 0;
                    measuredWidth = 0;
                    currentHeight += tagChild.getHeight() + Util.convertDp2Px(mContext, 10);
                }
                params.leftMargin = currentWidth;
                params.topMargin = currentHeight;

                tagChild.setLayoutParams(params);

                currentWidth += tagChild.getWidth();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // レイアウト生成完了時のリスナーを登録し、再描画を行う
        ViewGroup.LayoutParams parentParams = this.getLayoutParams();
        if(tagChild != null) {
            parentParams.height = currentHeight + tagChild.getHeight() + Util.convertDp2Px(mContext, 10);
        }
        this.setLayoutParams(parentParams);

        // TODO check what this does
        if(parentParams.height > Util.convertDp2Px(mContext, 100)) {
            ViewGroup ScrollParent = (ViewGroup)(getParent()).getParent();
            ViewGroup.LayoutParams param = ScrollParent.getLayoutParams();
            param.height = Util.convertDp2Px(mContext, 100);
            ScrollParent.setLayoutParams(param);
            ScrollParent.invalidate();
        }

        mNeedsReDraw = false;
    }

    /**
     * Handles all click event within this Activity
     *
     * @param view clicked view
     */
    @Override
    public void onClick(View view) {
        // to be declared in derived class
    }

    public interface OnCurrentTagUpdateListener {
        void onUpdate(String currentTags);
    }

    public void setTagUpdateListener(OnCurrentTagUpdateListener listener) {
        mOnCurrentTagUpdateListener = listener;
    }

}