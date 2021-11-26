package com.dfcj.videoim.push.um;

import android.content.Context;

import com.dfcj.videoim.util.other.LogUtils;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

public class PushHelper {

    public static void init(Context context) {

        //获取消息推送实例
        PushAgent pushAgent = PushAgent.getInstance(context);

        pushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);//服务端控制声音
       // pushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATIONPLAYSDKENABLE);//客户端允许呼吸灯点亮

        //自定义通知栏是否显示
        pushAgent.setNotificationOnForeground(true);
        //关闭免打扰模式
        pushAgent.setNoDisturbMode(0,0,0,0);

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                LogUtils.logd("注册成功：deviceToken：--> " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtils.logd("注册失败：--> " + "s:" + s + ",s1:" + s1);
            }
        });


        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void dealWithCustomAction(final Context context, final UMessage msg) {
                super.dealWithCustomAction(context, msg);
               // Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();

                if (msg!=null) {
                   // LogUtils.logd("点击通知栏："+msg.custom);
                }
                LogUtils.logd("点击通知栏了啊");

               /* Intent intent=new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);*/

            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);


    }


}
