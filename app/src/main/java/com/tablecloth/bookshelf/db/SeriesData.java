package com.tablecloth.bookshelf.db;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.util.ImageUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;

/**
 * Created by shnomura on 2014/08/16.
 */
public class SeriesData {

    private final static String TAGS_SEPARATOR_SYMBOL = "\n";

    Context mContext;

    // Basic info
    private  int mSeriesId = -1;
    private String mTitle;
    private String mAuthor;
    private String mCompany;
    private String mMagazine;
    private String mImagePath;

    // Sub info
    private String mTitlePronunciation;
    private String mAuthorPronunciation;
    private String mCompanyPronunciation;
    private String mMagazinePronunciation;

    //Additional info
    private ArrayList<Integer> mVolumeList;
    private String mRawTags; // raw data of tags, same format as saved in DB
    private String mMemo;
    private boolean mIsSeriesComplete = false;

    // Update info
    private long mInitUpdateUnix;
    private long mLastUpdateUnix;

    // Cache
    private Bitmap mImageCache;
    private String mVolumeTextCache;

    /**
     * Constructor
     */
    public SeriesData(@NonNull Context context) {
        mContext = context;

        mVolumeList = new ArrayList<>();

        mImageCache = null;
        mVolumeTextCache = null;
    }

    /**
     * Gets seriesId
     *
     * @return seriesId
     */
    public int getSeriesId() {
        return mSeriesId;
    }

    /**
     * Sets seriesId
     *
     * @param seriesId seriesId
     */
    public void setSeriesId(int seriesId) {
        mSeriesId = seriesId;
    }

    /**
     * Gets title
     *
     * @return title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets title
     *
     * @param title title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Gets author
     *
     * @return author
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Sets author
     *
     * @param author author
     */
    public void setAuthor(String author) {
        mAuthor = author;
    }

    /**
     * Gets company
     *
     * @return company
     */
    public String getCompany() {
        return mCompany;
    }

    /**
     * Sets company
     *
     * @param company company
     */
    public void setCompany(String company) {
        mCompany = company;
    }

    /**
     * Gets magazine
     *
     * @return magazine
     */
    public String getMagazine() {
        return mMagazine;
    }

    /**
     * Sets magazine
     *
     * @param magazine magazine
     */
    public void setMagazine(String magazine) {
        mMagazine = magazine;
    }

    /**
     * Gets imagePath
     *
     * @return imagePath
     */
    public String getImagePath() {
        return mImagePath;
    }

    /**
     * Sets imagePath
     *
     * @param imagePath imagePath
     */
    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    /**
     * Gets titlePronunciation
     *
     * @return titlePronunciation
     */
    public String getTitlePronunciation() {
        return mTitlePronunciation;
    }

    /**
     * Sets titlePronunciation
     *
     * @param titlePronunciation titlePronunciation
     */
    public void setTitlePronunciation(String titlePronunciation) {
        mTitlePronunciation = titlePronunciation;
    }

    /**
     * Gets authorPronunciation
     *
     * @return authorPronunciation
     */
    public String getAuthorPronunciation() {
        return mAuthorPronunciation;
    }

    /**
     * Sets authorPronunciation
     *
     * @param authorPronunciation authorPronunciation
     */
    public void setAuthorPronunciation(String authorPronunciation) {
        mAuthorPronunciation = authorPronunciation;
    }

    /**
     * Gets companyPronunciation
     *
     * @return companyPronunciation
     */
    public String getCompanyPronunciation() {
        return mCompanyPronunciation;
    }

    /**
     * Sets companyPronunciation
     *
     * @param companyPronunciation companyPronunciation
     */
    public void setCompanyPronunciation(String companyPronunciation) {
        mCompanyPronunciation = companyPronunciation;
    }

    /**
     * Gets magazinePronunciation
     *
     * @return magazinePronunciation
     */
    public String getMagazinePronunciation() {
        return mMagazinePronunciation;
    }

    /**
     * Sets magazinePronunciation
     *
     * @param magazinePronunciation magazinePronunciation
     */
    public void setMagazinePronunciation(String magazinePronunciation) {
        mMagazinePronunciation = magazinePronunciation;
    }

