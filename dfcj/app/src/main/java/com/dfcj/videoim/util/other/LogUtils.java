package com.dfcj.videoim.util.other;



import androidx.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.dfcj.videoim.appconfig.AppConfig;


/**
 * 如果用于android平台，将信息记录到“LogCat”。如果用于java平台，将信息记录到“Console”
 * 使用logger封装
 * 使用方式: 1 在gradle中配置 compile 'com.orhanobut:logger:1.13'//打印日志
 *         2 在application中初始化logger
 *         3 注意:如果有library 在gradle中添加 defaultPublishConfig "debug"
 */
public class LogUtils {
    public static boolean DEBUG_ENABLE = false;// 是否调试模式

    /**
     * 在application调用初始化logger
     */
    public static void logInit(boolean debug) {
        DEBUG_ENABLE = debug;
        if (DEBUG_ENABLE) {


            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(false)  // （可选）是否显示线程信息。默认值为true
                    .methodCount(2)         // （可选）要显示的方法行数。默认值2
                    .methodOffset(7)        // （可选）隐藏内部方法调用，直到offset为止。默认值5
                    //.logStrategy(new CustomLogCatStrategy()) // （可选）更改日志策略以打印出来。默认LogCat
                    .tag(""+AppConfig.DEBUG_TAG)   // （可选）每个日志的全局标签。默认值PRETTY_LOGGER
                    .build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
                @Override
                public boolean isLoggable(int priority, @Nullable String tag) {
                    return DEBUG_ENABLE;

                }
            });


/*
            Logger.init(AppConfig.DEBUG_TAG)                 // default PRETTYLOGGER log的名称
                    .methodCount(2)                 // default 2
                    .hideThreadInfo()
                    .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                    .methodOffset(0);                // default 0*/
        } else {
          /*  Logger.init()                 // default PRETTYLOGGER log的名称
                    .methodCount(3)                 // default 2
                    .hideThreadInfo()               // default shown
                    .logLevel(LogLevel.NONE)        // default LogLevel.FULL
                    .methodOffset(2);*/

            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(false)  // （可选）是否显示线程信息。默认值为true
                    .methodCount(2)         // （可选）要显示的方法行数。默认值2
                    .methodOffset(7)        // （可选）隐藏内部方法调用，直到offset为止。默认值5
                    //   .logStrategy(new CustomLogCatStrategy()) // （可选）更改日志策略以打印出来。默认LogCat
                    .tag(""+AppConfig.DEBUG_TAG)   // （可选）每个日志的全局标签。默认值PRETTY_LOGGER
                    .build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
                @Override
                public boolean isLoggable(int priority, @Nullable String tag) {
                    return DEBUG_ENABLE;

                }
            });



        }
    }

    public static void logd(String tag, String message) {
        if (DEBUG_ENABLE) {
            Logger.d(tag, message);
        }
    }

    public static void logd(String message) {
        if (DEBUG_ENABLE) {
            Logger.d(message);
        }
    }

    public static void loge(Throwable throwable, String message, Object... args) {
        if (DEBUG_ENABLE) {
            Logger.e(throwable, message, args);
        }
    }

    public static void loge(String message, Object... args) {
        if (DEBUG_ENABLE) {
            Logger.e(message, args);
        }
    }

    public static void logi(String message, Object... args) {
        if (DEBUG_ENABLE) {
            Logger.i(message, args);
        }
    }

    public static void logv(String message, Object... args) {
        if (DEBUG_ENABLE) {
            Logger.v(message, args);
        }
    }

    public static void logw(String message, Object... args) {
        if (DEBUG_ENABLE) {
            Logger.w(message, args);
        }
    }

    public static void logwtf(String message, Object... args) {
        if (DEBUG_ENABLE) {
            Logger.wtf(message, args);
        }
    }

    public static void logjson(String message) {
        if (DEBUG_ENABLE) {
            Logger.json(message);
        }
    }

    public static void logxml(String message) {
        if (DEBUG_ENABLE) {
            Logger.xml(message);
        }
    }
}
