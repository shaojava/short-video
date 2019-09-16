package com.yunbao.phonelive.utils;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.yunbao.phonelive.AppContext;

/**
 * Created by chenfangwei on 2018/10/11.
 */

public class TextUtil {

 public static void setImageSpan(TextView textView,int resoure,int textSize,int start,int end,String text){
     Drawable drawable=AppContext.sInstance.getResources().getDrawable(resoure);
     drawableSetBound(drawable,textSize);
     ImageSpan imgSpan;
     imgSpan = new ImageSpan(drawable);


     SpannableString spannableString = new SpannableString(text);
     spannableString.setSpan(imgSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

     textView.setText(spannableString);
 }
    public static void setImageSpanEnd(TextView textView,int textSize,int resoure,String text){
        text=text.trim()+"   ";
        int length=text.length();
        setImageSpan(textView,resoure,textSize,length-1,length,text);
 }

    private static void drawableSetBound(Drawable drawable, int inteagerfixedSize) {
        int height=drawable.getIntrinsicHeight();
        int width=drawable.getIntrinsicWidth();
        int dimen=ResourceUtil.dp2px(inteagerfixedSize,AppContext.sInstance);

        int widthBound=(int)((float)dimen*width/(float)height);
        drawable.setBounds(0, 0, widthBound,
                dimen);
    }
}
