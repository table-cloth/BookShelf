package com.tablecloth.bookshelf.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tablecloth.bookshelf.activity.BaseActivity;
import com.tablecloth.bookshelf.data.BookData;

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
    final protected static String KEY_BOOK_SERIES_ID = "id";

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
     * Get book series id set in intent data
     *
     * @return book series id
     */
    protected int getBookSeriesId() {
        return mIntentData.getIntExtra(
                KEY_BOOK_SERIES_ID, BookData.BOOK_SERIES_ERROR_VALUE);
    }

    /**
     * Set text for title
     *
     * @param titleTextViewId Id for title text view
     */
    protected void setTitleText(int titleTextViewId) {
        setText4TextView(titleTextViewId,
                mIntentData.getIntExtra(KEY_TITLE_STR_ID, VALUE_DEFAULT_STR_ID));
    }

    /**
     * Set text for message
     *
     * @param messageTextViewId Id for message text view
     */
    protected void setMessageText(int messageTextViewId) {
        setText4TextView(messageTextViewId,
                mIntentData.getIntExtra(KEY_MESSAGE_STR_ID, VALUE_DEFAULT_STR_ID));
    }

    /**
     * Set text for positive btn
     *
     * @param btnPositiveTextViewId Id for positive btn text view
     */
    protected void setBtnPositiveText(int btnPositiveTextViewId) {
        setText4TextView(btnPositiveTextViewId,
                mIntentData.getIntExtra(KEY_BTN_POSITIVE_STR_ID, VALUE_DEFAULT_STR_ID));
    }

    /**
     * Set text for negative btn
     *
     * @param btnNegativeTextViewId Id for negative btn text view
     */
    protected void setBtnNegativeText(int btnNegativeTextViewId) {
        setText4TextView(btnNegativeTextViewId,
                mIntentData.getIntExtra(KEY_BTN_NEGATIVE_STR_ID, VALUE_DEFAULT_STR_ID));
    }


    /**
     * Set text for text view
     *
     * @param textViewId Text view id
     * @param strId String id
     */
    protected void setText4TextView(int textViewId, int strId) {
        ((TextView)findViewById(textViewId)).setText(strId);
    }

    /**
     * Set hint for text view
     *
     * @param textViewId Text view id
     * @param strId String id
     */
    protected void setHint4TextView(int textViewId, int strId) {
        ((TextView)findViewById(textViewId)).setHint(strId);
    }

    /**
     * Set text and hint for text view
     *
     * @param textViewId Text view id
     * @param textStrId Text string id
     * @param hintStrId Hint string id
     */
    protected void setText4TextView(int textViewId, int textStrId, int hintStrId) {
        TextView textView = (TextView)findViewById(textViewId);
        textView.setHint(hintStrId);
        textView.setText(textStrId);
    }
}
