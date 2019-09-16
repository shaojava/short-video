package com.yunbao.phonelive.custom;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.bean.CommentBean;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.FaceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cxf on 2018/7/12.
 */

public class TextRender {

    private static final String REGEX = "\\[([\u4e00-\u9fa5\\w])+\\]";
    private static final Pattern PATTERN;
    private static final int FACE_WIDTH;

    static {
        PATTERN = Pattern.compile(REGEX);
        FACE_WIDTH = DpUtil.dp2px(24);
    }

    public static CharSequence renderChatMessage(String content) {
        Matcher matcher = PATTERN.matcher(content);
        boolean hasFace = false;
        SpannableStringBuilder builder = null;
        while (matcher.find()) {
            // 获取匹配到的具体字符
            String key = matcher.group();
            Integer imgRes = FaceUtil.getFaceImageRes(key);
            if (imgRes != null && imgRes != 0) {
                hasFace = true;
                if (builder == null) {
                    builder = new SpannableStringBuilder(content);
                }
                Drawable faceDrawable = ContextCompat.getDrawable(AppContext.sInstance, imgRes);
                faceDrawable.setBounds(0, 0, FACE_WIDTH, FACE_WIDTH);
                ImageSpan imageSpan = new ImageSpan(faceDrawable, ImageSpan.ALIGN_BOTTOM);
                // 匹配字符串的开始位置
                int startIndex = matcher.start();
                builder.setSpan(imageSpan, startIndex, startIndex + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (hasFace) {
            return builder;
        } else {
            return content;
        }
    }

    public static CharSequence getFaceImageSpan(String content, int imgRes) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Drawable faceDrawable = ContextCompat.getDrawable(AppContext.sInstance, imgRes);
        faceDrawable.setBounds(0, 0, FACE_WIDTH, FACE_WIDTH);
        ImageSpan imageSpan = new ImageSpan(faceDrawable, ImageSpan.ALIGN_BOTTOM);
        builder.setSpan(imageSpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }


    public static CharSequence renderComment(CommentBean bean) {
        String atInfo = bean.getAt_info();
        if (TextUtils.isEmpty(atInfo)) {
            return bean.getContent();
        }
        try {
            String content = bean.getContent();
            JSONArray jsonArray = JSONArray.parseArray(atInfo);
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            for (int i = 0, size = jsonArray.size(); i < size; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String atContent = "@" + obj.getString("name");
                int index = content.indexOf(atContent);
                if (index >= 0) {
                    builder.setSpan(new ForegroundColorSpan(0xfff3e835), index, index + atContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return builder;
        } catch (Exception e) {
            return bean.getContent();
        }
    }

    public static CharSequence renderComment2(String reply, String toName, CommentBean bean) {
        String content = reply + toName + bean.getContent();
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        int index1 = reply.length();
        builder.setSpan(new ForegroundColorSpan(0xff45375d), 0, index1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int index2 = index1 + toName.length();
        builder.setSpan(new ForegroundColorSpan(0xff969696), index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String atInfo = bean.getAt_info();
        if (!TextUtils.isEmpty(atInfo)) {
            try {
                JSONArray jsonArray = JSONArray.parseArray(atInfo);
                for (int i = 0, size = jsonArray.size(); i < size; i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String atContent = "@" + obj.getString("name");
                    int index = content.indexOf(atContent);
                    if (index >= 0) {
                        builder.setSpan(new ForegroundColorSpan(0xfff3e835), index, index + atContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } catch (Exception e) {

            }
        }
        return builder;
    }

    public static CharSequence renderComment3(String toName, String content, String atInfo) {
        content = toName + content;
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        builder.setSpan(new ForegroundColorSpan(0xff969696), 0, toName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(atInfo)) {
            try {
                JSONArray jsonArray = JSONArray.parseArray(atInfo);
                for (int i = 0, size = jsonArray.size(); i < size; i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String atContent = "@" + obj.getString("name");
                    int index = content.indexOf(atContent);
                    if (index >= 0) {
                        builder.setSpan(new ForegroundColorSpan(0xfff3e835), index, index + atContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } catch (Exception e) {

            }
        }
        return builder;
    }


}
