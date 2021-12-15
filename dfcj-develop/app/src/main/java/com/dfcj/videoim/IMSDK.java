package com.dfcj.videoim;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ActivityUtils;
import com.dfcj.videoim.appconfig.AppApplicationMVVM;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.entity.ShopMsgBody;
import com.dfcj.videoim.listener.ShopClickListener;
import com.dfcj.videoim.listener.ShopManager;
import com.dfcj.videoim.ui.video.VideoCallingActivity;
import com.dfcj.videoim.util.MyDialogUtil;
import com.dfcj.videoim.util.etoast.etoast2.EToastUtils;
import com.dfcj.videoim.util.other.GsonUtil;
import com.dfcj.videoim.util.other.LogUtils;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wzq.mvvmsmart.base.BaseApplicationMVVM;
import com.wzq.mvvmsmart.utils.KLog;

public class IMSDK {
    private static IMSDK imsdk;

    public static int CHAT_TYPE = 0;
    public static int CHAT_TYPE_VIDEO = 1;

    public static void initIMSDK(Application application) {
        BaseApplicationMVVM.setApplication(application);
        KLog.init(BuildConfig.DEBUG);
        AppApplicationMVVM.initCrash();
        LiveEventBus.config().supportBroadcast(application).lifecycleObserverAlwaysActive(true);
        LogUtils.logInit(BuildConfig.My_LOG_DEBUG);
        SharedPrefsUtils prefsUtils = new SharedPrefsUtils(application);
        application.registerActivityLifecycleCallbacks(EToastUtils.init());
        ARouter.init(application);
        MultiDex.install(application);
    }

    public static IMSDK getImSdk() {
        if (imsdk == null) {
            imsdk = new IMSDK();
        }
        return imsdk;
    }

    /**
     * 初始化
     * @param token 用户token
     * @return IMSDK
     */
    public void setToken(String token) {
        SharedPrefsUtils.putValue(AppConstant.USERTOKEN, token);
        //
        SharedPrefsUtils.putValue(AppConstant.CHAT_TYPE, CHAT_TYPE);
    }

    /**
     * 设置商品数据
     *
     * @param goodsData 商品数据对象
     */
    public void setGoodsData(ShopMsgBody goodsData) {
        SharedPrefsUtils.putValue(AppConstant.SHOP_MSG_BODY_DATA, GsonUtil.newGson22().toJson(goodsData));
    }

    /**
     * 设置会话类型
     *
     * @param chatType 聊天类型  1会话  2视频
     */
    public void setChatType(int chatType) {
        SharedPrefsUtils.putValue(AppConstant.CHAT_TYPE, chatType);
    }

    /**
     * 打开聊天界面
     */
    public void show() {
        ActivityUtils.startActivity(new Intent(ActivityUtils.getTopActivity(), MainActivity.class));
    }

    /**
     * 关闭聊天界面
     */
    public void closeIMActivity() {
        ActivityUtils.finishActivity(MainActivity.class);
    }

    /**
     * 关闭视频界面
     */
    public void closeVideoActivity() {
        ActivityUtils.finishActivity(VideoCallingActivity.class);
    }

    /**
     * 设置卡片点击监听
     */
    public void setShopClickListener(ShopClickListener shopClickListener) {
        ShopManager.instance().setGoodsChangeListener(shopClickListener);
    }


    /**
     * 打开im  选择会话还是选择视频
     */
    public void showSelImDialog(AppCompatActivity appCompatActivity) {
        MyDialogUtil.showSelectImDialog(appCompatActivity);
    }


    /**
     * 打开im  选择会话还是选择视频
     */
    public void setUserInfo(String userName,String userImg) {
       SharedPrefsUtils.putValue(AppConstant.MyUserIcon,userImg);
       SharedPrefsUtils.putValue(AppConstant.MyUserName,userName);
    }

}
