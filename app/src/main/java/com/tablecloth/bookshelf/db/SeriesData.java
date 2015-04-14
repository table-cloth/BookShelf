package com.tablecloth.bookshelf.db;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

import com.tablecloth.bookshelf.util.ImageUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;

/**
 * Created by shnomura on 2014/08/16.
 */
public class SeriesData {
    public int mSeriesId = -1;
    public String mTitle;
    public String mTitlePronunciation;
    public String mAuthor;
    public String mAuthorPronunciation;
    public String mCompany;
    public String mCompanyPronunciation;
    public String mMagazine;
    public String mMagazinePronunciation;
    private Bitmap mImageCache;
    public String mImagePath;
    public String mMemo;

    public ArrayList<String> mTagsList;
    public boolean mIsSeriesEnd = false;
    public ArrayList<Integer> mVolumeList;

    public long mInitUpdateUnix;
    public long mLastUpdateUnix;

    public SeriesData(String seriesName) {
        mTitle = seriesName;
        mVolumeList = new ArrayList<Integer>();
        mTagsList = new ArrayList<String>();
    }
    
    public void getImage(final Handler handler, Activity activity, final ListenerUtil.LoadBitmapListener listener) {
    	if(mImageCache != null) {
            listener.onFinish(mImageCache);
            return;
        }
    	if(Util.isEmpty(mImagePath)) {
            return;
        }
        // 画像パスからの取得
        getImageByPath(activity, new ListenerUtil.LoadBitmapListener() {
            @Override
            public void onFinish(final Bitmap bitmap) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFinish(bitmap);
                    }
                });
            }

            @Override
            public void onError() {
                // パスからの取得に失敗した場合はURLからの取得を試す
                getImageByUrl(new ListenerUtil.LoadBitmapListener() {
                    @Override
                    public void onFinish(final Bitmap bitmap) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFinish(bitmap);
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError();
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * 画像ファイルパスから画像を取得する
     * @param activity
     * @param listener
     */
    private void getImageByPath(Activity activity, ListenerUtil.LoadBitmapListener listener) {
        if(Util.isEmpty(mImagePath)) {
            listener.onError();
            return;
        }
        try {
            mImageCache = ImageUtil.getBitmapFromPath(activity, mImagePath);
            if(mImageCache != null) {
                ExifInterface exifInterface = new ExifInterface(mImagePath);
                int exifR = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                switch (exifR) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        mImageCache = ImageUtil.rotateBitmap(mImageCache, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        mImageCache = ImageUtil.rotateBitmap(mImageCache, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        mImageCache = ImageUtil.rotateBitmap(mImageCache, 270);
                        break;
                    default:
                        break;
                }
                listener.onFinish(mImageCache);
                return;
            }
        } catch(OutOfMemoryError e) {
            ToastUtil.show(activity, "メモリー不足のため、画像の取得に失敗しました");
            e.printStackTrace();
        } catch (IOException e) {
            ToastUtil.show(activity, "予期せぬエラーのため、画像の取得に失敗しました");
            e.printStackTrace();
        }
        listener.onError();
    }

    /**
     * 画像URLから画像を取得する
     * @param listener
     */
    private void getImageByUrl(final ListenerUtil.LoadBitmapListener listener) {
        if(Util.isEmpty(mImagePath)) {
            listener.onError();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(mImagePath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    if(bitmap != null) {
                        listener.onFinish(bitmap);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listener.onError();
            }
        }).start();
    }

    public void setImage(String filePath, Bitmap bitmap) {
    	if(bitmap == null) {
    		if(mImageCache != null) {
    			mImageCache.recycle();
    			mImageCache = null;
    		}
    	} else {
    		mImageCache = bitmap;
    	}
    	mImagePath = filePath;
    }
    public void setImage(String filePath) {
    	setImage(filePath, null);
    }

    public SeriesData() {
        mVolumeList = new ArrayList<Integer>();
        mTagsList = new ArrayList<String>();
    }


    /**
     * 小さい巻数->最大巻数の順番入っている前提
     * @param volume
     */
    public void addVolume(int volume) {
        // 新しく所持巻情報を追加
        mVolumeList.add(volume);
        Collections.sort(mVolumeList);
    }

    public void removeVolume(int volume) {
        // 新しく所持巻情報を追加
    	int index = mVolumeList.indexOf(volume);
    	if(index >= 0) mVolumeList.remove(index);
    	Collections.sort(mVolumeList);
    }

    public String getVolumeString() {
        String ret = "";
        int prevVolume = -1; // 最後に所持情報がある巻数
        int firstContinueVolume = -1; // 連番が始まった巻数
        int firstVolume = 0; // はじめに記録された巻数
        //ソート処理
        Collections.sort(mVolumeList);

        // 所持している巻数を小さいものから順番に見ていく
        for(int i = 0 ; i <= mVolumeList.size() ; i ++ ) {
            int value = -1;
        	if(i < mVolumeList.size()) {
        		value = mVolumeList.get(i);
        	}

            // はじめの巻専用
            if(i == 0) {
            	firstVolume = value;
                firstContinueVolume = value;
                prevVolume = value;
            }
            // 連番中
            else if(prevVolume + 1 == value) {
            	prevVolume = value;
            // 連番終了・または単発の巻数がある
            } else {
            	if(firstVolume != firstContinueVolume) {
            		ret += "、";
            	}
            	// 連番
            	if(firstContinueVolume < prevVolume) {
            		ret += firstContinueVolume + "巻 〜 " + prevVolume + "巻";
            	}
            	// 単発
            	else {
            		ret += prevVolume + "巻";
            	}
            	
            	firstContinueVolume = value;
            	prevVolume = value;
            }
        }
    	
        if(mVolumeList.size() <= 0) {
            ret += "所持巻なし";
        }
        return ret;
    }

}
