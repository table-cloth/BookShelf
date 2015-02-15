package com.tablecloth.bookshelf.util;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.tablecloth.bookshelf.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.view.ViewGroup;

/**
 * 汎用的な処理を格納する
 * Created by shnomura on 2014/08/17.
 */
public class Util {
    public static boolean isDebuggable(Context context) {
        return ( 0 != ( context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
    }

    public static boolean isEmpty(String str) {
        if(str == null || str.length() <= 0) return true;
        return false;
    }
    public static boolean isEmpty(byte[] byteData) {
        if(byteData == null || byteData.length <= 0) return true;
        return false;
    }
    public static boolean isEmpty(String[] str) {
        if(str == null || str.length <= 0) return true;
        return false;
    }
    
    public static void initAdview(Activity activity, ViewGroup parentView) {
    	// adView を作成する
        AdView adView = new AdView(activity);
        adView.setAdUnitId(G.AD_UNIT_ID);
        adView.setAdSize(AdSize.SMART_BANNER);

//        // 属性 android:id="@+id/mainLayout" が与えられているものとして
//        // LinearLayout をルックアップする
//        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);

        // adView を追加する
        parentView.addView(adView);

        // 一般的なリクエストを行う
        AdRequest adRequest = new AdRequest.Builder().build();

        // 広告リクエストを行って adView を読み込む
        adView.loadAd(adRequest);
//    	// AdView をリソースとしてルックアップしてリクエストを読み込む
//        AdView adView = (AdView)activity.findViewById(adviewId);
//        adView.setAdSize(AdSize.SMART_BANNER);
//        adView.setAdUnitId(G.AD_UNIT_ID);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);
    }
}
