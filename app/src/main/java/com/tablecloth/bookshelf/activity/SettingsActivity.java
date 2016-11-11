package com.tablecloth.bookshelf.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.SettingsDao;
import com.tablecloth.bookshelf.dialog.BtnListDialogActivity;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.GAEvent;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;

import java.security.MessageDigest;

/**
 * Setting Screen to setup all kind of user / application settings
 *
 * Created by Minami on 2015/03/15.
 */
public class SettingsActivity extends BaseActivity {

    private SettingsDao mSettingsDao;

    /**
     * Get Intent instance to activate this activity
     *
     * @param context context
     * @return intent instance
     */
    public static Intent getIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return R.layout.activity_settings;
    }

    /**
     * Constructor
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init dao
        mSettingsDao = new SettingsDao(this);

        // init ad
        Util.initAdview(this, (ViewGroup) findViewById(R.id.banner));

        // init settings UI
        initCatalogShowTypeSettingUI();

        // Init back button
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });

        // E-mail
        findViewById(R.id.settings_inquiry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateMailer();
            }
        });

        // Review
        findViewById(R.id.settings_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateGooglePlay();
            }
        });
    }

    /**
     * Initlaize SpinnerView & Adapter for ShowType Setting UI
     * Currently the choices are Grid or List
     */
    private void initCatalogShowTypeSettingUI() {

        final int gridTextId = R.string.settings_value_grid;
        int listTextId = R.string.settings_value_list;

        // Initialize spinner
        final String[] spinnerViewTextsList = new String[] {
                getString(gridTextId), // show grid
                getString(listTextId)  // show list
        };

        // Init Spinner to set Book Series Catalog show type (grid or list)
        ArrayAdapter<String> spinnerAdapter = getSpinnerAdapter(spinnerViewTextsList);
        Spinner spinnerView = ((Spinner)findViewById(R.id.settings_view_type_spinner));
        spinnerView.setAdapter(spinnerAdapter);

        // Set item click callback
        spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Whether selected item is View in Grid style
                boolean isGridViewTypeSelected =
                        getString(gridTextId)
                        .equals(spinnerViewTextsList[position]);

                // Save view type
                mSettingsDao.save(Const.DB.Settings.KEY.SERIES_SHOW_TYPE,
                        isGridViewTypeSelected
                                ? Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID
                                : Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_LIST);

                // Send track event
                sendGoogleAnalyticsEvent(
                        GAEvent.Type.USER_ACTION,
                        GAEvent.Event.SETTINGS_SET_SHOW_TYPE,
                        isGridViewTypeSelected
                                ? GAEvent.Param.GRID
                                : GAEvent.Param.LIST);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Update selected position of Spinner
        String currentViewSetting = mSettingsDao.load(
                Const.DB.Settings.KEY.SERIES_SHOW_TYPE,
                Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID);
        spinnerView.setSelection(spinnerAdapter.getPosition(getString(
                Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID.equals(currentViewSetting)
                        ? gridTextId
                        : listTextId)));
    }

    /**
     * Start GooglePlay for this application page
     */
    private void activateGooglePlay() {
        // GooglePlayを開く
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + SettingsActivity.this.getPackageName()));
        SettingsActivity.this.startActivity(intent);
    }

    /**
     * Activate E-mail with
     */
    private void activateMailer() {

        // Create email info
        String address[] = new String[] {
                getString(R.string.email)
        };
        String subject = getString(R.string.inquiry_subject);

        // Activate mailer application
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

    /**
     * Get inquiry e-mail contents
     *
     * @return email content text
     */
    private String getMailerText() {

        // Get info from package manager
        PackageManager pkgManager = SettingsActivity.this.getPackageManager();
        String appVersion = "";
        try {
            PackageInfo pkgInfo = pkgManager.getPackageInfo(SettingsActivity.this.getPackageName(), PackageManager.GET_META_DATA );
            appVersion = pkgInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return getString(R.string.email_inquiry_line1_title)
                + getString(R.string.email_inquiry_line2_id, getInquiryM5Id())
                + getString(R.string.email_inquiry_line3_app_version, appVersion)
                + getString(R.string.email_inquiry_line4_os_version, Build.VERSION.RELEASE)
                + getString(R.string.email_inquiry_line5_device, Build.MODEL)
                + getString(R.string.email_inquiry_line6_closing);
    }

    /**
     * Get M5 encrypted ID
     * @return user's inquiry ID
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

    /**
     * Get Spinner Adapter instance
     *
     * @param spinnerShowTexts Array texts to show in spinner
     * @return Spinner instance with Adapter set
     */
    @NonNull
    private ArrayAdapter<String> getSpinnerAdapter(@NonNull String[] spinnerShowTexts) {

        // Create adapter
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        for(String text : spinnerShowTexts) {
            spinnerAdapter.add(text);
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return spinnerAdapter;
    }

}
