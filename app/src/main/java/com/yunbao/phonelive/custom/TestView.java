package com.yunbao.phonelive.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by cxf on 2018/7/31.
 */

public class TestView extends View {

    private Paint mPaint;
    private Paint mPaint2;
    private int mWidth;
    private int mHeight;
    private float mScale;

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScale = context.getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(0xffff0000);

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setDither(true);
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setStrokeWidth(1);
        mPaint2.setColor(0xff000000);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        path.lineTo(0, 0);
        path.moveTo(dp2px(65), dp2px(64));
        path.rCubicTo(-dp2px(60), 0, -dp2px(30), -dp2px(50), -dp2px(58), -dp2px(58));
        canvas.drawPath(path, mPaint);

        Path path2 = new Path();
        path2.lineTo(0, 0);
        path2.moveTo(dp2px(65), dp2px(64));
        path2.rCubicTo(-dp2px(60), 0, -dp2px(40), -dp2px(50), -dp2px(30), -dp2px(58));
        canvas.drawPath(path2, mPaint2);
    }

    private int dp2px(int dpVal) {
        return (int) (mScale * dpVal + 0.5f);
    }

}
