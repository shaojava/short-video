package com.yunbao.phonelive.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2018/7/10.
 */

public class MyImageView2 extends ImageView {

    private int mImageRes;

    public MyImageView2(Context context) {
        this(context, null);
    }

    public MyImageView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyImageView2);
        mImageRes = ta.getResourceId(R.styleable.MyImageView2_imageRes, 0);
        ta.recycle();
    }


    @Override
    public void setImageResource(int resId) {
        if (mImageRes != resId) {
            mImageRes = resId;
            super.setImageResource(resId);
        }
    }
}
