package com.tablecloth.bookshelf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.dialog.BtnListDialogActivity;
import com.tablecloth.bookshelf.dialog.SearchDialogActivity;
import com.tablecloth.bookshelf.util.CustomListView;
import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.FilterDao;
import com.tablecloth.bookshelf.dialog.EditSeriesDialogActivity;
import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;
import com.tablecloth.bookshelf.util.GAEvent;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.IntentUtil;
import com.tablecloth.bookshelf.util.JsonUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ProgressUtil;
import com.tablecloth.bookshelf.util.Rakuten;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.VersionUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by shnomura on 2014/08/16.
 */
public class ListActivity extends BaseActivity {

    private CustomListView mListView;
    ArrayList<SeriesData> mDataArrayList = new ArrayList<SeriesData>();
    ListAdapter mListAdapter;
    Spinner spinnerView;

    // 楽天WebAPIでの検索結果の保持用変数
    ArrayList<SeriesData> mApiSearchResultArrayList = new ArrayList<SeriesData>();
    JSONObject mAPISearchResultJsonObject = null;

    ProgressUtil mProgress;
    
    
    // http://shogogg.hatenablog.jp/entry/20110118/1295326773
    int mDraggingPosition = -1;

    // データ編集時用のID情報一時保管庫
    // 使い終わったらnullにする
    private int[] mSelectSeriesIds = null;

    // 操作モード
    private int mMode = G.MODE_VIEW;
    
    
    private int mSearchMode = G.SEARCH_MODE_ALL;
    private String mSearchContent = "";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list);

        mProgress = new ProgressUtil(ListActivity.this);

        //mMode = MODE_VIEW;
        switchMode(G.MODE_VIEW);

        // リスト部分の生成・設定処理
        mListView = (CustomListView)findViewById(R.id.list_view);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        mListView.setSortable(true);
//        mListView.setDragListener(new DragListener());

        setClickListener();
        setOtherListener();
        
        // 広告の初期化処理
        Util.initAdview(this, (ViewGroup)findViewById(R.id.banner));

        // バージョン情報の確認・アップデートダイアログの表示・及びバージョン情報の更新
        VersionUtil versionUtil = new VersionUtil(ListActivity.this);
        versionUtil.showUpdateDialog();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
        // DBから情報取得＋ビュー更新
        refreshData();
    }

    /**
     * 一覧情報を再取得・再表示する
     */
    private void refreshData() {

        mDataArrayList.clear();
        SeriesData[] series = null;

        switch (mMode) {
            case G.MODE_VIEW:
                series = FilterDao.loadSeries(ListActivity.this);
                if(series != null) {
                    for(int i = 0 ; i < series.length ; i ++) {
                        SeriesData data = series[i];
                        if(data != null) {
                            mDataArrayList.add(data);
                        }
                    }
                }
                break;
            case G.MODE_SEARCH:
                series = FilterDao.loadSeries(ListActivity.this, mSearchMode, mSearchContent);
                if(series != null) {
                    for(int i = 0 ; i < series.length ; i ++) {
                        SeriesData data = series[i];
                        if(data != null) {
                            mDataArrayList.add(data);
                        }
                    }
                }
                break;
            // 検索結果を表示する。
            case G.MODE_API_SEARCH_RESULT:
                if(mApiSearchResultArrayList != null) {
                    for(int i = 0 ; i < mApiSearchResultArrayList.size() ; i ++) {
                        mDataArrayList.add(mApiSearchResultArrayList.get(i));
                    }
                }
                break;
        }


        mListAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mDataArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView title;
            TextView author;
            TextView volume;
            View v = convertView;

            if(v==null){
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.book_list_row, null);
            }
            if(mDataArrayList != null) {
                final SeriesData series = (SeriesData) mDataArrayList.get(position);
                if (series != null) {
                    // データの設定
                    title = (TextView) v.findViewById(R.id.title);
                    author = (TextView) v.findViewById(R.id.author);
                    volume = (TextView) v.findViewById(R.id.volume);
                    final ImageView image = (ImageView) v.findViewById(R.id.image);

                    title.setText(series.mTitle);
                    author.setText(series.mAuthor);
                    if(mMode == G.MODE_API_SEARCH_RESULT) {
                        volume.setVisibility(View.GONE);
                    } else {
                        volume.setText(series.getVolumeString());
                        volume.setVisibility(View.VISIBLE);
                    }
                    image.setImageResource(R.drawable.no_image);
                    series.getImage(mHandler, ListActivity.this, new ListenerUtil.LoadBitmapListener() {
                        @Override
                        public void onFinish(Bitmap bitmap) {
                            if(bitmap != null) {
                                image.setImageBitmap(bitmap);
                            }
                            else {
                                image.setImageResource(R.drawable.no_image);
                            }
                        }

                        @Override
                        public void onError() {
                            image.setImageResource(R.drawable.no_image);
                        }
                    });

                    // WebAPIの検索結果時以外は先品詳細画面へ
                    if(mMode != G.MODE_API_SEARCH_RESULT) {
                        // リストの各要素のタッチイベント
                        v.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSelectSeriesIds = new int[]{series.mSeriesId};
                                Intent intent = SimpleDialogActivity.getIntent(ListActivity.this, "削除しますか？", "登録された作品の情報を削除しますか？\n\n復元は出来ませんのでご注意ください", "はい", "いいえ");
                                startActivityForResult(intent, G.REQUEST_CODE_LIST_ROW_DELETE_SERIES);
                            }
                        });
                        v.findViewById(R.id.delete_btn).setVisibility(View.VISIBLE);

                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // リストのセルをタップで
                                startActivity(IntentUtil.getSeriesDetailIntent(ListActivity.this, series.mSeriesId));
                            }
                        });
                    // WebAPIの検索結果表示時の場合は一部処理をかえる
                    } else {
                        // リストの各要素のタッチイベント
                        v.findViewById(R.id.delete_btn).setVisibility(View.GONE);

                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = EditSeriesDialogActivity.getIntent(ListActivity.this, "この作品を追加しますか？", "追加", series);
//                                Intent intent = SimpleDialogActivity.getIntent(ListActivity.this, "確認", "この作品を本棚に追加しますか？", "はい", "いいえ");
                                ListActivity.this.startActivityForResult(intent, G.REQUEST_CODE_LIST_ADD_SERIES);
                            }
                        });
                    }
                }
            }
            return v;
        }
    }

    /**
     * タッチイベントを設定
     */
    private void setClickListener() {
        // 新規追加ボタン
        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 登録種類を詮索
                Intent intent = BtnListDialogActivity.getIntent(ListActivity.this, "作品登録方法を選択", "作品を登録する方法を選択してください", "決定", "キャンセル");
                startActivityForResult(intent, G.REQUEST_CODE_SELECT_ADD_SERIES_TYPE);
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_ADD_SERIES_BTN);
            }
        });
        
        // 検索ボタン
        findViewById(R.id.btn_search).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.TAP_SEARCH_BTN);
				switchMode(G.MODE_SEARCH);
		    	refreshData();
			}
		});
        
