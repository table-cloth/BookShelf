package com.tablecloth.bookshelf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.util.CustomListView;
import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.FilterDao;
import com.tablecloth.bookshelf.dialog.EditSeriesDialogActivity;
import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;
import com.tablecloth.bookshelf.util.GAEvent;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.IntentUtil;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.VersionUtil;

/**
 * Created by shnomura on 2014/08/16.
 */
public class ListActivity extends BaseActivity {

    private CustomListView mListView;
    ArrayList<SeriesData> mDataArrayList = new ArrayList<SeriesData>();
    ListAdapter mListAdapter;
    Spinner spinnerView;
    
    
    
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

        if(mMode == G.MODE_SEARCH) {
        	series = FilterDao.loadSeries(ListActivity.this, mSearchMode, mSearchContent);
        } else {
        	series = FilterDao.loadSeries(ListActivity.this);
        }
        if(series != null) {
            for(int i = 0 ; i < series.length ; i ++) {
                SeriesData data = series[i];
                if(data != null) {
                    mDataArrayList.add(data);
                }
            }
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
            ImageView image;
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
                    image = (ImageView) v.findViewById(R.id.image);

                    title.setText(series.mTitle);
                    author.setText(series.mAuthor);
                    volume.setText(series.getVolumeString());
                    Bitmap bitmap = series.getImage(ListActivity.this);
                    if(bitmap != null) {
                        image.setImageBitmap(bitmap);
                    }
                    else {
                    	image.setImageResource(R.drawable.no_image);
                    }

                    // リストの各要素のタッチイベント
                    v.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectSeriesIds = new int[]{series.mSeriesId};
                            Intent intent = SimpleDialogActivity.getIntent(ListActivity.this, "削除しますか？", "登録された作品の情報を削除しますか？\n\n復元は出来ませんのでご注意ください", "はい", "いいえ");
                            startActivityForResult(intent, G.REQUEST_CODE_LIST_ROW_DELETE_SERIES);
                        }
                    });

                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // リストのセルをタップで
                            startActivity(IntentUtil.getSeriesDetailIntent(ListActivity.this, series.mSeriesId));
                        }
                    });
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
                Intent intent = EditSeriesDialogActivity.getIntent(ListActivity.this, "作品情報を追加", "追加", -1);
                startActivityForResult(intent, G.REQUEST_CODE_LIST_ADD_SERIES);
                sendGoogleAnalyticsEvent(GAEvent.Category.USER_ACTION, GAEvent.Action.LIST_ACTIVITY, GAEvent.Label.TAP_ADD_SERIES_BTN);
            }
        });
        
        // 検索ボタン
        findViewById(R.id.btn_search).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
                sendGoogleAnalyticsEvent(GAEvent.Category.USER_ACTION, GAEvent.Action.LIST_ACTIVITY, GAEvent.Label.SHOW_MODE_VIEW);
    			findViewById(R.id.header_area_mode_view).setVisibility(View.VISIBLE);
    			findViewById(R.id.header_area_mode_search).setVisibility(View.GONE);
    			break;
    			
    		case G.MODE_SEARCH:
                sendGoogleAnalyticsEvent(GAEvent.Category.USER_ACTION, GAEvent.Action.LIST_ACTIVITY, GAEvent.Label.SHOW_MODE_SEARCH);
    			findViewById(R.id.header_area_mode_view).setVisibility(View.GONE);
    			findViewById(R.id.header_area_mode_search).setVisibility(View.VISIBLE); 
    			findViewById(R.id.search_content).requestFocus();
    			break;
    	}
    	mMode = newMode;
    }
}
