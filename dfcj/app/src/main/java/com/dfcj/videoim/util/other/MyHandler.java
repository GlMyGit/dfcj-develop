package com.dfcj.videoim.util.other;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.dfcj.videoim.base.BaseActivity;


import java.lang.ref.WeakReference;

public class MyHandler extends Handler {


    public static WeakReference<BaseActivity> mActivity;
    public MyHandler(@NonNull Looper looper, BaseActivity activity) {
        super(looper);
        mActivity = new WeakReference<BaseActivity>(activity);
    }

//    public static WeakReference<RxFragment> mActivity2;
//    public  MyHandler(@NonNull Looper looper, RxFragment activity) {
//        super(looper);
//        mActivity2 = new WeakReference<RxFragment>(activity);
//    }

    @Override
    public void handleMessage(Message msg) {
        // 处理消息...
    }

}