//        // 設定ボタン
//        findViewById(R.id.btn_settings).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(ListActivity.this, SettingsDialogActivity.class));
//			}
//		});
        
        // 検索キャンセルボタン
        findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switchMode(G.MODE_VIEW);
		    	refreshData();
			}
		});
        findViewById(R.id.btn_api_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode(G.MODE_VIEW);
                refreshData();
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
    private void setOtherListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // リストのアイテム内の削除ボタン押下
            case G.REQUEST_CODE_LIST_ROW_DELETE_SERIES:
                if(resultCode == G.RESULT_POSITIVE) {
                    if (mSelectSeriesIds != null) {
                        for (int i = 0; i < mSelectSeriesIds.length; i++) {
                            FilterDao.deleteSeries(mSelectSeriesIds[i]);
                    		ToastUtil.show(ListActivity.this, "作品を削除しました");
                        }
                    }
                    refreshData();
                }
                break;
            // 作品情報追加画面から戻ったとき
            // 基本的な保存処理は「作品情報追加画面」に任せる
            case G.REQUEST_CODE_LIST_ADD_SERIES:
            	if(resultCode == G.RESULT_POSITIVE) {
                    switchMode(G.MODE_VIEW);
            		refreshData();
            		ToastUtil.show(ListActivity.this, "作品を追加しました");
            	}
                break;

            // アップデートダイアログ
            case G.REQUEST_CODE_UPDATE_DIALOG:
                if(resultCode == G.RESULT_POSITIVE) {
                    // マーケットへ飛ばす
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Util.getMarketUriStr(ListActivity.this.getPackageName(), "update_dialog")));
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
                            intent = SearchDialogActivity.getIntent(ListActivity.this, "作品を検索", "作品を検索する項目を選択してください", "検索", "キャンセル");
                            startActivityForResult(intent, G.REQUEST_CODE_LIST_SEARCH_RAKUTEN);
                            break;
                        // 作品手動登録画面
                        case G.RESULT_DATA_SELECTED_BTN_MANUAL:
                            intent = EditSeriesDialogActivity.getIntent(ListActivity.this, "作品情報を追加", "追加", -1);
                            startActivityForResult(intent, G.REQUEST_CODE_LIST_ADD_SERIES);
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
                            String url = Rakuten.getRakutenBooksBookURI(ListActivity.this, key, value);
                            Rakuten.RakutenAPIAsyncLoader loader = new Rakuten.RakutenAPIAsyncLoader(ListActivity.this, url);

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
                                                String url = Rakuten.getRakutenBooksTotalUri(ListActivity.this, key, value);
                                                Rakuten.RakutenAPIAsyncLoader loader = new Rakuten.RakutenAPIAsyncLoader(ListActivity.this, url);

                                                final String jsonRetryStr = loader.loadInBackground();

                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if(Util.isEmpty(jsonRetryStr)) {
                                                            mProgress.close();
                                                            // 検索結果が空の場合はトースト通知でお知らせ
                                                            ToastUtil.show(ListActivity.this, "取得できる作品がありませんでした。内容を変更しもう一度お試しください。");
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
            ToastUtil.show(ListActivity.this, "30件以上の作品がヒットしました");
        } else {
            ToastUtil.show(ListActivity.this, count+"件の作品がヒットしました");
        }

        // 取得した結果を作品情報一覧へと分解する
        for(int i = 0 ; i < jsonObjList.size() ; i ++ ) {
            JSONObject detailData = JsonUtil.getJsonObject(jsonObjList.get(i), Rakuten.Key.ITEM_DETAIL);
            SeriesData data = new SeriesData();

            data.mTitle = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.TITLE_NAME);
            data.mTitlePronunciation = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.TITLE_NAME_KANA);
            data.mAuthor = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.AUTHOR_NAME);
            data.mAuthorPronunciation = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.AUTHOR_NAME_KANA);
            data.mMagazine = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.MAGAZINE_NAME);
            data.mMagazinePronunciation = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.MAGAZINE_NAME_KANA);
            data.mCompany = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.COMPANY_NAME);
            // 画像は現在未対応（そのうち対応入れる）
            // URLを保持できる用に機能の変更が必要
            String imageUrl = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.IMAGE_URL_LARGE);
            if(Util.isEmpty(imageUrl)) imageUrl = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.IMAGE_URL_MEDIUM);
            if(Util.isEmpty(imageUrl)) imageUrl = JsonUtil.getJsonObjectData(detailData, Rakuten.Key.IMAGE_URL_SMALL);
            // URLの取得に成功した場合は登録する
            if(!Util.isEmpty(imageUrl)) {
                data.mImagePath = imageUrl;
            }

            mApiSearchResultArrayList.add(data);
        }
    }

    
