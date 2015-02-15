package com.tablecloth.bookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import com.tablecloth.bookshelf.activity.ListActivity;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ImageUtil;

/**
 * Created by shnomura on 2014/08/16.
 * 指定のデータにて本情報の検索をかける用のクラス
 */
public class FilterDao {
    private static DB mDb = null; // DBクラス

    /**
     * シリーズ詳細の取得
     * @return
     */
//    public static SeriesData loadSeriesDetail(Context context, int series_id) {
//        instantiateDB(context);
//        return new SeriesData("sample");
//    }

    /**
     * シリーズ情報の保存
     * 最終更新日時を返すので、登録直後に本の情報を検索したい場合はそれを使う
     * @param data
     */
    public static void saveSeries(SeriesData data) {
//        long nowUnix = Calendar.getInstance().getTimeInMillis() / 1000L;
//        data.mLastUpdateUnix = nowUnix;
        if(isSeriesExist(data.mSeriesId)) {
            ContentValues contentValues = convertSeries2ContentValues(data, true);

            mDb.getSQLiteDatabase().update(DB.BookSeriesTable.TABLE_NAME, contentValues, DB.BookSeriesTable.SERIES_ID + " = ?", new String[]{Integer.toString(data.mSeriesId)});
        } else {
            ContentValues contentValues = convertSeries2ContentValues(data, false);

//            data.mInitUpdateUnix = nowUnix;
            mDb.getSQLiteDatabase().insert(DB.BookSeriesTable.TABLE_NAME, null, contentValues);
        }
//        return nowUnix;
    }

    /**
     * シリーズ情報の削除
     * 復元不可能なので要注意
     * @param seriesId
     * @return
     */
    public static void deleteSeries(int seriesId) {
        mDb.getSQLiteDatabase().delete(DB.BookSeriesTable.TABLE_NAME, DB.BookSeriesTable.SERIES_ID + " = ?", new String[]{Integer.toString(seriesId)});
    }

    /**
     * SeriesIDを取得、取得失敗した場合は-1を返す
     * @param lastUpdateUnix
     * @return
     */
    public static int getSeriesId(long lastUpdateUnix) {
        Cursor cursor = mDb.getSQLiteDatabase().rawQuery("SELECT " + DB.BookSeriesTable.SERIES_ID + " FROM " + DB.BookSeriesTable.TABLE_NAME + " WHERE " + DB.BookSeriesTable.LAST_UPDATE_UNIX + " = " + lastUpdateUnix, null);
        if(cursor.moveToFirst()) {
            // データが存在する
            return cursor.getInt(cursor.getColumnIndex(DB.BookSeriesTable.SERIES_ID));
        } else {
            // データが存在しない
            return -1;
        }

    }

