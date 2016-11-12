package com.tablecloth.bookshelf.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tablecloth.bookshelf.activity.BaseActivity;

/**
 * Base class for all dialogs
 *
 * Created by Minami on 2014/08/17.
 */
public abstract class DialogBaseActivity extends BaseActivity implements View.OnClickListener {

    // Key values for Intent extras
    final protected static String KEY_TITLE_STR_ID = "title";
    final protected static String KEY_MESSAGE_STR_ID = "message";
    final protected static String KEY_BTN_POSITIVE_STR_ID = "btn_positive";
    final protected static String KEY_BTN_NEGATIVE_STR_ID = "btn_negative";
    final protected static String KEY_DATA_TYPE_STR_ID = "data_type";
    final protected static String KEY_ID = "id";

    // Default values for Intent extras
    final protected static int VALUE_DEFAULT_STR_ID = -1;

    // Intent to handle extra data
    private Intent mIntentData;

    /**
     * On create
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntentData = getIntent();
    }

    /**
     * Handles all click event within this Activity
     *
     * @param view clicked view
     */
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
        }
    }

    /**
     * Set text for title
     *
     * @param titleTextViewId Id for title text view
     */
    protected void setTitleText(int titleTextViewId) {
        setText4ViewText(titleTextViewId,
                mIntentData.getIntExtra(KEY_TITLE_STR_ID, VALUE_DEFAULT_STR_ID));
    }

    /**
     * Set text for message
     *
     * @param messageTextViewId Id for message text view
     */
    protected void setMessageText(int messageTextViewId) {
        setText4ViewText(messageTextViewId,
                mIntentData.getIntExtra(KEY_MESSAGE_STR_ID, VALUE_DEFAULT_STR_ID));
    }

    /**
     * Set text for positive btn
     *
     * @param btnPositiveTextViewId Id for positive btn text view
     */
    protected void setBtnPositiveText(int btnPositiveTextViewId) {
        setText4ViewText(btnPositiveTextViewId,
                mIntentData.getIntExtra(KEY_BTN_POSITIVE_STR_ID, VALUE_DEFAULT_STR_ID));
    }

    /**
     * Set text for negative btn
     *
     * @param btnNegativeTextViewId Id for negative btn text view
     */
    protected void setBtnNegativeText(int btnNegativeTextViewId) {
        setText4ViewText(btnNegativeTextViewId,
                mIntentData.getIntExtra(KEY_BTN_NEGATIVE_STR_ID, VALUE_DEFAULT_STR_ID));
    }


    /**
     * Set text for title
     *
     * @param textViewId Text view id
     * @param strId String id
     */
    private void setText4ViewText(int textViewId, int strId) {
        ((TextView)findViewById(textViewId))
                .setText(getString(strId));
    }

    /**
     * Finish activity with result
     *
     * @param result Result to set
     */
    protected void finishWithResult(int result) {
        setResult(result);
        finish();
    }

    /**
     * Finish activity with result
     *
     * @param result Result to set
     * @param data Intent data to set
     */
    protected void finishWithResult(int result, Intent data) {
        setResult(result, data);
        finish();
    }
}
