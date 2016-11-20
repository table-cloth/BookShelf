package com.tablecloth.bookshelf.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookSeriesData;
import com.tablecloth.bookshelf.db.BookSeriesDao;
import com.tablecloth.bookshelf.db.SettingsDao;
import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.GAEvent;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ProgressDialogUtil;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Setting Screen to setup all kind of user / application settings
 *
 * Created by Minami on 2015/03/15.
 */
public class SettingsActivity extends BaseActivity {

    private SettingsDao mSettingsDao;
    private BookSeriesDao mBookSeriesDao;
    private Context mContext;

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

        mContext = this;

        // init dao
        mSettingsDao = new SettingsDao(this);
        mBookSeriesDao = new BookSeriesDao(this);

        // init ad
        Util.initAdView(this, (ViewGroup) findViewById(R.id.banner));

        // init settings UI
        initShowTypeManagementUI();
        initBookSeriesManagementUI();

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
     * Initialize UIs related to book management category
     */
    private void initBookSeriesManagementUI() {
        View setPronunciationBtn = findViewById(R.id.settings_manage_book_series_pronunciation);
        setPronunciationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // show progress dialog
                final ProgressDialogUtil progressUtil = ProgressDialogUtil.getInstance(SettingsActivity.this);
                progressUtil.show(
                        mHandler,
                        mContext.getString(R.string.updating_pronunciation_data),
                        null,
                        ProgressDialog.STYLE_HORIZONTAL,
                        100, // set 100 for temp value
                        new ListenerUtil.OnFinishListener() {
                            @Override
                            public void onFinish() {

                                final ArrayList<BookSeriesData> bookSeriesDataList = mBookSeriesDao.loadAllBookSeriesDataList();
                                if(Util.isEmpty(bookSeriesDataList)) {
                                    progressUtil.dismiss();
                                    ToastUtil.show(mContext, R.string.error_no_book_series_registered);
                                    return;
                                }

                                progressUtil.setMaxProgress(mHandler, bookSeriesDataList.size());

                                // Update actual book series
                                for (final BookSeriesData bookSeriesData : bookSeriesDataList) {
                                    bookSeriesData.updateAllPronunciationTextData(new ListenerUtil.OnFinishListener() {
                                        @Override
                                        public void onFinish() {
                                            mBookSeriesDao.saveSeries(bookSeriesData);
                                            int progress = progressUtil.getProgress() + 1;
                                            progressUtil.setProgress(mHandler, progress);

                                            if(progress >= bookSeriesDataList.size()) {
                                                progressUtil.dismiss();
                                                ToastUtil.show(mContext, R.string.updating_pronunciation_data_finished);
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }

    /**
     * Initialize UIs related to show type category
     */
    private void initShowTypeManagementUI() {
        initCatalogShowTypeSettingUI();
        initSortTypeSettingUI();
    }

    private void initSortTypeSettingUI() {
        final String[] spinnerTextListSort = {
                getString(R.string.settings_value_sort_order_id),
                getString(R.string.settings_value_sort_order_title),
                getString(R.string.settings_value_sort_order_author),
                getString(R.string.settings_value_sort_order_magazine),
                getString(R.string.settings_value_sort_order_company),
        };

        // This should be relative with spinnerTextListSort order
        final int itemSortId = 0;
        final int itemSortTitle = 1;
        final int itemSortAuthor = 2;
        final int itemSortMagazine = 3;
        final int itemSortCompany = 4;

        Spinner spinnerView = getSpinnerViewWithAdapter(
                R.id.settings_view_sort_spinner,
                spinnerTextListSort,
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String saveValue;
                        switch (position) {
                            case itemSortId:
                                saveValue = Const.DB.Settings.VALUE.SERIES_SORT_TYPE_ID;
                                break;

                            case itemSortTitle:
                                saveValue = Const.DB.Settings.VALUE.SERIES_SORT_TYPE_TITLE;
                                break;

                            case itemSortAuthor:
                                saveValue = Const.DB.Settings.VALUE.SERIES_SORT_TYPE_AUTHOR;
                                break;

                            case itemSortMagazine:
                                saveValue = Const.DB.Settings.VALUE.SERIES_SORT_TYPE_MAGAZINE;
                                break;

                            case itemSortCompany:
                                saveValue = Const.DB.Settings.VALUE.SERIES_SORT_TYPE_COMPANY;
                                break;

                            default:
                                saveValue = Const.DB.Settings.VALUE.SERIES_SORT_TYPE_ID;
                                break;
                        }

                        // Save to preference
                        mSettingsDao.save(Const.DB.Settings.KEY.SERIES_SORT_TYPE, saveValue);

                        // Send track event
                        sendGoogleAnalyticsEvent(
                                GAEvent.Type.USER_ACTION,
                                GAEvent.Event.SETTINGS_SET_SHOW_TYPE,
                                saveValue);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });

        // Set default view for spinner
        String savedValue = mSettingsDao.load(
                Const.DB.Settings.KEY.SERIES_SORT_TYPE,
                Const.DB.Settings.VALUE.SERIES_SORT_TYPE_ID);
        int selection;
        switch (savedValue) {
            case Const.DB.Settings.VALUE.SERIES_SORT_TYPE_ID:
                selection = itemSortId;
                break;

            case Const.DB.Settings.VALUE.SERIES_SORT_TYPE_TITLE:
                selection = itemSortTitle;
                break;

            case Const.DB.Settings.VALUE.SERIES_SORT_TYPE_AUTHOR:
                selection = itemSortAuthor;
                break;

            case Const.DB.Settings.VALUE.SERIES_SORT_TYPE_MAGAZINE:
                selection = itemSortMagazine;
                break;

            case Const.DB.Settings.VALUE.SERIES_SORT_TYPE_COMPANY:
                selection = itemSortCompany;
                break;

            default:
                selection = itemSortId;
                break;
        }
        spinnerView.setSelection(selection);
    }



    /**
     * Initialize SpinnerView & Adapter for ShowType Setting UI
     * Currently the choices are Grid or List
     */
    private void initCatalogShowTypeSettingUI() {

        final int gridTextId = R.string.settings_value_view_type_grid;
        int listTextId = R.string.settings_value_view_type_list;

        // Initialize spinner
        final String[] spinnerViewTextsList = {
                getString(gridTextId), // show grid
                getString(listTextId)  // show list
        };

        // Init Spinner to set Book Series Catalog show type (grid or list)
        Spinner spinnerView = getSpinnerViewWithAdapter(
                R.id.settings_view_type_spinner,
                spinnerViewTextsList,
                new AdapterView.OnItemSelectedListener() {
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
                                        ? GAEvent.Param.SETTINGS_SET_SHOW_TYPE_GRID
                                        : GAEvent.Param.SETTINGS_SET_SHOW_TYPE_LIST);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        // Update selected position of Spinner
        String currentViewSetting = mSettingsDao.load(
                Const.DB.Settings.KEY.SERIES_SHOW_TYPE,
                Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID);
        spinnerView.setSelection(
                Const.DB.Settings.VALUE.SERIES_SHOW_TYPE_GRID.equals(currentViewSetting)
                        ? 0
                        : 1);
    }

    /**
     * Get spinner view with adapter & given text
     *
     * @param spinnerViewId Spinner view id
     * @param textList Text list to show in spinner
     * @param listener OnItemSelected listener
     * @return Spinner instance
     */
    private Spinner getSpinnerViewWithAdapter(
            int spinnerViewId, @NonNull String[] textList,
            @NonNull AdapterView.OnItemSelectedListener listener) {

        ArrayAdapter<String> spinnerAdapter = getSpinnerAdapter(textList);
        Spinner spinnerView = ((Spinner)findViewById(spinnerViewId));
        spinnerView.setAdapter(spinnerAdapter);
        spinnerView.setOnItemSelectedListener(listener);
        return spinnerView;
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