    /**
     * そのシリーズが登録済みかを確認
     * @param seriesId
     * @return
     */
    private static boolean isSeriesExist(int seriesId) {
        // IDがそもそも設定されていない場合
        if(seriesId <= 0) {
            return false;
        }

        Cursor cursor = mDb.getSQLiteDatabase().rawQuery("SELECT * FROM " + DB.BookSeriesTable.TABLE_NAME + " WHERE " + DB.BookSeriesTable.SERIES_ID + " = " + seriesId, null);
        if(cursor.moveToFirst()) {
            // データが存在する
            return true;
        } else {
            // データが存在しない
            return false;
        }
    }
    private static boolean isSeriesVolumeExist(int seriesId, int volume) {
        // IDがそもそも設定されていない場合
        if(seriesId <= 0 || volume < 0) {
            return false;
        }
        Cursor cursor = mDb.getSQLiteDatabase().rawQuery("SELECT * FROM " + DB.BookDetail.TABLE_NAME + " WHERE " + DB.BookDetail.SERIES_ID + " = " + seriesId + " AND " + DB.BookDetail.SERIES_VOLUME + " = " + volume, null);
        if(cursor.moveToFirst()) {
            // データが存在する
            return true;
        } else {
            // データが存在しない
            return false;
        }
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

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM ");
        sql.append(DB.BookSeriesTable.TABLE_NAME);
        if(searchContent != null && searchContent.length() > 0) {
            String whereClause = DB.BookSeriesTable.TABLE_NAME + "." + G.SEARCH_CONTENT_ALL;
            switch (searchMode) {
    		case G.SEARCH_MODE_ALL:
    		default:
//    			whereClause = DB.BookSeriesTable.TABLE_NAME + "." + G.SEARCH_CONTENT_ALL;
//    			break;
    		case G.SEARCH_MODE_TITLE:
    			whereClause = DB.BookSeriesTable.TITLE_NAME;
    			break;
    		case G.SEARCH_MODE_AUTHOR:
    			whereClause = DB.BookSeriesTable.AUTHOR_NAME;
    			break;
    		case G.SEARCH_MODE_COMPANY:
    			whereClause = DB.BookSeriesTable.COMPANY_NAME;
    			break;
    		case G.SEARCH_MODE_MAGAZINES:
    			whereClause = DB.BookSeriesTable.MAGAZINE_NAME;
    			break;
    		}
            sql.append(" WHERE ");
            sql.append(whereClause);
            sql.append(" LIKE");
            sql.append(" '%" + searchContent + "%' ");
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

                        String[] tagsStr = getTagsData(cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.TAGS)));
                        if(tagsStr != null) {
                            for(int k = 0 ; k < tagsStr.length ; k ++) {
                                retData[i].mTagsList.add(tagsStr[i]);
                            }
                        }
                        int isSeriesEnd = cursor.getInt(cursor.getColumnIndex(DB.BookSeriesTable.SERIES_IS_FINISH));
                        if(isSeriesEnd == 0) {
                            retData[i].mIsSeriesEnd = false;
                        } else {
                            retData[i].mIsSeriesEnd = true;
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
        if(cursor != null) {
            try {
                int max = cursor.getCount();
                for(int i = 0 ; i < max ; i ++) {
                    if(cursor.moveToNext()) {
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

                        String[] tagsStr = getTagsData(cursor.getString(cursor.getColumnIndex(DB.BookSeriesTable.TAGS)));
                        if(tagsStr != null) {
                            for(int k = 0 ; k < tagsStr.length ; k ++) {
                                retData.mTagsList.add(tagsStr[i]);
                            }
                        }
                        int isSeriesEnd = cursor.getInt(cursor.getColumnIndex(DB.BookSeriesTable.SERIES_IS_FINISH));
                        if(isSeriesEnd == 0) {
                            retData.mIsSeriesEnd = false;
                        } else {
                            retData.mIsSeriesEnd = true;
                        }
                        retData.mInitUpdateUnix = cursor.getLong(cursor.getColumnIndex(DB.BookSeriesTable.INIT_UPDATE_UNIX));
                        retData.mLastUpdateUnix = cursor.getLong(cursor.getColumnIndex(DB.BookSeriesTable.LAST_UPDATE_UNIX));

                        int[] volumes = loadVolumes(context, seriesId);
                        if(volumes != null) {
                        	for(int k = 0 ; k < volumes.length ; k ++) {
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

    /**
     * DBの初期化処理
     * @param context
     */
    private static void instantiateDB(Context context) {
        if(mDb == null) {
            mDb = DB.getInstance(context);
        }
    }

    private static String[] getTagsData(String tagsStr) {
        String[] ret = null;
        if(tagsStr != null && tagsStr.length() > 0) {
            ret = tagsStr.split("|||");
        }
        return ret;
    }

    private static String getTagsStr(ArrayList<String> tagsStr) {
        String ret = null;
        if(tagsStr != null) {
            for(int i = 0 ; i < tagsStr.size() ; i ++) {
                if(i != 0) {
                    ret += "|||";
                }
                ret += tagsStr.get(i);
            }
        }
        return ret;
    }

    private static ContentValues convertSeries2ContentValues(SeriesData data, boolean isUpdate) {
        ContentValues cv = new ContentValues();
        // SeriesIdが0以下（設定されていない場合）は登録しない
        if(data.mSeriesId > 0) {
            cv.put(DB.BookSeriesTable.SERIES_ID, data.mSeriesId);
        }
        cv.put(DB.BookSeriesTable.AUTHOR_NAME, data.mAuthor);
        cv.put(DB.BookSeriesTable.AUTHOR_PRONUNCIATION, data.mAuthorPronunciation);
        cv.put(DB.BookSeriesTable.TITLE_NAME, data.mTitle);
        cv.put(DB.BookSeriesTable.TITLE_PRONUNCIATION, data.mTitlePronunciation);
        cv.put(DB.BookSeriesTable.MAGAZINE_NAME, data.mMagazine);
        cv.put(DB.BookSeriesTable.MAGAZINE_PRONUNCIATION, data.mMagazinePronunciation);
        cv.put(DB.BookSeriesTable.COMPANY_NAME, data.mCompany);
        cv.put(DB.BookSeriesTable.COMPANY_PRONUNCIATION, data.mCompanyPronunciation);
        cv.put(DB.BookSeriesTable.IMAGE_PATH, data.mImagePath);
        cv.put(DB.BookSeriesTable.MEMO, data.mMemo);
        cv.put(DB.BookSeriesTable.TAGS, getTagsStr(data.mTagsList));
        cv.put(DB.BookSeriesTable.SERIES_IS_FINISH, data.mIsSeriesEnd);
        Calendar now = Calendar.getInstance();
        cv.put(DB.BookSeriesTable.LAST_UPDATE_UNIX, now.getTimeInMillis());
        if(!isUpdate) cv.put(DB.BookSeriesTable.LAST_UPDATE_UNIX, now.getTimeInMillis());
        return cv;
    }
    
    /**
     * 巻数情報の保存
     * 最終更新日時を返す
     * @param data
     */
    public static void saveVolume(int seriesId, int volume) {
        if(isSeriesVolumeExist(seriesId, volume)) {
            ContentValues contentValues = convertSeriesVolume2ContentValues(seriesId, volume, true); 
            mDb.getSQLiteDatabase().update(DB.BookDetail.TABLE_NAME, contentValues, DB.BookDetail.SERIES_ID + " = ? AND " + DB.BookDetail.SERIES_VOLUME + " = ? ", new String[]{Integer.toString(seriesId), Integer.toString(volume)});
        } else {
            ContentValues contentValues = convertSeriesVolume2ContentValues(seriesId, volume, false);
            long log = mDb.getSQLiteDatabase().insert(DB.BookDetail.TABLE_NAME, null, contentValues);
            Log.e("newROW", "newROW = " + log);
        }

//        long nowUnix = Calendar.getInstance().getTimeInMillis() / 1000L;
//        data.mLastUpdateUnix = nowUnix;
//        return nowUnix;
    }
    
    /**
     * 巻数情報の削除
     * 復元不可能なので要注意
     * @param seriesId
     * @return
     */
    public static void deleteVolume(int seriesId, int volume) {
        mDb.getSQLiteDatabase().delete(DB.BookDetail.TABLE_NAME, DB.BookDetail.SERIES_ID + " = ? AND " + DB.BookDetail.SERIES_VOLUME + " = ?", new String[]{Integer.toString(seriesId), Integer.toString(volume)});
    }
    
    /**
     * 巻数情報DB用カーソル作成
     * @param data
     * @return
     */
    private static ContentValues convertSeriesVolume2ContentValues(int seriesId, int volume, boolean isUpdate) {
        ContentValues cv = new ContentValues();
        // SeriesIdが0以下（設定されていない場合）は登録しない
        if(seriesId > 0) {
            cv.put(DB.BookDetail.SERIES_ID, seriesId);
        }
        cv.put(DB.BookDetail.SERIES_VOLUME, volume);
        Calendar now = Calendar.getInstance();
        cv.put(DB.BookDetail.LAST_UPDATE_UNIX, now.getTimeInMillis());
        if(!isUpdate) cv.put(DB.BookDetail.LAST_UPDATE_UNIX, now.getTimeInMillis());
        return cv;
    }
    
    public static int[] loadVolumes(Context context, int seriesId) {
        instantiateDB(context);

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM ");
        sql.append(DB.BookDetail.TABLE_NAME);
        sql.append(" WHERE " + DB.BookDetail.SERIES_ID + " = " + seriesId);

        Cursor cursor = mDb.getSQLiteDatabase().rawQuery(sql.toString(), null);
        int[] retData = null;

        // cursorからSeriesDataを生成
        if(cursor != null) {
            try {
                int max = cursor.getCount();
                retData = new int[max];
                for(int i = 0 ; i < max ; i ++) {
                    if(cursor.moveToNext()) {
                    	retData[i] = cursor.getInt(cursor.getColumnIndex(DB.BookDetail.SERIES_VOLUME));
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
