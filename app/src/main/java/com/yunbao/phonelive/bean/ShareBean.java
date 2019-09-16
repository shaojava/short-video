package com.yunbao.phonelive.bean;

/**
 * Created by cxf on 2018/6/12.
 */

public class ShareBean {
    private String type;
    private int checkedIcon;
    private int unCheckedIcon;
    private String text;
    private boolean checked;

    public ShareBean(String type, int checkedIcon, int unCheckedIcon, String text) {
        this.type = type;
        this.checkedIcon = checkedIcon;
        this.unCheckedIcon = unCheckedIcon;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCheckedIcon() {
        return checkedIcon;
    }

    public void setCheckedIcon(int checkedIcon) {
        this.checkedIcon = checkedIcon;
    }

    public int getUnCheckedIcon() {
        return unCheckedIcon;
    }

    public void setUnCheckedIcon(int unCheckedIcon) {
        this.unCheckedIcon = unCheckedIcon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
