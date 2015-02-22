package com.tablecloth.bookshelf.util;

import android.content.Context;

import com.tablecloth.bookshelf.R;

/**
 * Created by shnomura on 2015/02/22.
 */
public class Rakuten {

    public class Key {
        final public static String ITEM_LIST ="Items";
        final public static String ITEM_DETAIL ="Item";
        final public static String TITLE_NAME = "title";
        final public static String TITLE_NAME_KANA = "titleKana";
        final public static String MAGAZINE_NAME = "seriesName";
        final public static String MAGAZINE_NAME_KANA = "seriesNameKana";
        final public static String AUTHOR_NAME = "author";
        final public static String AUTHOR_NAME_KANA = "authorKana";
        final public static String COMPANY_NAME = "publisherName";
        final public static String IMAGE_URL_SMALL = "smallImageUrl";
        final public static String IMAGE_URL_MEDIUM = "mediumImageUrl";
        final public static String IMAGE_URL_LARGE = "largeImageUrl";
    }

    /**
     * 楽天API・書籍検索専用のURLを返す
     * 一番細かく情報が取れるので、可能であればこちらを使う
     * @return
     */
    protected String getRakutenBooksBookURI(Context context, String key, String value) {
        // 楽天ブックス系API・楽天ブックス書籍検索API
        // https://app.rakuten.co.jp/services/api/BooksBook/Search/20130522?format=json&isbn=9784812471692&applicationId=1019452313987815323
        // 楽天ブックス系API・楽天ブックス総合検索API
        // https://app.rakuten.co.jp/services/api/BooksTotal/Search/20130522?format=json&isbnjan=9784812471692&applicationId=1019452313987815323

        String apiName = context.getString(R.string.rakuten_api_name_books);
        return getRakutenURI(context, apiName, key, value);
    }

    /**
     * 楽天API・書籍関連の総合検索用のURLを返す
     * 読みなどの細かい情報はとれないが、情報が取得できる可能性は高い。
     * @param key
     * @param value
     * @return
     */
    protected String getRakutenBooksTotalUri(Context context, String key, String value) {
        String apiName = context.getString(R.string.rakuten_api_name_all);
        return getRakutenURI(context, apiName, key, value);
    }

    /**
     * 楽天APIの検索URLを作成し、返す。
     * @param apiName
     * @param key
     * @param value
     * @return
     */
    private String getRakutenURI(Context context, String apiName, String key, String value) {
        String uri = "";
        uri += "https://app.rakuten.co.jp/services/api/";
        uri += apiName;
        uri += "/Search/20130522?format=json";
        uri += "&" + key + "=" + value;
        uri += "&applicationId=" + context.getString(R.string.id_rakuten_app_id);
        return uri;
    }
}
