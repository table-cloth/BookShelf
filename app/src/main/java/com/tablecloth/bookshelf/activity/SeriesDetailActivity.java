package com.tablecloth.bookshelf.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tablecloth.bookshelf.R;
import com.tablecloth.bookshelf.db.DB;
import com.tablecloth.bookshelf.db.FilterDao;
import com.tablecloth.bookshelf.db.SeriesData;
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

    SeriesData mSeriesData = null;
    int mSeriesId = -1;
    NumberPicker mPicker;

    ImageView mImageView = null;
    LayoutInflater mLayoutnflater;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_series_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSeriesId = getIntent().getIntExtra(G.INTENT_SERIES_ID, -1);
        if(mSeriesId == -1) {
            ToastUtil.show(SeriesDetailActivity.this, "本の情報の取得に失敗しました");
            SeriesDetailActivity.this.finish();
        }

        mLayoutnflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mImageView = (ImageView) findViewById(R.id.image);

        mPicker = (NumberPicker) findViewById(R.id.number_picker);
        mPicker.setMaxValue(200);
        mPicker.setMinValue(0);
        mPicker.setFocusable(true);
        mPicker.setFocusableInTouchMode(true);

        // DBから情報取得
        refreshData();
        // 作品の情報を元に、レイアウトを反映
        initLayout();

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
				Intent intent = EditSeriesDialogActivity.getIntent(SeriesDetailActivity.this, "作品情報を編集", "保存", mSeriesId);
                startActivityForResult(intent, G.REQUEST_CODE_EDIT_SERIES_DETAIL);
			}
		});
    	
    	// 巻情報を追加
    	findViewById(R.id.btn_add).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int value = mPicker.getValue();
				if(mSeriesData.mVolumeList != null && mSeriesData.mVolumeList.contains(value)) {
					ToastUtil.show(SeriesDetailActivity.this, value + "巻は既に所持しています");
				} else {
					mSeriesData.addVolume(value);
					FilterDao.saveVolume(mSeriesId, value);
					initLayout();
				}
			}
		});
    	
    	// 巻情報を削除
    	findViewById(R.id.btn_delete).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int value = mPicker.getValue();
				if(mSeriesData.mVolumeList != null && !mSeriesData.mVolumeList.contains(value)) {
					ToastUtil.show(SeriesDetailActivity.this, value + "巻は所持していません");
				} else {
					mSeriesData.removeVolume(value);
					FilterDao.deleteVolume(mSeriesId, value);
					initLayout();
				}
			}
		});
    	
    	// 画像登録・編集
    	mImageView.setOnClickListener(new OnClickListener() {
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
    private void refreshData() {
        mSeriesData = FilterDao.loadSeries(SeriesDetailActivity.this, mSeriesId);
    }

    /**
     * 本の情報をレイアウトに反映させる
     */
    private void initLayout() {
    	if(mSeriesData != null) {
	        ((TextView)findViewById(R.id.title)).setText(mSeriesData.mTitle);
            if(!Util.isEmpty(mSeriesData.mTitlePronunciation)) {
                ((TextView) findViewById(R.id.title_pronuncitation)).setText("（" + mSeriesData.mTitlePronunciation + "）");
                findViewById(R.id.title_pronuncitation).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.title_pronuncitation).setVisibility(View.GONE);
            }

	        ((TextView)findViewById(R.id.author)).setText(mSeriesData.mAuthor);
            if(!Util.isEmpty(mSeriesData.mAuthorPronunciation)) {
                ((TextView) findViewById(R.id.author_pronunciation)).setText("（" + mSeriesData.mAuthorPronunciation + "）");
                findViewById(R.id.author_pronunciation).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.author_pronunciation).setVisibility(View.GONE);
            }

	        ((TextView)findViewById(R.id.magazine)).setText(mSeriesData.mMagazine);
            if(!Util.isEmpty(mSeriesData.mMagazinePronunciation)) {
                ((TextView)findViewById(R.id.magazine_pronunciation)).setText("（" + mSeriesData.mMagazinePronunciation+"）");
                findViewById(R.id.magazine_pronunciation).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.magazine_pronunciation).setVisibility(View.GONE);
            }
            ((TextView)findViewById(R.id.company)).setText(mSeriesData.mCompany);
	        ((TextView)findViewById(R.id.memo)).setText(mSeriesData.mMemo);
	        ((TextView)findViewById(R.id.volume)).setText(mSeriesData.getVolumeString());
            final View plus = findViewById(R.id.plus);
	        mSeriesData.getImage(mHandler, SeriesDetailActivity.this, new ListenerUtil.LoadBitmapListener() {
                @Override
                public void onFinish(Bitmap bitmap) {
                    if(bitmap != null) {
                        mImageView.setImageBitmap(bitmap);
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

            // タグ情報の設定
            ViewGroup tagContainer = (ViewGroup)findViewById(R.id.tag_container);
            tagContainer.removeAllViews();
            ViewUtil.setTagInfoNormal(SeriesDetailActivity.this, mSeriesData.mTagsList, tagContainer);
            tagContainer.invalidate();
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
        	// 作品情報編集から戻ったとき
            case G.REQUEST_CODE_EDIT_SERIES_DETAIL:
                if(resultCode == G.RESULT_POSITIVE) {
                    refreshData();
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
	                    mSeriesData.setImage(filePath, bitmap);
            		} catch(OutOfMemoryError e) {
                 	   ToastUtil.show(SeriesDetailActivity.this, "メモリ不足のため画像の取得に失敗しました");
                 	   e.printStackTrace();
                    }
            		FilterDao.saveSeries(mSeriesData);
                    final View plus = findViewById(R.id.plus);
					mSeriesData.getImage(mHandler, SeriesDetailActivity.this, new ListenerUtil.LoadBitmapListener() {
                        @Override
                        public void onFinish(Bitmap bitmap) {
                            if(bitmap != null) {
                                mImageView.setImageBitmap(bitmap);
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
            	break;
            case G.REQUEST_CODE_IMAGE_GALLERYS:
            	if(resultCode == RESULT_OK) {
				InputStream is = null;
				try {
					Bitmap bitmap = getImage(data);
					String filePath = ImageUtil.saveBitmap2LocalStorage(SeriesDetailActivity.this, bitmap);
					mSeriesData.setImage(filePath, bitmap);
					// bitmap.recycle();
					// bitmap = null;
				} catch (NullPointerException e) {
					ToastUtil.show(SeriesDetailActivity.this, "画像の取得に失敗しました");
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					ToastUtil.show(SeriesDetailActivity.this, "メモリ不足のため画像の取得に失敗しました");
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
				FilterDao.saveSeries(mSeriesData);
                final View plus = findViewById(R.id.plus);
				mSeriesData.getImage(mHandler, SeriesDetailActivity.this, new ListenerUtil.LoadBitmapListener() {
                    @Override
                    public void onFinish(Bitmap bitmap) {
                        if (bitmap != null) {
                            mImageView.setImageBitmap(bitmap);
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
