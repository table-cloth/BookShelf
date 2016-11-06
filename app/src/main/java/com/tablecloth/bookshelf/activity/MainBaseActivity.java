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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookData;
import com.tablecloth.bookshelf.db.BookSeriesDao;
import com.tablecloth.bookshelf.data.SeriesData;
import com.tablecloth.bookshelf.dialog.BtnListDialogActivity;
import com.tablecloth.bookshelf.dialog.EditSeriesDialogActivity;
import com.tablecloth.bookshelf.dialog.SearchDialogActivity;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.GAEvent;
import com.tablecloth.bookshelf.util.ImageUtil;
import com.tablecloth.bookshelf.util.IntentUtil;
import com.tablecloth.bookshelf.util.JsonUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ProgressUtil;
import com.tablecloth.bookshelf.util.Rakuten;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.VersionUtil;
import com.tablecloth.bookshelf.util.ViewUtil;
import com.tablecloth.bookshelf.view.BookCoverImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minami on 2015/03/15.
 * ListActivity / GridActivity両方で行う共通の処理・変数等はこちらに保持しておく
 */
public abstract class MainBaseActivity extends BaseActivity {

    // number of book series item shown in a single row, for grid layout
    final private int BOOK_SERIES_ITEM_COUNT_PER_COLUM_GRID = 4;

    protected Spinner spinnerView;
    protected ArrayList<SeriesData> mDataArrayList = new ArrayList<SeriesData>();

    // 楽天WebAPIでの検索結果の保持用変数
    protected ArrayList<SeriesData> mApiSearchResultArrayList = new ArrayList<SeriesData>();
    protected JSONObject mAPISearchResultJsonObject = null;

    protected ProgressUtil mProgress;

    // データ編集時用のID情報一時保管庫
    // 使い終わったらnullにする
    protected int[] mSelectSeriesIds = null;

    // 操作モード
    protected int mMode = G.MODE_VIEW;


    protected int mSearchMode = G.SEARCH_MODE_ALL;
    protected String mSearchContent = "";

    BookSeriesDao mBookSeriesDao;

    // 各種ヘッダー
    View mHeader;
    View mHeaderView;
    View mHeaderSearch;
    View mHeaderApiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgress = new ProgressUtil(MainBaseActivity.this);
        mBookSeriesDao = new BookSeriesDao(MainBaseActivity.this);

        mHeader = findViewById(R.id.header);
        mHeaderView = mHeader.findViewById(R.id.header_area_mode_view);
        mHeaderSearch = mHeader.findViewById(R.id.header_area_mode_search);
        mHeaderApiSearch = mHeader.findViewById(R.id.header_area_mode_api_search);

        switchMode(G.MODE_VIEW);

        // 広告の初期化処理
        Util.initAdview(this, (ViewGroup) findViewById(R.id.banner));

        // バージョン情報の確認・アップデートダイアログの表示・及びバージョン情報の更新
        VersionUtil versionUtil = new VersionUtil(MainBaseActivity.this);
        versionUtil.showUpdateDialog();

        setClickListener();
        setOtherListener();
    }

    /**
     * 一覧表示・検索表示等のモードを切り替える
     *
     * @param newMode
     */
    private void switchMode(int newMode) {
        switch (newMode) {
            case G.MODE_VIEW:
            default:
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SHOW_MODE_VIEW);
                mHeaderView.setVisibility(View.VISIBLE);
                mHeaderSearch.setVisibility(View.GONE);
                mHeaderApiSearch.setVisibility(View.GONE);
//                findViewById(R.id.btn_search).setVisibility(View.VISIBLE);
                break;

            case G.MODE_SEARCH:
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SHOW_MODE_SEARCH);
                mHeaderView.setVisibility(View.GONE);
                mHeaderSearch.setVisibility(View.VISIBLE);
                mHeaderApiSearch.setVisibility(View.GONE);
                boolean flag = findViewById(R.id.search_content).requestFocus();
                findViewById(R.id.search_content).setVisibility(View.VISIBLE);
