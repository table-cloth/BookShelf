package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.Rakuten;

/**
 * Dialog activity to select / input API search category & content
 *
 * Created by Minami on 2014/08/17.
 */
public class SearchContentInputDialogActivity extends DialogBaseActivity {

    int mSpinnerSelectedItemId = 0;

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return R.layout.activity_search_dialog;
    }

    /**
     * Get intent with given extra data
     *
     * @param context context
     * @param titleStrId string id for title
     * @param messageStrId string id for content message
     * @param btnPositiveStrId string id for positive button
     * @param btnNegativeStrId string id for negative button
     * @return Intent instance
     */
    @NonNull
    public static Intent getIntent(@NonNull Context context, int titleStrId, int messageStrId, int btnPositiveStrId, int btnNegativeStrId) {
        Intent intent = new Intent(context, SearchContentInputDialogActivity.class);

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

        initSpinner();
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
            case R.id.btn_positive: // Search
                int selectedItemId =
                        mSpinnerSelectedItemId >= 0
                                && mSpinnerSelectedItemId < Rakuten.SEARCH_KEY_LIST.length
                                ? mSpinnerSelectedItemId
                                : 0;
                String searchKey = Rakuten.SEARCH_KEY_LIST[selectedItemId];
                String searchContent = ((EditText)findViewById(R.id.data_content)).getText().toString();

                finishWithResult(Const.RESULT_CODE.POSITIVE,
                        new Intent().putExtra(Const.INTENT_EXTRA.KEY_STR_SELECTED_KEY, searchKey).
                                putExtra(Const.INTENT_EXTRA.KEY_STR_SELECTED_VALUE, searchContent));
                break;

            case R.id.btn_negative: // cancel
                finishWithResult(Const.RESULT_CODE.NEGATIVE);
                break;
        }
    }

    /**
     * Initialize spinner instance
     * This spinner is used to select API search category
     */
    private void initSpinner() {
        Spinner spinnerView = ((Spinner)findViewById(R.id.spinner_search_content));
        spinnerView.setAdapter(getSpinnerAdapter());
        spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerSelectedItemId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

    }

    /**
     * Get array adapter for spinner
     *
     * @return Spinner adapter instance
     */
    @NonNull
    private ArrayAdapter<String> getSpinnerAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        // init with list to show in spinner
        for(int i = 0 ; i < Rakuten.SEARCH_CONTENT_LIST.length ; i ++) {
            adapter.add(getString(Rakuten.SEARCH_CONTENT_LIST[i]));
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

}
