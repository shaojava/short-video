package com.yunbao.phonelive.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2018/6/12.
 */

public class ImageTextView extends LinearLayout {

    private Context mContext;
    private ImageView mImageView;
    private TextView mTextView;
    private int mImageSrc;
    private int mImageWidth;
    private String mText;
    private int mTextColor;
    private int mTextHeight;
    private float mTextSize;
    private int mTextMargin;
    private int mTextMarginLeft;
    private int mTextMarginRight;
    private int mTextMarginTop;
    private int mTextMarginBottom;
    private int mImageMargin;
    private int mImageMarginLeft;
    private int mImageMarginRight;
    private int mImageMarginTop;
    private int mImageMarginBottom;
    private int mImagePadding;
    private boolean mChecked;
    private int mCheckedImageSrc;
    private int mCheckedTextColor;

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView);
        mImageSrc = ta.getResourceId(R.styleable.ImageTextView_imageSrc, 0);
        mImageWidth = (int) ta.getDimension(R.styleable.ImageTextView_imageWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        mText = ta.getString(R.styleable.ImageTextView_text1);
        mTextColor = ta.getColor(R.styleable.ImageTextView_textColor1, 0xff000000);
        mTextHeight = (int) ta.getDimension(R.styleable.ImageTextView_textHeight, 0);
        mTextSize = ta.getDimension(R.styleable.ImageTextView_textSize1, 0);
        mTextMargin = (int) ta.getDimension(R.styleable.ImageTextView_textMargin, 0);
        mTextMarginLeft = (int) ta.getDimension(R.styleable.ImageTextView_textMarginLeft, mTextMargin);
        mTextMarginRight = (int) ta.getDimension(R.styleable.ImageTextView_textMarginRight, mTextMargin);
        mTextMarginTop = (int) ta.getDimension(R.styleable.ImageTextView_textMarginTop, mTextMargin);
        mTextMarginBottom = (int) ta.getDimension(R.styleable.ImageTextView_textMarginBottom, mTextMargin);
        mImageMargin = (int) ta.getDimension(R.styleable.ImageTextView_imageMargin, 0);
        mImageMarginLeft = (int) ta.getDimension(R.styleable.ImageTextView_imageMarginLeft, mImageMargin);
        mImageMarginRight = (int) ta.getDimension(R.styleable.ImageTextView_imageMarginRight, mImageMargin);
        mImageMarginTop = (int) ta.getDimension(R.styleable.ImageTextView_imageMarginTop, mImageMargin);
        mImageMarginBottom = (int) ta.getDimension(R.styleable.ImageTextView_imageMarginBottom, mImageMargin);
        mImagePadding = (int) ta.getDimension(R.styleable.ImageTextView_imagePadding, 0);
        mChecked = ta.getBoolean(R.styleable.ImageTextView_isChecked, false);
        mCheckedImageSrc = ta.getResourceId(R.styleable.ImageTextView_checkedImageSrc, mImageSrc);
        mCheckedTextColor = ta.getResourceId(R.styleable.ImageTextView_checkedTextColor, mTextColor);
        ta.recycle();
        setOrientation(VERTICAL);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImageView = new SquareImageView(mContext);
        LinearLayout.LayoutParams params1 = new LayoutParams(mImageWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.setMargins(mImageMarginLeft, mImageMarginTop, mImageMarginRight, mImageMarginBottom);
        params1.gravity = Gravity.CENTER_HORIZONTAL;
        mImageView.setLayoutParams(params1);
        mImageView.setPadding(mImagePadding, mImagePadding, mImagePadding, mImagePadding);
        addView(mImageView);
        mTextView = new TextView(mContext);
        LinearLayout.LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTextHeight);
        params2.setMargins(mTextMarginLeft, mTextMarginTop, mTextMarginRight, mTextMarginBottom);
        mTextView.setLayoutParams(params2);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setSingleLine(true);
        if (mTextHeight > 0) {
            mTextView.setText(mText);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        }
        onCheckChanged(mChecked);
        addView(mTextView);
    }


    public void setImageSrc(int resId) {
        mImageSrc = resId;
    }

    public void setImageResource(int resId) {
        mImageSrc = resId;
        mImageView.setImageResource(mImageSrc);
    }

    public void setText(String text) {
        if (mTextView != null) {
            mTextView.setText(text);
        }
    }

    public void setCheckedImageSrc(int resId) {
        mCheckedImageSrc = resId;
    }

    public void setCheckedTextColor(int color) {
        mCheckedTextColor = color;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        onCheckChanged(checked);
    }

    public boolean isChecked() {
        return mChecked;
    }

    private void onCheckChanged(boolean checked) {
        if (mImageView != null && mTextView != null) {
            if (checked) {
                if (mCheckedImageSrc != 0) {
                    mImageView.setImageResource(mCheckedImageSrc);
                }
                if (mTextHeight > 0) {
                    mTextView.setTextColor(mCheckedTextColor);
                }

            } else {
                if (mImageSrc != 0) {
                    mImageView.setImageResource(mImageSrc);
                }
                if (mTextHeight > 0) {
                    mTextView.setTextColor(mTextColor);
                }
            }
        }

    }

}
