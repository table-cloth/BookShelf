package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.db.TagHistoryDao;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;
import com.tablecloth.bookshelf.view.BaseTagRelativeLayout;
import com.tablecloth.bookshelf.view.CurrentTagRelativeLayout;
import com.tablecloth.bookshelf.view.RecentTagRelativeLayout;

import java.util.ArrayList;

/**
 * タグの編集用ダイアログ
 * Created by shnomura on 2015/04/02.
 */
public class TagsEditDialogActivity extends DialogBaseActivity {

    final public static String KEY_TAGS = "tags_data";
    CurrentTagRelativeLayout tagContainer;
    RecentTagRelativeLayout recentTagContainer;
    String tagsData;
    EditText addTagEditText;
//    ViewGroup recentTagTmpContainer;
//    ViewTreeObserver.OnGlobalLayoutListener mRectentTagLayoutListener;
//    ViewTreeObserver.OnGlobalLayoutListener mCurrentTagLayoutListener;

    TagHistoryDao mTagHistoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        ((TextView)findViewById(R.id.title)).setText(intent.getStringExtra(KEY_TITLE));
        ((TextView)findViewById(R.id.btn_positive)).setText(intent.getStringExtra(KEY_BTN_POSITIVE));

        mTagHistoryDao = new TagHistoryDao(this);
        tagsData = intent.getStringExtra(KEY_TAGS);
        tagContainer = (CurrentTagRelativeLayout)findViewById(R.id.tag_container);
        recentTagContainer = (RecentTagRelativeLayout)findViewById(R.id.tag_recent_container);
        findViewById(R.id.btn_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(KEY_TAGS, tagsData);
                setResult(G.RESULT_POSITIVE, data);
                finish();
            }
        });

        // テキスト入力によるタグ追加
        addTagEditText = (EditText)findViewById(R.id.data_content);
        findViewById(R.id.btn_add_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTag = addTagEditText.getText().toString();
                addTagEditText.setText("");
                ArrayList<String> tagsTmp = SeriesData.convertTagsRawText2TagsList(tagsData);

                // 登録失敗
                if(Util.isEmpty(newTag)) {
                    ToastUtil.show(TagsEditDialogActivity.this, "追加するタグを入力してください");
                    return;
                } else if(tagsTmp != null && tagsTmp.contains(newTag)) {
                    ToastUtil.show(TagsEditDialogActivity.this, "既に登録済みのタグです");
                    return;
                }

                // 登録成功
                if(tagsTmp == null) tagsTmp = new ArrayList<String>();
                tagsTmp.add(newTag);
                tagsData = SeriesData.convertTagsList2TagsRawText(tagsTmp);

                // save newly added tag to history DB
                mTagHistoryDao.saveTag(newTag);

                updateCurrentTags();
                updateRecentTags();

            }
        });

