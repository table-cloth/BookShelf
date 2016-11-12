package com.tablecloth.bookshelf.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.data.BookData;
import com.tablecloth.bookshelf.data.BookSeriesData;
import com.tablecloth.bookshelf.db.BookSeriesDao;
import com.tablecloth.bookshelf.db.BookVolumeDao;
import com.tablecloth.bookshelf.dialog.BookSeriesAddEditDialogActivity;
import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ImageUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;

/**
 * Activity to see detail info of Book Series
 *
 * Created by minami on 14/09/07.
 */
public class SeriesDetailActivity extends BaseActivity implements OnClickListener {

    // View
    private TextView mBookTitleTextView = null;
    private TextView mBookAuthorTextView = null;
    private TextView mBookMagazineTextView = null;
    private TextView mBookCompanyTextView = null;

    private TextView mBookTitlePronounceTextView = null;
    private TextView mBookAuthorPronounceTextView = null;
    private TextView mBookMagazinePronounceTextView = null;

    private TextView mBookMemoTextView = null;
    private TextView mBookVolumeTextView = null;
    private ViewGroup mBookTagViewGroup = null;

    private ImageView mBookCoverImageView = null;
    private View mPlusIconInBookCoverView = null;

    private NumberPicker mBookVolumePicker = null;


    // BookData related
//    private final int mShowBookSeriesId;
    private BookSeriesData mShowBookSeriesData = null;
    private BookSeriesDao mBookSeriesDao;
    private BookVolumeDao mBookVolumeDao;

    /**
     * Get layout ID to show in the activity
     *
     * @return layout ID
     */
    @Override
    protected int getContentViewID() {
        return R.layout.activity_series_detail;
    }

    /**
     * Get intent with given extra data
     *
     * @param context context
     * @param seriesId book series id
     * @return Intent instance
     */
    @NonNull
    public static Intent getIntent(@NonNull Context context, int seriesId) {
        Intent intent = new Intent(context, SeriesDetailActivity.class);
        intent.putExtra(G.INTENT_SERIES_ID,seriesId);
        return intent;
    }

    /**
     * Constructor
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init book series id
        final int bookSeriesId = getIntent().getIntExtra(
                G.INTENT_SERIES_ID, BookData.BOOK_SERIES_ERROR_VALUE);
        if(bookSeriesId == BookData.BOOK_SERIES_ERROR_VALUE) {
            ToastUtil.show(SeriesDetailActivity.this, R.string.series_data_error_failed_2_get_data);
            SeriesDetailActivity.this.finish();
            return;
        }

        // Init DB
        mBookSeriesDao = new BookSeriesDao(this);
        mBookVolumeDao = new BookVolumeDao(this);
        updateShowBookSeriesData(bookSeriesId);

        // Init View
        mBookTitleTextView = (TextView)findViewById(R.id.title);
        mBookAuthorTextView = (TextView)findViewById(R.id.author);
        mBookMagazineTextView = (TextView)findViewById(R.id.magazine);
        mBookCompanyTextView = (TextView)findViewById(R.id.company);
        mBookTitlePronounceTextView = (TextView)findViewById(R.id.title_pronuncitation);
        mBookAuthorPronounceTextView = (TextView)findViewById(R.id.author_pronunciation);
        mBookMagazinePronounceTextView = (TextView)findViewById(R.id.magazine_pronunciation);

        mBookMemoTextView = (TextView)findViewById(R.id.memo);
        mBookVolumeTextView = (TextView)findViewById(R.id.volume);
        mBookTagViewGroup = (ViewGroup)findViewById(R.id.tag_container);
        mBookCoverImageView = (ImageView) findViewById(R.id.book_cover_image);
        mPlusIconInBookCoverView = findViewById(R.id.plus);
        mBookVolumePicker = (NumberPicker) findViewById(R.id.number_picker);

        mBookVolumePicker.setMaxValue(200);
        mBookVolumePicker.setMinValue(0);
        mBookVolumePicker.setFocusable(true);
        mBookVolumePicker.setFocusableInTouchMode(true);

        // Apply book series data to layout
        applyBookSeriesData2Layout();

        initializeClickListeners();

        // Init ad
        Util.initAdView(this, (ViewGroup)findViewById(R.id.banner));
    }

    /**
     * Handles all click event within this Activity
     *
     * @param view clicked view
     */
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.btn_back: // btn back
                finish();
                break;

