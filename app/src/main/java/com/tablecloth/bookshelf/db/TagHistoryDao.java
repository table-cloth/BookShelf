package com.tablecloth.bookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.tablecloth.bookshelf.util.Const;
import com.tablecloth.bookshelf.util.Util;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Data accessor for previously used tags
 *
 * Created by nomura on 2016/11/05.
 */
public class TagHistoryDao extends DaoBase {

    /**
     * Constructor
     *
     * @param context context
     */
    public TagHistoryDao(Context context) {
        super(context);
    }


    /**
     * Checks whether given tag is already registered
     *
     * @param tag tag
     * @return true if already registered
     */
    public boolean isTagRegistered(String tag) {
        return isValidTag(tag)
                && isDataAvailable(SqlText.createLoadTagSQL(tag));
    }

    /**
     * Load registered tags
     *
     * @return list of tags registered
     */
    @Nullable
    public ArrayList<String> loadAllTags() {

        Cursor cursor = openCursor(SqlText.createLoadAllTagsSQL());
        if(cursor == null) {
            return null;
        }

        try {
            ArrayList<String> tagsList = new ArrayList<>();
            for (boolean nextIsAvailable = cursor.moveToFirst(); nextIsAvailable; nextIsAvailable = cursor.moveToNext()) {
                String value = getStringFromCursor(cursor, Const.DB.TagHistoryTable.TAG_NAME);
                if(!Util.isEmpty(value)) {
                    tagsList.add(value);
                }
            }
            return tagsList;
        } finally {
            closeCursor(cursor);
        }
    }

    /**
     * Saves tag data
     *
     * @param tag tag
     * @return is save success
     */
    public boolean saveTag(String tag) {
        boolean isUpdate = isTagRegistered(tag);

        ContentValues contentValues = createContentValues4TagHistory(tag);
        if(contentValues == null) {
            return false;
        }

        if(isUpdate) {
            int result = DB.getDB(mContext).getSQLiteDatabase(mContext).update(
                    Const.DB.TagHistoryTable.TABLE_NAME,
                    contentValues,
                    SqlText.createWhereClause4UpdateTag(),
                    new String[] {tag});
            return result >= 0;
        } else {
            long result = DB.getDB(mContext).getSQLiteDatabase(mContext).insert(
                    Const.DB.TagHistoryTable.TABLE_NAME, null, contentValues);
            return result != -1L;
        }
    }

    /**
     * Checks whether given tag is valid
     * This does not check if data exists in DB
     * This only checks whether the given tag is legal value or not
     *
     * @param tag tag
     * @return true if valid
     */
    private boolean isValidTag(String tag) {
        return !Util.isEmpty(tag);
    }

    /**
     * Create ContentValues for given book volume, in given series id
     *
     * @param tag tag
     * @return ContentValues instance or null if invalid data is given
     */
    @Nullable
    private ContentValues createContentValues4TagHistory(String tag) {
        // return null if invalid value is given
        if(!isValidTag(tag)) {
            return null;
        }

        Calendar now = Calendar.getInstance();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.DB.TagHistoryTable.TAG_NAME, tag);
        contentValues.put(Const.DB.TagHistoryTable.LAST_UPDATE, now.getTimeInMillis());
        return contentValues;
    }
}
