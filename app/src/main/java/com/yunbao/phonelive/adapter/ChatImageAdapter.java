package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunbao.phonelive.glide.ImgLoader;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxf on 2018/8/1.
 */

public class ChatImageAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mFilePathList;
    private LinkedList<ImageView> mViewList;
    private View.OnClickListener mOnClickListener;

    public ChatImageAdapter(Context context, List<String> filePathList, View.OnClickListener onClickListener) {
        mContext = context;
        mFilePathList = filePathList;
        mViewList = new LinkedList<>();
        mOnClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return mFilePathList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = null;
        if (mViewList.size() > 0) {
            imageView = mViewList.getFirst();
            mViewList.removeFirst();
        } else {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setOnClickListener(mOnClickListener);
        }
        ImgLoader.display(mFilePathList.get(position), imageView);
        container.addView(imageView);
        return imageView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object != null) {
            ImageView imageView = (ImageView) object;
            imageView.setImageDrawable(null);
            container.removeView(imageView);
            mViewList.addLast(imageView);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public void refreshList(List<String> filePathList) {
        mFilePathList = filePathList;
        notifyDataSetChanged();
    }
}
