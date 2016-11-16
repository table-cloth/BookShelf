package com.tablecloth.bookshelf.util;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class for general fuctions
 *
 * Created by Minami on 2014/08/17.
 */
public class Util {

    /**
     * Whether is debug mode
     *
     * @param context context
     * @return whether is debug mode
     */
    public static boolean isDebugMode(@NonNull Context context) {
        return ( 0 != ( context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
    }

    /**
     * Whether value is empty
     *
     * @param str value
     * @return whether is empty
     */
    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.length() <= 0;
    }

    /**
     * Whether value is empty
     *
     * @param byteData value
     * @return whether is empty
     */
    public static boolean isEmpty(@Nullable byte[] byteData) {
        return byteData == null || byteData.length <= 0;
    }

    /**
     * Whether value is empty
     *
     * @param str value
     * @return whether is empty
     */
    public static boolean isEmpty(@Nullable String[] str) {
        return str == null || str.length <= 0;
    }

    /**
     * Whether value is empty
     *
     * @param list value
     * @return whether is empty
     */
    public static boolean isEmpty(@Nullable ArrayList list) {
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

    /**
     * Initalize ad view
     *
     * @param activity activity
     * @param parentView parent view to add ad
     */
    public static void initAdView(@NonNull Activity activity, @NonNull ViewGroup parentView) {
    	// Create ad view
        AdView adView = new AdView(activity);
        adView.setAdUnitId(Const.API.AD_UNIT_ID);
        adView.setAdSize(AdSize.BANNER);
        parentView.addView(adView);

        // Send ad request & loadInt
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    /**
     * Get URI for GooglePlay
     *
     * @param packageName package name
     * @param referrer referrer
     * @return GooglePlay URI
     */
    @NonNull
    public static String getMarketUriStr(@NonNull String packageName, @NonNull String referrer) {
        return "market://details?id=" + packageName + "&referrer=" + referrer;
    }

    /**
     * Convert Dp value to Pixel value
     *
     * @param context context
     * @param dp dp
     * @return px
     */
    public static int convertDp2Px(@NonNull Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((float)dp * density + 0.5f);
    }


    public static void getKanaText(String text) {

//        List<String> result = new ArrayList<String>();
//
//        JapaneseAnalyzer analyzer = new JapaneseAnalyzer();
//
//        try {
//            TokenStream stream = analyzer.tokenStream(null, new StringReader(text));
//            stream.reset();
//            while (stream.incrementToken()) {
//                result.add(stream.getAttribute(CharTermAttribute.class).toString());
//            }
//        } catch (IOException e) {
//            // not thrown b/c we're using a string reader...
//            throw new RuntimeException(e);
//        }
//
//        int a = 0;
//
//        Tokenizer tokenizer = new Tokenizer.Builder().build();
//        List<Token> tokenList = tokenizer.tokenize(text);
//
//        for(Token token : tokenList) {
//            String pronounce = token.getPronunciation();
//            String tex = token.getReading();
//            String allFeatures = token.getAllFeatures();
//            String[] allFeatureList = token.getAllFeaturesArray();
//            int a = 0;
//        }
//
//        StringBuilder sb = new StringBuilder(256);
//        try (JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.Mode.NORMAL)) {
//            tokenizer.setReader(new StringReader(kanjiText));
//            ReadingAttribute readingAttribute = tokenizer.addAttribute(ReadingAttribute.class);
//            CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
//            tokenizer.reset();
//            while (tokenizer.incrementToken()) {
//                String kana = readingAttribute.getReading();
//                if (kana == null) {
//                    kana = charTermAttribute.toString();
//                }
//                sb.append(kana);
//            }
//        }
    }

}
