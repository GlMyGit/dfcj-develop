package com.dfcj.videoim.listener;

import android.content.Context;

public class GoodsManager {


    Context mContext;
    static GoodsManager mDatamanager;
    GoodsChangeListener goodsChangeListener;


    public GoodsManager(Context context){
        this.mContext = context;
    }


    public static GoodsManager instance(Context context){
        if(mDatamanager == null){
            mDatamanager = new GoodsManager(context);
        }
        return mDatamanager;
    }

    /**注册监听器**/
    public void setGoodsChangeListener(GoodsChangeListener Listener){
        this.goodsChangeListener = Listener;
    }

    /**调用监听器中的方法**/
    public void goodsChange(String data){
        goodsChangeListener.toGoodsDetailChange(data);
    }



}
