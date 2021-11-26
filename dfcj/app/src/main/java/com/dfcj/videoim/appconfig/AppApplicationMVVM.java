package com.dfcj.videoim.appconfig;


import android.content.Context;

import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.dfcj.videoim.BuildConfig;
import com.jeremyliao.liveeventbus.LiveEventBus;

import com.dfcj.videoim.MainActivity;
import com.dfcj.videoim.R;
import com.dfcj.videoim.util.etoast.etoast2.EToastUtils;
import com.dfcj.videoim.util.other.LogUtils;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.wzq.mvvmsmart.base.BaseApplicationMVVM;
import com.wzq.mvvmsmart.crash.CaocConfig;
import com.wzq.mvvmsmart.utils.KLog;



public class AppApplicationMVVM extends BaseApplicationMVVM {

    private SharedPrefsUtils prefsUtils;

    private static AppApplicationMVVM instance;

    public static AppApplicationMVVM instance() {
        return instance;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化全局异常崩溃
        initCrash();
        LiveEventBus
                .config()
                .supportBroadcast(this) // 配置支持跨进程、跨APP通信，传入Context，需要在application onCreate中配置
                .lifecycleObserverAlwaysActive(true); //    整个生命周期（从onCreate到onDestroy）都可以实时收到消息

        LogUtils.logInit(BuildConfig.My_LOG_DEBUG);
        prefsUtils=new SharedPrefsUtils(this);
        registerActivityLifecycleCallbacks(EToastUtils.init());

       /* if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }*/
        ARouter.init(this); // 尽可能早，推荐在Application中初始化



    }

    private void initTenceImSdk(){


        MultiDex.install(this);
        // bugly上报
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
//        strategy.setAppVersion(V2TIMManager.getInstance().getVersion());
//        CrashReport.initCrashReport(getApplicationContext(), PrivateConstants.BUGLY_APPID, true, strategy);



    }



    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(MainActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }

    static {
        ClassicsFooter.REFRESH_FOOTER_LOADING = "加载中...";
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }
}
