package com.dfcj.videoim.listener;

import android.content.Context;

import com.dfcj.videoim.entity.ShopMsgBody;

public class ShopManager {
    static ShopManager shopManager;
    ShopClickListener shopClickListener;


    public ShopManager() {
    }


    public static ShopManager instance() {
        if (shopManager == null) {
            shopManager = new ShopManager();
        }
        return shopManager;
    }

    /**
     * 注册监听器
     **/
    public void setGoodsChangeListener(ShopClickListener Listener) {
        this.shopClickListener = Listener;
    }

    /**
     * 调用监听器中的方法
     **/
    public void goodsChange(ShopMsgBody body) {
        shopClickListener.onClick(body);
    }


}
