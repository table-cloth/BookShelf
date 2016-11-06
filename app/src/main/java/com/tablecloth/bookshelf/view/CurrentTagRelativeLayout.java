package com.tablecloth.bookshelf.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
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
public class CurrentTagRelativeLayout extends BaseTagRelativeLayout {

    public CurrentTagRelativeLayout(Context context) {
        super(context);
    }
    public CurrentTagRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CurrentTagRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 描画関数
    @Override
    protected void onDraw(Canvas canvas) {
        if(!mNeedsReLayout) {
            super.onDraw(canvas);
            return;

        }

        Log.e("onDraw::CurrentTag", "onDraw::CurrentTag");
        // 履歴領域の情報を取得
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
                tagChild.setOnClickListener(onClickListener);

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
        if(parentParams.height > Util.convertDp2Px(mContext, 100)) {
            ViewGroup ScrollParent = (ViewGroup)((ViewGroup)getParent()).getParent();
            ViewGroup.LayoutParams param = ScrollParent.getLayoutParams();
            param.height = Util.convertDp2Px(mContext, 100);
            ScrollParent.setLayoutParams(param);
            ScrollParent.invalidate();
        }

        mNeedsReLayout = false;

//        super.onDraw(canvas);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // get tag to delete
            String deleteTag = ((TextView)view.findViewById(R.id.tag_name)).getText().toString();

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
