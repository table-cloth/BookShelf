package com.tablecloth.bookshelf.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
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
import com.tablecloth.bookshelf.dialog.EditSeriesDialogActivity;
import com.tablecloth.bookshelf.dialog.SimpleDialogActivity;
import com.tablecloth.bookshelf.util.G;
import com.tablecloth.bookshelf.util.ImageUtil;
import com.tablecloth.bookshelf.util.ListenerUtil;
import com.tablecloth.bookshelf.util.ToastUtil;
import com.tablecloth.bookshelf.util.Util;
import com.tablecloth.bookshelf.util.ViewUtil;

/**
 * Created by minami on 14/09/07.
 * １作品の詳細表示画面
 */
public class SeriesDetailActivity extends BaseActivity {

    // View
    private TextView mBookTitleTextView = null;
    private TextView mBookAuthorTextView = null;
    private TextView mBookMagazineTextView = null;
    private TextView mBookCompanyTextView = null;

    private TextView mBookTitlePronunceTextView = null;
    private TextView mBookAuthorPronunceTextView = null;
    private TextView mBookMagazinePronunceTextView = null;

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
        mBookTitlePronunceTextView = (TextView)findViewById(R.id.title_pronuncitation);
        mBookAuthorPronunceTextView = (TextView)findViewById(R.id.author_pronunciation);
        mBookMagazinePronunceTextView = (TextView)findViewById(R.id.magazine_pronunciation);

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

        setClickListener();

