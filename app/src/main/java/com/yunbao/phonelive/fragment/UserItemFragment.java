package com.yunbao.phonelive.fragment;

import android.os.Bundle;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.utils.VideoStorge;

/**
 * Created by cxf on 2018/6/10.
 */

public abstract class UserItemFragment extends AbsFragment {

    protected boolean mFirst = true;//是否是第一次加载
    protected String mHashCode;
    protected String mUid;//对方的uid
    protected boolean mIsMainUserCenter;//是否是MainFragment 里面的个人中心

    public abstract void loadData();

    @Override
    protected void main() {
        if (mHashCode == null) {
            mHashCode = String.valueOf(this.hashCode());
        }
        Bundle bundle = getArguments();
        mIsMainUserCenter = bundle.getBoolean(Constants.IS_MAIN_USER_CENTER, false);
        if (mIsMainUserCenter && AppConfig.getInstance().isLogin()) {
            mUid = AppConfig.getInstance().getUid();
        } else {
            mUid = bundle.getString(Constants.UID);
        }
    }

    @Override
    public void onDestroy() {
        VideoStorge.getInstance().remove(mHashCode);
        super.onDestroy();
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public void setFirst(boolean first) {
        mFirst = first;
    }

    public abstract void onLoginUserChanged(String uid);


    public abstract void clearData();

}