//        // タグ履歴の再描画・再配置処理
//        mRectentTagLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(reFormatRecentTags()) {
//                    try {
//                        recentTagContainer.getViewTreeObserver().removeOnGlobalLayoutListener(mRectentTagLayoutListener);
//                    } catch (Exception e) {
//                        try {
//                            recentTagContainer.getViewTreeObserver().removeGlobalOnLayoutListener(mRectentTagLayoutListener);
//                        } catch (Exception e2) {
//                            e2.printStackTrace();
//                        }
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        mCurrentTagLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(reFormatCurrentTags()) {
//                    try {
//                        recentTagContainer.getViewTreeObserver().removeOnGlobalLayoutListener(mCurrentTagLayoutListener);
//                    } catch (Exception e) {
//                        try {
//                            recentTagContainer.getViewTreeObserver().removeGlobalOnLayoutListener(mCurrentTagLayoutListener);
//                        } catch (Exception e2) {
//                            e2.printStackTrace();
//                        }
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
        updateCurrentTags();
        updateRecentTags();

    }

    private void updateCurrentTags() {
        tagContainer.removeAllViews();

//        tagContainer.getViewTreeObserver().addOnGlobalLayoutListener(mCurrentTagLayoutListener);
        tagContainer = (CurrentTagRelativeLayout)ViewUtil.setTagInfoLargeDelete(
                TagsEditDialogActivity.this, SeriesData.convertTagsRawText2TagsList(tagsData),
                tagContainer, View.INVISIBLE);
        tagContainer.setTagData(tagsData);
        tagContainer.setTagUpdateListener(new BaseTagRelativeLayout.OnCurrentTagUpdateListener() {
            @Override
            public void onUpdate(String currentTags) {
                tagsData = currentTags;
                updateRecentTags();
                updateCurrentTags();
            }
        });
        tagContainer.setReLayoutFlag(true);
//        tagContainer.invalidate();
//        tagContainer.invalidate();
    }

    private void updateRecentTags() {
        // タグ履歴領域変動時のリスナーを登録
//        recentTagContainer.getViewTreeObserver().addOnGlobalLayoutListener(mRectentTagLayoutListener);
        recentTagContainer.removeAllViews();

        // タグ履歴領域に最新のタグを入れて、再描画(invalidate)を呼び出す
        ArrayList<String> tagsLog = mTagHistoryDao.loadAllTags();
        recentTagContainer = (RecentTagRelativeLayout)ViewUtil.setTagInfoLarge(TagsEditDialogActivity.this, tagsLog, recentTagContainer, View.INVISIBLE);
        recentTagContainer.setTagData(SeriesData.convertTagsList2TagsRawText(tagsLog));
        recentTagContainer.setCurrentTagData(tagsData);
        recentTagContainer.setTagUpdateListener(new BaseTagRelativeLayout.OnCurrentTagUpdateListener() {
            @Override
            public void onUpdate(String currentTags) {
                tagsData = currentTags;
                updateRecentTags();
                updateCurrentTags();
            }
        });
        recentTagContainer.setReLayoutFlag(true);

//        recentTagContainer.invalidate();

//        if(recentTagTmpContainer != null) recentTagTmpContainer.removeAllViews();
//        recentTagTmpContainer = ViewUtil.setTagInfoNormal(TagsEditDialogActivity.this, tagsLog, recentTagContainer);
//        reInvalidateRecentTags();
    }

        @Override
    protected int getContentViewID() {
        return R.layout.activity_edit_tag_dialog;
    }

    public static Intent getIntent(Context context, String title, ArrayList<String> tags, String btnPositive) {
        Intent intent = new Intent(context, TagsEditDialogActivity.class);

        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_TAGS, SeriesData.convertTagsList2TagsRawText(tags));
        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);

        return intent;
    }

////    /**
////     * タグ履歴のレイアウト再配置
////     * @param hasFocus
////     */
////    @Override
////    public void onWindowFocusChanged(boolean hasFocus) {
////        super.onWindowFocusChanged(hasFocus);
////
////        if(recentTagContainer == null || recentTagTmpContainer == null) {
////            ToastUtil.show(TagsEditDialogActivity.this, "エラー発生");
////        }
//
////        reInvalidateRecentTags();
//    }

