package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookData;
import com.tablecloth.bookshelf.db.TagHistoryDao;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;
import com.tablecloth.bookshelf.view.BaseTagRelativeLayout;
import com.tablecloth.bookshelf.view.CurrentTagRelativeLayout;
import com.tablecloth.bookshelf.view.RecentTagRelativeLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * タグの編集用ダイアログ
 * Created by Minami on 2015/04/02.
 */
public class TagsEditDialogActivity extends DialogBaseActivity {

    final public static String KEY_TAGS = "tags_data";
    CurrentTagRelativeLayout tagContainer;
    RecentTagRelativeLayout recentTagContainer;
    String tagsData;
    EditText addTagEditText;

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
                ArrayList<String> tagsTmp = BookData.convertTagsRawText2TagsList(tagsData);

                // 登録失敗
                if(Util.isEmpty(newTag)) {
                    ToastUtil.show(TagsEditDialogActivity.this, "追加するタグを入力してください");
                    return;
                } else if(tagsTmp != null && tagsTmp.contains(newTag)) {
                    ToastUtil.show(TagsEditDialogActivity.this, "既に登録済みのタグです");
                    return;
                }

                // 登録成功
                tagsTmp.add(newTag);
                tagsData = BookData.convertTagsList2TagsRawText(tagsTmp);

                // save newly added tag to history DB
                mTagHistoryDao.saveTag(newTag);

                updateCurrentTags();
                updateRecentTags();

            }
        });

        updateCurrentTags();
        updateRecentTags();

    }

    private void updateCurrentTags() {
        updateTagContainer(
                tagContainer,
                BookData.convertTagsRawText2TagsList(tagsData),
                ViewUtil.TAGS_LAYOUT_TYPE_LARGE_WITH_DELETE);

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
    }

    private void updateTagContainer(ViewGroup container, ArrayList<String> tagList, int tagsLayoutType) {
        container.removeAllViews();

        ArrayList<ViewGroup> tagViewList = ViewUtil.getTagViewList(this, tagList, tagsLayoutType);
        for(ViewGroup tagView : tagViewList) {
            container.addView(tagView);
        }
    }

    private void updateRecentTags() {
        // タグ履歴領域に最新のタグを入れて、再描画(invalidate)を呼び出す
        ArrayList<String> tagsLog = mTagHistoryDao.loadAllTags();

        updateTagContainer(
                recentTagContainer,
                tagsLog,
                ViewUtil.TAGS_LAYOUT_TYPE_LARGE);

        recentTagContainer.setTagData(BookData.convertTagsList2TagsRawText(tagsLog));
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
    }

        @Override
    protected int getContentViewID() {
        return R.layout.activity_edit_tag_dialog;
    }

    public static Intent getIntent(Context context, String title, ArrayList<String> tags, String btnPositive) {
        Intent intent = new Intent(context, TagsEditDialogActivity.class);

        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_TAGS, BookData.convertTagsList2TagsRawText(tags));
        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);

        return intent;
    }
}
