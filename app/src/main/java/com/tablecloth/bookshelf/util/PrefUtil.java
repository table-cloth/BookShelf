package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Minami on 2015/02/15.
 */
public class PrefUtil {

    final SharedPreferences mPref;
    Context mAppContext;
    final private static int DEF_INT = -1;

    public PrefUtil(Context appContext) {
        mAppContext = appContext;
        mPref = mAppContext.getSharedPreferences(appContext.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * 保存
     * @param value
     */
    public boolean save(String key, int value) {
        if(mPref.edit().putInt(key, value).commit()) return true;
        return false;
    }

    /**
     * 読み込み
     * @param key
     * @return
     */
    public int load(String key) {
        return mPref.getInt(key, DEF_INT);
    }

    /**
     * てうす
     */

    public class CONSTANT {
        final public static String INIT_VERSION_CODE = "INIT_VERSION_CODE"; // 始めにインストールしたバージョン情報
        final public static String VERSION_CODE = "VERSION_CODE"; // 最後に表示したバージョン情報
    }

}
