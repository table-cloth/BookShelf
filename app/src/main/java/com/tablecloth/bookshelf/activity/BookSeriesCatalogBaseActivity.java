package com.tablecloth.bookshelf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookData;
import com.tablecloth.bookshelf.data.BookSeriesData;
import com.tablecloth.bookshelf.db.BookSeriesDao;
import com.tablecloth.bookshelf.db.SettingsDao;
import com.tablecloth.bookshelf.dialog.BookSeriesAddEditDialogActivity;
import com.tablecloth.bookshelf.dialog.SearchContentInputDialogActivity;
import com.tablecloth.bookshelf.http.HttpPostHandler;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.GAEvent;
import com.tablecloth.bookshelf.util.ImageUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ProgressDialogUtil;
import com.tablecloth.bookshelf.util.Rakuten;
import com.tablecloth.bookshelf.util.GooTextConverter;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;

import java.util.ArrayList;

/**
 * Base class for Activity showing catalog of BookSeries
 * All common features in above 2 activities are to be declared in this class
 *
 * Created by Minami on 2015/03/15.
 */
public abstract class BookSeriesCatalogBaseActivity extends BaseActivity implements View.OnClickListener {

    // Number of book series item shown in a page, for API search result
    private final int API_SEARCH_RESULT_SERIES_PER_PAGE = 30;
    // Number of book series item shown in a single row, for grid layout
    private final int BOOK_SERIES_ITEM_COUNT_PER_COLUM_GRID = 4;

    // Catalog adapter
    // Needs to be set to catalog collection view instance in derived class
    protected BookSeriesCatalogAdapter mCatalogAdapter;
    // BookSeriesData list to show in list / grid
    protected ArrayList<BookSeriesData> mBookSeriesDataList = new ArrayList<>();
    // Progress Dialog Util instance
    protected ProgressDialogUtil mProgress;

    // Current show mode
    protected int mShowMode = Const.VIEW_MODE.VIEW;

    // Search related
    protected int mSearchMode = Const.SEARCH_MODE.ALL;
    protected String mSearchContent = "";
    private ArrayList<BookSeriesData> mSearchResultBookSeriesCache = new ArrayList<>();

    // Data accessor
    BookSeriesDao mBookSeriesDao;
    SettingsDao mSettingsDao;

    // View instances
    View mHeaderAreaView;
    View mHeaderModeAreaView;
    View mHeaderSearchAreaView;
    View mHeaderApiSearchAreaView;
    EditText mSearchContentEditText;
    Spinner mSearchContentSpinnerView;

    /**
     * Constructor
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize dao
        mBookSeriesDao = new BookSeriesDao(this);
        mSettingsDao = new SettingsDao(this);

        // initialize view / callbacks
        mHeaderAreaView = findViewById(R.id.header);
        mHeaderModeAreaView = mHeaderAreaView.findViewById(R.id.header_area_mode_view);
        mHeaderSearchAreaView = mHeaderAreaView.findViewById(R.id.header_area_mode_search);
        mHeaderApiSearchAreaView = mHeaderAreaView.findViewById(R.id.header_area_mode_api_search);
        mSearchContentEditText = (EditText)findViewById(R.id.search_content);
        mSearchContentSpinnerView = ((Spinner)findViewById(R.id.spinner_search_content));

        initializeCallbackEvents();

        mProgress = ProgressDialogUtil.getInstance(this);
        mCatalogAdapter = new BookSeriesCatalogAdapter();

        // book catalog activity always starts with View Mode
        switchMode(Const.VIEW_MODE.VIEW);
        refreshData();

        // initialize Ad
        Util.initAdView(this, (ViewGroup) findViewById(R.id.banner));
    }

    /**
     * Switch mode status
     *
     * @param newMode mode type
     */
    private void switchMode(int newMode) {
        switch (newMode) {
            case Const.VIEW_MODE.VIEW:
            default:
                sendGoogleAnalyticsEvent(
                        GAEvent.Type.USER_ACTION, GAEvent.Event.SHOW_MODE_VIEW);
                mHeaderModeAreaView.setVisibility(View.VISIBLE);
                mHeaderSearchAreaView.setVisibility(View.GONE);
                mHeaderApiSearchAreaView.setVisibility(View.GONE);
                break;

            case Const.VIEW_MODE.SEARCH_DB:
                sendGoogleAnalyticsEvent(
                        GAEvent.Type.USER_ACTION, GAEvent.Event.SHOW_MODE_SEARCH);
                mHeaderModeAreaView.setVisibility(View.GONE);
                mHeaderSearchAreaView.setVisibility(View.VISIBLE);
                mHeaderApiSearchAreaView.setVisibility(View.GONE);
                mSearchContentEditText.requestFocus();
                break;

            case Const.VIEW_MODE.SEARCH_API:
                sendGoogleAnalyticsEvent(
                        GAEvent.Type.USER_ACTION, GAEvent.Event.SHOW_MODE_API_SEARCH_RESULT);
                mHeaderModeAreaView.setVisibility(View.GONE);
                mHeaderSearchAreaView.setVisibility(View.GONE);
                mHeaderApiSearchAreaView.setVisibility(View.VISIBLE);
                break;
        }
        mShowMode = newMode;
    }

