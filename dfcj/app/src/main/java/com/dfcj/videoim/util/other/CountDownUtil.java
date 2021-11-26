package com.dfcj.videoim.util.other;

import android.os.CountDownTimer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CountDownUtil {


    public CountDownTimer mTimer;

    private int interval = 1000;

    public CountDownUtil() {
    }


    public static String addDate(String day, int x)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//12小时制
        Date date = null;
        try
        {
            date = format.parse(day);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if (date == null) return "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, x);//24小时制
        //cal.add(Calendar.HOUR, x);12小时制
        date = cal.getTime();
        System.out.println("front:" + date);
        cal = null;
        return format.format(date);
    }


    /*
     * 将时间转换为时间戳
     */
    public static Long dateToStamp(String time)  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        return ts;
    }


        public static String date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 日期转时间戳
     *
     * @param
     * @param format
     * @return
     */
    public static long getTimeStamp(String dates, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dates).getTime() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;

    }

    /**
     * 开始倒计时
     *
     * @param startTime      开始时间（时间戳）
     * @param minuteInterval 时间间隔（单位：分）
     * @param callBack
     */
    public void start(long startTime, int minuteInterval, OnCountDownCallBack callBack) {
        long lengthTime = minuteInterval * 60 * interval;
        //查看是否为毫秒的时间戳
        boolean isMillSecond = (String.valueOf(startTime).length() == 13);
        startTime = startTime * (isMillSecond ? 1 : interval);
        long endTime = startTime + lengthTime;
        long curTime = System.currentTimeMillis();
        mTimer = getTimer(endTime - curTime, interval, callBack);
        if (Math.abs(curTime - startTime) > lengthTime) {
            if (callBack != null) {
                callBack.onFinish();
            }
        } else {
            mTimer.start();
        }
    }

    private CountDownTimer getTimer(long millisInFuture, long interval, OnCountDownCallBack callBack) {
        return new CountDownTimer(millisInFuture, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                int day = 0;
                int hour = 0;
                int minute = (int) (millisUntilFinished / interval / 60);
                int second = (int) (millisUntilFinished / interval % 60);
                if (minute > 60) {
                    hour = minute / 60;
                    minute = minute % 60;
                }
                if (hour > 24) {
                    day = hour / 24;
                    hour = hour % 24;
                }
                if (callBack != null) {
                    callBack.onProcess(day, hour, minute, second);
                }
            }

            @Override
            public void onFinish() {
                if (callBack != null) {
                    callBack.onFinish();
                }
            }
        };
    }

    /**
     * 开始倒计时
     *
     * @param endTime  结束时间（时间戳）
     * @param callBack
     */
    public void start(long endTime, OnCountDownCallBack callBack) {
        long curTime = System.currentTimeMillis();
        mTimer = getTimer(endTime - curTime, interval, callBack);
        if (endTime < curTime) {
            if (callBack != null) {
                callBack.onFinish();
            }
        } else {
            mTimer.start();
        }
    }

    /**
     * 必用
     */
    public void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public interface OnCountDownCallBack {

        void onProcess(int day, int hour, int minute, int second);

        void onFinish();
    }


}
