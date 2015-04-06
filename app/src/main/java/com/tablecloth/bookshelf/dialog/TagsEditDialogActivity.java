package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.FilterDao;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;

import java.util.ArrayList;

/**
 * タグの編集用ダイアログ
 * Created by shnomura on 2015/04/02.
 */
public class TagsEditDialogActivity extends DialogBaseActivity {

    final public static String KEY_TAGS = "tags_data";
    ViewGroup tagContainer;
    ViewGroup recentTagContainer;
    String tagsData;
    EditText addTagEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        ((TextView)findViewById(R.id.title)).setText(intent.getStringExtra(KEY_TITLE));
        ((TextView)findViewById(R.id.btn_positive)).setText(intent.getStringExtra(KEY_BTN_POSITIVE));

        tagsData = intent.getStringExtra(KEY_TAGS);
        tagContainer = (ViewGroup)findViewById(R.id.tag_container);
        recentTagContainer = (ViewGroup)findViewById(R.id.tag_recent_container);
        findViewById(R.id.btn_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(KEY_TAGS, tagsData);
                setResult(G.RESULT_POSITIVE, data);
                finish();
            }
        });
        addTagEditText = (EditText)findViewById(R.id.data_content);
        findViewById(R.id.btn_add_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTag = addTagEditText.getText().toString();
                addTagEditText.setText("");
                ArrayList<String> tagsTmp = FilterDao.getTagsData(tagsData);

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
                tagsData = FilterDao.getTagsStr(tagsTmp);
                updateTags();
                FilterDao.saveTags(TagsEditDialogActivity.this, newTag);

            }
        });

        updateTags();

    }

    private void updateTags() {
        ArrayList<String> tags = FilterDao.getTagsData(tagsData);
        tagContainer.removeAllViews();
        recentTagContainer.removeAllViews();
        ViewUtil.setTagInfoNormal(TagsEditDialogActivity.this, tags, tagContainer);
        tagContainer.invalidate();

        ArrayList<String> tagsLog = FilterDao.loadTags(TagsEditDialogActivity.this);

        recentTagContainer = ViewUtil.setTagInfoNormal(TagsEditDialogActivity.this, tagsLog, tagContainer);
        recentTagContainer.invalidate();
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_edit_tag_dialog;
    }

    public static Intent getIntent(Context context, String title, ArrayList<String> tags, String btnPositive) {
        Intent intent = new Intent(context, TagsEditDialogActivity.class);

        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_TAGS, FilterDao.getTagsStr(tags));
        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);

        return intent;
    }
}
