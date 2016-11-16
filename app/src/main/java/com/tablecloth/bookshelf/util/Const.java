package com.tablecloth.bookshelf.util;

/**
 * Manages global constant values
 *
 * Created by Minami on 2016/11/04.
 */
public class Const {

    /**
     * API related constants
     */
    public static class API {
        final public static String AD_UNIT_ID = "ca-app-pub-9740549564683244/9984495812";
    }

    /**
     * Preference keys
     */
    public static class PREF_KEYS {
        final public static String INIT_VERSION_CODE_INT = "INIT_VERSION_CODE_INT"; // first version installed
        final public static String VERSION_CODE_INT = "VERSION_CODE_INT"; // latest previous version installed
        final public static String BOOK_SERIES_ADD_TYPE_INT = "BOOK_SERIES_ADD_TYPE"; // book series add type
    }

    /**
     * Preference values
     */
    public static class PREF_VALUES {
        final public static int BOOK_SERIES_ADD_TYPE_BUNCH = 0; // add volume "from ~ to"
        final public static int BOOK_SERIES_ADD_TYPE_SINGELE = 1; // add volume one by one
        final public static int BOOK_SERIES_ADD_TYPE_DEFAULT = BOOK_SERIES_ADD_TYPE_BUNCH;
    }

    public static class SORT {
        final public static int TYPE_TITLE = 0;
        final public static int TYPE_AUTHOR = 1;
        final public static int TYPE_MAGAZINE = 2;
        final public static int TYPE_COMPANY = 3;
    }
    
    /**
     * Activity request code related constants
     */
    public static class REQUEST_CODE {
        public static final int SERIES_LIST_ADD_SERIES = 1;
        public static final int EDIT_SERIES_DETAIL = 2;
        public static final int IMAGE_CHANGE_CONFIRM_1 = 3;
        public static final int IMAGE_CHANGE_CONFIRM_2 = 4;
        public static final int IMAGE_GALLERY = 5;
        public static final int IMAGE_CAMERA = 6;
        public static final int SELECT_ADD_SERIES_TYPE = 7;
        public static final int LIST_SEARCH_RAKUTEN = 8;
        public static final int SIMPLE_CHECK = 9; // 簡易的な確認用
        public static final int TAGS_EDIT = 10;

        public static final int UPDATE_DIALOG = 100;
    }

    /**
     * Activity result code related constants
     */
    public static class RESULT_CODE {
        // Normal result code
        public static final int NONE = 0; // When there is no result to return
        public static final int POSITIVE = 1; // When result is positive / valid
        public static final int NEGATIVE = 2; // When result is negative / invalid

        // Unique results
        // Check if this values are really needed
        // Delete if they can be replaced with POSITIVE / NEGATIVE
        public static final int DELETE_SERIES = 3; // 特殊な導線用
    }

    /**
     * Activity result data related constants
     */
    public static class INTENT_EXTRA {
        // keys
        public static final String KEY_INT_SELECTED_ID = "selected_id";
        public static final String KEY_STR_SELECTED_KEY = "selected_key";
        public static final String KEY_STR_SELECTED_VALUE = "selected_value";
        public static final String KEY_INT_EDIT_SERIES = "key_edit_series";


        // values
        public static final int VALUE_DEFAULT_ERROR = -1;
        public static final int VALUE_SELECTED_BTN_SEARCH = 0;
        public static final int VALUE_SELECTED_BTN_MANUAL = 1;
        public static final int VALUE_EDIT_SERIES_EDIT = 0;
        public static final int VALUE_EDIT_SERIES_DELETE = 1;
    }

    /**
     * Search mode
     */
    public static class SEARCH_MODE {
        public static final int ALL = 0;
        public static final int TITLE = 1;
        public static final int AUTHOR = 2;
        public static final int MAGAZINE = 3;
        public static final int COMPANY = 4;
        public static final int TAG = 5;
    }

    /**
     * View mode
     */
    public static class VIEW_MODE {
        public static final int VIEW = 0;
        public static final int DELETE = 1;
        public static final int SEARCH_DB = 2;
        public static final int SEARCH_API = 3;
    }

