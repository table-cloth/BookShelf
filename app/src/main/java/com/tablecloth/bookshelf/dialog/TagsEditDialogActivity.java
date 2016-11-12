package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookData;
import com.tablecloth.bookshelf.db.TagHistoryDao;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;
import com.tablecloth.bookshelf.view.BaseTagContainerLayout;
import com.tablecloth.bookshelf.view.CurrentTagContainerLayout;
import com.tablecloth.bookshelf.view.RecentTagContainerLayout;

import java.util.ArrayList;

/**
 * Dialog activity for editing tags
 *
 * Created by Minami on 2015/04/02.
 */
public class TagsEditDialogActivity extends DialogBaseActivity {

    String mRawTagsText;
    CurrentTagContainerLayout mCurrentTagContainer;
    RecentTagContainerLayout mRecentTagContainer;
    EditText mAddTagEditText;

    TagHistoryDao mTagHistoryDao;

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return R.layout.activity_edit_tag_dialog;
    }

    /**
     * Get intent with given extra data
     *
     * @param context          context
     * @param titleStrId       string id for title
     * @param btnPositiveStrId string id for positive button
     * @param rawTags          raw tags in text
     * @return Intent instance
     */
    @NonNull
    public static Intent getIntent(@NonNull Context context, int titleStrId, int btnPositiveStrId, @Nullable String rawTags) {
        Intent intent = new Intent(context, TagsEditDialogActivity.class);

        intent.putExtra(KEY_TITLE_STR_ID, titleStrId);
        intent.putExtra(KEY_BTN_POSITIVE_STR_ID, btnPositiveStrId);
        intent.putExtra(KEY_RAW_TAGS, rawTags);

        return intent;
    }

    /**
     * OnCreate
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitleText(R.id.title);
        setBtnPositiveText(R.id.btn_positive);

        mCurrentTagContainer = (CurrentTagContainerLayout) findViewById(R.id.tag_container);
        mRecentTagContainer = (RecentTagContainerLayout) findViewById(R.id.tag_recent_container);
        mAddTagEditText = (EditText) findViewById(R.id.data_content);

        mTagHistoryDao = new TagHistoryDao(this);
        mRawTagsText = getRawTags();

        findViewById(R.id.btn_positive).setOnClickListener(this);
        findViewById(R.id.btn_add_tag).setOnClickListener(this);

        mAddTagEditText.setOnEditorActionListener(getEditorActionListener());

        BaseTagContainerLayout.OnCurrentTagUpdateListener tagUpdateListener
                = getTagUpdateListener();
        mCurrentTagContainer.setTagUpdateListener(tagUpdateListener);
        mRecentTagContainer.setTagUpdateListener(tagUpdateListener);

        updateCurrentTags();
        updateRecentTags();
    }

    /**
     * Get tag update listener
     *
     * @return OnCurrentTagUpdateListener for all tag container
     */
    @NonNull
    private BaseTagContainerLayout.OnCurrentTagUpdateListener getTagUpdateListener() {
        return new BaseTagContainerLayout.OnCurrentTagUpdateListener() {
            @Override
            public void onUpdate(String currentTags) {
                mRawTagsText = currentTags;
                updateRecentTags();
                updateCurrentTags();
            }
        };
    }

    /**
     * Get editor action listener for tag edit text
     *
     * @return OnEditorActionListener for tag edit text
     */
    @NonNull
    private TextView.OnEditorActionListener getEditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Action of enter key is set as "EditorInfo.IME_ACTION_DONE" in xml
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    UpdateWithNewTag(mAddTagEditText.getText().toString());
                    mAddTagEditText.setText("");
                }
                return false;
            }
        };
    }

    /**
     * Handles all click event within this Activity
     *
     * @param view Clicked view
     */
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.btn_positive: // Decide btn. Pass tag data back to prev Activity
                finishWithResult(G.RESULT_POSITIVE,
                        new Intent().putExtra(KEY_RAW_TAGS, mRawTagsText));
                break;

            case R.id.btn_add_tag: // Add new tags to current & history
                UpdateWithNewTag(mAddTagEditText.getText().toString());
                mAddTagEditText.setText("");
                break;
        }
    }

    /**
     * Add new tag to current tags
     *
     * @param newTag Tag to add
     * @return Is tag added successfully
     */
    private boolean addNewTag(@Nullable String newTag) {
        ArrayList<String> tagsInList = BookData.convertTagsRawText2TagsList(mRawTagsText);

        // Return if failed to add for some reason
        if (Util.isEmpty(newTag)) {
            ToastUtil.show(this, R.string.tag_error_enter_tag_2_add);
            return false;
        } else if (tagsInList.contains(newTag)) {
            ToastUtil.show(this, R.string.tag_error_already_added);
            return false;
        }

        tagsInList.add(newTag);
        mRawTagsText = BookData.convertTagsList2TagsRawText(tagsInList);
        return true;
    }

    /**
     * Update current tags container
     */
    private void updateCurrentTags() {
        // for current tags, use tag data of what is currently set
        updateTagContainer(
                mCurrentTagContainer,
                BookData.convertTagsRawText2TagsList(mRawTagsText),
                ViewUtil.TAGS_LAYOUT_TYPE_LARGE_WITH_DELETE);
    }

    /**
     * Update recent tags container
     */
    private void updateRecentTags() {
        // for recent tags, re-load all from history
        ArrayList<String> tagsLog = mTagHistoryDao.loadAllTags();

        updateTagContainer(
                mRecentTagContainer,
                tagsLog,
                ViewUtil.TAGS_LAYOUT_TYPE_LARGE);
    }

    /**
     * Update tags container
     *
     * @param container Tags container to update
     * @param tagList New tag info in list
     * @param tagsLayoutType TagView type
     */
    private void updateTagContainer(@NonNull BaseTagContainerLayout container, @Nullable ArrayList<String> tagList, int tagsLayoutType) {
        container.removeAllViews();

        ArrayList<ViewGroup> tagViewList = ViewUtil.getTagViewList(this, tagList, tagsLayoutType);
        for (ViewGroup tagView : tagViewList) {
            container.addView(tagView);
        }

        container.setTagData(BookData.convertTagsList2TagsRawText(tagList));
        container.setReDrawFlag(true);
    }


    /**
     * Add new tag & update if necessary
     *
     * @param newTag2Add new tag to add
     */
    private void UpdateWithNewTag(String newTag2Add) {
        if(addNewTag(newTag2Add)) {
            mTagHistoryDao.saveTag(newTag2Add);
        }
        updateCurrentTags();
        updateRecentTags();
    }

}

