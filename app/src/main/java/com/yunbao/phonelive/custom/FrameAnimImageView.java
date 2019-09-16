package com.yunbao.phonelive.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.util.List;

/**
 * Created by cxf on 2017/9/23.
 */

public class FrameAnimImageView extends ImageView {

    private Paint mPaint;
    private List<Integer> mImgList;
    private BitmapFactory.Options mOptions;
    private int mPosition;
    private Bitmap mCurBitmap;
    private int mWidth;
    private int mHeight;
    private boolean isStarted;
    private int mDuration = 150;
    private Rect mSrc;
    private Rect mDst;
    public static final int FIT_WIDTH = 0;
    public static final int FIT_HEIGHT = 1;
    private int mScaleType = FIT_WIDTH;
    private boolean mAnimating;

    public FrameAnimImageView(Context context) {
        this(context, null);
    }

    public FrameAnimImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameAnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOptions = new BitmapFactory.Options();
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inSampleSize = 1;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mSrc = new Rect();
        mDst = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mAnimating) {
            if (isStarted) {
                if (mCurBitmap != null) {
                    mCurBitmap.recycle();
                }
                getNextBitmap();
                if (mCurBitmap != null) {
                    int w = mCurBitmap.getWidth();
                    int h = mCurBitmap.getHeight();
                    mSrc.left = 0;
                    mSrc.right = w;
                    mSrc.top = 0;
                    mSrc.bottom = h;
                    if (mScaleType == FIT_WIDTH) {
                        int targetH = (int) ((mWidth / (float) w) * h);
                        int y = (mHeight - targetH) / 2;
                        mDst.left = 0;
                        mDst.right = mWidth;
                        mDst.top = y;
                        mDst.bottom = y + targetH;
                    } else if (mScaleType == FIT_HEIGHT) {
                        int targetW = (int) ((mHeight / (float) h) * w);
                        int x = (mWidth - targetW) / 2;
                        mDst.left = x;
                        mDst.right = x + targetW;
                        mDst.top = 0;
                        mDst.bottom = mHeight;
                    }
                    canvas.drawBitmap(mCurBitmap, mSrc, mDst, mPaint);
                    if (mPosition < mImgList.size()) {
                        postInvalidateDelayed(mDuration);
                    } else {
                        mAnimating = false;
                        isStarted = false;
                        setImageResource(mImgList.get(mImgList.size()-1));
                    }
                }
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private void getNextBitmap() {
        if (mPosition < mImgList.size()) {
            try {
                byte[] bytes = IOUtils.toByteArray(getResources().openRawResource(mImgList.get(mPosition)));
                mCurBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, mOptions);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPosition++;
        } else {
            mCurBitmap = null;
        }
    }

    public void startAnim() {
        if (mAnimating || isStarted) {
            return;
        }
        mAnimating = true;
        isStarted = true;
        mPosition = 0;
        invalidate();
    }

    public FrameAnimImageView setSource(List<Integer> list) {
        mImgList = list;
        return this;
    }

    public FrameAnimImageView setFrameScaleType(int scaleType) {
        mScaleType = scaleType;
        return this;
    }

    public FrameAnimImageView setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    public void release() {
        isStarted = false;
        if (mCurBitmap != null) {
            mCurBitmap.recycle();
        }
        mAnimating = false;
        setImageDrawable(null);
    }


    public boolean isAnimating(){
        return mAnimating;
    }
}