            case R.id.btn_edit: // btn edit book series detail
                Intent editIntent = BookSeriesAddEditDialogActivity.getIntent(
                        this,
                        R.string.edit_book_series_detail,
                        R.string.save,
                        mShowBookSeriesData.getSeriesId());
                startActivityForResult(editIntent, G.REQUEST_CODE_EDIT_SERIES_DETAIL);
                break;

            case R.id.btn_add: // btn add volume
                int registerVolume = mBookVolumePicker.getValue();
                // failed to save (volume already registered)
                if(mShowBookSeriesData.getVolumeList().contains(registerVolume)) {
                    ToastUtil.show(this,
                            getString(R.string.series_data_volume_error_already_registered, registerVolume));
                } else {
                    // success volume register
                    if(mBookVolumeDao.saveBookVolume(mShowBookSeriesData.getSeriesId(), registerVolume)) {
                        mShowBookSeriesData.addVolume(registerVolume);
                        applyBookSeriesData2Layout();
                    // fail volume register
                    } else {
                        ToastUtil.show(this, getString(R.string.series_data_error_failed_2_update_data));
                    }
                }
                break;

            case R.id.btn_delete: // btn delete volume
                int deleteVolume = mBookVolumePicker.getValue();
                if(!mShowBookSeriesData.getVolumeList().contains(deleteVolume)) {
                    ToastUtil.show(this,
                            getString(R.string.series_data_volume_error_not_registered, deleteVolume));
                } else {
                    // success volume delete
                    if(mBookVolumeDao.deleteBookVolume(mShowBookSeriesData.getSeriesId(), deleteVolume)) {
                        mShowBookSeriesData.removeVolume(deleteVolume);
                        applyBookSeriesData2Layout();
                    // fail volume delete
                    } else {
                        ToastUtil.show(this, getString(R.string.series_data_error_failed_2_update_data));
                    }

                }
                break;

