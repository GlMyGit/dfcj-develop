package com.dfcj.videoim.util.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NotificationUtil {

    private static final String TAG = "DebugLog";

    /**
     * 通过定时闹钟发送通知
     * @param context
     * @param notifyObjectMap
     */
    public static void  notifyByAlarm(Context context, Map<Integer,NotifyObject> notifyObjectMap){
        //将数据存储起来
        int count = 0;
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Set<Integer> keySet = notifyObjectMap.keySet();
        for (Integer key0: keySet) {
            if(!notifyObjectMap.containsKey(key0)){
                break;
            }

            NotifyObject obj = notifyObjectMap.get(key0);
            if(obj == null){
                break;
            }

            if(obj.times.size() <= 0){
                if(obj.firstTime > 0){
                    try {
                        Map<String, Serializable> map = new HashMap<>();
                        map.put("KEY_NOTIFY_ID",obj.type);
                        map.put("KEY_NOTIFY",NotifyObject.to(obj));
                        AlarmTimerUtil.setAlarmTimer(context,++count,obj.firstTime,"TIMER_ACTION",map);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                for (long time: obj.times) {
                    if(time > 0){
                        try {
                            Map<String,Serializable> map = new HashMap<>();
                            map.put("KEY_NOTIFY_ID",obj.type);
                            map.put("KEY_NOTIFY",NotifyObject.to(obj));
                            AlarmTimerUtil.setAlarmTimer(context,++count,time,"TIMER_ACTION",map);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        SharedPreferences mPreferences = context.getSharedPreferences("SHARE_PREFERENCE_NOTIFICATION",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putInt("KEY_MAX_ALARM_ID",count);
        edit.commit();
    }

    public static void  notifyByAlarmByReceiver(Context context,NotifyObject obj){
        if(context == null || obj== null)return;
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMsg(context,obj,obj.type,System.currentTimeMillis(),manager);
    }

    /**
     * 消息通知
     * @param context
     * @param obj
     */
    private static void notifyMsg(Context context,NotifyObject obj,int nid,long time,NotificationManager mNotifyMgr){
        if(context == null || obj == null)return;
        if(mNotifyMgr == null){
            mNotifyMgr =  (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if(time <= 0)return;

        PendingIntent pi;

        //准备intent
        if(obj.activityClass!=null){

            Intent intent = new Intent(context,obj.activityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
            if(obj.param != null && obj.param.trim().length() > 0){
                intent.putExtra("param",obj.param);
            }

            // 构建 PendingIntent
            pi = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
            Intent intent1 = new Intent();
            pi = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        //notification
        Notification notification = null;
        String contentText = obj.content;

        //版本兼容

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//兼容Android8.0
            String id ="my_channel_01";
// NotificationManager.IMPORTANCE_NONE 关闭通知
//      NotificationManager.IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
//      NotificationManager.IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
//      NotificationManager.IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
//      NotificationManager. IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
            int importance =NotificationManager.IMPORTANCE_HIGH;
            CharSequence name = "notice";
            NotificationChannel mChannel =new NotificationChannel(id, name,importance);
            mChannel.enableLights(true);
            mChannel.setDescription("just show notice");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);//允许震动
            mChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});//震动频率设置
            mNotifyMgr.createNotificationChannel(mChannel);

            Notification.Builder builder = new Notification.Builder(context,id);
            builder.setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(obj.title)
                    .setContentText(obj.content)
                    .setOngoing(false)
                    .setSmallIcon(obj.icon)
                    .setWhen(System.currentTimeMillis());
            if(obj.subText != null && obj.subText.trim().length() > 0){
                builder.setSubText(obj.subText);
            }
            notification =  builder.build();
        }else if (Build.VERSION.SDK_INT >= 23) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(obj.title)
                    .setContentText(contentText)
                    .setSmallIcon(obj.icon)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setWhen(System.currentTimeMillis());
            if(obj.subText != null && obj.subText.trim().length() > 0){
                builder.setSubText(obj.subText);
            }
            notification = builder.build();
        } else if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(obj.title)
                    .setContentText(obj.content)
                    .setOngoing(false)
                    .setSmallIcon(obj.icon)
                    .setWhen(System.currentTimeMillis());
            if(obj.subText != null && obj.subText.trim().length() > 0){
                builder.setSubText(obj.subText);
            }
            notification =  builder.build();
        }
        if(notification != null){
            mNotifyMgr.notify(nid, notification);
        }
    }

    /**
     * 取消所有通知 同时取消定时闹钟
     * @param context
     */
    public static void clearAllNotifyMsg(Context context){
        try{

            NotificationManager mNotifyMgr =
                    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.cancelAll();

            SharedPreferences mPreferences = context.getSharedPreferences("SHARE_PREFERENCE_NOTIFICATION",Context.MODE_PRIVATE);
            int max_id = mPreferences.getInt("KEY_MAX_ALARM_ID",0);
            for (int i = 1;i <= max_id;i++){
                AlarmTimerUtil.cancelAlarmTimer(context,"TIMER_ACTION",i);
            }
            //清除数据
            mPreferences.edit().remove("KEY_MAX_ALARM_ID").commit();

        }catch (Exception e){
            Log.e(TAG,"取消通知失败",e);
        }
    }

}
