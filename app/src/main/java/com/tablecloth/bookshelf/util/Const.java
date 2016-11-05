package com.tablecloth.bookshelf.util;

/**
 * Created by nomura on 2016/11/04.
 */

public class Const {
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
                final public static String SERIES_SHOW_TYPE = "series_show_type"; // 作品一覧の表示タイプ
            }
            // 設定値ははこちらのクラスにて管理する
            public static class VALUE {
                final public static String SERIES_SHOW_TYPE_GRID = "grid"; // 作品一覧の表示タイプ
                final public static String SERIES_SHOW_TYPE_LIST = "list"; // 作品一覧の表示タイプ
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