            case R.id.book_cover_image: // image view of book series cover
                Intent imageIntent = SimpleDialogActivity.getIntent(
                        this,
                        R.string.change_image,
                        R.string.series_data_confirm_change_book_cover,
                        R.string.yes,
                        R.string.no);
                startActivityForResult(imageIntent, G.REQUEST_CODE_IMAGE_CHANGE_CONFIRM_1);
                break;
        }
    }



    /**
     * Initialize all onClickListeners in this Activity
     */
    private void initializeClickListeners() {
        findViewById(R.id.btn_back).setOnClickListener(this); // btn back
    	findViewById(R.id.btn_edit).setOnClickListener(this); // btn edit book series detail
    	findViewById(R.id.btn_add).setOnClickListener(this); // btn add book volume
    	findViewById(R.id.btn_delete).setOnClickListener(this); // btn delete book volume
    	mBookCoverImageView.setOnClickListener(this); // book cover image view
    }

    /**
     * Load book series data from DB and set to BookSeriesData instance
     *
     * @param bookSeriesId Book series id to load
     */
    private void updateShowBookSeriesData(int bookSeriesId) {
        mShowBookSeriesData = mBookSeriesDao.loadBookSeriesData(bookSeriesId);
    }

    /**
     * Set text and visibility of textView given
     * If text is empty, visibility will be set as Gone
     * If text is not empyty, visibility will be set as Visible
     *
     * @param textView TextView instance to set text and visibility
     * @param text Text to set in TextView
     */
    private void setTextAndVisibility(@NonNull TextView textView, @NonNull String text) {
        textView.setText(text);
        textView.setVisibility(Util.isEmpty(text)
                ? View.GONE
                : View.VISIBLE);
    }

    /**
     * Update activity layout according to BookSeriesData info
     */
    private void applyBookSeriesData2Layout() {
        if(mShowBookSeriesData == null) {
            throw new NullPointerException("Trying to apply null BookSeriesData instance to layout");
        }

        // Basic data
        setTextAndVisibility(mBookTitleTextView, mShowBookSeriesData.getTitle());
        setTextAndVisibility(mBookAuthorTextView, mShowBookSeriesData.getAuthor());
        setTextAndVisibility(mBookMagazineTextView, mShowBookSeriesData.getMagazine());
        setTextAndVisibility(mBookCompanyTextView, mShowBookSeriesData.getCompany());

        // pronunciation
        setTextAndVisibility(mBookTitlePronounceTextView, mShowBookSeriesData.getTitlePronunciation());
        setTextAndVisibility(mBookAuthorPronounceTextView, mShowBookSeriesData.getAuthorPronunciation());
        setTextAndVisibility(mBookMagazinePronounceTextView, mShowBookSeriesData.getMagazinePronunciation());

        // extra data
        setTextAndVisibility(mBookMemoTextView, mShowBookSeriesData.getMemo());
        setTextAndVisibility(mBookVolumeTextView, mShowBookSeriesData.getVolumeText());

        //tag
        mBookTagViewGroup.removeAllViews();
        ArrayList<ViewGroup> tagsViewList =ViewUtil.getTagViewList(this, mShowBookSeriesData.getTagsAsList(), ViewUtil.TAGS_LAYOUT_TYPE_NORMAL);
        for(ViewGroup tagView : tagsViewList) {
            mBookTagViewGroup.addView(tagView);
        }

        // image
        mPlusIconInBookCoverView.setVisibility(View.GONE);
        Bitmap imageCache =ImageUtil.getImageCache(mShowBookSeriesData.getSeriesId());
        if(imageCache != null) {
            mBookCoverImageView.setImageBitmap(imageCache);
        } else {
            mShowBookSeriesData.loadImage(
                    mHandler, this, new ListenerUtil.LoadBitmapListener() {
                        @Override
                        public void onFinish(@NonNull Bitmap bitmap) {
                            mBookCoverImageView.setImageBitmap(bitmap);
                            ImageUtil.setImageCache(mShowBookSeriesData.getSeriesId(), bitmap);
                        }

                        @Override
                        public void onError() {
                            // only show plus icon then Image is null
                            mPlusIconInBookCoverView.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    /**
     * Called on activity result
     *
     * @param requestCode request code
     * @param resultCode result code
     * @param data intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case G.REQUEST_CODE_EDIT_SERIES_DETAIL: // Return from BookSeriesData edit screen
                if(isResultPositive(resultCode)) {
                    int resultAction = getIntentExtraInt(data, G.RESULT_DATA_KEY_EDIT_SERIES);
                    if (G.RESULT_DATA_VALUE_EDIT_SERIES_EDIT == resultAction) {
                        updateShowBookSeriesData(mShowBookSeriesData.getSeriesId());
                        applyBookSeriesData2Layout();
                    } else if (G.RESULT_DATA_VALUE_EDIT_SERIES_DELETE == resultAction) {
                        SeriesDetailActivity.this.finish();
                    }
                }
                break;

            case G.REQUEST_CODE_IMAGE_CHANGE_CONFIRM_1: // Return from book cover image edit dialog (1)
                if(isResultPositive(resultCode)) {
                	Intent confirmDialogIntent = SimpleDialogActivity.getIntent(
                            this,
                            R.string.image_select_method,
                            R.string.image_select_choose_image_select_method,
                            R.string.image_select_select_from_gallery,
                            R.string.image_select_select_from_camera);
                    startActivityForResult(confirmDialogIntent, G.REQUEST_CODE_IMAGE_CHANGE_CONFIRM_2);
                }
                break;

            case G.REQUEST_CODE_IMAGE_CHANGE_CONFIRM_2: // Return from book cover image edit dialog (2)
                // 1st button (Gallery)
                if(resultCode == G.RESULT_POSITIVE) {
                	Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, G.REQUEST_CODE_IMAGE_GALLERYS);
                // 2nd button (Camera)
                } else if(resultCode == G.RESULT_NEGATIVE){
                	Intent cameraIntent = new Intent();
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, G.REQUEST_CODE_IMAGE_CAMERA);
                }
                break;

            case G.REQUEST_CODE_IMAGE_CAMERA: // Return from capture image with camera
            	if(isResultPositive(resultCode)) {
                    Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
                    if (cameraBitmap == null) {
                        ToastUtil.show(this, R.string.series_data_error_load_image);
                        return;
                    }
                    updateAndSaveBookCoverImage(cameraBitmap);
                }
            	break;

            case G.REQUEST_CODE_IMAGE_GALLERYS: // Return from select image from gallery
            	if(isResultPositive(resultCode)) {
                    Bitmap galleryBitmap = loadImageFromGalleryIntent(data);
                    if(galleryBitmap == null) {
                        ToastUtil.show(this, R.string.series_data_error_load_image);
                        return;
                    }
                    updateAndSaveBookCoverImage(galleryBitmap);
                }
			break;
        }
    }

    /**
     * Load bitmap image from Gallery activity result
     *
     * @param data Intent instance
     * @return Bitmap or null
     */
    @Nullable
    private Bitmap loadImageFromGalleryIntent(@NonNull Intent data) {
    	BitmapFactory.Options imageOptions = new BitmapFactory.Options();
    	imageOptions.inJustDecodeBounds = true;
    	imageOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap;
        int imageSizeMaxPx = 200;
        InputStream inputStream = null;

        // If the image size is too big, sample the image into smaller size
		try {
			inputStream = getContentResolver().openInputStream(data.getData());
		
	    	float imageScaleWidth = (float)imageOptions.outWidth / imageSizeMaxPx;
	    	float imageScaleHeight = (float)imageOptions.outHeight / imageSizeMaxPx;
	
	    	// If the size is capable with resizing (down-scaling), do so
	    	if (imageScaleWidth > 2 && imageScaleHeight > 2) {
	    	    BitmapFactory.Options sampleImageOptions = new BitmapFactory.Options();

                // Check both horizontal & vertical
                // Make the scale fit with the smaller value / side
	    	    int imageScale = (int)Math.floor((imageScaleWidth > imageScaleHeight ? imageScaleHeight : imageScaleWidth));

                // inSampleSize should be value of "squares of 2" and "smaller than actual image size"
	    	    for (int i = 2; i <= imageScale; i *= 2) {
                    sampleImageOptions.inSampleSize = i;
	    	    }
	
	    	    bitmap = BitmapFactory.decodeStream(inputStream, null, sampleImageOptions);
            // When no resizing is not needed
	    	} else {
	    	    bitmap = BitmapFactory.decodeStream(inputStream);
	    	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
    }

    /**
     * Updates BookCoverImageView with given bitmap
     * @param bitmap Bitmap instance to set as book cover image
     */
    private void updateAndSaveBookCoverImage(@Nullable Bitmap bitmap) {
        if(bitmap == null) {
            return;
        }

        String savedImageFilePath = ImageUtil.saveBitmap2LocalStorage(SeriesDetailActivity.this, bitmap);
        if(Util.isEmpty(savedImageFilePath)) {
            ToastUtil.show(this, R.string.series_data_error_failed_2_update_data);
            return;
        }

        mShowBookSeriesData.setImagePath(savedImageFilePath);
        mBookSeriesDao.saveSeries(mShowBookSeriesData);
        ImageUtil.setImageCache(mShowBookSeriesData.getSeriesId(), bitmap);

        findViewById(R.id.plus).setVisibility(View.GONE);
        mBookCoverImageView.setImageBitmap(bitmap);
    }
}
