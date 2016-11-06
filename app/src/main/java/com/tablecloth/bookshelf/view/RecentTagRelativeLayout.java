package com.tablecloth.bookshelf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookData;
import com.tablecloth.bookshelf.data.SeriesData;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;

import java.util.ArrayList;

/**
 * Created by Minami on 2015/04/10.
 */
public class RecentTagRelativeLayout extends BaseTagRelativeLayout {

    public RecentTagRelativeLayout(Context context) {
        super(context);
    }

    public RecentTagRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecentTagRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCurrentTagData(String currentTagData) {
        setTagData(currentTagData);
    }

    /**
     * Draw method
     *
     * @param canvas canvas to draw
     */
    @Override
    protected void onDraw(Canvas canvas) {

        if(!mNeedsReLayout) {
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
                tagChild.setOnClickListener(onClick);

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

        mNeedsReLayout = false;

//        super.onDraw(canvas);
    }

    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String newTag = ((TextView)view.findViewById(R.id.tag_name)).getText().toString();
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
}
