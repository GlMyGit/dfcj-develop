package com.dfcj.videoim.api;

/**
 * Created by Riven
 */
public class ApiConstants {



    //public static final String lOAN_STEWARD_MAIN_HOST = "http://10.23.24.207:8082/";// 测试
   // public static final String lOAN_STEWARD_MAIN_HOST = "http://10.221.57.117:6082//";// 正式
//    public static final String lOAN_STEWARD_MAIN_HOST = "http://10.221.57.117:6082//";// 正式
    public static final String lOAN_STEWARD_MAIN_HOST = "http://10.22.29.95:8082/";// 测试
//    public static final String lOAN_STEWARD_MAIN_HOST = "http://10.221.57.117:6082//";// 正式
//b3e831208f3fc00c6bf2dc81549c7e60597bdd3e


    public static final String lOAN_STEWARD_MAIN_HOST_JK = "https://route.showapi.com/90-86?";// 正式

    //     fe0fcc6c8dfe476bbb783ee3cddc545e
    //     578949
    //新浪图片 Host
    public static final String SINA_PHOTO_HOST = "http://gank.io/api/";

    /**
     * 根据HostType类型种类获取host
     */
    public static String getHost(int hostType) {

        String host;
        switch (hostType) {
            case HostType.lOAN_STEWARD_MAIN_HOST:
                host = lOAN_STEWARD_MAIN_HOST;
                break;
            case HostType.GANK_GIRL_PHOTO:
                host = SINA_PHOTO_HOST;
                break;
            default:
                host = "";
                break;
        }
        return host;
    }
}