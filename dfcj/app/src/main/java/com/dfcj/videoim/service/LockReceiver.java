package com.dfcj.videoim.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.other.LogUtils;

/**
 * 锁屏广播接收
 */
public class LockReceiver extends BroadcastReceiver {

    private static final String TAG = "DebugLog";

    public LockReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.logd("监听屏幕状态0999");
        String action = intent.getAction();
//        LogUtils.logd("监听屏幕状态1");

        if (!TextUtils.isEmpty(action)) {
            Log.e(TAG, "锁屏onReceive---" + action);
            switch (action) {
                case Intent.ACTION_POWER_CONNECTED:
                    LogUtils.logd("ACTION_POWER_CONNECTED");
                    break;
                case Intent.ACTION_BOOT_COMPLETED:
                    LogUtils.logd("ACTION_BOOT_COMPLETED");
                  /*  Intent bootActivityIntent=new Intent(context,LockScreenActivity.class);
                    bootActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(bootActivityIntent);//要启动应用程序的首界面*/
                    break;
                case Intent.ACTION_SCREEN_ON:// 开屏
                    LogUtils.logd("ACTION_SCREEN_ON");

                  /*  Intent intent2=new Intent("com.pro.lock");
                    context.startActivity(intent2);
*/
                    //TgSystem.setTopApp(context);

                   // startMyAct(context);

                  //  Intent intent2=new Intent("com.pro.lock");

                 //   context.startActivity(intent2);

                    if(AppUtils.isShenHe()){

                    }else{

                       // LockScreenActivity.startActivity(context);
                    }

                    break;
                case Intent.ACTION_SCREEN_OFF:// 锁屏
                    Log.e(TAG, "锁屏了on界面");

                   // TgSystem.setTopApp(context);

                /*    boolean serviceRunning = AppUtils.isServiceRunning(context, "com.project.movement.service.MyActivityService");
                    if(!serviceRunning){
                        context.startService(new Intent(context, MyActivityService.class));

                    }
*/
                    // Intent intent2=new Intent("com.pro.lock");
                   // context.startActivity(intent2);



                    break;

                case  Intent.ACTION_USER_PRESENT:// 解锁
                    LogUtils.logd("解锁了");
                    //LockScreenActivity.startActivity(context);
                    //startMyAct(context,false);

                    //sit(context);

                   // TgSystem.setTopApp(context);

                  /*  Intent dialogIntent = new Intent(context, LockScreenSuoActivity.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(dialogIntent);*/

                   // context.startService(new Intent(context,MyActivityService.class));
                   // Intent intent22 = new Intent(context, MyActivityService.class);
                  //  intent22.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                   // context.startService(intent22);


                    break;

            }
        }
    }



    private void startMyAct(Context context,boolean isff){




      /*  NotificationUtils notificationUtils = new NotificationUtils(context);
        String content = "fullscreen intent test";
        notificationUtils.sendNotificationFullScreen(context.getString(R.string.app_name), content, "22");
*/

     /*   LockScreenActivity.setBool(isff);

        Intent screenIntent = new Intent();
        screenIntent.setClass(context.getApplicationContext(), LockScreenActivity.class);
        //screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //activity需要新的任务栈
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        //screenIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.getApplicationContext().startActivity(screenIntent);*/

     /*   new Handler().post(new Runnable() {
            @Override
            public void run() {

            }
        });
*/

    }

}