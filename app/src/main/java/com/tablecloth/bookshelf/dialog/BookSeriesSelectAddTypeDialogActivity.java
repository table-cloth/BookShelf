package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioGroup;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.Const;

/**
 * Dialog activity for selecting type to add book series
 * Current type:
 *  - Add by Search API
 *  - Add manually
 *
 * Created by Minami on 2015/02/21.
 */
public class BookSeriesSelectAddTypeDialogActivity extends DialogBaseActivity {

    RadioGroup radioGroup;

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return R.layout.activity_btn_list_dialog;
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
    public static Intent getIntent(@NonNull Context context, int titleStrId, int messageStrId, int btnPositiveStrId, int btnNegativeStrId) {
        Intent intent = new Intent(context, BookSeriesSelectAddTypeDialogActivity.class);

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

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        setDefaultRadioCheck();

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
     * @param view clicked view
     */
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.btn_positive: // add book series btn
                // default select value will be
                int selectedCheckBoxType = getDefaultRadioType();
                switch (radioGroup.getCheckedRadioButtonId()) {
                    default:
                    case R.id.radio_0: // Search with API
                        selectedCheckBoxType = Const.INTENT_EXTRA.VALUE_SELECTED_BTN_SEARCH;
                        break;

                    case R.id.radio_1: // Search manually
                        selectedCheckBoxType = Const.INTENT_EXTRA.VALUE_SELECTED_BTN_MANUAL;
                        break;
                }

                finishWithResult(Const.RESULT_CODE.POSITIVE,
                        new Intent().putExtra(
                                Const.INTENT_EXTRA.KEY_INT_SELECTED_ID,
                                selectedCheckBoxType));
                break;

            case R.id.btn_negative: // cancel add book series
                finishWithResult(Const.RESULT_CODE.NEGATIVE);
                break;
        }
    }

    /**
     * Set check on default radio
     */
    private void setDefaultRadioCheck() {
        radioGroup.check(R.id.radio_0);
    }

    /**
     * Get type for default radio check
     *
     * @return default select type
     */
    private int getDefaultRadioType() {
        return Const.INTENT_EXTRA.VALUE_SELECTED_BTN_SEARCH;
    }
}
