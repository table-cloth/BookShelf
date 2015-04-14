package com.tablecloth.bookshelf.util;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by shnomura on 2015/03/29.
 */
public class SpinnerUtil {

    public static ArrayAdapter<String> getSpinnerAdapter(Context context, String[] selections) {
        if(selections == null) return null;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item);

        // 検索用Spinner選択肢
        for(int i = 0 ; i < selections.length ; i ++) {
            adapter.add(selections[i]);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }
}
