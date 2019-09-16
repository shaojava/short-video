package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TieZhiBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DownloadUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import java.io.File;
import java.util.List;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.sdk.common.TiUtils;

/**
 * Created by cxf on 2018/6/23.
 * 萌颜 贴纸
 */

public class TieZhiAdapter extends RecyclerView.Adapter<TieZhiAdapter.Vh> {

    private Context mContext;
    private List<TieZhiBean> mList;
    private LayoutInflater mInflater;
    private Drawable mCheckDrawable;
    private int mCheckedPosition;
    private OnItemClickListener<TieZhiBean> mOnItemClickListener;
    private static final int MAX_DOWNLOAD_TASK = 3;
    private SparseArray<String> mLoadingTaskMap;
    private DownloadUtil mDownloadUtil;


    public TieZhiAdapter(Context context) {
        mContext = context;
        mList = TieZhiBean.getTieZhiList();
        mInflater = LayoutInflater.from(context);
        mCheckDrawable = ContextCompat.getDrawable(context, R.drawable.bg_item_tiezhi);
        mLoadingTaskMap = new SparseArray<>();
        mDownloadUtil = new DownloadUtil();
    }


    public void setOnItemClickListener(OnItemClickListener<TieZhiBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_tiezhi, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        View mLoading;
        View mDownLoad;
        TieZhiBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            mLoading = itemView.findViewById(R.id.loading);
            mDownLoad = itemView.findViewById(R.id.download);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mBean.isDownloaded()) {
                        if (mDownloadUtil != null && mLoadingTaskMap.size() < MAX_DOWNLOAD_TASK &&
                                mLoadingTaskMap.indexOfKey(mPosition) < 0) {//不存在这个key
                            String name = mBean.getName();
                            mLoadingTaskMap.put(mPosition, name);
                            L.e("贴纸未下载------>开始下载");
                            mBean.setDownloading(true);
                            notifyItemChanged(mPosition, "payload");
                            mDownloadUtil.download(name, AppConfig.VIDEO_TIE_ZHI_PATH, name, mBean.getUrl(), new DownloadUtil.Callback() {
                                @Override
                                public void onSuccess(File file) {
                                    if (file != null) {
                                        File targetDir = new File(TiSDK.getStickerPath(mContext));
                                        try {
                                            //解压到贴纸目录
                                            TiUtils.unzip(file, targetDir);
                                            mBean.setDownloadSuccess(mContext);
                                        } catch (Exception e) {
                                            ToastUtil.show(WordUtil.getString(R.string.tiezhi_download_failed));
                                            mBean.setDownloading(false);
                                        } finally {
                                            file.delete();
                                            notifyItemChanged(mPosition, "payload");
                                            mLoadingTaskMap.remove(mPosition);
                                        }
                                    }
                                }

                                @Override
                                public void onProgress(int progress) {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    ToastUtil.show(WordUtil.getString(R.string.tiezhi_download_failed));
                                    mBean.setDownloading(false);
                                    notifyItemChanged(mPosition, "payload");
                                    mLoadingTaskMap.remove(mPosition);
                                }
                            });
                        }
                    } else {
                        if (mCheckedPosition != mPosition) {
                            mList.get(mCheckedPosition).setChecked(false);
                            mList.get(mPosition).setChecked(true);
                            notifyItemChanged(mCheckedPosition, "payload");
                            notifyItemChanged(mPosition, "payload");
                            mCheckedPosition = mPosition;
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onItemClick(mBean, mPosition);
                            }
                        }
                    }
                }
            });
        }

        void setData(TieZhiBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                if (position == 0) {
                    mImg.setImageResource(R.mipmap.icon_tiezhi_none);
                } else {
                    ImgLoader.display(bean.getThumb(), mImg);
                }
            }
            if (bean.isDownloading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isDownloaded()) {
                if (mDownLoad.getVisibility() == View.VISIBLE) {
                    mDownLoad.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mDownLoad.getVisibility() != View.VISIBLE) {
                    mDownLoad.setVisibility(View.VISIBLE);
                }
            }
            if (bean.isChecked()) {
                itemView.setBackground(mCheckDrawable);
            } else {
                itemView.setBackground(null);
            }
        }
    }


    public void clear() {
        if (mList != null) {
            mList.clear();
        }
        if (mLoadingTaskMap != null) {
            for (int i = 0, size = mLoadingTaskMap.size(); i < size; i++) {
                String tag = mLoadingTaskMap.valueAt(i);
                HttpUtil.cancel(tag);
            }
        }
    }
}
