package com.tablecloth.bookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.Util;

/**
 * Created by shnomura on 2014/08/16.
 * 指定のデータにて本情報の検索をかける用のクラス
 */
public class FilterDao extends DaoBase {

    /**
     * Constructor
     *
     * @param context context
     */
    public FilterDao(@NonNull Context context) {
        super(context);
    }

    /**
     * 全てのシリーズ情報の取得
     * @param context
     * @return
     */
    public static SeriesData[] loadSeries(Context context, int searchMode, String searchContent) {
        instantiateDB(context);
        if(searchContent == null || searchContent.length() <= 0) {
        	searchContent = "";
        }
        // 半角スペース・全角スペースで分割
        String[] content = searchContent.split("[ 　]");
        if(content == null || content.length <= 0) {
            content = new String[] { searchContent };
        }

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM ");
        sql.append(DB.BookSeriesTable.TABLE_NAME);
        if(content != null && content.length > 0) {
            // 検索モードによってWhere文を変える
            int[] modes;
            if(searchMode == G.SEARCH_MODE_ALL) {
                modes = new int[]{ G.SEARCH_MODE_TITLE, G.SEARCH_MODE_AUTHOR, G.SEARCH_MODE_COMPANY, G.SEARCH_MODE_MAGAZINES , G.SEARCH_MODE_TAG};
            } else {
                modes = new int[] { searchMode };
            }
            String whereClause = " WHERE "; // 検索条件文字列
            for(int x = 0 ; x < content.length ; x ++) {

                // ２件目以降の検索条件時はORをつける
                if (x > 0 && !Util.isEmpty(whereClause)) {
                    whereClause += " AND ";
                }

                whereClause += " ( ";

                for (int i = 0; i < modes.length; i++) {
                    // ２件目以降の検索条件時はORをつける
                    if (i > 0 && !Util.isEmpty(whereClause)) {
                        whereClause += " OR ";
                    }
                    switch (modes[i]) {
                        case G.SEARCH_MODE_TITLE:
                            whereClause += DB.BookSeriesTable.TITLE_NAME;
                            break;
                        case G.SEARCH_MODE_AUTHOR:
                            whereClause += DB.BookSeriesTable.AUTHOR_NAME;
                            break;
                        case G.SEARCH_MODE_COMPANY:
                            whereClause += DB.BookSeriesTable.COMPANY_NAME;
                            break;
                        case G.SEARCH_MODE_MAGAZINES:
                            whereClause += DB.BookSeriesTable.MAGAZINE_NAME;
                            break;
                        case G.SEARCH_MODE_TAG:
                            whereClause += DB.BookSeriesTable.TAGS;
                    }
                    whereClause += " LIKE  '%";
                    whereClause += content[x];
                    whereClause += "%' ";
                }

                whereClause += " ) ";
            }
            sql.append(whereClause);
        }


        
        Cursor cursor = mDb.getSQLiteDatabase().rawQuery(sql.toString(), null);
        SeriesData[] retData = null;

        // cursorからSeriesDataを生成
        if(cursor != null) {
            try {
                int max = cursor.getCount();
                retData = new SeriesData[max];
                for(int i = 0 ; i < max ; i ++) {
                    if(cursor.moveToNext()) {
                        String title = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.TITLE_NAME));
                        retData[i] = new SeriesData(title);
                        retData[i].mSeriesId = cursor.getInt(cursor.getColumnIndex(DB.BookSeriesTable.SERIES_ID));
                        retData[i].mTitlePronunciation = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.TITLE_PRONUNCIATION));
                        retData[i].mAuthor = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.AUTHOR_NAME));
                        retData[i].mAuthorPronunciation = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.AUTHOR_PRONUNCIATION));
                        retData[i].mMagazine = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.MAGAZINE_NAME));
                        retData[i].mMagazinePronunciation = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.MAGAZINE_PRONUNCIATION));
                        retData[i].mCompany = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.COMPANY_NAME));
                        retData[i].mCompanyPronunciation = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.COMPANY_PRONUNCIATION));
                        retData[i].mImagePath = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.IMAGE_PATH));
                        retData[i].mMemo = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.MEMO));
                        retData[i].mTagsList = getTagsData(cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.TAGS)));

                        int isSeriesEnd = cursor.getInt(cursor.getColumnIndex(DB.BookSeriesTable.SERIES_IS_FINISH));
                        if(isSeriesEnd == 0) {
                            retData[i].mIsSeriesComplete = false;
                        } else {
                            retData[i].mIsSeriesComplete = true;
                        }
                        retData[i].mInitUpdateUnix = cursor.getLong(cursor.getColumnIndex(DB.BookSeriesTable.INIT_UPDATE_UNIX));
                        retData[i].mLastUpdateUnix = cursor.getLong(cursor.getColumnIndex(DB.BookSeriesTable.LAST_UPDATE_UNIX));

                        int[] volumes = loadVolumes(context, retData[i].mSeriesId);
                        if(volumes != null) {
                        	for(int k = 0 ; k < volumes.length ; k ++) {
                        		retData[i].addVolume(volumes[k]);
                        	}
                        }
