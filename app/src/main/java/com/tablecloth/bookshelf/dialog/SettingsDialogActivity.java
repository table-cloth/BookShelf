package com.tablecloth.bookshelf.dialog;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tablecloth.bookshelf.db.FilterDao;
import com.tablecloth.bookshelf.db.SeriesData;
import com.tablecloth.bookshelf.util.DBUtil;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.R;

/**
 * 設定ボタンタップ後に表示されるダイアログ
 * Created by shnomura on 2014/11/24.
 */
public class SettingsDialogActivity extends DialogBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        // テキスト設定

        ((TextView)findViewById(R.id.btn_backup_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
		        SeriesData[] series = FilterDao.loadSeries(SettingsDialogActivity.this);
		        JSONArray jsonArray = DBUtil.convertSeriesData2Json(series);

		        String jsonString = jsonArray.toString();
//		        String fileName = FileUtil.saveBackupFile(ListActivity.this, jsonString, true);
//		        Uri uri = FileUtil.getSendToFileUri(FileUtil.getInternalStorageDirectory(ListActivity.this, fileName));
		        
		        Intent intent = new Intent(Intent.ACTION_SEND);
		        intent.setType("text/*");
		        intent.putExtra(Intent.EXTRA_SUBJECT, "バックアップ情報");
		        intent.putExtra(Intent.EXTRA_TEXT, jsonString);
//		        intent.putExtra(Intent.EXTRA_STREAM, uri);	
		        startActivity(intent);
		        
//		        JSONArray jsonArray2;
//				try {
//					jsonArray2 = new JSONArray(jsonString);
//			        SeriesData[] data = DBUtil.convertJson2SeriesData(jsonArray2);
//			        if(data != null) {
//				        for(int i = 0 ; i < data.length ; i ++) {
//				        	FilterDao.saveSeries(data[i]);
//				        	for(int j = 0 ; j < data[i].mVolumeList.size() ; j ++) {
//				        		FilterDao.saveVolume(data[i].mSeriesId, data[i].mVolumeList.get(j));
//				        	}
//				        }
//				        mListAdapter.notifyDataSetChanged();
//			        }
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
            	
                SettingsDialogActivity.this.finish();
            }
        });
        ((TextView)findViewById(R.id.btn_backup_load)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsDialogActivity.this.finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_dialog;
    }

    public static Intent getIntent(Context context, String title, String message, String btnPositive, String btnNegative) {
        Intent intent = new Intent(context, SettingsDialogActivity.class);

        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_MESSAGE, message);
        intent.putExtra(KEY_BTN_POSITIVE, btnPositive);
        intent.putExtra(KEY_BTN_NEGATIVE, btnNegative);

        return intent;
    }
}
