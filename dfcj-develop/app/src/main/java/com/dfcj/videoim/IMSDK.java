package com.dfcj.videoim;

import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.entity.ShopMsgBody;
import com.dfcj.videoim.listener.ShopClickListener;
import com.dfcj.videoim.listener.ShopManager;
import com.dfcj.videoim.util.other.GsonUtil;
import com.dfcj.videoim.util.other.SharedPrefsUtils;

public class IMSDK {
    private static IMSDK imsdk;

    public static int CHAT_TYPE = 0;
    public static int CHAT_TYPE_VIDEO = 1;

    public static IMSDK getImsdk() {
        if (imsdk == null) {
            imsdk = new IMSDK();
        }
        return imsdk;
    }

    /**
     * 初始化
     *
     * @param token 用户token
     * @return IMSDK
     */
    public IMSDK setToken(String token) {
        SharedPrefsUtils.putValue(AppConstant.USERTOKEN, token);
        SharedPrefsUtils.putValue(AppConstant.SHOP_MSG_BODY_DATA, "");
        SharedPrefsUtils.putValue(AppConstant.CHAT_TYPE, CHAT_TYPE);
        return imsdk;
    }

    /**
     * 设置商品数据
     *
     * @param shopMsgBody 商品数据对象
     */
    public IMSDK setShopData(ShopMsgBody shopMsgBody) {
        SharedPrefsUtils.putValue(AppConstant.SHOP_MSG_BODY_DATA, GsonUtil.newGson22().toJson(shopMsgBody));
        return imsdk;
    }

    /**
     * 设置会话类型
     *
     * @param chatType 聊天类型
     */
    public IMSDK setChatType(int chatType) {
        SharedPrefsUtils.putValue(AppConstant.CHAT_TYPE, chatType);
        return imsdk;
    }

    /**
     * 打开聊天界面
     */
    public IMSDK show() {
        ActivityUtils.startActivity(new Intent(ActivityUtils.getTopActivity(), MainActivity.class));
        return imsdk;
    }

    /**
     * 关闭聊天界面
     */
    public IMSDK close() {
        ActivityUtils.finishActivity(MainActivity.class);
        return imsdk;
    }

    /**
     * 设置卡片点击监听
     */
    public IMSDK setShopClickListener(ShopClickListener shopClickListener) {
        ShopManager.instance().setGoodsChangeListener(shopClickListener);
        return imsdk;
    }
}
