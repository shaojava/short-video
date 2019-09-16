package com.yunbao.phonelive.bean;

/**
 * Created by cxf on 2017/8/11.
 * 搜索结果列表数据实体类
 */

public class SearchBean {
    private String id;
    private String user_nicename;
    private String avatar;
    private String avatar_thumb;
    private int sex;
    private String signature;
    private int isattention;
    private String province;
    private String city;
    private String birthday;
    private String age;
    private String praise;
    private int fans;
    private int follows;

    public SearchBean() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getIsattention() {
        return isattention;
    }

    public void setIsattention(int isattention) {
        this.isattention = isattention;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPraise() {
        return praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }


    public UserBean castToUserBean() {
        UserBean u = new UserBean();
        u.setId(this.id);
        u.setUser_nicename(this.user_nicename);
        u.setAvatar(this.avatar);
        u.setAvatar_thumb(this.avatar_thumb);
        u.setSex(this.sex);
        u.setSignature(this.signature);
        u.setProvince(this.province);
        u.setCity(this.city);
        u.setBirthday(this.birthday);
        u.setAge(this.age);
        u.setPraise(this.praise);
        u.setFans(this.fans);
        u.setFollows(this.follows);
        return u;
    }
}
