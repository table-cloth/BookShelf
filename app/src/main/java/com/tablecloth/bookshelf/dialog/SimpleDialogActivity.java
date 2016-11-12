package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.R;

/**
 * Dialog activity for simple dialog with 2 buttons
 * Will only handle simple events of Positive & Negative
 *
 * Created by Minami on 2014/08/17.
 */
public class SimpleDialogActivity extends DialogBaseActivity {

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return R.layout.activity_simple_dialog;
    }

    /**
     * Get intent with given extra data
     *
     * @param context context
     * @param titleStrId string id for title
     * @param messageStrId string id for message content
     * @param btnPositiveStrId string id for positive button
     * @param btnNegativeStrId string id for negative button
     * @return Intent instance
     */
    @NonNull
    public static Intent getIntent(Context context, int titleStrId, int messageStrId, int btnPositiveStrId, int btnNegativeStrId) {
        Intent intent = new Intent(context, SimpleDialogActivity.class);

        intent.putExtra(KEY_TITLE_STR_ID, titleStrId);
        intent.putExtra(KEY_MESSAGE_STR_ID, messageStrId);
        intent.putExtra(KEY_BTN_POSITIVE_STR_ID, btnPositiveStrId);
        intent.putExtra(KEY_BTN_NEGATIVE_STR_ID, btnNegativeStrId);

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
        setMessageText(R.id.message);
        setBtnPositiveText(R.id.btn_positive);
        setBtnNegativeText(R.id.btn_negative);

        findViewById(R.id.btn_positive).setOnClickListener(this);
        findViewById(R.id.btn_negative).setOnClickListener(this);
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
            case R.id.btn_positive: // Positive event
                finishWithResult(G.RESULT_POSITIVE);
                break;

            case R.id.btn_negative: // Negative event
                finishWithResult(G.RESULT_NEGATIVE);
                break;
        }
    }
}
