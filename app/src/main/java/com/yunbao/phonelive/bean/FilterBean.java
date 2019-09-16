package com.yunbao.phonelive.bean;

import android.graphics.Bitmap;

/**
 * Created by cxf on 2018/6/22.
 */

public class FilterBean {

    public static final int FILTER_ORIGINAL = 0;
    public static final int FILTER_LANG_MAN = 1;
    public static final int FILTER_QING_XIN = 2;
    public static final int FILTER_WEI_MEI = 3;
    public static final int FILTER_FEN_NEN = 4;
    public static final int FILTER_HUAI_JIU = 5;
    public static final int FILTER_QING_LIANG = 6;
    public static final int FILTER_LANG_DIAO = 7;
    public static final int FILTER_RI_XI = 8;

    private int id;
    private int imgSrc;
    private int filterBitmapSrc;
    private boolean checked;
    private Bitmap bitmap;

    public FilterBean() {
    }

    public FilterBean(int id, int imgSrc, int filterBitmapSrc) {
        this.id = id;
        this.imgSrc = imgSrc;
        this.filterBitmapSrc = filterBitmapSrc;
    }

    public FilterBean(int id, int imgSrc, int filterBitmapSrc, boolean checked) {
        this.id = id;
        this.imgSrc = imgSrc;
        this.filterBitmapSrc = filterBitmapSrc;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFilterBitmapSrc() {
        return filterBitmapSrc;
    }

    public void setFilterBitmapSrc(int filterBitmapSrc) {
        this.filterBitmapSrc = filterBitmapSrc;
    }

    public int getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(int imgSrc) {
        this.imgSrc = imgSrc;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
