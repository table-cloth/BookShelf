package com.tablecloth.bookshelf.util;

/**
 * Created by shnomura on 2015/02/19.
 */
public class Event {
    /**
     * イベントの種別
     * →イベントの発生画面に変更したい
     */
    public class Category {
        final public static String USER_ACTION = "USER_ACTION";
    }

    /**
     * イベントの発生画面
     * →イベントの内容に変更したい
     */
    public class Action {
        final public static String LIST_ACTIVITY = "LIST_ACTIVITY";
    }

    /**
     * イベントの内容
     * →イベントのパラメーター（あれば追加する系）に変更したい
     */
    public class Label {
        final public static String SHOW_MODE_VIEW = "LIST_SHOW_MODE_VIEW"; // 画面表示タイプ・一覧表示
        final public static String SHOW_MODE_SEARCH = "LIST_SHOW_MODE_SEARCH"; // 画面表示タイプ・検索表示
        final public static String TAP_ADD_SERIES_BTN = "TAP_ADD_SERIES_BTN"; // 作品追加ボタンタップ
    }
}
