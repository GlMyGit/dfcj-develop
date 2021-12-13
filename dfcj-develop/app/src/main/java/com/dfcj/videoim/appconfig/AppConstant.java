package com.dfcj.videoim.appconfig;

/**
 * Created by Administrator on 2018/5/17.
 */

public class AppConstant {

    public static final String USERTOKEN = "USERTOKEN";
    public static final String MYUSERID = "UserId";
    public static final String MyUserName = "MyUserName";
    public static final String MyUserIcon = "MyUserIcon";

    public static final String MYUSERPHONE = "UserPhone";
    public static final String DEVICETOKEN = "DeviceToken";
    public static final String ISSECURITPWD = "Is_Securitypwd";
    public static final String IMEIVAL = "ImeiVal";
    public static final String USERopenid = "USERopenid";

    public static final String STAFF_CODE = "staffCode";
    public static final String STAFF_NAME = "staffName";
    public static final String STAFF_IMGE = "staffImge";

    public static final String SHOP_MSG_BODY_DATA = "shopMsgBodyData";

    public static final String CHAT_TYPE = "chatType";

    public static final String CloudCustomData = "Cloud_Custom_Data";
    public static final String SDKAppId = "SDK_APP_ID";
    public static final String SDKUserSig = "SDK_USER_SIG";

    public static final String VideoStatus = "Video_Status";
    public static final String Video_mRoomId = "Video_mRoomId";


    public static final String PRO_ID = "prod_id";
    public static final String PROUDT_URL = "product_url";
    public static final String PROUDT_NAME = "product_name";

    public static final String zfb_number = "zfb_number";
    public static final String zfb_name = "zfb_name";

    public static final String LOAN_STATUS = "loan_status";

    public static final String QI_XIAN = "qi_xian";

    public static final String MSG_ID = "msg_id";
    public static final String CERTIFICATION_STATUS = "certification_status";
    public static final String ISNOTICE = "isNotice";


    public static final String DefaultBottomAdCode = "DefaultBottomAd";
    public static final String DefaultBottomAdType = "DefaultBottomAdType";
    public static final String DefaultBottomAdId = "DefaultBottomAdId";
    public static final String DefaultBottomAdPLatFormType = "DefaultBottomAdPLatFormType";

    public static final String DefaultJiOrXiliuAdCode = "DefaultJiOrXiliuAdCode";
    public static final String DefaultJiOrXiliuAdType = "DefaultJiOrXiliuAdType";
    public static final String DefaultJiOrXiliuAdId = "DefaultJiOrXiliuAdId";
    public static final String DefaultJiOrXiliuAdPLatFormType = "DefaultJiOrXiliuAdPLatFormType";

    public static final String checkStatus = "checkStatus";


    public static final String ISDayJl = "ISDayJl";


    public static final String ChaPingCode = "ChaPingCode";
    public static final String ChaPingType = "ChaPingType";
    public static final String ChaPingId = "ChaPingId";


    public static final String xieyistatus = "xieyistatus";
    private static final String SHARED_NAME = "outerId_pref";
    public static final String OUTER_ID = "outerId";


    //自定义消息类型(EventBus code)
    public static final int SEND_MSG_TYPE_SERVICE = 100;//文本类型
    public static final int SEND_MSG_TYPE_TEXT = 101;//文本类型
    public static final int SEND_MSG_TYPE_IMAGE = 102;//图片类型
    public static final int SEND_MSG_TYPE_CARD = 103;//卡片类型
    public static final int SEND_VIDEO_TYPE_START = 201;//开始视频
    public static final int SEND_VIDEO_TYPE_END = 202;//结束视频
    public static final int SEND_VIDEO_TYPE_CANCEL = 203;//取消视频
    public static final int SEND_VIDEO_TYPE_REFUSE = 204;//拒绝视频
    public static final int SEND_VIDEO_TYPE_OVERTIME = 205;//视频超时

    //关闭activity(EventBus code)
    public static final int CLOSE_ACTIVITY_CODE = 1000;

}
