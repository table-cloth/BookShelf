package com.tablecloth.bookshelf.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class for Json
 *
 * Created by Minami on 2015/02/22.
 */
public class JsonUtil {

    /**
     * Get json object
     *
     * @param jsonStr json raw text
     * @return JSONObject instance or null
     */
    @Nullable
    public static JSONObject getJsonObject(@NonNull String jsonStr) {
        if(Util.isEmpty(jsonStr)) {
            return null;
        }
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Get json object with given key
     *
     * @param jsonObj JSONObject to search from
     * @param key search key
     * @return JSONObject instance or null
     */
    @Nullable
    public static JSONObject getJsonObject(@NonNull JSONObject jsonObj, @NonNull String key) {
        JSONObject obj = null;
        try {
            obj = jsonObj.getJSONObject(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Get json array from json obj with given key
     *
     * @param jsonObj JSONObject to search from
     * @param key search key
     * @return JSONArray instance or null
     */
    @Nullable
    public static JSONArray getJsonArray(@NonNull JSONObject jsonObj, @NonNull String key) {
        JSONArray array = null;
        try {
            array = jsonObj.getJSONArray(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * Convert JSONArray to JSONObject in list format
     *
     * @param jsonArr JSONArray to convert
     * @return JSONObject in list
     */
    @NonNull
    public static List<JSONObject> convertJsonArray2JsonObjectList(@NonNull JSONArray jsonArr) {
        List<JSONObject> objList = new ArrayList<>();
        try {
            for(int i = 0 ; i < jsonArr.length() ; i ++) {
                objList.add(jsonArr.getJSONObject(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objList;
    }

    /**
     * Get text value, related with given key in JSONObject
     *
     * @param jsonObj JSONObject to search from
     * @param key search key
     * @return value related with key
     */
    public static String getValue(@NonNull JSONObject jsonObj, @NonNull String key) {
        String data = "";
        try {
            data = jsonObj.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
