package com.tablecloth.bookshelf.util;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.tablecloth.bookshelf.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 汎用的な処理を格納する
 * Created by Minami on 2014/08/17.
 */
public class Util {
    public static boolean isDebugMode(Context context) {
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
    public static boolean isEmpty(ArrayList list) {
        return list == null || list.isEmpty();
    }

    /**
     * Checks whether given value is equal
     * Will return false if any is null
     *
     * @param strA text to compare
     * @param strB text to compare
     * @return is equal
     */
    public static boolean isEqual(@Nullable String strA, @Nullable String strB) {
        return !(strA == null || strB ==null)
                && strA.equals(strB);
    }

    public static void initAdview(Activity activity, ViewGroup parentView) {
    	// adView を作成する
        AdView adView = new AdView(activity);
        adView.setAdUnitId(G.AD_UNIT_ID);
        adView.setAdSize(AdSize.BANNER);

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

    /**
     * 特定のアプリのマーケットへ飛ばすURLを取得
     * @param packageName
     * @param referrer
     * @return
     */
    public static String getMarketUriStr(String packageName,String referrer) {
        return "market://details?id=" + packageName + "&referrer=" + referrer;
    }

    public static int convertDp2Px(Context context, int dp) {
        // density (比率)を取得する
        float density = context.getResources().getDisplayMetrics().density;

        // dpをpixelに変換する ( dp × density + 0.5f（四捨五入) )
        return (int) ((float)dp * density + 0.5f);
    }


}
