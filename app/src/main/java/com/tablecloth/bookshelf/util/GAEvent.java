package com.tablecloth.bookshelf.util;

/**
 * GoogleAnalytics用イベント一覧
 * Created by shnomura on 2015/02/19.
 */
public class GAEvent {
    /**
     * イベントの種別
     * →イベントの発生画面に変更したい
     */
    public class Type {
        final public static String USER_ACTION = "Action"; // ユーザーの行動集計用
        final public static String USER_Data = "Data"; // ユーザーの情報分析用（作品登録率等）
    }

    /**
     * イベントの発生画面
     * →イベントの内容に変更したい
     */
    public class Event {
        final public static String SHOW_MODE_VIEW = "ShowModeView"; // 画面表示タイプ・一覧表示
        final public static String SHOW_MODE_SEARCH = "ShowModeSearch"; // 画面表示タイプ・検索表示
        final public static String SHOW_MODE_API_SEARCH_RESULT = "ShowModeApiSearchResult"; // 画面表示タイプ・WebAPI検索結果表示
        final public static String TAP_SEARCH_BTN = "TapAddSeriesBtn"; // 検索ボタンタップ
        final public static String TAP_ADD_SERIES_BTN = "TapAddSeriesBtn"; // 作品追加ボタンタップ
        final public static String TAP_ADD_SERIES_SEARCH_BTN = "TapAddSeriesBySearch"; // 作品追加→検索ボタンタップ
        final public static String TAP_ADD_SERIES_MANUAL_BTN = "TapAddSeriesByManual"; // 作品追加ボタンタップ
        final public static String ADD_SERIES = "AddSeries"; // 作品追加
        final public static String TAP_SETTINGS_BTN = "TapSettingsBtn"; // 設定画面起動
        final public static String SETTINGS_SET_SHOW_TYPE = "SettingsSetShowType"; // 表示形式選択
        final public static String SETTINGS_TAP_REVIEW = "SettingsReview"; // レビュー選択
        final public static String SETTINGS_TAP_MAILER = "SettingsTapShowType"; // メーラー選択

    }

    /**
     * イベントの内容
     * →イベントのパラメーター（あれば追加する系）に変更したい
     */
    public class Param {
        final public static String LIST = "List";
        final public static String GRID = "Grid";
//        final public static String SHOW_MODE_VIEW = "LIST_SHOW_MODE_VIEW"; // 画面表示タイプ・一覧表示
//        final public static String SHOW_MODE_SEARCH = "LIST_SHOW_MODE_SEARCH"; // 画面表示タイプ・検索表示
//        final public static String SHOW_MODE_API_SEARCH_RESULT = "SHOW_MODE_API_SEARCH_RESULT"; // 画面表示タイプ・WebAPI検索結果表示
//        final public static String TAP_ADD_SERIES_BTN = "TAP_ADD_SERIES_BTN"; // 作品追加ボタンタップ
    }
}
