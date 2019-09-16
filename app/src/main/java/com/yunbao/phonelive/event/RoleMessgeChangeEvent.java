package com.yunbao.phonelive.event;

/**
 * Created by chenfangwei on 2018/10/19.
 */

public class RoleMessgeChangeEvent {
  public static int OUT_LOGIN=1;
  private int change_type;

    public RoleMessgeChangeEvent(int change_type) {
        this.change_type = change_type;
    }


    public int getChangeType() {
        return change_type;
    }



}
