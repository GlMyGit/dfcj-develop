package com.dfcj.videoim.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * APPLICATION
 */
public class BaseApplication extends Application {

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    //Application其实就是Context
    public static Context getAppContext() {
        return baseApplication;
    }

    //getAppResources
    public static Resources getAppResources() {
        return baseApplication.getResources();
    }

}
