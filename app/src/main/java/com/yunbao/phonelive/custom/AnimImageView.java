package com.yunbao.phonelive.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yunbao.phonelive.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by cxf on 2017/9/23.
 */

public class AnimImageView extends ImageView {

    private Paint mPaint;
    private List<Integer> mImgList;
    private BitmapFactory.Options mOptions;
    private int mPosition;
    private Bitmap mCurBitmap;
    private int mWidth;
    private int mHeight;
    private int mInterval;
    private Rect mSrc;
    private Rect mDst;
    private boolean mAnimating;
    private int mSrcId;

    public AnimImageView(Context context) {
        this(context, null);
    }

    public AnimImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AnimImageView);
        mSrcId = ta.getResourceId(R.styleable.AnimImageView_srcId, 0);
        mInterval = ta.getInteger(R.styleable.AnimImageView_interval, 100);
        ta.recycle();
        mOptions = new BitmapFactory.Options();
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inSampleSize = 1;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mSrc = new Rect();
        mDst = new Rect();
    }


    public void setImgList(List<Integer> imgList) {
        mImgList = imgList;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mWidth == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
            mDst.left = 0;
            mDst.right = mWidth;
            mDst.top = 0;
            mDst.bottom = mHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAnimating) {
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
                canvas.drawBitmap(mCurBitmap, mSrc, mDst, mPaint);
                postInvalidateDelayed(mInterval);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private void getNextBitmap() {
        if (mImgList != null && mPosition < mImgList.size()) {
            try {
                byte[] bytes = IOUtils.toByteArray(getResources().openRawResource(mImgList.get(mPosition)));
                mCurBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, mOptions);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPosition++;
            if (mPosition == mImgList.size()) {
                mPosition = 0;
            }
        } else {
            mCurBitmap = null;
        }
    }

    public void startAnim() {
        if (mAnimating) {
            return;
        }
        mAnimating = true;
        mPosition = 0;
        invalidate();
    }

    public void stopAnim() {
        if (mAnimating) {
            mAnimating = false;
            if (mCurBitmap != null) {
                mCurBitmap.recycle();
            }
            if (mSrcId != 0) {
                setImageResource(mSrcId);
            }
        }else{
            if (mSrcId != 0) {
                setImageResource(mSrcId);
            }
        }
    }


    public void release() {
        if (mAnimating) {
            mAnimating = false;
            if (mCurBitmap != null) {
                mCurBitmap.recycle();
            }
            setImageDrawable(null);
        }
    }

    public boolean isAnimating() {
        return mAnimating;
    }

    @Override
    protected void onDetachedFromWindow() {
        release();
        super.onDetachedFromWindow();
    }
}
