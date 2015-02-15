package com.tablecloth.bookshelf.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.tablecloth.bookshelf.db.SeriesData;

public class DBUtil {
	final private static String TITLE = "title";
	final private static String TITLE_PRONOUNCIATION = "title_pronounciation";
	final private static String AUTHOR = "author";
	final private static String AUTHOR_PRONOUNCIATION = "title_pronounciation";
	final private static String COMPANY = "company";
	final private static String COMPANY_PRONOUNCIATION = "company_pronounciation";
	final private static String MAGAZINE = "magazine";
	final private static String MAGAZINE_PRONOUNCIATION = "magazine_pronounciation";
	final private static String IMAGE_PATH = "image_path";
	final private static String MEMO = "memo";
	final private static String VOLUME = "volume";


	public static JSONArray convertSeriesData2Json(SeriesData[] data) {
		JSONArray jsonArray = new JSONArray();
		if(data != null) {
			for(int i = 0 ; i < data.length ; i ++) {
				JSONObject obj = new JSONObject();
				try {
					obj.put(TITLE, data[i].mTitle);
					obj.put(TITLE_PRONOUNCIATION, data[i].mTitlePronunciation);
					obj.put(AUTHOR, data[i].mAuthor);
					obj.put(AUTHOR_PRONOUNCIATION, data[i].mAuthorPronunciation);
					obj.put(COMPANY, data[i].mCompany);
					obj.put(COMPANY_PRONOUNCIATION, data[i].mCompanyPronunciation);
					obj.put(MAGAZINE, data[i].mMagazine);
					obj.put(MAGAZINE_PRONOUNCIATION, data[i].mMagazinePronunciation);
					obj.put(IMAGE_PATH, data[i].mImagePath);
					obj.put(MEMO, data[i].mMemo);
					obj.put(VOLUME, volume2JsonStr(data[i].mVolumeList));
					jsonArray.put(obj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		Log.e("Convert2JSON", "::2Json::"+jsonArray.toString());
		return jsonArray;
	}
	
	public static SeriesData[] convertJson2SeriesData(JSONArray jsonArray) {
		SeriesData[] data = new SeriesData[jsonArray.length()];		
		if(jsonArray != null) {
			for(int i = 0 ; i < jsonArray.length() ; i ++) {
				try {
					JSONObject obj = jsonArray.getJSONObject(i);
					data[i] = new SeriesData();
					
					if(!obj.isNull(TITLE)) data[i].mTitle = obj.getString(TITLE);
					if(!obj.isNull(TITLE_PRONOUNCIATION)) data[i].mTitlePronunciation = obj.getString(TITLE_PRONOUNCIATION);
					if(!obj.isNull(AUTHOR)) data[i].mAuthor = obj.getString(AUTHOR);
					if(!obj.isNull(AUTHOR_PRONOUNCIATION)) data[i].mAuthorPronunciation = obj.getString(AUTHOR_PRONOUNCIATION);
					if(!obj.isNull(COMPANY)) data[i].mCompany = obj.getString(COMPANY);
					if(!obj.isNull(COMPANY_PRONOUNCIATION)) data[i].mCompanyPronunciation = obj.getString(COMPANY_PRONOUNCIATION);
					if(!obj.isNull(MAGAZINE)) data[i].mMagazine = obj.getString(MAGAZINE);
					if(!obj.isNull(MAGAZINE_PRONOUNCIATION)) data[i].mMagazinePronunciation = obj.getString(MAGAZINE_PRONOUNCIATION);
					if(!obj.isNull(IMAGE_PATH)) data[i].mImagePath = obj.getString(IMAGE_PATH);
					if(!obj.isNull(MEMO)) data[i].mMemo = obj.getString(MEMO);
					if(!obj.isNull(VOLUME)) {
						ArrayList<Integer> list = volumeJson2IntList(obj.getString(VOLUME));
						for(int j = 0 ; j < list.size() ; j ++) {
							data[i].addVolume(list.get(j));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}
	
	private static String volume2JsonStr(ArrayList<Integer> volumeList) {
		String volumeStr = "";
		String tmp = "";
		if(volumeList != null) {
			for(int i = 0 ; i < volumeList.size() ; i ++) {
				if(i > 0 && volumeList.size() > 0) volumeStr += ",";
				volumeStr += volumeList.get(i);
			}
		}
		return volumeStr;
	}
	
	private static ArrayList<Integer> volumeJson2IntList(String jsonVolumeStr) {
		ArrayList<Integer> volumeList = new ArrayList<Integer>();
		String[] volumeStr = jsonVolumeStr.split(",");
		for(int i = 0 ; i < volumeStr.length ; i ++) {
			try {
				if(volumeStr[i].length() > 0) volumeList.add(Integer.valueOf(volumeStr[i]));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return volumeList;
	}
}