    /**
     * Gets rawTags
     *
     * @return rawTags
     */
    public String getRawTags() {
        return mRawTags;
    }

    /**
     * Sets rawTags
     *
     * @param rawTags rawTags
     */
    public void setRawTags(String rawTags) {
        mRawTags = rawTags;
    }

    /**
     * Gets memo
     *
     * @return memo
     */
    public String getMemo() {
        return mMemo;
    }

    /**
     * Sets memo
     *
     * @param memo memo
     */
    public void setMemo(String memo) {
        mMemo = memo;
    }

    /**
     * Gets seriesComplete
     *
     * @return seriesComplete
     */
    public boolean isSeriesComplete() {
        return mIsSeriesComplete;
    }

    /**
     * Sets seriesComplete
     *
     * @param seriesComplete seriesComplete
     */
    public void setSeriesComplete(boolean seriesComplete) {
        mIsSeriesComplete = seriesComplete;
    }

    /**
     * Gets initUpdateUnix
     *
     * @return initUpdateUnix
     */
    public long getInitUpdateUnix() {
        return mInitUpdateUnix;
    }

    /**
     * Sets initUpdateUnix
     *
     * @param initUpdateUnix initUpdateUnix
     */
    public void setInitUpdateUnix(long initUpdateUnix) {
        mInitUpdateUnix = initUpdateUnix;
    }

    /**
     * Gets lastUpdateUnix
     *
     * @return lastUpdateUnix
     */
    public long getLastUpdateUnix() {
        return mLastUpdateUnix;
    }

    /**
     * Sets lastUpdateUnix
     *
     * @param lastUpdateUnix lastUpdateUnix
     */
    public void setLastUpdateUnix(long lastUpdateUnix) {
        mLastUpdateUnix = lastUpdateUnix;
    }

    /**
     * Get text to show volumes
     * Uses cache if available, else update cache
     *
     * @return volume text or "" if no volume is set
     */
    @NonNull
    public String getVolumeText() {
        if(!Util.isEmpty(mVolumeTextCache)) {
            return mVolumeTextCache;
        }
        mVolumeTextCache = createVolumeText();
        return mVolumeTextCache;
    }

    /**
     * Set volumeList
     *
     * @param volumeList volumeList
     */
    public void setVolumeList(ArrayList<Integer> volumeList) {
        mVolumeList = volumeList;
    }

    /**
     * Add volume to this series
     *
     * @param volume volume
     */
    public void addVolume(int volume) {
        if(!mVolumeList.contains(volume)) {
            mVolumeList.add(volume);

            // Clear cache since volume data is changed
            clearVolumeTextCache();
        }
    }

    /**
     * Remove volume from this series
     *
     * @param volume volume
     */
    public void removeVolume(int volume) {
    	int index = mVolumeList.indexOf(volume);
    	if(index >= 0 && index < mVolumeList.size()) {
            mVolumeList.remove(index);

            // Clear cache since volume data is changed
            clearVolumeTextCache();
        }
    }

