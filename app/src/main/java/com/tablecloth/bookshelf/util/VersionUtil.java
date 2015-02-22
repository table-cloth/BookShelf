package com.tablecloth.bookshelf.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;

/**
 * Created by shnomura on 2015/02/15.
 *
 * アップデートダイアログやバージョンアップデート内容を記録するためのクラス
 */
public class VersionUtil {

    final Activity mActivity;
    final Context mAppContext;
    final PrefUtil mPref;

    public VersionUtil(Activity activity) {
        mActivity = activity;
        mAppContext = mActivity.getApplicationContext();
        mPref = new PrefUtil(mAppContext);
    }

    /**
     * アップデートダイアログの表示が必要な場合のみ表示する
     * 表示した場合はtrueを返す
     * @return
     */
    public boolean showUpdateDialog() {
        // 最後に起動した記録のあるバージョンが、現在のバージョンより古い場合はアップデートダイアログを表示可能
        if(getCurrentVersionCode(mAppContext) > loadLastShownVersionCode()) {
            Intent intent = SimpleDialogActivity.getIntent(mAppContext, "お知らせ", "■検索機能を追加しました！\n\n■設定した一覧画面にて設定した写真が正常に表示されない不具合を修正しました。\n\n■また、その他要望等ございましたらレビューにて記載をお願いいたします。", "レビューする", "しない");
            mActivity.startActivityForResult(intent, G.REQUEST_CODE_UPDATE_DIALOG);

            // バージョン情報を更新
            updateVersionInfo();
            return true;
        }
        return false;
    }

    /**
     * 現在のバージョン情報を取得
     * @return
     */
    public static int getCurrentVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return -1;
    }

    /**
     * 初回バージョン情報を取得
     * @return
     */
    private int loadInitialVersionCode() {
        return mPref.load(PrefUtil.CONSTANT.INIT_VERSION_CODE);
    }

    /**
     * 最後に起動したバージョン情報を取得
     * @return
     */
    private int loadLastShownVersionCode() {
        return mPref.load(PrefUtil.CONSTANT.VERSION_CODE);
    }

    /**
     * 最後に起動したバージョン情報を保存
     */
    private void updateVersionInfo() {
        mPref.save(PrefUtil.CONSTANT.VERSION_CODE, getCurrentVersionCode(mAppContext));
    }


}