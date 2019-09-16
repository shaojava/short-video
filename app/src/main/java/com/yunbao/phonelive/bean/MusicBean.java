package com.yunbao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cxf on 2018/6/20.
 */

public class MusicBean implements Parcelable {
    private int id;
    private String title;
    private String author;
    private String img_url;
    private String length;
    private String file_url;
    private String use_nums;
    private String localPath;//本地存储的路径
    private int iscollect;//是否收藏
    private boolean expand;

    public MusicBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getUse_nums() {
        return use_nums;
    }

    public void setUse_nums(String use_nums) {
        this.use_nums = use_nums;
    }


    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public int getIscollect() {
        return iscollect;
    }

    public void setIscollect(int iscollect) {
        this.iscollect = iscollect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public MusicBean(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.author = in.readString();
        this.img_url = in.readString();
        this.length = in.readString();
        this.file_url = in.readString();
        this.use_nums = in.readString();
        this.localPath = in.readString();
        this.iscollect = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.img_url);
        dest.writeString(this.length);
        dest.writeString(this.file_url);
        dest.writeString(this.use_nums);
        dest.writeString(this.localPath);
        dest.writeInt(this.iscollect);
    }

    public static final Creator<MusicBean> CREATOR = new Creator<MusicBean>() {
        @Override
        public MusicBean[] newArray(int size) {
            return new MusicBean[size];
        }

        @Override
        public MusicBean createFromParcel(Parcel in) {
            return new MusicBean(in);
        }
    };

}
