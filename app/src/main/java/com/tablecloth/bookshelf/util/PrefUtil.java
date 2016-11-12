package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Preference util class
 *
 * Created by Minami on 2015/02/15.
 */
public class PrefUtil {

    final private SharedPreferences mPref;
    private Context mAppContext;
    final private static int DEF_INT = -1;

    /**
     * Calls constructor and gets instance of PrefUtil
     *
     * @param appContext Context of app
     * @return PrefUtil instance
     */
    @NonNull
    public static PrefUtil getInstance(@NonNull Context appContext) {
        return new PrefUtil(appContext);
    }

    /**
     * Constructor
     *
     * @param appContext Context of app
     */
    private PrefUtil(@NonNull Context appContext) {
        mAppContext = appContext;
        mPref = mAppContext.getSharedPreferences(appContext.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Save given value
     *
     * @param value value to save
     */
    public boolean save(@NonNull String key, int value) {
        return mPref.edit().putInt(key, value).commit();
    }

    /**
     * Load value with given key
     *
     * @param key key to load
     * @return value related with key, or default value (-1)
     */
    public int load(String key) {
        return mPref.getInt(key, DEF_INT);
    }
}