    /**
     * DB related constants
     */
    public static class DB {

        // DB name for all bool series / details data to be saved
        public static final String DB_NAME = "book_shelf";

        /**
         * Settings related constants
         */
        public static class Settings {

            /**
             * Column name for Settings Table
             */
            public static class SettingsTable {
                // Table name for each settings
                public static final String TABLE_NAME = "settings";
                // Available variables for settings table are only "key" & "value"
                // To be capable with any settings, variable types are to be String
                public static final String KEY = "key";
                public static final String VALUE = "value";
            }

            // 設定項目はこちらのクラスにて管理する
            public static class KEY {
                public static final String SERIES_SHOW_TYPE = "series_show_type"; // 作品一覧の表示タイプ
            }
            // 設定値ははこちらのクラスにて管理する
            public static class VALUE {
                public static final String SERIES_SHOW_TYPE_GRID = "grid"; // 作品一覧の表示タイプ
                public static final String SERIES_SHOW_TYPE_LIST = "list"; // 作品一覧の表示タイプ
            }
        }

        /**
         * Column names for Book Series Table
         */
        public static class BookSeriesTable {
            // Table name for Book Series
            public static final String TABLE_NAME = "book_series_table";
            // Given ID for this series
            // This ID is also used to relate with Book Detail table
            public static final String SERIES_ID = "series_id";
            // Title of the series
            public static final String TITLE_NAME  = "title_name";
            // Series name spell (how to read)
            public static final String TITLE_PRONUNCIATION = "title_pronunciation";
            // Author
            public static final String AUTHOR_NAME = "author_name";
            // Author spell (how to read)
            public static final String AUTHOR_PRONUNCIATION = "author_pronunciation";
            // Publisher company name
            public static final String COMPANY_NAME = "company_name";
            // Publisher company spell (how to read)
            public static final String COMPANY_PRONUNCIATION = "company_pronunciation";
            // Magazine name
            public static final String MAGAZINE_NAME = "magazine_name";
            // Magazine spell (how to read)
            public static final String MAGAZINE_PRONUNCIATION = "magazine_pronunciation";
            // Series image info (data is saved in byte array)
            // For details, see "http://d.hatena.ne.jp/suusuke/20080127/1201417192"
            // Uses BLOB
            // Since byte array is too long, file path of where image byte data is saved, is given
            public static final String IMAGE_PATH = "image_path";
            // Tags
            // When Several tags are set, it is saved as "<TagA>|||<TabB>"
            public static final String TAGS = "tags";
            // Additional memo
            public static final String MEMO = "memo";
            // Whether series is complete
            // 0(false) or 1(true)
            public static final String SERIES_IS_FINISH = "series_is_finish";
            // Date initially registered
            public static final String INIT_UPDATE_UNIX = "init_update_unix";
            // Date of last update
            public static final String LAST_UPDATE_UNIX = "last_update_unix";
        }

        /**
         * Column names for Book Detail Table
         * Book detail == specific volume of the series
         */
        public static class BookVolumeDetailTable {
            // Table name for book detail
            public static final String TABLE_NAME = "book_detail";
            // Given ID for this series
            // This ID is also used to relate with Book Series table
            public static final String SERIES_ID = "series_id";
            // Volume data
            public static final String SERIES_VOLUME = "series_volume";
            // Date initially registered
            public static final String INIT_UPDATE_UNIX = "init_update_unix";
            // Date of last update
            public static final String LAST_UPDATE_UNIX = "last_update_unix";
        }

        /**
         * Column names for Tag Hisotory Table
         */
        public static class TagHistoryTable {
            // Table name for book series tags
            public static final String TABLE_NAME = "tags";
            // Name of tags
            // If several tags are registered, they will be merged with "|||" in between
            // eg) if "TagA" & "TagB" is registered, content will be "TagA|||TagB"
            public static final String TAG_NAME = "tags_name";
            // Date of last update
            public static final String LAST_UPDATE = "last_update";
        }
    }
}
