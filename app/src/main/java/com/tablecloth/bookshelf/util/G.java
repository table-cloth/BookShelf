package com.tablecloth.bookshelf.util;

import java.util.Collection;

/**
 * あらゆるクラスで使用する用の定数を保格納する
 * Created by shnomura on 2014/08/17.
 */
public class G {
	
	final public static String AD_UNIT_ID = "ca-app-pub-9740549564683244/9984495812";
	
    final public static int RESULT_NONE = 0;
    final public static int RESULT_POSITIVE = 1;
    final public static int RESULT_NEGATIVE = 2;

    final public static String RESULT_DATA_SELECTED_ID = "selected_id"; // intを返す
    final public static int RESULT_DATA_SELECTED_BTN_ISBN = 0;
    final public static int RESULT_DATA_SELECTED_BTN_MANUAL = 1;
    final public static String RESULT_DATA_SELECTED_KEY = "selected_key"; // Stringを返す
    final public static String RESULT_DATA_SELECTED_VALUE = "selected_value"; // Stringを返す


    final public static int REQUEST_CODE_LIST_ROW_DELETE_SERIES = 1;
    final public static int REQUEST_CODE_LIST_ADD_SERIES = 2;
//    final public static int REQUEST_CODE_NEW_SERIES_DETAIL = 3;
    final public static int REQUEST_CODE_EDIT_SERIES_DETAIL = 3;
    final public static int REQUEST_CODE_IMAGE_CHANGE_CONFIRM_1 = 4;
    final public static int REQUEST_CODE_IMAGE_CHANGE_CONFIRM_2 = 5;
    final public static int REQUEST_CODE_IMAGE_GALLERYS = 6;
    final public static int REQUEST_CODE_IMAGE_CAMERA = 7;
    final public static int REQUEST_CODE_SELECT_ADD_SERIES_TYPE = 8;
    final public static int REQUEST_CODE_LIST_SEARCH_RAKUTEN = 9;



    final public static int REQUEST_CODE_UPDATE_DIALOG = 100;


    final public static int DATA_TYPE_SERIES = 0;
    final public static int DATA_TYPE_BOOK = 1;

    final public static int SELECT_DATA_TITLE = 0;
    final public static int SELECT_DATA_TITLE_PRONUNCIATION = 1;
    final public static int SELECT_DATA_AUTHOR = 2;
    final public static int SELECT_DATA_AUTHOR_PRONUNCIATION = 3;
    final public static int SELECT_DATA_COMPANY = 4;
    final public static int SELECT_DATA_COMPANY_PRONUNCIATION = 5;
    final public static int SELECT_DATA_IMAGE = 6;
    final public static int SELECT_DATA_VOLUME = 7;
    final public static int SELECT_DATA_TAGS = 8;
    
//    final public static String SEARCH_CONTENT_ALL = "%"; // 検索条件＝無条件の場合に指定
    final public static int SEARCH_MODE_ALL = 0; // 検索対象＝全ての場合にしてい
    final public static int SEARCH_MODE_TITLE = 1; // 検索対象＝タイトルの場合にしてい
    final public static int SEARCH_MODE_AUTHOR = 2; // 検索対象＝作者の場合にしてい
    final public static int SEARCH_MODE_COMPANY = 3; // 検索対象＝出版社の場合にしてい
    final public static int SEARCH_MODE_MAGAZINES = 4; // 検索対象＝掲載誌の場合にしてい
    // 各種検索条件一覧
    final public static String[] SEARCH_MODE_LIST = {
            "全て", // SEARCH_MODE_ALL
            "タイトル名", // SEARCH_MODE_ALL
            "作者名", // SEARCH_MODE_ALL
            "出版社名", // SEARCH_MODE_ALL
            "掲載誌名", // SEARCH_MODE_ALL
    };

    final public static int MODE_VIEW = 0; // デフォルトの閲覧モード
//  final public static int MODE_DELETE = 1; // 削除モード
    final public static int MODE_SEARCH = 2; // 検索モード
    // intentの情報
    final public static String INTENT_SERIES_ID = "series_id";
}
