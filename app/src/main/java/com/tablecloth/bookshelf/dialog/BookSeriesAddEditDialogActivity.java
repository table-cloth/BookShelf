package com.tablecloth.bookshelf.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookData;
import com.tablecloth.bookshelf.db.BookSeriesDao;
import com.tablecloth.bookshelf.data.BookSeriesData;
import com.tablecloth.bookshelf.util.G;
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

    private ViewGroup mTagContainer = null;

    // static BookData to use for API search,
    // where there is info, but seriesId is -1
    private static BookSeriesData sTemporaryBookSeriesData = null;

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
     * @param bookSeriesData book series instance to save in this dialog's tmp file
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
        mBookSeriesData = mBookSeriesDao.loadBookSeriesData(getBookSeriesId());


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
                        G.REQUEST_CODE_TAGS_EDIT);
                break;

            case R.id.btn_delete: // delete current series
                startActivityForResult(
                        SimpleDialogActivity.getIntent(
                                this,
                                R.string.delete_book_series,
                                R.string.delete_book_series_warning,
                                R.string.delete,
                                R.string.cancel),
                        G.REQUEST_CODE_SIMPLE_CHECK);
                break;

            case R.id.btn_back: // cancel current action
                finishWithResult(G.RESULT_NONE);
                break;

            case R.id.btn_positive: // save current data
                saveBookSeries();
                finishWithResult(G.RESULT_POSITIVE,
                        new Intent().putExtra(
                                G.RESULT_DATA_KEY_EDIT_SERIES,
                                G.RESULT_DATA_VALUE_EDIT_SERIES_EDIT));
                break;
        }
    }

    /**
     * Save book series with currently shown data
     */
    private void saveBookSeries() {
        String title = getRowContents(R.id.data_detail_row_title);

        // fail to save if the title is empty
        if(Util.isEmpty(title)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show(BookSeriesAddEditDialogActivity.this,
                            R.string.series_data_error_title_is_empty);
                }
            });
            return;
        }

        String titlePronunciation =getRowContents(R.id.data_detail_row_title_pronunciation);
        String author = getRowContents(R.id.data_detail_row_author);
        String authodPronunciation =getRowContents(R.id.data_detail_row_author_pronunciation);
        String magazine = getRowContents(R.id.data_detail_row_magazine);
        String magazinePronunciation =getRowContents(R.id.data_detail_row_magazine_pronunctaion);
        String company = getRowContents(R.id.data_detail_row_company);
        String memo = getRowContents(R.id.data_detail_row_memo);

        mBookSeriesData.setTitle(title);
        mBookSeriesData.setTitlePronunciation(titlePronunciation);
        mBookSeriesData.setAuthor(author);
        mBookSeriesData.setAuthorPronunciation(authodPronunciation);
        mBookSeriesData.setAuthor(magazine);
        mBookSeriesData.setAuthorPronunciation(magazinePronunciation);
        mBookSeriesData.setAuthor(company);
        mBookSeriesData.setAuthor(memo);

        mBookSeriesDao.saveSeries(mBookSeriesData);
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
            case G.REQUEST_CODE_SIMPLE_CHECK: // Return from book series delete confirm
                if(resultCode == G.RESULT_POSITIVE) {
                    // success to delete
                    if(mBookSeriesDao.deleteBookSeries(mBookSeriesData.getSeriesId())) {
                        ToastUtil.show(BookSeriesAddEditDialogActivity.this,
                                R.string.delete_book_series_done);
                        finishWithResult(G.RESULT_POSITIVE,
                                new Intent().putExtra(
                                        G.RESULT_DATA_KEY_EDIT_SERIES,
                                        G.RESULT_DATA_VALUE_EDIT_SERIES_DELETE));
                    // fail to delete
                    } else {
                        ToastUtil.show(BookSeriesAddEditDialogActivity.this,
                                R.string.delete_book_series_done_fail);
                    }
                }
                break;

            case G.REQUEST_CODE_TAGS_EDIT: // Return from tag edit screen
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

        // hint for book series title
        View titleViewArea = findViewById(R.id.data_detail_row_title);
        ((EditText)titleViewArea.findViewById(R.id.data_content)).setHint(R.string.hint_must);
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
        setRowContents(R.id.data_detail_row_magazine_pronunctaion,
                R.string.book_series_data_magazine_pronunciation,
                bookSeriesData.getMagazinePronunciation());

        // Company
        setRowContents(R.id.data_detail_row_company,
                R.string.book_series_data_company,
                bookSeriesData.getCompany());

        // Memo
        setRowContents(R.id.data_detail_row_memo,
                R.string.book_series_data_memo,
                bookSeriesData.getCompanyPronunciation());
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
     * Re-load tags from BookSeriesData & invalidate
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
