package com.yunbao.phonelive.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.yunbao.phonelive.utils.L;


public class EllipsizedTextView extends android.support.v7.widget.AppCompatTextView {

    private int mMaxLines;

    public EllipsizedTextView(Context context) {
        this(context, null);
    }

    public EllipsizedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EllipsizedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(attrs, new int[]{
                android.R.attr.maxLines
        }, defStyle, 0);

        mMaxLines = a.getInteger(0, 1);
        a.recycle();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {

        if ((text!=null&&text.length()>0)&&(mMaxLines != Integer.MAX_VALUE && mMaxLines > 1) && getWidth() != 0) {
            CharSequence charSequence=text.subSequence(text.length()-1,text.length());
            CharSequence charSequence2=text.subSequence(0,text.length()-1);
            int width=getWidth();
            StaticLayout layout = new StaticLayout(charSequence2, getPaint(), width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            //需要显示的文字加上"..."的总宽度
            float textAndEllipsizeWidth = 0;  
            for (int i = 0; i < mMaxLines; i++) {
                //此处用getWidth()计算的话会有误差，所以用getLineWidth()
                textAndEllipsizeWidth += layout.getLineWidth(i);
            }

            if(layout.getLineWidth(1)==layout.getLineWidth(0)&&layout.getLineWidth(0)>2){
                text = TextUtils.ellipsize(text, getPaint(), 2*layout.getLineWidth(1)-100, TextUtils.TruncateAt.END);
                text=TextUtils.concat(text,charSequence);
                L.e("text==L.e(\"text==\"+text);\n" +
                        "            }"+text);
            }

        }
        super.setText(text, type);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (width > 0 && oldWidth != width) {
            setText(getText());
        }
    }

    @Override
    public int getMaxLines() {
        return mMaxLines;
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        mMaxLines = maxLines;
    }
}
