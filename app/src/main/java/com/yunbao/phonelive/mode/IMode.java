package com.yunbao.phonelive.mode;

/**
 * Created by arui on 2018/9/27.
 */
  /*
    * @author cfw
    * 模式改变的接口规范
    * */
public interface IMode {

    public static final int ADVERTISING=1;//广告
    public static final int DEFAULT=2;//普通

    public void modelChange(int model);
}