    /**
     * Load bitmap image
     * Uses cache if available
     * If failed to load image from file path, it will re-try loading from url
     *
     * @param handler handler
     * @param activity activity
     * @param listener LoadBitmapListener instance
     */
    public void loadImage(@NonNull final Handler handler, @NonNull Activity activity, @NonNull final ListenerUtil.LoadBitmapListener listener) {
        if(mImageCache != null) {
            listener.onFinish(mImageCache);
            return;
        }
        if(Util.isEmpty(mImagePath)) {
            listener.onError();
            return;
        }

        // Try loading image from file path
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
                // If failed to load image from file path, re-try loading image from url
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
     * Converts tags list into raw text tags data
     *
     * @param tagsList tags list
     * @return raw text tags data
     */
    @NonNull
    public String convertTagsList2TagsRawText(@Nullable ArrayList<String> tagsList) {
        StringBuilder rawTagsText = new StringBuilder();
        boolean isFirstValue = true;

        if(tagsList != null && !tagsList.isEmpty()) {
            for (String tag : tagsList) {
                if (isFirstValue) {
                    isFirstValue = false;
                } else {
                    rawTagsText.append(TAGS_SEPARATOR_SYMBOL);
                }
                rawTagsText.append(tag);
            }
        }
        return rawTagsText.toString();
    }

    /**
     * Converts raw text tags data into tags list
     *
     * @param rawTextTagsData raw text tags data
     * @return tags list
     */
    @NonNull
    public ArrayList<String> convertTagsRawText2TagsList(String rawTextTagsData) {
        // return if tags is empty
        if(Util.isEmpty(rawTextTagsData)) {
            return new ArrayList<>();
        }

        String[] tagsArray = rawTextTagsData.contains(TAGS_SEPARATOR_SYMBOL)
                ? rawTextTagsData.split(TAGS_SEPARATOR_SYMBOL)
                : new String[] {rawTextTagsData};
        return new ArrayList<>(Arrays.asList(tagsArray));
    }

    /**
     * Clear volume text cache
     */
    private void clearVolumeTextCache() {
        mVolumeTextCache = null;
    }

    /**
     * Creates text to show volumes from list
     * list will be sorted before generating text
     *
     * @return volume text
     */
    @NonNull
    private String createVolumeText() {

        // return if no volume is set
        if(mVolumeList == null || mVolumeList.size() <= 0) {
            mContext.getString(R.string.series_data_no_volume);
        }


        // sort so list becomes smaller value -> larger value
        Collections.sort(mVolumeList);

        // initialize values with first index value
        StringBuilder volumeTextBuilder = new StringBuilder();
        int firstVolume = mVolumeList.get(0); // first volume set in list
        int currentVolume; // current volume in loop
        int prevVolume = firstVolume; // previous volume in loop
        int firstVolume4CurrentConsecutive = firstVolume; // start volume of current consecutive

        // go through list from smaller values
        for(int i = 1; i < mVolumeList.size() ; i ++) {
            currentVolume = mVolumeList.get(i);
            boolean isLastVolume = i + 1 == mVolumeList.size();

            // if last value or current volume is not consecutive
            if(isLastVolume
                    || currentVolume != prevVolume + 1) {
                // If current first consecutive volume does not equal first volume if list
                if(firstVolume4CurrentConsecutive != firstVolume) {
                    volumeTextBuilder.append(
                            mContext.getString(R.string.series_data_volume_separator_symbol));
                }

                // use current volume if this is last volume, else use prevVolume
                int volume = isLastVolume
                        ? currentVolume
                        : prevVolume;

                // if volume is consecutive
                if(firstVolume4CurrentConsecutive < volume) {
                    String text = firstVolume4CurrentConsecutive
                            + mContext.getString(R.string.series_data_volume_symbol)
                            + mContext.getString(R.string.series_data_volume_conjunction_symbol)
                            + volume
                            + mContext.getString(R.string.series_data_volume_conjunction_symbol);
                    volumeTextBuilder.append(text);
                }
                // if volume is not consecutive
                else {
                    String text = volume
                            + mContext.getString(R.string.series_data_volume_symbol);
                    volumeTextBuilder.append(text);
                }

                firstVolume4CurrentConsecutive = currentVolume;
            }
            prevVolume = currentVolume;
        }

        return volumeTextBuilder.toString();
    }

    /**
     * Load image bitmap from file path
     * Result will be given to LoadBitmapLister
     *
     * @param activity activity
     * @param listener LoadBitmapListener instance
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
            ToastUtil.show(activity, mContext.getString(R.string.series_data_error_load_image_by_memory));
            ImageUtil.clearCache();
            e.printStackTrace();
        } catch (IOException e) {
            ToastUtil.show(activity, mContext.getString(R.string.series_data_error_load_image));
            e.printStackTrace();
        }
        listener.onError();
    }

    /**
     * Load image bitmap from url
     * Result will be given to LoadBitmapLister
     *
     * @param listener LoadBitmapListener instance
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
}
