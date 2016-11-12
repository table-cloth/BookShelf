package com.tablecloth.bookshelf.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;

/**
 * Created by Minami on 2015/02/15.
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
        int versionDiff = getCurrentVersionCode(mAppContext) - loadLastShownVersionCode();
        // １つ以上前のバージョンからアップデートした場合
//        if(versionDiff > 1) {
//            Intent intent = SimpleDialogActivity.getIntent(mAppContext, "機能追加のお知らせ", "■作品の表示形式を追加しました！\n以前のリスト形式の表示に戻したい場合は画面左上の「設定ボタン」から変更をお願いいたします。\n\n■タグ機能を追加しました！\n作品の登録時、編集時にタグの編集ができるようになりました。\nタグによる検索にも対応しておりますので、是非ご利用ください。\n\n■その他ご意見、又はご要望等ございましたらレビューにて記載をお願いいたします。", "レビューする", "しない");
//            mActivity.startActivityForResult(intent, G.REQUEST_CODE_UPDATE_DIALOG);
//
//            // バージョン情報を更新
//            updateVersionInfo();
//            return true;
//        } else if(versionDiff == 1) {

        // １つ以上前のバージョンからアップデートした場合
        String suffix = "\n\nーーーーー\n少しでも良いアプリになるよう改善を行っております。\n★5のレビューを頂けますと励みになります！\n宜しくお願い致します (*- -)(*_ _)ペコリ";

        if(versionDiff == 1) {
//            Intent intent = SimpleDialogActivity.getIntent(
//                    mAppContext,
//                    "お知らせ",
//                    "■作品の検索時に、正しくない画像が表示される不具合を修正しました。\n■設定画面の項目の表示を修正しました。\n\nご迷惑をおかけし申し訳ございませんでした。\n今後ともよろしくお願い致します。" + suffix,
//                    "レビューする",
//                    "しない");
//            mActivity.startActivityForResult(intent, G.REQUEST_CODE_UPDATE_DIALOG);
            Intent intent = SimpleDialogActivity.getIntent(
                    mAppContext,
                    R.string.update_dialog_title,
                    R.string.update_dialog_content,
                    R.string.update_dialog_do_review,
                    R.string.update_dialog_do_not_review);
            mActivity.startActivityForResult(intent, G.REQUEST_CODE_UPDATE_DIALOG);

            // バージョン情報を更新
            updateVersionInfo();
            return true;

        } else if(versionDiff > 1) {
//            Intent intent = SimpleDialogActivity.getIntent(
//                    mAppContext,
//                    "お知らせ",
//                    "■作品の画像の読み込み速度を改善しました。\n\n■お問い合わせ・レビューの項目を追加しました。\n　設定画面よりお問い合わせ・レビューをお願い致します。" + suffix,
//                    "レビューする",
//                    "しない");
//            mActivity.startActivityForResult(intent, G.REQUEST_CODE_UPDATE_DIALOG);
            Intent intent = SimpleDialogActivity.getIntent(
                    mAppContext,
                    R.string.update_dialog_title,
                    R.string.update_dialog_content,
                    R.string.update_dialog_do_review,
                    R.string.update_dialog_do_not_review);
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
