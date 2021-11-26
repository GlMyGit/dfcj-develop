package com.dfcj.videoim.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class ScreenUtil {

    private static final String TAG = ScreenUtil.class.getSimpleName();

    public static int getScreenHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public static int getPxByDp(Context conn,float dp) {
        float scale = conn.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public static int[] scaledSize(int containerWidth, int containerHeight, int realWidth, int realHeight) {
        Log.i(TAG, "scaledSize  containerWidth: " + containerWidth + " containerHeight: " + containerHeight
                + " realWidth: " + realWidth + " realHeight: " + realHeight);
        float deviceRate = (float) containerWidth / (float) containerHeight;
        float rate = (float) realWidth / (float) realHeight;
        int width = 0;
        int height = 0;
        if (rate < deviceRate) {
            height = containerHeight;
            width = (int) (containerHeight * rate);
        } else {
            width = containerWidth;
            height = (int) (containerWidth / rate);
        }
        return new int[]{width, height};
    }

    public static int dip2px(Context conn,float dpValue) {
        final float scale =conn.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