//    class DragListener extends CustomListView.SimpleDragListener {
//        @Override
//        public int onStartDrag(int position) {
//            mDraggingPosition = position;
//            mListView.invalidateViews();
//            return position;
//        }
//        
//        @Override
//        public int onDuringDrag(int positionFrom, int positionTo) {
//            if (positionFrom < 0 || positionTo < 0
//                    || positionFrom == positionTo) {
//                return positionFrom;
//            }
//            int i;
//            if (positionFrom < positionTo) {
//                final int min = positionFrom;
//                final int max = positionTo;
//                final String data = PREFS[min];
//                i = min;
//                while (i < max) {
//                    PREFS[i] = PREFS[++i];
//                }
//                PREFS[max] = data;
//            } else if (positionFrom > positionTo) {
//                final int min = positionTo;
//                final int max = positionFrom;
//                final String data = PREFS[max];
//                i = max;
//                while (i > min) {
//                    PREFS[i] = PREFS[--i];
//                }
//                PREFS[min] = data;
//            }
//            mDraggingPosition = positionTo;
//            mListView.invalidateViews();
//            return positionTo;
//        }
//        
//        @Override
//        public boolean onStopDrag(int positionFrom, int positionTo) {
//            mDraggingPosition = -1;
//            mListView.invalidateViews();
//            return super.onStopDrag(positionFrom, positionTo);
//        }
//    }

    // Spinnerアアプターについて
    // http://techbooster.jpn.org/andriod/ui/606/
    private ArrayAdapter<String> getSpinnerAdapter() {
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_spinner_item);

        // 検索用Spinner選択肢
        for(int i = 0 ; i < G.SEARCH_MODE_LIST.length ; i ++) {
            adapter.add(G.SEARCH_MODE_LIST[i]);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    	return adapter;
    }

    /**
     * 一覧表示・検索表示等のモードを切り替える
     * @param newMode
     */
    private void switchMode(int newMode) {
    	switch(newMode) {
    		case G.MODE_VIEW:
    		default:
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SHOW_MODE_VIEW);
    			findViewById(R.id.header_area_mode_view).setVisibility(View.VISIBLE);
    			findViewById(R.id.header_area_mode_search).setVisibility(View.GONE);
                findViewById(R.id.header_area_mode_api_search).setVisibility(View.GONE);
                findViewById(R.id.btn_search).setVisibility(View.VISIBLE);
                break;
    			
    		case G.MODE_SEARCH:
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SHOW_MODE_SEARCH);
    			findViewById(R.id.header_area_mode_view).setVisibility(View.GONE);
    			findViewById(R.id.header_area_mode_search).setVisibility(View.VISIBLE);
                findViewById(R.id.header_area_mode_api_search).setVisibility(View.GONE);
    			findViewById(R.id.search_content).requestFocus();
                findViewById(R.id.btn_search).setVisibility(View.VISIBLE);
                break;
            case G.MODE_API_SEARCH_RESULT:
                sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SHOW_MODE_API_SEARCH_RESULT);
                findViewById(R.id.header_area_mode_view).setVisibility(View.GONE);
                findViewById(R.id.header_area_mode_search).setVisibility(View.GONE);
                findViewById(R.id.header_area_mode_api_search).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_search).setVisibility(View.GONE);
                break;
    	}
    	mMode = newMode;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMode != G.MODE_VIEW) {
                switchMode(G.MODE_VIEW);
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
