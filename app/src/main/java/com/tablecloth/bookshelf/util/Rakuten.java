package com.tablecloth.bookshelf.util;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.tablecloth.bookshelf.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

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
        final public static String ISBN = "isbnjan";
        final public static String SEARCH_RESULT_PAGE_COUNT = "pageCount"; // 検索結果が何ページ分あるか
        final public static String SEARCH_RESULT_PAGE_CURRENT = "page"; // 今現在検索結果の何ページ目にいるか
        final public static String SEARCH_RESULT_COUNT = "count"; // 検索結果が何件あるか
    }

    // 楽天API用の検索対象一覧
    final public static String[] SEARCH_CONTENT_LIST = {
            "タイトル名",
            "タイトル名（カナ入力）",
            "作者名",
            "作者名（カナ入力）",
            "掲載誌名",
            "掲載誌名（カナ入力）",
            "出版社名",
            "ISBN",
    };

    /**
     * 楽天API・書籍検索専用のURLを返す
     * 一番細かく情報が取れるので、可能であればこちらを使う
     * @return
     */
    public static String getRakutenBooksBookURI(Context context, String key, String value) {
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
    public static String getRakutenBooksTotalUri(Context context, String key, String value) {
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
    private static String getRakutenURI(Context context, String apiName, String key, String value) {
        String uri = "";
        uri += "https://app.rakuten.co.jp/services/api/";
        uri += apiName;
        uri += "/Search/20130522?format=json";
        uri += "&" + key + "=" + value;
        uri += "&sort=%2BreleaseDate"; // 古い順にソートする
        uri += "&applicationId=" + context.getString(R.string.id_rakuten_app_id);
        return uri;
    }

    /**
     * 非同期でHTTPアクセスを行い、楽天WebAPIから情報を取得するためのクラス
     * http://codezine.jp/article/detail/7276?p=2
     *
     */
    public static class RakutenAPIAsyncLoader extends AsyncTaskLoader<String> {
        String mUrl = ""; // 呼び出すWebApi用URL

        public RakutenAPIAsyncLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        public String loadInBackground() {
            // WebAPIの呼び出し処理(HTTP通信等）を行う
            HttpClient httpClient = new DefaultHttpClient();
            try {
                String responseBody = httpClient.execute(new HttpGet(mUrl),
                        // UTF-8でデコードするためhandleResponseをオーバーライドする
                        new ResponseHandler<String>() {
                            @Override
                            public String handleResponse(HttpResponse response)
                                    throws ClientProtocolException, IOException {

                                // 成功時のみデータを返す。それ以外は空文字を返す
                                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                                    return EntityUtils.toString(response.getEntity(), "UTF-8");
                                }
                                return "";
                            }
                        });

                return responseBody;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                httpClient.getConnectionManager().shutdown();
            }
            return "";
        }
    }

}
