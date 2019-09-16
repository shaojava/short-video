package com.yunbao.phonelive.bean;

import java.io.File;

/**
 * Created by cxf on 2018/7/17.
 */

public class ImageChooseBean {
    private File mImageFile;

    private boolean mChecked;

    public ImageChooseBean(File imageFile) {
        mImageFile = imageFile;
    }

    public File getImageFile() {
        return mImageFile;
    }

    public void setImageFile(File imageFile) {
        mImageFile = imageFile;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