//                        ///TODO 所持している巻数情報は後々
//                        retData[i].addVolume(1);
//                        retData[i].addVolume(2);
//                        retData[i].addVolume(4);

                    // cursor情報取得失敗
                    } else {
                        break;
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return retData;
    }
    
    public static SeriesData[] loadSeries(Context context) {
    	return loadSeries(context, G.SEARCH_MODE_ALL, "");
    }


    /**
     * 全てのシリーズ情報の取得
     * @param context
     * @return
     */
    public static SeriesData loadSeries(Context context, int seriesId) {
        instantiateDB(context);

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM ");
        sql.append(DB.BookSeriesTable.TABLE_NAME);
        sql.append(" WHERE " + DB.BookSeriesTable.SERIES_ID + " = " + seriesId);

        Cursor cursor = mDb.getSQLiteDatabase().rawQuery(sql.toString(), null);
        SeriesData retData = null;

        // cursorからSeriesDataを生成
        if (cursor != null) {
            try {
                int max = cursor.getCount();
                for (int i = 0; i < max; i++) {
                    if (cursor.moveToNext()) {
                        String title = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.TITLE_NAME));
                        retData = new SeriesData(title);
                        retData.mSeriesId = cursor.getInt(cursor.getColumnIndex(DB.BookSeriesTable.SERIES_ID));
                        retData.mTitlePronunciation = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.TITLE_PRONUNCIATION));
                        retData.mAuthor = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.AUTHOR_NAME));
                        retData.mAuthorPronunciation = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.AUTHOR_PRONUNCIATION));
                        retData.mMagazine = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.MAGAZINE_NAME));
                        retData.mMagazinePronunciation = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.MAGAZINE_PRONUNCIATION));
                        retData.mCompany = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.COMPANY_NAME));
                        retData.mCompanyPronunciation = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.COMPANY_PRONUNCIATION));
                        retData.mImagePath = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.IMAGE_PATH));
                        retData.mMemo = cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.MEMO));
                        retData.mTagsList = getTagsData(cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.TAGS)));

                        int isSeriesEnd = cursor.getInt(cursor.getColumnIndex(DB.BookSeriesTable.SERIES_IS_FINISH));
                        if (isSeriesEnd == 0) {
                            retData.mIsSeriesComplete = false;
                        } else {
                            retData.mIsSeriesComplete = true;
                        }
                        retData.mInitUpdateUnix = cursor.getLong(cursor.getColumnIndex(DB.BookSeriesTable.INIT_UPDATE_UNIX));
                        retData.mLastUpdateUnix = cursor.getLong(cursor.getColumnIndex(DB.BookSeriesTable.LAST_UPDATE_UNIX));

                        int[] volumes = loadVolumes(context, seriesId);
                        if (volumes != null) {
                            for (int k = 0; k < volumes.length; k++) {
                                retData.addVolume(volumes[k]);
                            }
                        }
//                        ///TODO 所持している巻数情報は後々
//                        retData.addVolume(1);
//                        retData.addVolume(2);
//                        retData.addVolume(4);

                        // cursor情報取得失敗
                    } else {
                        break;
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return retData;
    }

}
