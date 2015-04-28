package com.tablecloth.bookshelf.activity;

import android.location.SettingInjectorService;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.DB;
import com.tablecloth.bookshelf.db.SettingsDao;
import com.tablecloth.bookshelf.util.GAEvent;
import com.tablecloth.bookshelf.util.SpinnerUtil;
import com.tablecloth.bookshelf.util.Util;

/**
 * Created by shnomura on 2015/03/15.
 */
public class SettingsActivity extends BaseActivity {

//    TextView mViewTypeStatus = null;
    String[] mViewTypeSelection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初期化処理
        mViewTypeSelection = new String[] { getString(R.string.settings_value_grid), getString(R.string.settings_value_list) };

        // 広告の初期化処理
        Util.initAdview(this, (ViewGroup) findViewById(R.id.banner));

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });


        // 表示設定
        String value = mSettings.load(SettingsDao.KEY.SERIES_SHOW_TYPE, SettingsDao.VALUE.SERIES_SHOW_TYPE_GRID);
        // 検索対象選択スピナーボタン
        Spinner spinnerView = ((Spinner)findViewById(R.id.settings_view_type_spinner));

        ArrayAdapter<String> spinnerAdapter = SpinnerUtil.getSpinnerAdapter(SettingsActivity.this, mViewTypeSelection);
        if(spinnerAdapter != null) {
            spinnerView.setAdapter(spinnerAdapter);
            spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // 選択した表示形式がリスト形式かを選択
                    if(getString(R.string.settings_value_list).equals(mViewTypeSelection[position])) {
                        mSettings.save(SettingsDao.KEY.SERIES_SHOW_TYPE, SettingsDao.VALUE.SERIES_SHOW_TYPE_LIST);
                        sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SETTINGS_SET_SHOW_TYPE, GAEvent.Param.LIST);
                    } else {
                        mSettings.save(SettingsDao.KEY.SERIES_SHOW_TYPE, SettingsDao.VALUE.SERIES_SHOW_TYPE_GRID);
                        sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SETTINGS_SET_SHOW_TYPE, GAEvent.Param.GRID);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        if(SettingsDao.VALUE.SERIES_SHOW_TYPE_LIST.equals(value)) {
            spinnerView.setSelection(spinnerAdapter.getPosition(getString(R.string.settings_value_list)));
        } else {
            spinnerView.setSelection(spinnerAdapter.getPosition(getString(R.string.settings_value_grid)));
        }
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_settings;
    }
}
