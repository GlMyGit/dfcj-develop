package com.dfcj.videoim.appconfig;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.dfcj.videoim.BuildConfig;
import com.dfcj.videoim.base.BaseApplication;
import com.dfcj.videoim.push.um.PushHelper;

import com.dfcj.videoim.util.other.LogUtils;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.dfcj.videoim.util.etoast.etoast2.EToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/5/17.
 */

public class MyAppliaction extends BaseApplication {

    private SharedPrefsUtils prefsUtils;
    public static Context mContext;
    //private PushAgent mPushAgent;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

     /*   int pid = android.os.Process.myPid();
        Log.i("DebugLog", "MyApplication is oncreate====="+"pid="+pid);
        String processNameString = "";
        ActivityManager mActivityManager = (ActivityManager)this.getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processNameString = appProcess.processName;
            }
        }

        if("com.project.movement".equals(processNameString)){
*/
            LogUtils.logInit(BuildConfig.My_LOG_DEBUG);
            prefsUtils=new SharedPrefsUtils(mContext);
            registerActivityLifecycleCallbacks(EToastUtils.init());
            if (BuildConfig.My_LOG_DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
                ARouter.openLog();     // 打印日志
                ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            }

            ARouter.init(this);

            initUMen();

            // 初始化MultiDex
            MultiDex.install(this);
          //  LockerService.startService(this);
            sInstance = this;


       // }else{



     //   }



    }




    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }


    private void initUMen() {


        // 初始化SDK
       // UMConfigure.init(this, "您的appkey", "您的渠道", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.init(mContext,"60583181b8c8d45c13a96d91",""+ChannelUtil.getChannelName(mContext), UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        MobclickAgent.setCatchUncaughtExceptions(true);


        //预初始化
       // PushHelper.init(this);
        //正式初始化
        initPushSDK();
        initChangShangPush();

    }

    /**
     * 初始化推送SDK，在用户隐私政策协议同意后，再做初始化
     */
    private void initPushSDK() {
        //if (UMUtils.isMainProgress(this)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushHelper.init(getApplicationContext());
                }
            }).start();
        //}
    }
    private void initChangShangPush(){

        HuaWeiRegister.register(this);
        MiPushRegistar.register(this,"2882303761519814470","5841981498470");

    }



    public static MyAppliaction sInstance;
    public static int mCount;
    public static boolean mFront;//是否前台

    private static Timer mTimer;
    static int cntStart = 0;
    static int waitTime = 10;
    static TimerTask mTask = null;



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }





    @SuppressLint("NewApi")
    public static void isRunningForegroundToApp1(Context context, final Class Class) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(20);
        /**枚举进程*/

        for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
            //*找到本应用的 task，并将它切换到前台
            if (taskInfo.baseActivity.getPackageName().equals(context.getPackageName())) {
                Log.e("DebugLog", "timerTask  pid " + taskInfo.id);
                Log.e("DebugLog", "timerTask  processName " + taskInfo.topActivity.getPackageName());
                Log.e("DebugLog", "timerTask  getPackageName " + context.getPackageName());
                activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                Intent intent = new Intent(context, Class);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(intent);
                break;
            }
        }
    }

    private static void cancelTimer() {
        cntStart = 0;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }



}
