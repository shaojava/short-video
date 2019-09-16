package com.yunbao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cxf on 2017/10/25.
 */

public class VideoBean implements Parcelable {
    private String id;
    private String uid;
    private String title;
    private String thumb;
    private String thumb_s;
    private String href;
    private String likes;
    private String views;
    private String comments;
    private String steps;
    private String shares;
    private String addtime;
    private String lat;
    private String lng;
    private String city;
    private String isdel;
    private UserBean userinfo;
    private String datetime;
    private int islike;
    private int isattent;
    private String distance;
    private int isstep;
    private int status;
    private int music_id;
    private MusicBean musicinfo;


    /* @author cfw
    * */
    private int advertising;
    private String url;

    private int is_ad;
    private String ad_url;
    private String ad_desc;

    public int getIsstep() {
        return isstep;
    }

    public void setIsstep(int isstep) {
        this.isstep = isstep;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getThumb_s() {
        return thumb_s;
    }

    public void setThumb_s(String thumb_s) {
        this.thumb_s = thumb_s;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsdel() {
        return isdel;
    }

    public void setIsdel(String isdel) {
        this.isdel = isdel;
    }

    public UserBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserBean userinfo) {
        this.userinfo = userinfo;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getIslike() {
        return islike;
    }

    public void setIslike(int islike) {
        this.islike = islike;
    }

    public int getIsattent() {
        return isattent;
    }

    public void setIsattent(int isattent) {
        this.isattent = isattent;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMusic_id() {
        return music_id;
    }

    public void setMusic_id(int music_id) {
        this.music_id = music_id;
    }

    public MusicBean getMusicinfo() {
        return musicinfo;
    }

    public void setMusicinfo(MusicBean musicinfo) {
        this.musicinfo = musicinfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.title);
        dest.writeString(this.thumb);
        dest.writeString(this.thumb_s);
        dest.writeString(this.href);
        dest.writeString(this.likes);
        dest.writeString(this.views);
        dest.writeString(this.comments);
        dest.writeString(this.steps);
        dest.writeString(this.shares);
        dest.writeString(this.addtime);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.city);
        dest.writeString(this.isdel);
        dest.writeString(this.datetime);
        dest.writeInt(this.islike);
        dest.writeInt(this.isattent);
        dest.writeString(this.distance);
        dest.writeInt(this.isstep);
        dest.writeParcelable(this.userinfo, flags);
        dest.writeInt(this.status);
        dest.writeInt(this.music_id);
        dest.writeInt(this.is_ad);
        dest.writeInt(this.advertising);
        dest.writeString(this.ad_url);
        dest.writeString(this.ad_desc);
        dest.writeString(this.ad_url);
        dest.writeString(this.url);
    }

    public VideoBean() {

    }

    public int getIs_ad() {
        return is_ad;
    }

    public void setIs_ad(int is_ad) {
        this.is_ad = is_ad;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getAd_desc() {
        return ad_desc;
    }

    public void setAd_desc(String ad_desc) {
        this.ad_desc = ad_desc;
    }

    public VideoBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.title = in.readString();
        this.thumb = in.readString();
        this.thumb_s = in.readString();
        this.href = in.readString();
        this.likes = in.readString();
        this.views = in.readString();
        this.comments = in.readString();
        this.steps = in.readString();
        this.shares = in.readString();
        this.addtime = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.city = in.readString();
        this.isdel = in.readString();
        this.datetime = in.readString();
        this.islike = in.readInt();
        this.isattent = in.readInt();
        this.distance = in.readString();
        this.isstep = in.readInt();
        this.userinfo = in.readParcelable(UserBean.class.getClassLoader());
        this.status = in.readInt();
        this.music_id = in.readInt();
        this.musicinfo = in.readParcelable(MusicBean.class.getClassLoader());

        this.is_ad=in.readInt();
        this.advertising=in.readInt();
        this.is_ad=in.readInt();
        this.ad_url=in.readString();
        this.ad_desc=in.readString();
        this.url=in.readString();

    }


    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }

        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }
    };

    public int getAdvertising() {
        return advertising;
    }

    public void setAdvertising(int advertising) {
        this.advertising = advertising;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void dto(){
        advertising=is_ad;
        url=ad_url;
    }
}
