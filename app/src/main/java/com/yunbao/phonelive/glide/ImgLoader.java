package com.yunbao.phonelive.glide;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yunbao.phonelive.AppContext;

import java.io.File;

/**
 * Created by cxf on 2017/8/9.
 */

public class ImgLoader {
    private static RequestManager sManager;

    static {
        sManager = Glide.with(AppContext.sInstance);
    }

    public static void display(int paramInt, ImageView paramImageView)
    {
        sManager.load(paramInt).into(paramImageView);
    }

    public static void display(String url, ImageView imageView) {
        sManager.load(url).into(imageView);
    }


    public static void display(File file, ImageView imageView) {
        sManager.load(file).into(imageView);
    }

    /**
     * 显示视频封面缩略图
     */
    public static void displayVideoThumb(String videoPath, ImageView imageView) {
        sManager.load(Uri.fromFile(new File(videoPath))).into(imageView);
    }

    public static void displayBitmap(String url, final BitmapCallback bitmapCallback) {
        sManager.load(url).asBitmap().skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                if (bitmapCallback != null) {
                    bitmapCallback.callback(bitmap);
                }
            }
        });
    }

    public static void display(String url, ImageView imageView, int placeholderRes) {
        sManager.load(url).placeholder(placeholderRes).into(imageView);
    }


    public interface BitmapCallback {
        void callback(Bitmap bitmap);
    }


}
