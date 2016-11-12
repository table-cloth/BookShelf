package com.tablecloth.bookshelf.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookSeriesData;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

/**
 * UTil for Rakuten Search API
 *
 * Created by Minami on 2015/02/22.
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
        final public static String SEARCH_RESULT_PAGE_COUNT = "pageCount"; // Number of page in search result
        final public static String SEARCH_RESULT_PAGE_CURRENT = "page"; // Which page you are in right now
        final public static String SEARCH_RESULT_COUNT = "count"; // Count of search result
    }

    // List of search targets
    final public static int[] SEARCH_CONTENT_LIST = {
            R.string.search_category_title,
            R.string.search_category_title_pronunciation,
            R.string.search_category_author,
            R.string.search_category_author_pronunciation,
            R.string.search_category_magazine,
            R.string.search_category_magazine_pronunciation,
            R.string.search_category_company,
            R.string.search_category_isbn,
    };

    // Rakuten API search key, related with "SEARCH_CONTENT_LIST"
    final public static String[] SEARCH_KEY_LIST = {
            Key.TITLE_NAME,
            Key.TITLE_NAME_KANA,
            Key.AUTHOR_NAME,
            Key.AUTHOR_NAME_KANA,
            Key.MAGAZINE_NAME,
            Key.MAGAZINE_NAME_KANA,
            Key.COMPANY_NAME,
            Key.ISBN
    };

    /**
     * Get URI for searching books in books category, with Rakuten API
     *
     * @param context context
     * @param key search key
     * @param value search value
     * @return search URI
     */
    @NonNull
    public static String getRakutenBooksBookURI(@NonNull Context context, @NonNull String key, @NonNull String value) {
        // 楽天ブックス系API・楽天ブックス書籍検索API
        // https://app.rakuten.co.jp/services/api/BooksBook/Search/20130522?format=json&isbn=9784812471692&applicationId=1019452313987815323
        // 楽天ブックス系API・楽天ブックス総合検索API
        // https://app.rakuten.co.jp/services/api/BooksTotal/Search/20130522?format=json&isbnjan=9784812471692&applicationId=1019452313987815323

        String apiName = context.getString(R.string.rakuten_api_name_books);
        return getRakutenURI(context, apiName, key, value);
    }

    /**
     * Get URI for searching books in total category, with Rakuten API
     *
     * @param context context
     * @param key search key
     * @param value search value
     * @return search URI
     */
    @NonNull
    public static String getRakutenBooksTotalUri(@NonNull Context context, @NonNull String key, @NonNull String value) {
        String apiName = context.getString(R.string.rakuten_api_name_all);
        return getRakutenURI(context, apiName, key, value);
    }

    /**
     * Callback for searching with API
     */
    public interface SearchResultListener {
        /**
         * Search success
         * Search result may be empty
         *
         * @param searchResults search result in json text
         */
        void onSearchSuccess(@NonNull String searchResults);

        /**
         * Search fail
         *
         * @param errorContent Error description text
         */
        void onSearchError(@NonNull String errorContent);
    }

    /**
     * Search results from given search info
     *
     * @param context context
     * @param handler handler
     * @param searchKey Search key for rakuten API
     * @param searchValue Search value for rakuten API
     * @param listener Search result listener
     */
    public static void searchFromRakutenAPI(
            @NonNull final Context context, @NonNull final Handler handler,
            @Nullable final String searchKey, @Nullable final String searchValue,
            @NonNull final SearchResultListener listener) {
        if(Util.isEmpty(searchKey)
                || Util.isEmpty(searchValue)) {
            listener.onSearchError("invalid search key or value. [SearchKey]" + searchKey + " / [SearchValue]" + searchValue);
            return;
        }

        // start new thread since network accessing is needed
        new Thread(new Runnable() {
            @Override
            public void run() {

                // search in Books / Books category
                String booksBookUri = Rakuten.getRakutenBooksBookURI(
                        context, searchKey, searchValue);
                RakutenAPIAsyncLoader loader =
                        new RakutenAPIAsyncLoader(context, booksBookUri);
                final String booksBookJsonText = loader.loadInBackground();

                // if result success
                if(!Util.isEmpty(booksBookJsonText)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run () {
                            listener.onSearchSuccess(booksBookJsonText);
                        }
                    });
                    return;
                }

                // search in Total category
                String booksTotalUri = Rakuten.getRakutenBooksTotalUri(
                        context, searchKey, searchValue);
                loader = new RakutenAPIAsyncLoader(context, booksTotalUri);
                final String booksTotalJsonText = loader.loadInBackground();

                // if result success
                if(!Util.isEmpty(booksTotalJsonText)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run () {
                            listener.onSearchSuccess(booksTotalJsonText);
                        }
                    });

                // if still fail
                } else {
                    listener.onSearchError("No valid results found");
                }

            }
        }).start();
    }


    @NonNull
    public static ArrayList<BookSeriesData> convertJsonText2BookSeriesDataList(
            @NonNull Context context, @NonNull String jsonText, @NonNull int maxCount) {
        ArrayList<BookSeriesData> seriesDataList = new ArrayList<>();
        if(Util.isEmpty(jsonText)) {
            return seriesDataList;
        }

        JSONObject jsonObj = JsonUtil.getJsonObject(jsonText);
        JSONArray jsonArray = JsonUtil.getJsonArray(jsonObj, Rakuten.Key.ITEM_LIST);
        List<JSONObject> jsonObjList = JsonUtil.convertJsonArray2JsonObjectList(jsonArray);

        // get value registered with key "count"
        int count;
        try {
            count = Integer.valueOf(
                    JsonUtil.getValue(jsonObj, Rakuten.Key.SEARCH_RESULT_COUNT));
        } catch(Exception e) {
            e.printStackTrace();
            return seriesDataList;
        }

        // show search result toast
        if(count >= maxCount) {
            ToastUtil.show(context, context.getString(R.string.apu_search_hit_count_over, maxCount));
        } else {
            ToastUtil.show(context, context.getString(R.string.apu_search_hit_count, count));
        }

        int showStartInex = 0;
        int showEndIndex = count >= maxCount
                ? maxCount
                : count;

        // 取得した結果を作品情報一覧へと分解する
        for(int i = showStartInex ; i < showEndIndex ; i ++) {
            if(i >= jsonObjList.size()) {
                continue;
            }

            BookSeriesData seriesData = Rakuten.createSeriesDataFromJsonDetailData(
                    context, JsonUtil.getJsonObject(jsonObjList.get(i), Rakuten.Key.ITEM_DETAIL));

            seriesDataList.add(seriesData);
        }
        return seriesDataList;
    }


    /**
     * Get actual URI for searching with Rakuten API
     *
     * @param context context
     * @param apiName api name
     * @param key search key
     * @param value search value
     * @return URI for searching in Rakuten URI
     */
    @NonNull
    private static String getRakutenURI(
            @NonNull Context context, @NonNull String apiName, @NonNull String key, String value) {
        if(!Util.isEmpty(value)) {
            value = value.replace("　", "%E3%80%80");
            value = value.replace(" ", "%20");
        }

        String uri = "";
        uri += "https://app.rakuten.co.jp/services/api/";
        uri += apiName;
        uri += "/Search/20130522?format=json";
        uri += "&" + key + "=" + value;
        uri += "&sort=%2BreleaseDate"; // Sort with old one comes first
        uri += "&applicationId=" + context.getString(R.string.id_rakuten_app_id);
        return uri;
    }

    /**
     * 非同期でHTTPアクセスを行い、楽天WebAPIから情報を取得するためのクラス
     * http://codezine.jp/article/detail/7276?p=2
     *
     */
    /**
     * Class for accessing network in background, to gain result from Rakuten API
     * http://codezine.jp/article/detail/7276?p=2
     */
    private static class RakutenAPIAsyncLoader extends AsyncTaskLoader<String> {
        String mUrl = "";

        /**
         * Constructor
         *
         * @param context context
         * @param url url to access
         */
        public RakutenAPIAsyncLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        /**
         * Load result, will give empty text if any invalid values
         *
         * @return search result or empty text
         */
        @NonNull
        @Override
        public String loadInBackground() {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                return httpClient.execute(new HttpGet(mUrl),
                        // Override handleResponse, to decode in UTF-8
                        new ResponseHandler<String>() {
                            @Override
                            public String handleResponse(HttpResponse response)
                                    throws IOException {

                                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                                    return EntityUtils.toString(response.getEntity(), "UTF-8");
                                }
                                return "";
                            }
                        });
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

    /**
     * Create BookSeriesData from JsonObject
     * JsobObject of Rakute.Key.ITEM_DETAIL is expected
     *
     * @param context context
     * @param jsonDetailData JSONObject instance with BookSeries data
     * @return BookSeriesData instance, or null if JSONObject is invalid
     */
    @Nullable
    public static BookSeriesData createSeriesDataFromJsonDetailData(@NonNull Context context, @Nullable JSONObject jsonDetailData) {
        if(jsonDetailData == null) {
            return null;
        }

        BookSeriesData data = new BookSeriesData(context);

        data.setTitle(JsonUtil.getValue(jsonDetailData, Rakuten.Key.TITLE_NAME));
        data.setAuthor(JsonUtil.getValue(jsonDetailData, Rakuten.Key.AUTHOR_NAME));
        data.setMagazine(JsonUtil.getValue(jsonDetailData, Rakuten.Key.MAGAZINE_NAME));
        data.setCompany(JsonUtil.getValue(jsonDetailData, Rakuten.Key.COMPANY_NAME));

        data.setTitlePronunciation(JsonUtil.getValue(jsonDetailData, Rakuten.Key.TITLE_NAME_KANA));
        data.setAuthorPronunciation(JsonUtil.getValue(jsonDetailData, Rakuten.Key.AUTHOR_NAME_KANA));
        data.setMagazinePronunciation(JsonUtil.getValue(jsonDetailData, Rakuten.Key.MAGAZINE_NAME_KANA));

        // set ImageUrl
        String imageUrl = JsonUtil.getValue(jsonDetailData, Rakuten.Key.IMAGE_URL_LARGE);
        if(Util.isEmpty(imageUrl)) {
            imageUrl = JsonUtil.getValue(jsonDetailData, Rakuten.Key.IMAGE_URL_MEDIUM);
        }
        if(Util.isEmpty(imageUrl)) {
            imageUrl = JsonUtil.getValue(jsonDetailData, Rakuten.Key.IMAGE_URL_SMALL);
        }
        if(!Util.isEmpty(imageUrl)) {
            data.setImagePath(imageUrl);
        }

        return data;
    }

}