//                findViewById(R.id.btn_search).setVisibility(View.VISIBLE);
                break;
            case G.MODE_API_SEARCH_RESULT:
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SHOW_MODE_API_SEARCH_RESULT);
                mHeaderView.setVisibility(View.GONE);
                mHeaderSearch.setVisibility(View.GONE);
                mHeaderApiSearch.setVisibility(View.VISIBLE);
//                findViewById(R.id.btn_search).setVisibility(View.GONE);
                break;
        }
        mMode = newMode;
    }

    /**
     * 一覧情報を再取得・再表示する
     */
    private void refreshData() {

        switch (mMode) {
            case G.MODE_VIEW:
                mDataArrayList = mBookSeriesDao.loadAllBookSeriesDataList();
                break;
            case G.MODE_SEARCH:
                mDataArrayList = mBookSeriesDao.loadBookSeriesDataList(mSearchMode, mSearchContent);
                break;
            // 検索結果を表示する。
            case G.MODE_API_SEARCH_RESULT:
                mDataArrayList = mApiSearchResultArrayList;
                break;
            // Safety net. Show everything if mode is invalid
            default:
                mDataArrayList = mBookSeriesDao.loadAllBookSeriesDataList();
                break;
        }

        notifyDataSetChanged();
    }

    /**
     * データに変動があり、notifyDataSetChanged()が呼び出される必要があるときに呼ばれる
     */
    protected abstract void notifyDataSetChanged();

    @Override
    public void onResume() {
        super.onResume();
        if(!isShowTypeCorrect()) {
            // 設定に合っている画面を開き、この画面を閉じる
            String value = mSettings.load(Const.DB.Settings.KEY.SERIES_SHOW_TYPE, getShowType());
            if(Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_LIST.equals(value)) {
                startActivity(new Intent(this, ListActivity.class));
                MainBaseActivity.this.finish();
            } else {
                startActivity(new Intent(this, GridActivity.class));
                MainBaseActivity.this.finish();
            }
        } else {
            // DBから情報取得＋ビュー更新
            refreshData();
        }
    }

    /**
     * 現在の表示形式設定と、表示されている画面が一致するかを確認
     * @return
     */
    protected abstract boolean isShowTypeCorrect();

    /**
     * 現在表示している画面の種類を返す
     */
    protected abstract String getShowType();

    /**
     * Viewモード以外の時はViewモードに戻す
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMode != G.MODE_VIEW) {
                switchMode(G.MODE_VIEW);
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
//            // リストのアイテム内の削除ボタン押下
//            case G.REQUEST_CODE_LIST_ROW_DELETE_SERIES:
//                if(resultCode == G.RESULT_POSITIVE) {
//                    if (mSelectSeriesIds != null) {
//                        for (int i = 0; i < mSelectSeriesIds.length; i++) {
//                            FilterDao.deleteSeries(mSelectSeriesIds[i]);
//                            ToastUtil.show(MainBaseActivity.this, "作品を削除しました");
//                        }
//                    }
//                    refreshData();
//                }
//                break;
            // 作品情報追加画面から戻ったとき
            // 基本的な保存処理は「作品情報追加画面」に任せる
            case G.REQUEST_CODE_LIST_ADD_SERIES:
                if(resultCode == G.RESULT_POSITIVE) {
                    switchMode(G.MODE_VIEW);
                    refreshData();
                    ToastUtil.show(MainBaseActivity.this, "作品を追加しました");
                    sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.ADD_SERIES);

                }
                break;

            // アップデートダイアログ
            case G.REQUEST_CODE_UPDATE_DIALOG:
                if(resultCode == G.RESULT_POSITIVE) {
                    // マーケットへ飛ばす
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Util.getMarketUriStr(MainBaseActivity.this.getPackageName(), "update_dialog")));
                    if(intent != null) {
                        startActivity(intent);
                    }
                }
                break;
            // 作品の登録方法を選択
            case G.REQUEST_CODE_SELECT_ADD_SERIES_TYPE:
                if(resultCode == G.RESULT_POSITIVE) {
                    Intent intent;
                    int selectedId = data.getIntExtra(G.RESULT_DATA_SELECTED_ID, G.RESULT_DATA_SELECTED_BTN_SEARCH);
                    switch (selectedId) {
                        // 検索結果からの登録画面
                        case G.RESULT_DATA_SELECTED_BTN_SEARCH:
                        default:
                            intent = SearchDialogActivity.getIntent(MainBaseActivity.this, "作品を検索", "作品を検索する項目を選択してください", "検索", "キャンセル");
                            startActivityForResult(intent, G.REQUEST_CODE_LIST_SEARCH_RAKUTEN);
                            sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_ADD_SERIES_SEARCH_BTN);
                            break;
                        // 作品手動登録画面
                        case G.RESULT_DATA_SELECTED_BTN_MANUAL:
                            intent = EditSeriesDialogActivity.getIntent(MainBaseActivity.this, "作品情報を追加", "追加", -1);
                            startActivityForResult(intent, G.REQUEST_CODE_LIST_ADD_SERIES);
                            sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_ADD_SERIES_MANUAL_BTN);
                            break;
                    }
                }
                break;
            // 楽天APIを使用して検索
            case G.REQUEST_CODE_LIST_SEARCH_RAKUTEN:
                if(resultCode == G.RESULT_POSITIVE) {
                    mProgress.start("検索中…", null);

                    // 検索対象、及び検索内容を取得
                    final String key = data.getStringExtra(G.RESULT_DATA_SELECTED_KEY);
                    final String value = data.getStringExtra(G.RESULT_DATA_SELECTED_VALUE);

                    // HTTPアクセスが入るので別スレッドで回す
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            // 楽天APIを利用してJSONを取得する。
                            String url = Rakuten.getRakutenBooksBookURI(MainBaseActivity.this, key, value);
                            Rakuten.RakutenAPIAsyncLoader loader = new Rakuten.RakutenAPIAsyncLoader(MainBaseActivity.this, url);

                            final String jsonStr = loader.loadInBackground();

                            // JSONを取得後に検索結果一覧・または補完済みの作品登録画面を表示
                            // 検索結果が0件の場合：「取得できる作品がありませんでした。内容を変更しもう一度お試しください。」という通知を表示
                            // 検索結果が1件の場合：作品登録画面を開き、取得できている全ての情報を入力済みの状態で表示する
                            // 検索結果が2件以上の場合：作品一覧画面を開き、求めている作品を選んでもらう。選択後は「検索結果が1件の場合」と同じ流れになる
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if(Util.isEmpty(jsonStr)) {
                                        // 取得失敗した場合は書籍全般として再建策するする
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 楽天APIを利用してJSONを取得する。
                                                String url = Rakuten.getRakutenBooksTotalUri(MainBaseActivity.this, key, value);
                                                Rakuten.RakutenAPIAsyncLoader loader = new Rakuten.RakutenAPIAsyncLoader(MainBaseActivity.this, url);

                                                final String jsonRetryStr = loader.loadInBackground();

                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if(Util.isEmpty(jsonRetryStr)) {
                                                            mProgress.close();
                                                            // 検索結果が空の場合はトースト通知でお知らせ
                                                            ToastUtil.show(MainBaseActivity.this, "取得できる作品がありませんでした。内容を変更しもう一度お試しください。");
                                                        } else {
                                                            convertJsonStr2JsonArrayObject(jsonStr);
                                                            mProgress.close();
                                                            switchMode(G.MODE_API_SEARCH_RESULT);
                                                            refreshData();
                                                        }
                                                    }
                                                });

                                            }
                                        }).start();
                                    } else {
                                        convertJsonStr2JsonArrayObject(jsonStr);
                                        mProgress.close();
                                        switchMode(G.MODE_API_SEARCH_RESULT);
                                        refreshData();
                                    }
                                }
                            });
                        }
                    }).start();

                }
                break;
        }
    }

    /**
     * Json文字列から検索結果一覧情報を取得・メンバ変数に保持する
     * @param jsonStr
     */
    private void convertJsonStr2JsonArrayObject(String jsonStr) {
        mApiSearchResultArrayList = new ArrayList<SeriesData>();

        // JSON情報を分析する
        mAPISearchResultJsonObject = JsonUtil.getJsonObject(jsonStr);
        JSONArray jsonArray = JsonUtil.getJsonArray(mAPISearchResultJsonObject, Rakuten.Key.ITEM_LIST);
        List<JSONObject> jsonObjList = JsonUtil.getJsonObjectsList(jsonArray);

        String count = JsonUtil.getJsonObjectData(mAPISearchResultJsonObject, Rakuten.Key.SEARCH_RESULT_COUNT);
        int countVal = -1;
        try {
            countVal = Integer.valueOf(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(countVal > 30) {
            ToastUtil.show(MainBaseActivity.this, "30件以上の作品がヒットしました");
        } else {
            ToastUtil.show(MainBaseActivity.this, count+"件の作品がヒットしました");
        }

        // 取得した結果を作品情報一覧へと分解する
        for(int i = 0 ; i < jsonObjList.size() ; i ++ ) {
            JSONObject detailData = JsonUtil.getJsonObject(jsonObjList.get(i), Rakuten.Key.ITEM_DETAIL);
            SeriesData data = new SeriesData(this);

            data.setTitle(JsonUtil.getJsonObjectData(detailData, Rakuten.Key.TITLE_NAME));
            data.setTitlePronunciation(JsonUtil.getJsonObjectData(detailData, Rakuten.Key.TITLE_NAME_KANA));
            data.setAuthor(JsonUtil.getJsonObjectData(detailData, Rakuten.Key.AUTHOR_NAME));
            data.setAuthorPronunciation(JsonUtil.getJsonObjectData(detailData, Rakuten.Key.AUTHOR_NAME_KANA));
            data.setMagazine(JsonUtil.getJsonObjectData(detailData, Rakuten.Key.MAGAZINE_NAME));
            data.setMagazinePronunciation(JsonUtil.getJsonObjectData(detailData, Rakuten.Key.MAGAZINE_NAME_KANA));
            data.setCompany(JsonUtil.getJsonObjectData(detailData, Rakuten.Key.COMPANY_NAME));
            // 画像は現在未対応（そのうち対応入れる）
            // URLを保持できる用に機能の変更が必要
            String imageUrl = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.IMAGE_URL_LARGE);
            if(Util.isEmpty(imageUrl)) imageUrl = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.IMAGE_URL_MEDIUM);
            if(Util.isEmpty(imageUrl)) imageUrl = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.IMAGE_URL_SMALL);
            // URLの取得に成功した場合は登録する
            if(!Util.isEmpty(imageUrl)) {
                data.setImagePath(imageUrl);
            }

            mApiSearchResultArrayList.add(data);
        }
    }

    // Spinnerアアプターについて
    // http://techbooster.jpn.org/andriod/ui/606/
    private ArrayAdapter<String> getSpinnerAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainBaseActivity.this, android.R.layout.simple_spinner_item);

        // 検索用Spinner選択肢
        for(int i = 0 ; i < G.SEARCH_MODE_LIST.length ; i ++) {
            adapter.add(G.SEARCH_MODE_LIST[i]);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    /**
     * タッチイベントを設定
     */
    protected void setClickListener() {
        // 新規追加ボタン
        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 登録種類を詮索
                Intent intent = BtnListDialogActivity.getIntent(MainBaseActivity.this, "作品登録方法を選択", "作品を登録する方法を選択してください", "決定", "キャンセル");
                startActivityForResult(intent, G.REQUEST_CODE_SELECT_ADD_SERIES_TYPE);
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_ADD_SERIES_BTN);
            }
        });

        // 検索ボタン
        mHeaderView.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_SEARCH_BTN);
                switchMode(G.MODE_SEARCH);
                refreshData();
            }
        });

        // 検索キャンセルボタン
        mHeaderSearch.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode(G.MODE_VIEW);
                refreshData();
            }
        });

        mHeaderApiSearch.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode(G.MODE_VIEW);
                refreshData();
            }
        });

        // 設定ボタン
        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainBaseActivity.this, SettingsActivity.class));
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_SETTINGS_BTN);

            }
        });




        // 検索対象選択スピナーボタン
        spinnerView = ((Spinner)findViewById(R.id.spinner_search_content));
        spinnerView.setAdapter(getSpinnerAdapter());
        spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Spinnerの選択肢の順番=検索モードの並び順となっていることを確認すること
                mSearchMode = position;
                // 検索対象変化後に再度情報を読み込む
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * その他各種リスナー登録
     */
    protected void setOtherListener() {
        EditText searchEditText = (EditText) findViewById(R.id.search_content);
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s == null) mSearchContent = "";
                else mSearchContent = String.valueOf(s);
                refreshData();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Initializes view for bookSeries in listView / gridView
     *
     * @return initialized View
     */
    /**
     *
     * @param position position of the item in list / grid
     * @param convertView View for list / grid item
     * @param parentView Parent view of convertView
     * @param seriesData SeriesData instance to show
     * @param isGridView whether the view is for grid or list
     * @return
     */
    @NonNull
    protected View initializeBookSeriesItemView(int position, @Nullable View convertView, @Nullable ViewGroup parentView, @NonNull final SeriesData seriesData, boolean isGridView) {
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
        boolean isAPISearchMode = mMode == G.MODE_API_SEARCH_RESULT;

        // init final values
        final boolean doUseImageCache = !isAPISearchMode
                && BookData.isValidBookSeriesId(seriesData.getSeriesId());
        final ImageView bookCoverImageView = (ImageView)convertView.findViewById(R.id.book_cover_image);
        // set no_image for default
        bookCoverImageView.setImageResource(R.drawable.no_image);
        bookCoverImageView.setTag(seriesData.getTitle());

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
                ? ImageUtil.getImageCache(seriesData.getSeriesId())
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
        titleTextView.setText(seriesData.getTitle());
        authorTextView.setText(seriesData.getAuthor());
        if(volumeTextView.getVisibility() == View.VISIBLE) {
            volumeTextView.setText(seriesData.getVolumeText());
        }

        // set tag data
        tagContainer.removeAllViews();
        tagContainer = ViewUtil.setTagInfoSmall(
                this, seriesData.getTagsAsList(), tagContainer);
        // TODO check whether this invalidate is necessary
        tagContainer.invalidate();

        // set image data
        if(imageCache != null) {
            bookCoverImageView.setImageBitmap(imageCache);
        } else {
            seriesData.loadImage(mHandler, this, new ListenerUtil.LoadBitmapListener() {
                @Override
                public void onFinish(Bitmap bitmap) {
                    if(bitmap != null) {
                        if(Util.isEqual(seriesData.getTitle(),
                                String.valueOf(bookCoverImageView.getTag()))) {
                            bookCoverImageView.setImageBitmap(bitmap);
                            if(doUseImageCache) {
                                ImageUtil.setImageCache(seriesData.getSeriesId(), bitmap);
                            }
                        }
                    }
                }

                @Override
                public void onError() {
                }
            });
        }

        // set click listener
        // when current mode is search mode, move to adding series screen
        // else, move to check series detail screen
        convertView.setOnClickListener(isAPISearchMode
                ? getOnClick4AddSeriesConfirmScreen(seriesData)
                : getOnClick4StartSeriesDetailIntent(seriesData.getSeriesId()));

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
                    startActivity(IntentUtil.getSeriesDetailIntent(MainBaseActivity.this, seriesId));
                } else {
                    ToastUtil.show(MainBaseActivity.this, R.string.series_data_error_invalid_series_id_found);
                }
            }
        };
    }

    /**
     * Get OnClickListener instance for starting book series add confirm screen
     *
     * @param seriesData SeriesData instance
     * @return OnClickListener instance
     */
    @NonNull
    private View.OnClickListener getOnClick4AddSeriesConfirmScreen(@NonNull final SeriesData seriesData) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        // intent
                        EditSeriesDialogActivity.getIntent(
                                MainBaseActivity.this,
                                getString(R.string.series_data_add_confirm_content),
                                getString(R.string.add),
                                seriesData),
                        // request code
                        G.REQUEST_CODE_LIST_ADD_SERIES);
            }
        };
    }

}