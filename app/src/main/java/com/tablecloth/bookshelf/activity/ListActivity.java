package com.tablecloth.bookshelf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.actions.ReserveIntents;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.util.CustomListView;
import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.FilterDao;
import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.dialog.EditSeriesDialogActivity;
import com.tablecloth.bookshelf.dialog.SettingsDialogActivity;
import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;
import com.tablecloth.bookshelf.util.CustomListView;
import com.tablecloth.bookshelf.util.DBUtil;
import com.tablecloth.bookshelf.util.FileUtil;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.IntentUtil;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;

/**
 * Created by shnomura on 2014/08/16.
 */
public class ListActivity extends Activity{

    private CustomListView mListView;
    ArrayList<SeriesData> mDataArrayList = new ArrayList<SeriesData>();
    ListAdapter mListAdapter;
    
    
    
    // http://shogogg.hatenablog.jp/entry/20110118/1295326773
    int mDraggingPosition = -1;

//    String filterInfo = null;

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

    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
        // DBから情報取得＋ビュー更新
        refreshData();
    }

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
//        if(filterInfo == null) {
//            // とりあえず全ての情報を取得する
//
//        } else {
//            // 指定のデータのみ読み込む
//        }

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
                    if(bitmap != null) image.setImageBitmap(bitmap);
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
//                long unix = Calendar.getInstance().getTimeInMillis() / 1000L;
//                SeriesData data = new SeriesData("タイトル"+unix);
//                data.mAuthor = "作者" + unix;
//                FilterDao.saveSeries(data);
//                refreshData();
            }
        });
        
        // 検索ボタン
        findViewById(R.id.btn_search).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switchMode(G.MODE_SEARCH);
		    	refreshData();
//				mMode = MODE_SEARCH;
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

        ((Spinner)findViewById(R.id.spinner_search_content)).setAdapter(getSpinnerAdapter());
//        // 検索対象選択スピナーボタン
//        findViewById(R.id.spinner_search_content).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ToastUtil.show(ListActivity.this, "スピナータップ");
//			}
//		});
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
//    sds
    private ArrayAdapter<String> getSpinnerAdapter() {
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_spinner_item);
    	
    	return adapter;
    }
    
    private void switchMode(int newMode) {
    	switch(newMode) {
    		case G.MODE_VIEW:
    		default:
    			findViewById(R.id.header_area_mode_view).setVisibility(View.VISIBLE);
    			findViewById(R.id.header_area_mode_search).setVisibility(View.GONE); 
    			break;
    			
    		case G.MODE_SEARCH:
    			findViewById(R.id.header_area_mode_view).setVisibility(View.GONE);
    			findViewById(R.id.header_area_mode_search).setVisibility(View.VISIBLE); 
    			findViewById(R.id.search_content).requestFocus();
    			break;
    	}
    	mMode = newMode;
    }
}
