package com.tablecloth.bookshelf.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.activity.SettingsActivity;
import com.tablecloth.bookshelf.db.BookSeriesDao;
import com.tablecloth.bookshelf.data.BookSeriesData;
import com.tablecloth.bookshelf.db.SettingsDao;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ProgressDialogUtil;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;

import java.util.ArrayList;

/**
 * Dialog activity to add / edit book series
 *
 * Created by Minami on 2014/08/17.
 */
public class BookSeriesAddEditDialogActivity extends DialogBaseActivity {

    private BookSeriesData mBookSeriesData;
    private BookSeriesDao mBookSeriesDao;
    private SettingsDao mSettingsDao;

    private ViewGroup mTagContainer = null;
    private View mPronunciationInputAreaView = null;
    private Button mPronunciationOpenCloseButtonView = null;

    // static BookData to use for API search,
    // where there is info, but seriesId is -1
    private static BookSeriesData sTemporaryBookSeriesData = null;

    private ProgressDialogUtil mProgressUtil = null;

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return R.layout.activity_edit_series_dialog;
    }

    /**
     * Get intent with given extra data
     *
     * @param context context
     * @param titleStrId string id for title
     * @param btnPositiveStrId string id for positive button
     * @param bookSeriesId book series id
     * @return Intent instance
     */
    @NonNull
    public static Intent getIntent(@NonNull Context context, int titleStrId, int btnPositiveStrId, int bookSeriesId) {
        Intent intent = new Intent(context, BookSeriesAddEditDialogActivity.class);

        intent.putExtra(KEY_TITLE_STR_ID, titleStrId);
        intent.putExtra(KEY_BTN_POSITIVE_STR_ID, btnPositiveStrId);
        intent.putExtra(KEY_BOOK_SERIES_ID, bookSeriesId);

        return intent;
    }

    /**
     * Get intent with given extra data
     *
     * @param context context
     * @param titleStrId string id for title
     * @param btnPositiveStrId string id for positive button
     * @param bookSeriesData book series instance to saveInt in this dialog's tmp file
     * @return Intent instance
     */
    @NonNull
    public static Intent getIntent(@NonNull Context context, int titleStrId, int btnPositiveStrId, @NonNull BookSeriesData bookSeriesData) {
        sTemporaryBookSeriesData = bookSeriesData;

        Intent intent = new Intent(context, BookSeriesAddEditDialogActivity.class);

        intent.putExtra(KEY_TITLE_STR_ID, titleStrId);
        intent.putExtra(KEY_BTN_POSITIVE_STR_ID, btnPositiveStrId);

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

        mBookSeriesDao = new BookSeriesDao(this);
        mSettingsDao = new SettingsDao(this);

        mBookSeriesData = mBookSeriesDao.loadBookSeriesData(getBookSeriesId());

        mProgressUtil = ProgressDialogUtil.getInstance(this);

        // Add new book series
        if(mBookSeriesData == null) {
            mBookSeriesData = sTemporaryBookSeriesData == null
                    ? new BookSeriesData(this)
                    : sTemporaryBookSeriesData;
            initUI4AddBookSeries();

        // Edit registered book series
        } else {
            initUI4EditBookSeries();
        }

        findViewById(R.id.btn_tag_edit).setOnClickListener(this);
        findViewById(R.id.btn_positive).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
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

            case R.id.btn_tag_edit: // start tag edit
                startActivityForResult(
                        TagsEditDialogActivity.getIntent(
                                this,
                                R.string.tag_edit,
                                R.string.finish,
                                mBookSeriesData.getRawTags()),
                        Const.REQUEST_CODE.TAGS_EDIT);
                break;

            case R.id.btn_delete: // delete current series
                startActivityForResult(
                        SimpleDialogActivity.getIntent(
                                this,
                                R.string.delete_book_series,
                                R.string.delete_book_series_warning,
                                R.string.delete,
                                R.string.cancel),
                        Const.REQUEST_CODE.SIMPLE_CHECK);
                break;

            case R.id.btn_back: // cancel current action
                finishWithResult(Const.RESULT_CODE.NONE);
                break;

            case R.id.btn_positive: // saveInt current data
                updateInputData2BookSeriesData();
                // fail to saveInt if the title is empty
                if(!isBookSeriesDataValid()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(BookSeriesAddEditDialogActivity.this,
                                    R.string.series_data_error_title_is_empty);
                        }
                    });
                    return;
                }

                String autoSavePronunciationSettings =
                        mSettingsDao.load(
                                Const.DB.Settings.KEY.BOOK_SERIES_AUTO_SAVE_PRONUNCIATION,
                                Const.DB.Settings.VALUE.BOOK_SERIES_AUTO_SAVE_PRONUNCIATION_ON);

                // Update pronunciation info, and then save
                if(Util.isEqual(
                        autoSavePronunciationSettings,
                        Const.DB.Settings.VALUE.BOOK_SERIES_AUTO_SAVE_PRONUNCIATION_ON)) {

                    // show progress dialog
                    mProgressUtil.show(
                            mHandler,
                            getString(R.string.updating_pronunciation_data),
                            null,
                            ProgressDialog.STYLE_HORIZONTAL,
                            1, // only update for this book
                            new ListenerUtil.OnFinishListener() {
                                @Override
                                public void onFinish() {

                                    mBookSeriesData.updateAllPronunciationTextData(new ListenerUtil.OnFinishListener() {
                                        @Override
                                        public void onFinish() {

                                            mProgressUtil.dismiss();
                                            finishAfterSave();
                                        }
                                    });
                                }
                            });

                // Save without checking pronunciation
                } else {
                    finishAfterSave();
                }
                break;
        }
    }

    /**
     * Save current book series data and finish activity
     */
    private void finishAfterSave() {
        mBookSeriesDao.saveSeries(mBookSeriesData);

        ToastUtil.show(
                BookSeriesAddEditDialogActivity.this,
                R.string.series_data_done_add_series);

        finishWithResult(Const.RESULT_CODE.POSITIVE,
                new Intent().putExtra(
                        Const.INTENT_EXTRA.KEY_INT_EDIT_SERIES,
                        Const.INTENT_EXTRA.VALUE_EDIT_SERIES_EDIT));
    }

    @Override
    public void onPause() {
        super.onPause();
        mProgressUtil.onPause();
    }

    /**
     * Put user input data into BookSeriesData
     */
    private void updateInputData2BookSeriesData() {
        String title = getRowContents(R.id.data_detail_row_title);
        String titlePronunciation =getRowContents(R.id.data_detail_row_title_pronunciation);
        String author = getRowContents(R.id.data_detail_row_author);
        String authorPronunciation =getRowContents(R.id.data_detail_row_author_pronunciation);
        String magazine = getRowContents(R.id.data_detail_row_magazine);
        String magazinePronunciation =getRowContents(R.id.data_detail_row_magazine_pronunciation);
        String company = getRowContents(R.id.data_detail_row_company);
        String companyPronunciation = getRowContents(R.id.data_detail_row_company_pronunciation);
        String memo = getRowContents(R.id.data_detail_row_memo);

        mBookSeriesData.setTitle(title);
        mBookSeriesData.setTitlePronunciation(titlePronunciation);
        mBookSeriesData.setAuthor(author);
        mBookSeriesData.setAuthorPronunciation(authorPronunciation);
        mBookSeriesData.setMagazine(magazine);
        mBookSeriesData.setMagazinePronunciation(magazinePronunciation);
        mBookSeriesData.setCompany(company);
        mBookSeriesData.setCompanyPronunciation(companyPronunciation);
        mBookSeriesData.setMemo(memo);
    }

    /**
     * Check if the data needed to save BookSeriesData, is valid
     *
     * @return Whether needed data is all set
     */
    private boolean isBookSeriesDataValid() {
        return !Util.isEmpty(mBookSeriesData.getTitle());
    }

    /**
     * On activity result
     *
     * @param requestCode request code
     * @param resultCode result code
     * @param data Intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.REQUEST_CODE.SIMPLE_CHECK: // Return from book series delete confirm
                if(resultCode == Const.RESULT_CODE.POSITIVE) {
                    // success to delete
                    if(mBookSeriesDao.deleteBookSeries(mBookSeriesData.getSeriesId())) {
                        ToastUtil.show(BookSeriesAddEditDialogActivity.this,
                                R.string.delete_book_series_done);
                        finishWithResult(Const.RESULT_CODE.POSITIVE,
                                new Intent().putExtra(
                                        Const.INTENT_EXTRA.KEY_INT_EDIT_SERIES,
                                        Const.INTENT_EXTRA.VALUE_EDIT_SERIES_DELETE));
                    // fail to delete
                    } else {
                        ToastUtil.show(BookSeriesAddEditDialogActivity.this,
                                R.string.delete_book_series_done_fail);
                    }
                }
                break;

            case Const.REQUEST_CODE.TAGS_EDIT: // Return from tag edit screen
                if(data != null) {
                    String tagsStr = data.getStringExtra(TagsEditDialogActivity.KEY_RAW_TAGS);
                    mBookSeriesData.setRawTags(tagsStr);
                    updateTagContainer();
                }
                break;
        }
    }

    /**
     * Initialize UI for add book series
     */
    private void initUI4AddBookSeries() {
        initUIWithBookSeries(sTemporaryBookSeriesData == null
                ? mBookSeriesData
                : sTemporaryBookSeriesData);
        initUI4CommonFeatures();
        findViewById(R.id.btn_delete).setVisibility(View.GONE);
    }

    /**
     * Initialize UI for edit book series
     */
    private void initUI4EditBookSeries() {
        updateTagContainer();
        initUIWithBookSeries(mBookSeriesData);
        initUI4CommonFeatures();
        findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);
    }

    /**
     * Initialize UI for common feature
     */
    private void initUI4CommonFeatures() {
        setTitleText(R.id.title);
        setBtnPositiveText(R.id.btn_positive);

        // Hint for book series title
        View titleViewArea = findViewById(R.id.data_detail_row_title);
        ((EditText)titleViewArea.findViewById(R.id.data_content)).setHint(R.string.hint_must);

        // For pronunciation enter area
        mPronunciationInputAreaView = findViewById(R.id.data_pronunciation_container);

        View inputOpenCloseAreaView = findViewById(R.id.data_area_show_pronunciation);
        TextView kanaAreaContentTextView = (TextView) inputOpenCloseAreaView.findViewById(R.id.data_name);
        if(kanaAreaContentTextView != null) {
            kanaAreaContentTextView.setText(R.string.book_series_data_enter_pronunciation_by_manual);
        }
        mPronunciationOpenCloseButtonView = (Button) inputOpenCloseAreaView.findViewById(R.id.data_btn);
        if(mPronunciationOpenCloseButtonView != null) {
            mPronunciationOpenCloseButtonView.setText(R.string.open);
            mPronunciationOpenCloseButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentVisibility = mPronunciationInputAreaView.getVisibility();
                    if(currentVisibility == View.VISIBLE) {
                        mPronunciationInputAreaView.setVisibility(View.GONE);
                        mPronunciationOpenCloseButtonView.setText(R.string.open);
                    } else {
                        mPronunciationInputAreaView.setVisibility(View.VISIBLE);
                        mPronunciationOpenCloseButtonView.setText(R.string.close);
                    }
                }
            });
        }
        mPronunciationInputAreaView.setVisibility(View.GONE);
        mPronunciationOpenCloseButtonView.setText(R.string.open);
    }


    /**
     * Initialize UI using intent extra data
     */
    private void initUIWithBookSeries(BookSeriesData bookSeriesData) {
        // Set views for book series detail
        // title
        setRowContents(R.id.data_detail_row_title,
                R.string.book_series_data_title,
                bookSeriesData.getTitle());
        setRowContents(R.id.data_detail_row_title_pronunciation,
                R.string.book_series_data_title_pronunciation,
                bookSeriesData.getTitlePronunciation());

        // author
        setRowContents(R.id.data_detail_row_author,
                R.string.book_series_data_author,
                bookSeriesData.getAuthor());
        setRowContents(R.id.data_detail_row_author_pronunciation,
                R.string.book_series_data_author_pronunciation,
                bookSeriesData.getAuthorPronunciation());

        // Magazine
        setRowContents(R.id.data_detail_row_magazine,
                R.string.book_series_data_magazine,
                bookSeriesData.getMagazine());
        setRowContents(R.id.data_detail_row_magazine_pronunciation,
                R.string.book_series_data_magazine_pronunciation,
                bookSeriesData.getMagazinePronunciation());

        // Company
        setRowContents(R.id.data_detail_row_company,
                R.string.book_series_data_company,
                bookSeriesData.getCompany());
        setRowContents(R.id.data_detail_row_company_pronunciation,
                R.string.book_series_data_company_pronunciation,
                bookSeriesData.getCompanyPronunciation());

        // Memo
        setRowContents(R.id.data_detail_row_memo,
                R.string.book_series_data_memo,
                bookSeriesData.getMemo());
    }

    /**
     * Set row name & content
     *
     * @param rowViewId view id for data row
     * @param dataNameStrId string id for data name
     * @param dataContent string for data content
     */
    private void setRowContents(int rowViewId, int dataNameStrId, @Nullable String dataContent) {
        View rowView =findViewById(rowViewId);
        ((TextView)rowView.findViewById(R.id.data_name)).setText(dataNameStrId);
        ((EditText)rowView.findViewById(R.id.data_content)).setText(dataContent);
    }

    /**
     * Get row content
     *
     * @param rowViewId view id for data row
     * @return string for data content
     */
    @NonNull
    private String getRowContents(int rowViewId) {
        View rowView =findViewById(rowViewId);
        return ((TextView)rowView.findViewById(R.id.data_content)).getText().toString();
    }

    /**
     * Re-loadInt tags from BookSeriesData & invalidate
     */
    private void updateTagContainer() {
        if(mTagContainer == null) {
            mTagContainer = (ViewGroup)findViewById(R.id.tag_container);
        }

        mTagContainer.removeAllViews();
        ArrayList<ViewGroup> tagViewList =
                ViewUtil.getTagViewList(
                        this,
                        mBookSeriesData.getTagsAsList(),
                        ViewUtil.TAGS_LAYOUT_TYPE_NORMAL);

        for(ViewGroup tagView : tagViewList) {
            mTagContainer.addView(tagView);
        }
        mTagContainer.invalidate();
    }
}
