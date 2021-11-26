package com.dfcj.videoim.util.wx;


public class WechatUtils {

   // public WechatUtils() {}
//
//    private static WechatUtils wechat;
//
//    public static WechatUtils getInstance(){
//        if(wechat==null){
//            wechat=new WechatUtils();
//        }
//        return wechat;
//    }
//
//
//    public static WechatUtils getInstanceMy(){
//
//        return wechat;
//    }
////
////    public static WechatUtils getInstance22(){
////
////        wechat=new WechatUtils();
////
////        return wechat;
////    }
//
//    private  WechatResponseListener mListener;//监听微信回调的接口
//
//    public  WechatResponseListener  getListener() {
//        return mListener;
//    }
//
//    public   void setListener(WechatResponseListener listener) {
//        mListener = listener;
//    }
//
//    private IWXAPI wechatapi;
//
//    public IWXAPI getWXAPi(Context context) {
//        if (wechatapi == null) {
//            wechatapi = WXAPIFactory.createWXAPI(context, Constants.APP_ID, false);
//            wechatapi.registerApp(Constants.APP_ID);
//        }
//        return wechatapi;
//    }
//
//    //发送请求
//    public  void sendReq(Context context, PayReq req){
//        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//        if (!getWXAPi(context).isWXAppInstalled()) {
//            getWXAPi(context).registerApp(Constants.APP_ID);
//        }
//        getWXAPi(context).sendReq(req);
//
//    }
//
//    //接收请求
//    public  void setResponse(BaseResp baseResp){
//        switch (baseResp.errCode){
//            case BaseResp.ErrCode.ERR_OK:
//                if(mListener!=null){
//                    mListener.response(baseResp);
//                }
//                break;
//            case BaseResp.ErrCode.ERR_COMM:
//                if(mListener!=null){
//                    mListener.response(baseResp);
//                }
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                if(mListener!=null){
//                    mListener.response(baseResp);
//                }
//                break;
//        }
//    }
//
//    /**
//     * @param context
//     * @return
//     * @功能描述:获取微信的版本是否支持支付
//     * @返回类型:boolean:如果支持支付则返回true，不支持返回false！
//     * @时间:
//     */
//    //INFO 微信/判断微信版本是否支持支付
//    public  boolean getPaySupported(Context context) {
//        int wxSdkVersion = getWXAPi(context).getWXAppSupportAPI();
//        if (wxSdkVersion >= Build.PAY_SUPPORTED_SDK_INT) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * @return
//     * @功能描述:获取是否安装微信
//     * @返回类型:boolean:如果安装则返回true，没安装返回false！
//     */
//    //INFO 微信/判断是否安装微信
//    public  boolean getOpenWXApp(Context context) {
//        boolean installWX = getWXAPi(context).openWXApp();
//
//        return installWX;
//    }

}
