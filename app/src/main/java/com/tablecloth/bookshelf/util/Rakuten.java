package com.tablecloth.bookshelf.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.activity.BookSeriesCatalogBaseActivity;
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

//
//                    mProgress.close();
//                    // 検索結果が空の場合はトースト通知でお知らせ
//                    ToastUtil.show(BookSeriesCatalogBaseActivity.this, "取得できる作品がありませんでした。内容を変更しもう一度お試しください。");
//                } else {
//                    convertJsonStr2JsonArrayObject(jsonText);
//                    mProgress.close();
//                    switchMode(G.MODE_API_SEARCH_RESULT);
//                    refreshData();
//                }


//
//                // if result fail
//
//                // JSONを取得後に検索結果一覧・または補完済みの作品登録画面を表示
//                // 検索結果が0件の場合：「取得できる作品がありませんでした。内容を変更しもう一度お試しください。」という通知を表示
//                // 検索結果が1件の場合：作品登録cd画面を開き、取得できている全ての情報を入力済みの状態で表示する
//                // 検索結果が2件以上の場合：作品一覧画面を開き、求めている作品を選んでもらう。選択後は「検索結果が1件の場合」と同じ流れになる
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if(Util.isEmpty(jsonText)) {
//                            // 取得失敗した場合は書籍全般として再建策するする
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // 楽天APIを利用してJSONを取得する。
//                                    String url = Rakuten.getRakutenBooksTotalUri(BookSeriesCatalogBaseActivity.this, selectKey, selectValue);
//                                    Rakuten.RakutenAPIAsyncLoader loader = new Rakuten.RakutenAPIAsyncLoader(BookSeriesCatalogBaseActivity.this, url);
//
//                                    final String jsonRetryStr = loader.loadInBackground();
//
//                                    mHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if(Util.isEmpty(jsonRetryStr)) {
//                                                mProgress.close();
//                                                // 検索結果が空の場合はトースト通知でお知らせ
//                                                ToastUtil.show(BookSeriesCatalogBaseActivity.this, "取得できる作品がありませんでした。内容を変更しもう一度お試しください。");
//                                            } else {
//                                                convertJsonStr2JsonArrayObject(jsonText);
//                                                mProgress.close();
//                                                switchMode(G.MODE_API_SEARCH_RESULT);
//                                                refreshData();
//                                            }
//                                        }
//                                    });
//
//                                }
//                            }).start();
//                        } else {
//                            convertJsonStr2JsonArrayObject(jsonText);
//                            mProgress.close();
//                            switchMode(G.MODE_API_SEARCH_RESULT);
//                            refreshData();
//                        }
//                    }
//                });
            }
        }).start();
    }


    @NonNull
    public static ArrayList<BookSeriesData> convertJsonText2BookSeriesDataList(Context context, String jsonText, int maxCount) {
        ArrayList<BookSeriesData> seriesDataList = new ArrayList<>();
        if(Util.isEmpty(jsonText)) {
            return seriesDataList;
        }

        JSONObject jsonObj = JsonUtil.getJsonObject(jsonText);
        JSONArray jsonArray = JsonUtil.getJsonArray(jsonObj, Rakuten.Key.ITEM_LIST);
        List<JSONObject> jsonObjList = JsonUtil.getJsonObjectsList(jsonArray);

        // get value registered with key "count"
        int count = -1;
        try {
            count = Integer.valueOf(
                    JsonUtil.getJsonObjectData(jsonObj, Rakuten.Key.SEARCH_RESULT_COUNT));
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
     * 楽天APIの検索URLを作成し、返す。
     * @param apiName
     * @param key
     * @param value
     * @return
     */
    private static String getRakutenURI(Context context, String apiName, String key, String value) {
        if(!Util.isEmpty(value)) {
            value = value.replace("　", "%E3%80%80");
            value = value.replace(" ", "%20");
        }

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
    private static class RakutenAPIAsyncLoader extends AsyncTaskLoader<String> {
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

        data.setTitle(JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.TITLE_NAME));
        data.setAuthor(JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.AUTHOR_NAME));
        data.setMagazine(JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.MAGAZINE_NAME));
        data.setCompany(JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.COMPANY_NAME));

        data.setTitlePronunciation(JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.TITLE_NAME_KANA));
        data.setAuthorPronunciation(JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.AUTHOR_NAME_KANA));
        data.setMagazinePronunciation(JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.MAGAZINE_NAME_KANA));

        // set ImageUrl
        String imageUrl = JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.IMAGE_URL_LARGE);
        if(Util.isEmpty(imageUrl)) {
            imageUrl = JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.IMAGE_URL_MEDIUM);
        }
        if(Util.isEmpty(imageUrl)) {
            imageUrl = JsonUtil.getJsonObjectData(jsonDetailData, Rakuten.Key.IMAGE_URL_SMALL);
        }
        if(!Util.isEmpty(imageUrl)) {
            data.setImagePath(imageUrl);
        }

        return data;
    }

}
