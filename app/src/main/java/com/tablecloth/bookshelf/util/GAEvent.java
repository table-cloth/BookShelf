package com.tablecloth.bookshelf.util;

/**
 * Events for GoogleAnalytics
 *
 * Created by Minami on 2015/02/19.
 */
public class GAEvent {

    /**
     * Tracking event category
     */
    public class Type {
        final public static String USER_ACTION = "Action"; // For tracking user actions
        final public static String USER_Data = "Data"; // For tracking users data, such as register book count
    }

    /**
     * Tracking event content
     */
    public class Event {
        final public static String SHOW_MODE_VIEW = "ShowModeView";
        final public static String SHOW_MODE_SEARCH = "ShowModeSearch";
        final public static String SHOW_MODE_API_SEARCH_RESULT = "ShowModeApiSearchResult";
        final public static String TAP_SEARCH_BTN = "TapAddSeriesBtn";
        final public static String TAP_ADD_SERIES_BTN = "TapAddSeriesBtn";
        final public static String TAP_ADD_SERIES_SEARCH_BTN = "TapAddSeriesBySearch";
        final public static String TAP_ADD_SERIES_MANUAL_BTN = "TapAddSeriesByManual";
        final public static String ADD_SERIES = "AddSeries";
        final public static String TAP_SETTINGS_BTN = "TapSettingsBtn";
        final public static String SETTINGS_SET_SHOW_TYPE = "SettingsSetShowType";
        final public static String SETTINGS_TAP_REVIEW = "SettingsReview";
        final public static String SETTINGS_TAP_MAILER = "SettingsTapShowType";

    }

    /**
     * Tracking event parameter
     */
    public class Param {
        final public static String SETTINGS_SET_SHOW_TYPE_LIST = "List";
        final public static String SETTINGS_SET_SHOW_TYPE_GRID = "Grid";
    }
}