//    private boolean reFormatRecentTags() {
//
//        if(recentTagContainer == null || recentTagContainer.getWidth() == 0) return false;
//
//        // 履歴領域の情報を取得
//        int maxWidth = recentTagContainer.getWidth();
//        int maxChild = recentTagContainer.getChildCount();
//        int lineStartIndex = 0;
//        int lineStartViewId = -1;
//        if(maxChild > 0) {
//            recentTagContainer.getChildAt(0).setId(0);
//            lineStartViewId = recentTagContainer.getChildAt(0).getId();
//        }
//        int currentWidth = 0;
//        int measuredWidth = 0;
//        int prevId = -1;
//
//        // density (比率)を取得する
//        float density = getResources().getDisplayMetrics().density;
//
//        // 50 dp を pixel に変換する ( dp × density + 0.5f（四捨五入) )
//        int px = (int) (50f * density + 0.5f);
//
////        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////        LinearLayout currentRow = (LinearLayout)inflater.inflate(R.layout.tag_row_layout, null);
////
////        recentTagContainer.removeAllViews();
////        recentTagContainer.addView(currentRow);
//        int currentHeight = 0;
//        ViewGroup tagChild = null;
//
//        for(int i = 0 ; i < maxChild ; i ++) {
//            try {
//                tagChild = (ViewGroup)recentTagContainer.getChildAt(i);
//                tagChild.setVisibility(View.VISIBLE);
////                tagChild.setId(i);
//                // タップによるタグの登録処理
//                tagChild.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String newTag = ((TextView)view.findViewById(R.id.tag_name)).getText().toString();
//                        ArrayList<String> tagsTmp = FilterDao.getTagsData(tagsData);
//
//                        // 登録失敗
//                        if(Util.isEmpty(newTag)) {
//                            ToastUtil.show(TagsEditDialogActivity.this, "追加するタグを入力してください");
//                            return;
//                        } else if(tagsTmp != null && tagsTmp.contains(newTag)) {
//                            ToastUtil.show(TagsEditDialogActivity.this, "既に登録済みのタグです");
//                            return;
//                        }
//
//                        // 登録成功
//                        if(tagsTmp == null) tagsTmp = new ArrayList<String>();
//                        tagsTmp.add(newTag);
//                        tagsData = FilterDao.getTagsStr(tagsTmp);
//                        FilterDao.saveTags(TagsEditDialogActivity.this, newTag);
//
//                        String tags = tagContainer.getTagData();
//                        updateCurrentTags(FilterDao.getTagsData(tags));
//                        updateRecentTags();
//                    }
//                });
//
//
//                // ビューの設定
//                measuredWidth += tagChild.getWidth();
//
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tagChild.getLayoutParams();
//                // その行の１つ目 or 横幅に余裕がある場合
//                if(lineStartIndex != i && measuredWidth + tagChild.getWidth() >= maxWidth) {
////                    if(prevId != -1) {
////                        params.addRule(RelativeLayout.ALIGN_TOP, prevId);
////                        params.addRule(RelativeLayout.RIGHT_OF, prevId);
////                    }
////                } else {
////                    if(prevId != -1) {
////
////                        params.addRule(RelativeLayout.BELOW, lineStartViewId);
////                        lineStartViewId = tagChild.getId();
//
//                    lineStartIndex = i;
//                        currentWidth = 0;
//                        measuredWidth = 0;
//                        currentHeight += tagChild.getHeight() + convertDp2Px(10);
////                        currentRow.invalidate();
////                        currentRow = (LinearLayout)inflater.inflate(R.layout.tag_row_layout, null);
////                        recentTagContainer.addView(currentRow);
////                    }
//                }
//                params.leftMargin = currentWidth;
//                params.topMargin = currentHeight;
//                Log.e("index :: width / height", i + " :: " + currentWidth + " / " + currentHeight);
//
//                tagChild.setLayoutParams(params);
//
//                currentWidth += tagChild.getWidth();
/////                currentRow.addView(tagChild);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if(tagChild != null) {
//                prevId = tagChild.getId();
//            }
//        }
//
//        // レイアウト生成完了時のリスナーを登録し、再描画を行う
// //       recentTagContainer.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
//        ViewGroup.LayoutParams parentParams = recentTagContainer.getLayoutParams();
//        if(tagChild != null) {
//            parentParams.height = currentHeight + tagChild.getHeight() + convertDp2Px(10);
//            int dp100 = convertDp2Px(100);
//            if(parentParams.height >= dp100) {
//                ScrollView scrollView = (ScrollView)findViewById(R.id.tag_scroll_container);
//                parentParams = scrollView.getLayoutParams();
//                parentParams.height = dp100;
//                scrollView.setLayoutParams(parentParams);
//            }
//        }
////        findViewById(R.id.tag_scroll_container).invalidate();
//        return true;
//    }
//
//    private boolean reFormatCurrentTags() {
//
//        if(tagContainer == null || tagContainer.getWidth() == 0) return false;
//
//        // 履歴領域の情報を取得
//        int maxWidth = tagContainer.getWidth();
//        int maxChild = tagContainer.getChildCount();
//        int lineStartIndex = 0;
//        int lineStartViewId = -1;
//        if(maxChild > 0) {
//            tagContainer.getChildAt(0).setId(0);
//            lineStartViewId = tagContainer.getChildAt(0).getId();
//        }
//        int currentWidth = 0;
//        int measuredWidth = 0;
//
//        int currentHeight = 0;
//        ViewGroup tagChild = null;
//
//        for(int i = 0 ; i < maxChild ; i ++) {
//            try {
//                tagChild = (ViewGroup)tagContainer.getChildAt(i);
//                tagChild.setVisibility(View.VISIBLE);
////                tagChild.setId(i);
//                // タップによるタグの登録処理
//                tagChild.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String deleteTag = ((TextView)view.findViewById(R.id.tag_name)).getText().toString();
//
//                        // タグの削除処理
//                        ArrayList<String> tagsTmp = FilterDao.getTagsData(tagsData);
//                        if(tagsTmp == null) {
//                            ToastUtil.show(TagsEditDialogActivity.this, "タグの削除に失敗しました");
//                        }
//                        tagsTmp.remove(deleteTag);
//                        tagsData = FilterDao.getTagsStr(tagsTmp);
//
//                        String tags = tagContainer.getTagData();
//                        updateCurrentTags(FilterDao.getTagsData(tags));
//                        updateRecentTags();
//
//                    }
//                });
//
//
//                // ビューの設定
//                measuredWidth += tagChild.getWidth();
//
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tagChild.getLayoutParams();
//                // その行の１つ目 or 横幅に余裕がある場合
//                if(lineStartIndex != i && measuredWidth + tagChild.getWidth() >= maxWidth) {
//                    lineStartIndex = i;
//                    currentWidth = 0;
//                    measuredWidth = 0;
//                    currentHeight += tagChild.getHeight() + convertDp2Px(10);
//                }
//                params.leftMargin = currentWidth;
//                params.topMargin = currentHeight;
//
//                tagChild.setLayoutParams(params);
//
//                currentWidth += tagChild.getWidth();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        // レイアウト生成完了時のリスナーを登録し、再描画を行う
//        //       recentTagContainer.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
//        ViewGroup.LayoutParams parentParams = tagContainer.getLayoutParams();
//        if(tagChild != null) {
//            parentParams.height = currentHeight + tagChild.getHeight() + convertDp2Px(10);
//        }
//        tagContainer.setLayoutParams(parentParams);
////        findViewById(R.id.tag_recent_scroll_container).invalidate();
//        return true;
//    }