    /**
     * Refresh data list shown in catalog
     */
    private void refreshData() {

        switch (mShowMode) {

            case Const.VIEW_MODE.SEARCH_DB:
                mBookSeriesDataList = mBookSeriesDao.loadBookSeriesDataList(mSearchMode, mSearchContent);
                break;

            case Const.VIEW_MODE.SEARCH_API:
                mBookSeriesDataList = mSearchResultBookSeriesCache;
                break;

            case Const.VIEW_MODE.VIEW: // default view mode
            default:
                mBookSeriesDataList = mBookSeriesDao.loadAllBookSeriesDataList();
                break;
        }

        notifyDataSetChanged();
    }

    /**
     * Called when needs to refresh catalog views
     */
    private void notifyDataSetChanged() {
        if(mCatalogAdapter == null) {
            return ;
        }
        mCatalogAdapter.notifyDataSetChanged();
    }

    /**
     * Called on resume
     */
    @Override
    public void onResume() {
        super.onResume();
        if(getCurrentShowTypeText().equals(
                getSettingsShowTypeText())) {
            // Refresh in case something is updated while in background
            refreshData();
        } else {
            // activate right activity according to show type setting
            String value = mSettingsDao.load(Const.DB.Settings.KEY.SERIES_SHOW_TYPE, getCurrentShowTypeText());
            startActivity(new Intent(this,
                    Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID.equals(value)
                            ? BookSeriesGridCatalogActivity.class
                            : BookSeriesListCatalogActivity.class));
            finish();
        }
    }

    /**
     * Get show type from Settings
     *
     * @return show type name in Settings
     */
    @NonNull
    private String getSettingsShowTypeText() {
        return mSettingsDao.load(
                Const.DB.Settings.KEY.SERIES_SHOW_TYPE,
                Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID);
    }

    /**
     * Get current show type
     *
     * @return current show type name
     */
    @NonNull
    private String getCurrentShowTypeText() {
        return isGridCatalog()
                ? Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID
                : Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_LIST;
    }

