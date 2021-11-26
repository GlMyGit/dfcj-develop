package com.dfcj.videoim.util.notify;

import android.app.Activity;

import com.dfcj.videoim.R;

import java.util.Calendar;
import java.util.Date;

public class NotifyUtils {

    public static Long getMyLongTime(int hour,int fenzhong){


        Calendar cal = Calendar.getInstance();
        // 每天定点执行
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, fenzhong);
        cal.set(Calendar.SECOND, 0);
        Date time = cal.getTime();
        Long goodsTime=time.getTime();

        return goodsTime;

    }



    public static   NotifyObject getNotifyObjectEntity(int count,String title,String content,long goodsTime,Class<? extends Activity> activityClass){

        NotifyObject my1=new NotifyObject();
        my1.title=""+title;
        my1.content=""+content;
        my1.subText="";
        my1.type=count;
        my1.icon= R.drawable.dislike_icon;
        my1.firstTime=goodsTime;
        my1.activityClass= activityClass;
        my1.param="123213";

        return my1;

    }


}