//    private void reInvalidateRecentTags() {
//
////        if(recentTagContainer == null || recentTagTmpContainer == null) return;
//
//        // 履歴領域の情報を取得
//        int maxWidth = recentTagTmpContainer.getWidth();
//        int maxChild = recentTagTmpContainer.getChildCount();
//        int lineStartIndex = 0;
//        int lineStartViewId = -1;
//        if(maxChild > 0) lineStartViewId = recentTagTmpContainer.getChildAt(0).getId();
//        int currentWidth = 0;
//        int prevId = -1;
//
////        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////        LinearLayout currentRow = (LinearLayout)inflater.inflate(R.layout.tag_row_layout, null);
////
////        recentTagContainer.removeAllViews();
////        recentTagContainer.addView(currentRow);
//
//        for(int i = 0 ; i < maxChild ; i ++) {
//            ViewGroup tagChild = null;
//            try {
//                tagChild = (ViewGroup)recentTagTmpContainer.getChildAt(i);
//                // タップによるタグの登録処理
//                tagChild.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String newTag = ((TextView)view.findViewById(R.id.tag_name)).getText().toString();
//                        ArrayList<String> tagsTmp = FilterDao.getTagsData(tagsData);
//
//                        // 登録失敗
//                        if(Util.isEmpty(newTag)) {
//                            ToastUtil.show(TagsEditDialogActivity.this, "追加するタグを入力してください");
//                            return;
//                        } else if(tagsTmp != null && tagsTmp.contains(newTag)) {
//                            ToastUtil.show(TagsEditDialogActivity.this, "既に登録済みのタグです");
//                            return;
//                        }
//
//                        // 登録成功
//                        if(tagsTmp == null) tagsTmp = new ArrayList<String>();
//                        tagsTmp.add(newTag);
//                        tagsData = FilterDao.getTagsStr(tagsTmp);
//                        updateTags();
//                        FilterDao.saveTags(TagsEditDialogActivity.this, newTag);
//                    }
//                });
//
//
//                // ビューの設定
//                currentWidth += tagChild.getWidth();
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tagChild.getLayoutParams();
//                // その行の１つ目 or 横幅に余裕がある場合
//                if(lineStartIndex == i || currentWidth < maxWidth) {
//                    if(prevId != -1) {
//                        params.addRule(RelativeLayout.ALIGN_TOP, prevId);
//                        params.addRule(RelativeLayout.RIGHT_OF, prevId);
//                    }
//                } else {
//                    if(prevId != -1) {
//                        params.addRule(RelativeLayout.BELOW, lineStartViewId);
//                        lineStartViewId = tagChild.getId();
//                        lineStartIndex = i;
//                        currentWidth = 0;
//                        currentRow.invalidate();
//                        currentRow = (LinearLayout)inflater.inflate(R.layout.tag_row_layout, null);
//                        recentTagContainer.addView(currentRow);
//                    }
//                }
//                currentRow.addView(tagChild);
//            } catch (OutOfMemoryError e) {
//                e.printStackTrace();
//            }
//            if(tagChild != null) {
//                prevId = tagChild.getId();
//            }
//        }
//
//        // レイアウト生成完了時のリスナーを登録し、再描画を行う
//        recentTagContainer.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
//        recentTagContainer.invalidate();
//
//    }
//
//    private int convertDp2Px(int dp) {
//        // density (比率)を取得する
//        float density = getResources().getDisplayMetrics().density;
//
//        // dpをpixelに変換する ( dp × density + 0.5f（四捨五入) )
//        return (int) ((float)dp * density + 0.5f);
//    }
}
