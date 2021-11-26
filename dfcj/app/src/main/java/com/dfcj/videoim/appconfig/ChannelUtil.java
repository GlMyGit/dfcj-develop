package com.dfcj.videoim.appconfig;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;


import com.dfcj.videoim.util.other.LogUtils;
import com.umeng.analytics.AnalyticsConfig;


public class ChannelUtil {

    //设置渠道id
    public static void setChannidInfo(Context mContext){
        String myextrval=""+ getApplicationMetaValue2(mContext);

        if (!TextUtils.isEmpty(myextrval)) {
            AppConfig.CHANNEL_ID=getChannel(""+myextrval);
           // AppConfig.CHANNEL_ID=""+myextrval;
        }else{
            AppConfig.CHANNEL_ID="1";
        }

        LogUtils.logd("渠道1："+myextrval+"   渠道CHANNEL_ID:"+AppConfig.CHANNEL_ID+"   PRODUCT_ID:"+AppConfig.PRODUCT_ID);

    }


    //根据包名来查找到相应的APK渠道
    public static String getChannel(String packeg) {
        String productid = "";

        switch (packeg){
            case "az":
                productid = "9";
                break;
            case "mz":
                productid = "10";
                break;
            case "vivo":
                productid = "1";
                break;
            case "yyb":
                productid = "11";
                break;
            case "oppo":
                productid = "2";
                break;
            case "hw":
                productid = "12";
                break;
            case "market360":
                productid = "13";
                break;
            case "xm":
                productid = "8";
                break;
            case "lx":
                productid = "14";
                break;
            case "sx":
                productid = "15";
                break;
            case "sg":
                productid = "16";
                break;
            case "mmy":
                productid = "17";
                break;
            case "service":
                productid = "18";
                break;
            case "baidu":
                productid = "19";
                break;
            case "ali":
                productid = "20";
                break;
            case "toutiao":
                productid = "21";
                break;
            case "kuaishou":
                productid = "22";
                break;


        }

        return productid;

    }


    //获取友盟的渠道号
    public static String getApplicationMetaValue(String name, Context context) {
        String value = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }



    /**
     * 获取application中指定的meta-data
     * @param ctx
     * @return 如果没有获取成功(没有对应值,或者异常)，则返回值为空
     */
    public static String getChannelName(Context ctx){

        if (ctx == null) {
            return null;
        }
        String channelName = null;
        channelName = AnalyticsConfig.getChannel(ctx);

        return channelName;
    }


    //获取友盟的渠道号
    public static String getApplicationMetaValue2(Context context) {




        String value= "";
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        //此处这样写的目的是为了在debug模式下也能获取到渠道号，如果用getString的话只能在Release下获取到。
                        value = applicationInfo.metaData.get("UMENG_CHANNEL") + "";
                    }
                }

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }


}
