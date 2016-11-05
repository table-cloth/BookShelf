package com.tablecloth.bookshelf.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.Build;
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
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.GAEvent;
import com.tablecloth.bookshelf.util.SpinnerUtil;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;

import java.security.MessageDigest;

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
        String value = mSettings.load(Const.DB.Settings.KEY.SERIES_SHOW_TYPE, Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID);
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
                        mSettings.save(Const.DB.Settings.KEY.SERIES_SHOW_TYPE, Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_LIST);
                        sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SETTINGS_SET_SHOW_TYPE, GAEvent.Param.LIST);
                    } else {
                        mSettings.save(Const.DB.Settings.KEY.SERIES_SHOW_TYPE, Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID);
                        sendGoogleAnalyticsEvent(GAEvent.Type.USER_ACTION, GAEvent.Event.SETTINGS_SET_SHOW_TYPE, GAEvent.Param.GRID);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        if(Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_LIST.equals(value)) {
            spinnerView.setSelection(spinnerAdapter.getPosition(getString(R.string.settings_value_list)));
        } else {
            spinnerView.setSelection(spinnerAdapter.getPosition(getString(R.string.settings_value_grid)));
        }


        // お問い合わせ
        findViewById(R.id.settings_inquiry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateMailer();
            }
        });

        //
        findViewById(R.id.settings_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateGooglePlay();
            }
        });
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_settings;
    }

    private void activateGooglePlay() {
        // GooglePlayを開く
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + SettingsActivity.this.getPackageName()));
        SettingsActivity.this.startActivity(intent);
    }

    private void activateMailer() {
        String address[] = new String[] {"table.cloth.03@gmail.com"};
        String subject = getString(R.string.inquiry_subject);

        // メーラー立ち上げ
        try {
            Uri uri = Uri.parse("mailto:");
            Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
            intent.putExtra(Intent.EXTRA_EMAIL, address );
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, getMailerText());
            startActivity(intent);
        } catch(ActivityNotFoundException e) {
            ToastUtil.show(SettingsActivity.this, getString(R.string.error_no_mailer));
            e.printStackTrace();
        }
    }

    private String getMailerText() {

        // パッケージマネージャを準備
        PackageManager pkgMngr = SettingsActivity.this.getPackageManager();
        String appVersion = "";
        String apkPath = "";
        try {
            PackageInfo pkgInfo = pkgMngr.getPackageInfo(SettingsActivity.this.getPackageName(), PackageManager.GET_META_DATA );
            appVersion = pkgInfo.versionName;
            apkPath = pkgInfo.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // OSバージョン
        String osVersion = Build.VERSION.RELEASE;
        // 機種バージョン
        String model = Build.MODEL;

        String ret = "";
        ret += "＊＊お問い合わせ情報＊＊\n";
        ret += "お問い合わせID：" + getInquiryM5Id() + "\n";
        ret += "アプリバージョン：" + appVersion + "\n";
        ret += "OSバージョン：" + osVersion + "\n";
        ret += "機種名：" +  model + "\n";
        ret += "＊＊＊＊＊＊＊＊＊＊＊＊\n\n";

        return ret;
    }

    /**
     * M5で暗号化したお問い合わせIDを取得
     * @return
     */
    private String getInquiryM5Id() {
        String uuid = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(uuid.getBytes());
            byte[] hash = md.digest();

            StringBuffer sb = new StringBuffer();
            for(byte b : hash) {
                String s = Integer.toHexString(0xff & b);
                if(s.length() == 1) {
                    sb.append("0");
                }
                sb.append(s);
            }
            uuid = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return uuid;
    }


}