    /**
     * Called on key down
     *
     * @param keyCode key code
     * @param event event
     * @return false if current mode is not view mode
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && mShowMode != Const.VIEW_MODE.VIEW) {
            switchMode(Const.VIEW_MODE.VIEW);
            refreshData();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Called on activty result
     *
     * @param requestCode request code
     * @param resultCode result code
     * @param data intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Const.REQUEST_CODE.SERIES_LIST_ADD_SERIES: // Return from book series add screen
                if(resultCode == Const.RESULT_CODE.POSITIVE) {
                    switchMode(Const.VIEW_MODE.VIEW);
                    refreshData();
                    ToastUtil.show(this, R.string.series_data_done_add_series);
                    sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.ADD_SERIES);

                }
                break;

            case Const.REQUEST_CODE.UPDATE_DIALOG: // Return from update dialog
                if(resultCode == Const.RESULT_CODE.POSITIVE) {
                    // Activate GooglePlay marker
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(Util.getMarketUriStr(getPackageName(),
                                    getString(R.string.referrer_update_dialog)))));
                }
                break;

            case Const.REQUEST_CODE.SELECT_ADD_SERIES_TYPE: // Return from "select how to book series" dialog
                if(resultCode == Const.RESULT_CODE.POSITIVE) {
                    Intent intent;
                    int selectedBtnId = data.getIntExtra(
                            Const.INTENT_EXTRA.KEY_INT_SELECTED_ID,
                            Const.INTENT_EXTRA.VALUE_SELECTED_BTN_SEARCH);
                    switch (selectedBtnId) {
                        case Const.INTENT_EXTRA.VALUE_SELECTED_BTN_SEARCH: // search using API
                        default:
                            intent = SearchContentInputDialogActivity.getIntent(
                                    this,
                                    R.string.series_data_search,
                                    R.string.series_data_search_select_topic,
                                    R.string.search,
                                    R.string.cancel);
                            startActivityForResult(intent, Const.REQUEST_CODE.LIST_SEARCH_RAKUTEN);
                            sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_ADD_SERIES_SEARCH_BTN);
                            break;

                        case Const.INTENT_EXTRA.VALUE_SELECTED_BTN_MANUAL: // register manually
                            intent = BookSeriesAddEditDialogActivity.getIntent(
                                    this,
                                    R.string.seires_data_add_series_data,
                                    R.string.add,
                                    BookData.BOOK_SERIES_ERROR_VALUE);
                            startActivityForResult(intent, Const.REQUEST_CODE.SERIES_LIST_ADD_SERIES);
                            sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_ADD_SERIES_MANUAL_BTN);
                            break;
                    }
                }
                break;

            case Const.REQUEST_CODE.LIST_SEARCH_RAKUTEN: // Return from search with Rakuten API
                if(resultCode == Const.RESULT_CODE.POSITIVE) {

                    mProgress.show(mHandler, getString(R.string.searching_now), null);

                    String selectKey = data.getStringExtra(Const.INTENT_EXTRA.KEY_STR_SELECTED_KEY);
                    String selectValue = data.getStringExtra(Const.INTENT_EXTRA.KEY_STR_SELECTED_VALUE);

                    Rakuten.searchFromRakutenAPI(this, mHandler, selectKey, selectValue,
                            new Rakuten.SearchResultListener() {
                                @Override
                                public void onSearchSuccess(@NonNull String searchResults) {
                                    mSearchResultBookSeriesCache = Rakuten.convertJsonText2BookSeriesDataList(
                                            BookSeriesCatalogBaseActivity.this,
                                            searchResults,
                                            API_SEARCH_RESULT_SERIES_PER_PAGE);

                                    mProgress.dismiss();

                                    switchMode(Const.VIEW_MODE.SEARCH_API);
                                    refreshData();
                                }

                                @Override
                                public void onSearchError(@NonNull String errorContent) {
                                    ToastUtil.show(
                                            BookSeriesCatalogBaseActivity.this,
                                            R.string.series_data_error_no_search_result_found_please_retry);
                                }
                            });
                }
                break;
        }
    }

    /**
     * Get ArrayAdapter for spinner
     * Referenced: http://techbooster.jpn.org/andriod/ui/606/
     *
     * @return String ArrayAdapter for spinner
     */
    @NonNull
    private ArrayAdapter<String> getSpinnerAdapter() {
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        String[] searchModeList = new String[] {
                getString(R.string.search_mode_all),
                getString(R.string.search_mode_title),
                getString(R.string.search_mode_author),
                getString(R.string.search_mode_magazine),
                getString(R.string.search_mode_company),
                getString(R.string.search_mode_tag),
        };

        // Lists to show in spinner
        for(int i = 0 ; i < searchModeList.length ; i ++) {
            adapter.add(searchModeList[i]);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
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
            case R.id.add_button: // Add book series
                //        HttpPostTask httpPostTask = new HttpPostTask(
//                this,
//                "https://labs.goo.ne.jp/api/hiragana",
//                new HttpPostHandler() {
//                    @Override
//                    public void onPostSuccess(String response) {
//                        Log.d("Response", "SUCCESS : " + response);
//                    }
//
//                    @Override
//                    public void onPostFail(String response) {
//                        Log.d("Response", "FAIL : " + response);
//                    }
//                });

                GooTextConverter.convert2Katakana(
                        this,
                        "僕らの七日間戦争・そして〜伝説へ＝",
                        new HttpPostHandler() {
                            @Override
                            public void onPostSuccess(String response) {
                                String converted = GooTextConverter.getConvertedText(response);
                                Log.d("Response", "SUCCESS : " + converted);
                            }

                            @Override
                            public void onPostFail(String response) {
                                Log.d("Response", "ERROR : " + response);
                            }
                        });

//                startActivityForResult(
//                        BookSeriesSelectAddTypeDialogActivity.getIntent(
//                                this,
//                                R.string.series_data_add_select_how_short,
//                                R.string.series_data_add_select_how_long,
//                                R.string.decide,
//                                R.string.cancel),
//                        Const.REQUEST_CODE.SELECT_ADD_SERIES_TYPE);
//                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_ADD_SERIES_BTN);
                break;

            case R.id.btn_search: // Search book series registered
                switchMode(Const.VIEW_MODE.SEARCH_DB);
                refreshData();
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_SEARCH_BTN);
                break;

            case R.id.btn_cancel: // Cancel search book series registered / cancel API search
                switchMode(Const.VIEW_MODE.VIEW);
                refreshData();
                break;

            case R.id.btn_settings:
                startActivity(SettingsActivity.getIntent(this));
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_SETTINGS_BTN);
                break;

        }
    }

    /**
     * Initialize all callbacks
     */
    private void initializeCallbackEvents() {

        findViewById(R.id.add_button).setOnClickListener(this);
        mHeaderModeAreaView.findViewById(R.id.btn_search).setOnClickListener(this);
        mHeaderSearchAreaView.findViewById(R.id.btn_cancel).setOnClickListener(this); //
        mHeaderApiSearchAreaView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_settings).setOnClickListener(this);

        // Search type select spinner
        mSearchContentSpinnerView.setAdapter(getSpinnerAdapter());
        mSearchContentSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update search mode depending on item selected
                // Do make sure selection list in spinner,
                // is shown in same order as value of each search mode
                mSearchMode = position;
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Search content edit text
        ((EditText)findViewById(R.id.search_content)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update catalog every time text is changed
                if(s == null) mSearchContent = "";
                else mSearchContent = String.valueOf(s);
                refreshData();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // do nothing
            }
        });
    }

    /**
     * Initializes view for bookSeries in listView / gridView
     *
     * @param position position of the item in list / grid
     * @param convertView View for list / grid item
     * @param parentView Parent view of convertView
     * @param bookSeriesData BookSeriesData instance to show
     * @param isGridView whether the view is for grid or list
     * @return initialized View
     */
    @NonNull
    private View initializeBookSeriesItemView(int position, @Nullable View convertView, @Nullable ViewGroup parentView, @NonNull final BookSeriesData bookSeriesData, boolean isGridView) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(
                    // layout id
                    isGridView
                            ? R.layout.book_grid_item
                            : R.layout.book_list_item,
                    // root
                    null);
        }

        // is showing search result on internet
        boolean isAPISearchMode = mShowMode == Const.VIEW_MODE.SEARCH_API;

        // init final values
        final boolean doUseImageCache = !isAPISearchMode
                && BookData.isValidBookSeriesId(bookSeriesData.getSeriesId());
        final ImageView bookCoverImageView = (ImageView)convertView.findViewById(R.id.book_cover_image);
        // set no_image for default
        bookCoverImageView.setImageResource(R.drawable.no_image);
        bookCoverImageView.setTag(bookSeriesData.getTitle());

        // init non-final values
        ViewGroup tagContainer = (ViewGroup)convertView.findViewById(R.id.tag_container);
        TextView titleTextView = (TextView)convertView.findViewById(R.id.title);
        TextView authorTextView = (TextView)convertView.findViewById(R.id.author);
        TextView volumeTextView = (TextView)convertView.findViewById(R.id.volume);
        // volumes are only used when looking at non-search results
        volumeTextView.setVisibility(isAPISearchMode
                ? View.GONE
                : View.VISIBLE);

        // do not use image cache when searching on internet
        Bitmap imageCache = doUseImageCache
                ? ImageUtil.getImageCache(bookSeriesData.getSeriesId())
                : null;

        // set border for gridView
        if(isGridView) {
            // border above view
            convertView.findViewById(R.id.border_top).setVisibility(
                    position < BOOK_SERIES_ITEM_COUNT_PER_COLUM_GRID
                            ? View.VISIBLE
                            : View.GONE);
            // border below view
            convertView.findViewById(R.id.border_top).setVisibility(View.VISIBLE);
            // border left of view
            convertView.findViewById(R.id.border_left).setVisibility(
                    position % BOOK_SERIES_ITEM_COUNT_PER_COLUM_GRID == 0
                            ? View.VISIBLE
                            : View.GONE);
            // border right of view
            convertView.findViewById(R.id.border_right).setVisibility(View.VISIBLE);
        }

        // set basic values for each view
        titleTextView.setText(bookSeriesData.getTitle());
        authorTextView.setText(bookSeriesData.getAuthor());
        if(volumeTextView.getVisibility() == View.VISIBLE) {
            volumeTextView.setText(bookSeriesData.getVolumeText());
        }

        // set tag data
        tagContainer.removeAllViews();
        ArrayList<ViewGroup> tagViewList = ViewUtil.getTagViewList(
                this, bookSeriesData.getTagsAsList(), ViewUtil.TAGS_LAYOUT_TYPE_SMALL);
        for(ViewGroup tagView : tagViewList) {
            tagContainer.addView(tagView);
        }

        // TODO check whether this invalidate is necessary
        tagContainer.invalidate();

        // set image data
        if(imageCache != null) {
            bookCoverImageView.setImageBitmap(imageCache);
        } else {
            bookSeriesData.loadImage(mHandler, this, new ListenerUtil.LoadBitmapListener() {
                @Override
                public void onFinish(@NonNull Bitmap bitmap) {
                    if(Util.isEqual(bookSeriesData.getTitle(),
                            String.valueOf(bookCoverImageView.getTag()))) {
                        bookCoverImageView.setImageBitmap(bitmap);
                        if(doUseImageCache) {
                            ImageUtil.setImageCache(bookSeriesData.getSeriesId(), bitmap);
                        }
                    }
                }

                @Override
                public void onError() {
                }
            });
        }

        // Set click listener
        // When current mode is search mode, move to adding series screen
        // else, move to check series detail screen
        convertView.setOnClickListener(isAPISearchMode
                ? getOnClick4AddSeriesConfirmScreen(bookSeriesData)
                : getOnClick4StartSeriesDetailIntent(bookSeriesData.getSeriesId()));

        return convertView;
    }

    /**
     * Get OnClickListener instance for starting book series detail screen
     * Error toast will be shown if the given seriesId is not found in DB
     *
     * @param seriesId id for book series. Invalid if < 0.
     * @return OnClickListener instance
     */
    @NonNull
    private View.OnClickListener getOnClick4StartSeriesDetailIntent(final int seriesId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start activity or show error text if invalid seriesId
                if(mBookSeriesDao.isBookSeriesRegistered(seriesId)) {
                    startActivity(SeriesDetailActivity.getIntent(BookSeriesCatalogBaseActivity.this, seriesId));
                } else {
                    ToastUtil.show(BookSeriesCatalogBaseActivity.this, R.string.series_data_error_invalid_series_id_found);
                }
            }
        };
    }

    /**
     * Get OnClickListener instance for starting book series add confirm screen
     *
     * @param bookSeriesData BookSeries data instance
     * @return OnClickListener instance
     */
    @NonNull
    private View.OnClickListener getOnClick4AddSeriesConfirmScreen(final BookSeriesData bookSeriesData) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        // intent
                        BookSeriesAddEditDialogActivity.getIntent(
                                BookSeriesCatalogBaseActivity.this,
                                R.string.series_data_confirm_add_series,
                                R.string.add,
                                bookSeriesData),
                        // request code
                        Const.REQUEST_CODE.SERIES_LIST_ADD_SERIES);
            }
        };
    }

    /**
     * Adapter for catalog collection
     */
    private class BookSeriesCatalogAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBookSeriesDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBookSeriesDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return initializeBookSeriesItemView(position, convertView, parent,
                    mBookSeriesDataList.get(position), isGridCatalog());
        }
    }

    /**
     * Check if current catalog is shown in Grid format
     *
     * @return whether is Grid format
     */
    abstract protected boolean isGridCatalog();

    /**
     * Get book series catalog view
     *
     * @return BookSeries catalog view
     */
    abstract protected View getBookSeriesCatalogView();
}