        // 広告の初期化処理
        Util.initAdview(this, (ViewGroup)findViewById(R.id.banner));
    }

    private void setClickListener() {
    	// 戻るボタン
    	findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesDetailActivity.this.finish();
			}
		});
    	// 編集ボタン
    	findViewById(R.id.btn_edit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = EditSeriesDialogActivity.getIntent(SeriesDetailActivity.this, "作品情報を編集", "保存", mShowBookSeriesId);
                startActivityForResult(intent, G.REQUEST_CODE_EDIT_SERIES_DETAIL);
			}
		});

    	// 巻情報を追加
    	findViewById(R.id.btn_add).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int value = mBookVolumePicker.getValue();
				if(mShowBookSeriesData.getVolumeList() != null && mShowBookSeriesData.getVolumeList().contains(value)) {
					ToastUtil.show(SeriesDetailActivity.this, value + "巻は既に所持しています");
				} else {
					mShowBookSeriesData.addVolume(value);
					mBookVolumeDao.saveBookVolume(mShowBookSeriesId, value);
					initLayout();
				}
			}
		});

    	// 巻情報を削除
    	findViewById(R.id.btn_delete).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int value = mBookVolumePicker.getValue();
				if(mShowBookSeriesData.getVolumeList() != null && !mShowBookSeriesData.getVolumeList().contains(value)) {
					ToastUtil.show(SeriesDetailActivity.this, value + "巻は所持していません");
				} else {
					mShowBookSeriesData.removeVolume(value);
					mBookVolumeDao.deleteBookVolume(mShowBookSeriesId, value);
					initLayout();
				}
			}
		});

    	// 画像登録・編集
    	mBookCoverImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = SimpleDialogActivity.getIntent(SeriesDetailActivity.this, "画像変更", "作品の画像を変更しますか？", "はい", "いいえ");
                startActivityForResult(intent, G.REQUEST_CODE_IMAGE_CHANGE_CONFIRM_1);
			}
		});

    }

    /**
     * DBから最新情報を取得
     */
    private void updateShowBookSeriesData(int bookSeriesId) {
        mShowBookSeriesData = mBookSeriesDao.loadBookSeriesData(bookSeriesId);
    }

    /**
     * Set text and visibility of textView given
     * If text is empty, visibility will be set as Gone
     * If text is not empyty, visibility will be set as Visible
     *
     * @oaram textView
     * @param text Text to set in TextView
     */
    private void setTextAndVisibility(@NonNull TextView textView, @NonNull String text) {
        textView.setText(text);
        textView.setVisibility(Util.isEmpty(text)
                ? View.GONE
                : View.VISIBLE);
    }

    /**
     * 本の情報をレイアウトに反映させる
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
        setTextAndVisibility(mBookTitlePronunceTextView, mShowBookSeriesData.getTitlePronunciation());
        setTextAndVisibility(mBookAuthorPronunceTextView, mShowBookSeriesData.getAuthorPronunciation());
        setTextAndVisibility(mBookMagazinePronunceTextView, mShowBookSeriesData.getMagazinePronunciation());

        // extra data
        setTextAndVisibility(mBookMemoTextView, mShowBookSeriesData.getMemo();
        setTextAndVisibility(mBookVolumeTextView, mShowBookSeriesData.getVolumeText());

        //tag
        mBookTagViewGroup.removeAllViews();


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

            // タグ情報の設定
            ViewGroup tagContainer = (ViewGroup)findViewById(R.id.tag_container);
            tagContainer.removeAllViews();
            ViewUtil.setTagInfoNormal(SeriesDetailActivity.this, mShowBookSeriesData.getTagsAsList(), tagContainer);
            tagContainer.invalidate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
        	// 作品情報編集から戻ったとき
            case G.REQUEST_CODE_EDIT_SERIES_DETAIL:
                if(resultCode == G.RESULT_POSITIVE) {
                    reloadBookSeriesData();
                    initLayout();
                } else if(resultCode == G.RESULT_SPECIAL) {
                    SeriesDetailActivity.this.finish();
                }
                break;
             // 画像編集確認ダイアログから戻ったとき
            case G.REQUEST_CODE_IMAGE_CHANGE_CONFIRM_1:
                if(resultCode == G.RESULT_POSITIVE) {
                	Intent i = SimpleDialogActivity.getIntent(SeriesDetailActivity.this, "画像の選択方法", "画像の選択方法を選択してください", "ギャラリーから選択", "カメラで撮影");
                    startActivityForResult(i, G.REQUEST_CODE_IMAGE_CHANGE_CONFIRM_2);
                }
                break;
            // 画像編集確認ダイアログから戻ったとき
            case G.REQUEST_CODE_IMAGE_CHANGE_CONFIRM_2:
                if(resultCode == G.RESULT_POSITIVE) {
                	Intent i = new Intent();
                	i.setAction(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    startActivityForResult(i, G.REQUEST_CODE_IMAGE_GALLERYS);
                } else if(resultCode == G.RESULT_NEGATIVE){
                	Intent i = new Intent();
                	i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, G.REQUEST_CODE_IMAGE_CAMERA);
                }
                break;
            // 画像取得後
            case G.REQUEST_CODE_IMAGE_CAMERA:
            	if(resultCode == RESULT_OK) {
            		try {
	            		Bitmap bitmap = (Bitmap)data.getExtras().get("data");
	            		String filePath = ImageUtil.saveBitmap2LocalStorage(SeriesDetailActivity.this, bitmap);
                        mShowBookSeriesData.setImagePath(filePath);
                        ImageUtil.setImageCache(mShowBookSeriesId, bitmap);
            		} catch(OutOfMemoryError e) {
                 	   ToastUtil.show(SeriesDetailActivity.this, "メモリ不足のため画像の取得に失敗しました");
                       ImageUtil.clearCache();
                 	   e.printStackTrace();
                    }
            		mBookSeriesDao.saveSeries(mShowBookSeriesData);
                    final View plus = findViewById(R.id.plus);

                    Bitmap cacheImage = ImageUtil.getImageCache(mShowBookSeriesId);
                    if(cacheImage != null) {
                        mBookCoverImageView.setImageBitmap(cacheImage);
                        plus.setVisibility(View.GONE);
                    } else {
                        mShowBookSeriesData.loadImage(mHandler, SeriesDetailActivity.this, new ListenerUtil.LoadBitmapListener() {
                            @Override
                            public void onFinish(@NonNull Bitmap bitmap) {
                                if (bitmap != null) {
                                    mBookCoverImageView.setImageBitmap(bitmap);
                                    plus.setVisibility(View.GONE);
                                } else {
                                    plus.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError() {
                                plus.setVisibility(View.VISIBLE);
                            }
                        });
                    }
            	}
            	break;
            case G.REQUEST_CODE_IMAGE_GALLERYS:
            	if(resultCode == RESULT_OK) {
				InputStream is = null;
				try {
					Bitmap bitmap = getImage(data);
					String filePath = ImageUtil.saveBitmap2LocalStorage(SeriesDetailActivity.this, bitmap);
					mShowBookSeriesData.setImagePath(filePath);
                    ImageUtil.setImageCache(mShowBookSeriesId, bitmap);
                    // bitmap.recycle();
					// bitmap = null;
				} catch (NullPointerException e) {
					ToastUtil.show(SeriesDetailActivity.this, "画像の取得に失敗しました");
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					ToastUtil.show(SeriesDetailActivity.this, "メモリ不足のため画像の取得に失敗しました");
                    ImageUtil.clearCache();
					e.printStackTrace();
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				mBookSeriesDao.saveSeries(mShowBookSeriesData);
                final View plus = findViewById(R.id.plus);
                Bitmap cacheImage = ImageUtil.getImageCache(mShowBookSeriesId);
                if(cacheImage != null) {
                    mBookCoverImageView.setImageBitmap(cacheImage);
                    plus.setVisibility(View.GONE);
                } else {
                    mShowBookSeriesData.loadImage(mHandler, SeriesDetailActivity.this, new ListenerUtil.LoadBitmapListener() {
                        @Override
                        public void onFinish(@NonNull Bitmap bitmap) {
                            if (bitmap != null) {
                                mBookCoverImageView.setImageBitmap(bitmap);
                                findViewById(R.id.plus).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.plus).setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError() {
                            findViewById(R.id.plus).setVisibility(View.VISIBLE);
                        }
                    });
                }
			}
			break;
        }
    }
    
    private Bitmap getImage(Intent data) {
    	BitmapFactory.Options imageOptions = new BitmapFactory.Options();
    	imageOptions.inJustDecodeBounds = true;
    	imageOptions.inPreferredConfig = Bitmap.Config.RGB_565;

    	// もし、画像が大きかったら縮小して読み込む
    	//  今回はimageSizeMaxの大きさに合わせる
    	Bitmap bitmap;
    	int imageSizeMax = 200;
    	InputStream inputStream = null;
		try {
			inputStream = getContentResolver().openInputStream(data.getData());
		
	    	float imageScaleWidth = (float)imageOptions.outWidth / imageSizeMax;
	    	float imageScaleHeight = (float)imageOptions.outHeight / imageSizeMax;
	
	    	// もしも、縮小できるサイズならば、縮小して読み込む
	    	if (imageScaleWidth > 2 && imageScaleHeight > 2) {  
	    	    BitmapFactory.Options imageOptions2 = new BitmapFactory.Options();
	
	    	    // 縦横、小さい方に縮小するスケールを合わせる
	    	    int imageScale = (int)Math.floor((imageScaleWidth > imageScaleHeight ? imageScaleHeight : imageScaleWidth));    
	
	    	    // inSampleSizeには2のべき上が入るべきなので、imageScaleに最も近く、かつそれ以下の2のべき上の数を探す
	    	    for (int i = 2; i <= imageScale; i *= 2) {
	    	        imageOptions2.inSampleSize = i;
	    	    }
	
	    	    bitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions2);
	    	    Log.v("image", "Sample Size: 1/" + imageOptions2.inSampleSize);
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
}
