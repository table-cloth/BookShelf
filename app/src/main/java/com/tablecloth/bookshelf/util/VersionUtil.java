package com.tablecloth.bookshelf.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;

/**
 * Util class for Version / Update related
 *
 * Created by Minami on 2015/02/15.
 */
public class VersionUtil {

    private final PrefUtil mPref;

    /**
     * Get VersionUtil instance
     *
     * @param appContext Application context
     * @return VerionUtil instance
     */
    @NonNull
    public static VersionUtil getInstance(@NonNull Context appContext) {
        return new VersionUtil(appContext);
    }

    /**
     * Constructor
     *
     * @param appContext Application context
     */
    private VersionUtil(@NonNull Context appContext) {
        mPref = PrefUtil.getInstance(appContext);
    }

    /**
     * Check whether version update dialog should be shown
     *
     * @param appContext Application context
     * @return Whether version update dialog should be shown
     */
    public boolean isVersionUpdated(@NonNull Context appContext) {
        return getCurrentVersionCode(appContext) > loadLastShownVersionCode();
    }

    /**
     * Get Intent for showing update dialog
     *
     * @param appContext Application context
     * @return Intent or null if update dialog is not needed
     */
    @Nullable
    public Intent getUpdateDialogIntent(@NonNull Context appContext) {
        int versionDiff = getCurrentVersionCode(appContext) - loadLastShownVersionCode();
        if(versionDiff <= 0) {
            return null;
        }

        return SimpleDialogActivity.getIntent(
                appContext,
                R.string.update_dialog_title,
                R.string.update_dialog_content_normal,
                R.string.update_dialog_do_review,
                R.string.update_dialog_do_not_review);
    }

    /**
     * Save current version
     */
    public void updateVersionInfo(Context appContext) {
        mPref.saveInt(Const.PREF_KEYS.VERSION_CODE_INT, getCurrentVersionCode(appContext));
    }

    /**
     * Load initial version code of install
     *
     * @return Initial version code
     */
    private int loadInitialVersionCode() {
        return mPref.loadInt(Const.PREF_KEYS.INIT_VERSION_CODE_INT);
    }

    /**
     * Load last version code updated
     *
     * @return latest version update
     */
    private int loadLastShownVersionCode() {
        return mPref.loadInt(Const.PREF_KEYS.VERSION_CODE_INT);
    }

    /**
     * Get current version code
     *
     * @return current version code or -1 if error
     */
    private static int getCurrentVersionCode(Context appContext) {
        PackageManager packageManager = appContext.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(appContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return -1;
    }


}
