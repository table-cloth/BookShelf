package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.http.HttpPostHandler;
import com.tablecloth.bookshelf.http.HttpPostTask;

import org.json.JSONObject;

/**
 * Util for converting text in different format
 *
 * Created on 2016/11/19.
 */
public class GooTextConverter {
    private final static String PARAM_KEY_CONTENT_TYPE = "Content-type";
    private final static String PARAM_VALUE_CONTENT_TYPE = "application/json";

    private final static String PARAM_KEY_APP_ID = "app_id";
    private final static String PARAM_KEY_CONVERT_TEXT = "sentence";
    private final static String PARAM_KEY_OUTPUT_TYPE = "output_type";
    private final static String PARAM_KEY_REQUEST_ID = "request_id";
    private final static String PARAM_VALUE_OUTPUT_TYPE_HIRAGANA = "hiragana";
    private final static String PARAM_VALUE_OUTPUT_TYPE_KATAKANA = "katakana";

    private final static String RESPONSE_KEY_REQUEST_ID = PARAM_KEY_REQUEST_ID;
    private final static String RESPONSE_KEY_OUTPUT_TYPE = PARAM_KEY_OUTPUT_TYPE;
    private final static String RESPONSE_KEY_CONVERT_TEXT = "converted";

    private final static String REQUEST_URL = "https://labs.goo.ne.jp/api/hiragana";

    /**
     * Convert text to Hiragana text
     *
     * @param context Context
     * @param convertText Text to convert
     * @param handler HttpPostHandler
     */
    public static void convert2Hiragana(
            @NonNull Context context, @NonNull String convertText,
            @NonNull HttpPostHandler handler) {
        convertWithGooTextConverter(context, convertText, handler, true);
    }

    /**
     * Convert text to Katakana text
     *
     * @param context Context
     * @param convertText Text to convert
     * @param handler HttpPostHandler
     */
    public static void convert2Katakana(
            @NonNull Context context, @NonNull String convertText,
            @NonNull HttpPostHandler handler) {
        convertWithGooTextConverter(context, convertText, handler, false);
    }

    /**
     * Get converted text from Goo convert response data
     *
     * @param jsonText Json response text
     * @return converted text
     */
    @Nullable
    public static String getConvertedText(String jsonText) {
        JSONObject jsonObject = JsonUtil.getJsonObject(jsonText);
        if(jsonObject == null) {
            return null;
        }
        return JsonUtil.getValue(jsonObject, RESPONSE_KEY_CONVERT_TEXT);
    }

    /**
     * Convert text using Goo text converter
     *
     * @param context Context
     * @param convertText Text to convert
     * @param handler HttpPostHandler
     * @param convert2Hiragana Convert to Hiragana if true, else Katakana
     */
    private static void convertWithGooTextConverter(
            Context context, String convertText,
            HttpPostHandler handler, boolean convert2Hiragana) {

        HttpPostTask httpPostTask = new HttpPostTask(context, REQUEST_URL, handler);
        httpPostTask.addPostHeader(PARAM_KEY_CONTENT_TYPE, PARAM_VALUE_CONTENT_TYPE);
        httpPostTask.addPostParam(PARAM_KEY_APP_ID, context.getString(R.string.goo_hiragana));
        httpPostTask.addPostParam(PARAM_KEY_CONVERT_TEXT, convertText);
        httpPostTask.addPostParam(
                PARAM_KEY_OUTPUT_TYPE,
                convert2Hiragana
                        ? PARAM_VALUE_OUTPUT_TYPE_HIRAGANA
                        : PARAM_VALUE_OUTPUT_TYPE_KATAKANA);
        httpPostTask.execute();
    }
}
