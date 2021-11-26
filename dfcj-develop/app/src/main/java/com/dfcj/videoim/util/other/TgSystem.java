package com.dfcj.videoim.util.other;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class TgSystem {



        public static void startService(Context context, Intent intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }

        /**
         * 判断本地是否已经安装好了指定的应用程序包
         *
         * @param packageNameTarget ：待判断的 App 包名，如 微博 com.sina.weibo
         * @return 已安装时返回 true,不存在时返回 false
         */
        public static boolean appIsExist(Context context, String packageNameTarget) {
            if (packageNameTarget == null || packageNameTarget.isEmpty()) {
                return false;
            }

            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packageInfoList = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                packageInfoList = packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
            }
            for (PackageInfo packageInfo : packageInfoList) {
                if (packageInfo.packageName.equals(packageNameTarget)) {
                    return true;
                }
            }

            return false;
        }


    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public static void setTopApp22(Context context) {
        /**获取ActivityManager*/
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.AppTask> list = activityManager.getAppTasks();
            for (ActivityManager.AppTask appTask : list){
                appTask.moveToFront();
                break;
            }
        }else {
            /**获得当前运行的task(任务)*/
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                /**找到本应用的 task，并将它切换到前台*/
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                    activityManager.moveTaskToFront(taskInfo.id, 0);
                    break;
                }
            }
        }
    }




    //当本应用位于后台时，则将它切换到最前端
        public static void setTopApp(Context context) {
            if (isRunningForeground(context)) {
                return;
            }
            //获取ActivityManager
            ActivityManager activityManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            }

           // List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
          //  for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                // The name of the process that this object is associated with.
             //   LogUtils.logd("ppp:"+appProcess.processName);

               /* if (appProcess.processName.equals(packageName)
                        && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }*/
          //  }

            //获得当前运行的task(任务)
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                //找到本应用的 task，并将它切换到前台 context.getPackageName()
                LogUtils.logd("111:"+taskInfo.topActivity.getPackageName());
                LogUtils.logd("222:"+taskInfo.topActivity.getClassName());
                LogUtils.logd("333:"+taskInfo.id);
                LogUtils.logd("999:"+context.getPackageName());

                if ((taskInfo.topActivity.getPackageName()).equals((context.getPackageName()))) {

                   activityManager.moveTaskToFront(taskInfo.id, 0);
                    //activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                    break;
                }
            }
        }

        //判断本应用是否已经位于最前端：已经位于最前端时，返回 true；否则返回 false
        public static boolean isRunningForeground(Context context) {
            ActivityManager activityManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            }
            List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
            return false;
        }


}
