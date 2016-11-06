package com.tablecloth.bookshelf.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minami on 2015/02/22.
 */
public class JsonUtil {

    /**
     * JSONObjectを取得する
     * @param jsonStr
     * @return
     */
    public static JSONObject getJsonObject(String jsonStr) {
        if(Util.isEmpty(jsonStr)) return null;
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static JSONObject getJsonObject(JSONObject jsonObj, String key) {
        if(jsonObj == null) return null;
        JSONObject obj = null;
        try {
            obj = jsonObj.getJSONObject(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * JSONObjectからJSONの配列データを取得する
     * @param jsonObj
     * @param key
     * @return
     */
    public static JSONArray getJsonArray(JSONObject jsonObj, String key) {
        if(jsonObj == null) return null;
        JSONArray array = null;
        try {
            array = jsonObj.getJSONArray(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * JsonArrayからJSONObjectの一覧を取得する
     * @param jsonArr
     * @return
     */
    public static List<JSONObject> getJsonObjectsList(JSONArray jsonArr) {
        List<JSONObject> objList = new ArrayList<JSONObject>();
        if(jsonArr == null) return objList;
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
     * JsonObjectから指定Keyのデータを文字列で取得する
     * @param jsonObj
     * @param key
     * @return
     */
    public static String getJsonObjectData(JSONObject jsonObj, String key) {
        String data = "";
        try {
            data = jsonObj.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }



}
