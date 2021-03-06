package com.dfcj.videoim.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.dfcj.videoim.MainActivity;
import com.dfcj.videoim.adapter.ChatAdapter;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.base.BaseActivity;
import com.dfcj.videoim.entity.AudioMsgBody;
import com.dfcj.videoim.entity.CustomMsgEntity;
import com.dfcj.videoim.entity.FileMsgBody;
import com.dfcj.videoim.entity.ImageMsgBody;
import com.dfcj.videoim.entity.Message;
import com.dfcj.videoim.entity.MsgSendStatus;
import com.dfcj.videoim.entity.MsgType;
import com.dfcj.videoim.entity.SendOffineMsgEntity;
import com.dfcj.videoim.entity.ShopMsgBody;
import com.dfcj.videoim.entity.TextMsgBody;
import com.dfcj.videoim.entity.VideoMsgBody;
import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.ChatUiHelper;
import com.dfcj.videoim.util.other.GsonUtil;
import com.dfcj.videoim.util.other.LogUtils;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.google.gson.Gson;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMImageElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMOfflinePushInfo;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.tencent.imsdk.v2.V2TIMSoundElem;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.imsdk.v2.V2TIMVideoElem;
import com.wzq.mvvmsmart.utils.KLog;
import com.wzq.mvvmsmart.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ImUtils {

    public static final String mSenderId = "right";
    public static final String mTargetId = "left";
    private Context context;
    private ChatAdapter mAdapter;
    public boolean isLogin = false;
    public boolean isKeFuLogin = true;

    RecyclerView rvChatList;

    //public static String fsUserId = "106584";//??????
    public static String fsUserId = SharedPrefsUtils.getValue(AppConstant.STAFF_CODE);//??????
    public static String MyUserId = SharedPrefsUtils.getValue(AppConstant.MYUSERID);//??????

    //public static  String fsUserId="customer1";
    //public static String MyUserId="staff1";

    public ImUtils(Context context) {
        this.context = context;
    }

    public void initViewInfo(ChatAdapter mAdapter, RecyclerView rvChatList) {
        this.mAdapter = mAdapter;
        this.rvChatList = rvChatList;

    }


    public void initTencentImLogin() {
        KLog.d("sdk??????????????????:" + ImConstant.SDKAPPID);

        // 1. ??? IM ????????????????????? SDKAppID?????????????????? SDKAppID???
        // 2. ????????? config ??????
        V2TIMSDKConfig config = new V2TIMSDKConfig();
        // 3. ?????? log ?????????????????????????????? SDKConfig???
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO);
        // 4. ????????? SDK ????????? V2TIMSDKListener ??????????????????
        // initSDK ??? SDK ??????????????????????????????????????????????????? V2TIMSDKListener ?????????????????????
        V2TIMManager.getInstance().initSDK(context, ImConstant.SDKAPPID, config);
        V2TIMManager.getInstance().addIMSDKListener(new V2TIMSDKListener() {
            @Override
            public void onConnecting() {
                super.onConnecting();
                // ?????????????????????????????????
                KLog.d("?????????????????????");
            }

            @Override
            public void onConnectSuccess() {
                super.onConnectSuccess();
                // ???????????????????????????????????????
                KLog.d("????????????????????????");
            }

            @Override
            public void onConnectFailed(int code, String error) {
                super.onConnectFailed(code, error);
                // ??????????????????????????????
                KLog.d("??????????????????????????????");
                ActivityUtils.finishActivity(MainActivity.class);
            }

            @Override
            public void onKickedOffline() {
                super.onKickedOffline();
                //??????????????????
                KLog.d("??????????????????");
                ActivityUtils.finishActivity(MainActivity.class);
            }

            @Override
            public void onSelfInfoUpdated(V2TIMUserFullInfo info) {
                super.onSelfInfoUpdated(info);
                //??????????????????????????????
                KLog.d("??????????????????????????????");

            }
        });


    }


    //??????
    public void loginIm() {
        initTencentImLogin();


        String value = SharedPrefsUtils.getValue(AppConstant.MYUSERID);
        String SDKUserSig = SharedPrefsUtils.getValue(AppConstant.SDKUserSig);

        KLog.d("????????????"+value);
        KLog.d("?????????SDKUserSig???"+SDKUserSig);

        V2TIMManager.getInstance().login("" + value, SDKUserSig, new V2TIMCallback() {
            @Override
            public void onSuccess() {
                KLog.d("????????????");

                isLogin = true;

                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(1);
                }
            }

            @Override
            public void onError(int i, String s) {
                KLog.d("????????????:" + s);
                isLogin = false;


                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(2);
                }

            }
        });

    }


    private SpannableStringBuilder replace = null;
    private Message mMessgae = null;

    //?????????????????????????????????   //msgType  1??????  2 ?????????   3?????????  4????????????  5??????  6??????
    public void sendTextMsg(String hello, int msgType) {
        if (TextUtils.isEmpty(hello)) {
            return;
        }

        String cloudCustomData = SharedPrefsUtils.getValue(AppConstant.CloudCustomData);

        KLog.d("??????????????????" + hello);
        KLog.d("???????????????cloudCustomData???" + cloudCustomData);
        KLog.d("??????id???" + SharedPrefsUtils.getValue(AppConstant.STAFF_CODE));

        try {

            CustomMsgEntity customMsgEntity = new CustomMsgEntity();
            customMsgEntity.setMsgType(msgType);

            if (msgType == AppConstant.SEND_MSG_TYPE_TEXT) {//??????
                replace = ChatUiHelper.handlerEmojiText(hello, true);
                KLog.d("???????????????replace???" + replace);

                mMessgae = getBaseSendMessage(MsgType.TEXT);
                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(replace.toString());
                mTextMsgBody.setCharsequence(replace);
                mMessgae.setBody(mTextMsgBody);
                mTextMsgBody.setVideo(false);
                mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);

                //????????????
                mAdapter.addData(mMessgae);
                customMsgEntity.setMsgText("" + replace);

            } else if (msgType == AppConstant.SEND_MSG_TYPE_IMAGE) {//??????
                customMsgEntity.setImgUrl(hello);

                mMessgae = getBaseSendMessage(MsgType.IMAGE);
                ImageMsgBody mImageMsgBody = new ImageMsgBody();
                mImageMsgBody.setThumbUrl(hello);
                // mImageMsgBody.setThumbPath(imagePath);
                mMessgae.setBody(mImageMsgBody);
                mMessgae.setSenderId(mSenderId);
                mMessgae.setType(ChatAdapter.TYPE_SEND_IMAGE);
                //????????????
                mAdapter.addData(mMessgae);

            } else if (msgType == AppConstant.SEND_MSG_TYPE_CARD) {//??????
                customMsgEntity.setMsgText("" + hello);
                mMessgae = getBaseSendMessage(MsgType.KAPIAN);
                //?????????????????????????????????
                ShopMsgBody shopMsgBody = GsonUtil.newGson22().fromJson(hello, ShopMsgBody.class);
                mMessgae.setBody(shopMsgBody);
                mMessgae.setSenderId(mSenderId);
                mMessgae.setType(ChatAdapter.TYPE_KAPIAN_SEND_TEXT);
                //????????????
                mAdapter.addData(mMessgae);

            } else if (msgType == AppConstant.SEND_VIDEO_TYPE_START) {//????????????
                mMessgae = getBaseSendMessage(MsgType.TEXT);

                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(hello);
                mTextMsgBody.setVideo(true);
                mTextMsgBody.setCharsequence(hello);
                mMessgae.setBody(mTextMsgBody);

                mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
                customMsgEntity.setMsgText("" + hello);

            } else if (msgType == AppConstant.SEND_VIDEO_TYPE_CANCEL) {//????????????
                mMessgae = getBaseSendMessage(MsgType.TEXT);
                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(hello);
                mTextMsgBody.setVideo(true);
                mTextMsgBody.setCharsequence(hello);
                mMessgae.setBody(mTextMsgBody);
                mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
                customMsgEntity.setMsgText("" + hello);

            } else if (msgType == AppConstant.SEND_VIDEO_TYPE_END) {//????????????
                mMessgae = getBaseSendMessage(MsgType.TEXT);
                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(hello);
                mTextMsgBody.setVideo(true);
                mTextMsgBody.setCharsequence(hello);
                mMessgae.setBody(mTextMsgBody);
                mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
                customMsgEntity.setMsgText("" + hello);

            } else if (msgType == AppConstant.SEND_VIDEO_TYPE_OVERTIME) {//????????????
                mMessgae = getBaseSendMessage(MsgType.TEXT);
                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(hello);
                mTextMsgBody.setVideo(true);
                mTextMsgBody.setCharsequence(hello);
                mMessgae.setBody(mTextMsgBody);
                mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
                customMsgEntity.setMsgText("" + hello);

            }


            String sVal = GsonUtil.newGson22().toJson(customMsgEntity);
            byte[] strs = sVal.getBytes("UTF8");

            if (msgType == AppConstant.SEND_MSG_TYPE_TEXT) {


                KLog.d("?????????????????????"+replace.toString());

                V2TIMMessage textMessage = V2TIMManager.getMessageManager().createTextMessage(replace.toString());
                textMessage.setCloudCustomData(cloudCustomData);
                V2TIMManager.getMessageManager().sendMessage(textMessage, SharedPrefsUtils.getValue(AppConstant.STAFF_CODE),
                        null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT,false,new V2TIMOfflinePushInfo(), new V2TIMSendCallback<V2TIMMessage>() {

                            @Override
                            public void onSuccess(V2TIMMessage v2TIMMessage) {
                                KLog.d("????????????????????????");
                                if (msgType == AppConstant.SEND_VIDEO_TYPE_START ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_CANCEL ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_END ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_OVERTIME) {
                                } else {
                                    updateMsg(mMessgae);
                                }

                                if (yesMsgOnclickListener != null) {
                                    yesMsgOnclickListener.onYesMsgClick(true, msgType);
                                }

                                KLog.d("????????????????????????:"+new Gson().toJson(v2TIMMessage));

                            }

                            @Override
                            public void onError(int code, String s) {
                                KLog.d("???????????????????????????" + s+"    ?????????"+code);

                                if(code==80001){
                                    ToastUtils.showShort("???????????????????????????");
                                }

                                if (msgType == AppConstant.SEND_VIDEO_TYPE_START ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_CANCEL ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_END ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_OVERTIME) {
                                } else {

                                    updateFailedMsg(mMessgae);
                                }
                                if (yesMsgOnclickListener != null) {
                                    yesMsgOnclickListener.onYesMsgClick(false, msgType);
                                }
                            }

                            @Override
                            public void onProgress(int i) {

                            }
                        });

            }else{


                V2TIMMessage customMessage = V2TIMManager.getMessageManager().createCustomMessage(strs);
                customMessage.setCloudCustomData(cloudCustomData);

                //?????????????????????????????????????????????????????? true ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                KLog.d("???????????????" + fsUserId);

                V2TIMManager.getMessageManager().sendMessage(customMessage, SharedPrefsUtils.getValue(AppConstant.STAFF_CODE),
                        null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT,
                        false, new V2TIMOfflinePushInfo(), new V2TIMSendCallback<V2TIMMessage>() {
                            @Override
                            public void onProgress(int i) {

                            }

                            @Override
                            public void onSuccess(V2TIMMessage v2TIMMessage) {
                                KLog.d("???????????????");
                                if (msgType == AppConstant.SEND_VIDEO_TYPE_START ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_CANCEL ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_END ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_OVERTIME) {
                                } else {
                                    updateMsg(mMessgae);
                                }

                                if (yesMsgOnclickListener != null) {
                                    yesMsgOnclickListener.onYesMsgClick(true, msgType);
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                KLog.d("???????????????" + s);
                                if (msgType == AppConstant.SEND_VIDEO_TYPE_START ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_CANCEL ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_END ||
                                        msgType == AppConstant.SEND_VIDEO_TYPE_OVERTIME) {
                                } else {
                                    updateFailedMsg(mMessgae);
                                }
                                if (yesMsgOnclickListener != null) {
                                    yesMsgOnclickListener.onYesMsgClick(false, msgType);
                                }

                            }
                        });


            }





        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

// V2TIMManager.getInstance().sendC2CTextMessage(hello, ""+fsUserId, new V2TIMValueCallback<V2TIMMessage>() {
//            @Override
//            public void onSuccess(V2TIMMessage v2TIMMessage) {
//                updateMsg(mMessgae);
//                KLog.d("??????????????????");
//
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                ToastUtils.showShort("????????????");
//
//                KLog.d("?????????????????????"+s + "        --"+i);
//
//            }
//        });


    }

    public String callVideo(BaseActivity context, String mRoomId) {
        Map<String, String> data = new HashMap<>();
        data.put("roomId", mRoomId);
        data.put("userName", MyUserId);
        data.put("userImg", "");
        data.put("msgText", "????????????");
        data.put("msgType", "5");
        String dataStr = GsonUtil.GsonString(data);
        V2TIMOfflinePushInfo v2TIMOfflinePushInfo = new V2TIMOfflinePushInfo();
        String inviteID = V2TIMManager.getSignalingManager().invite(fsUserId, dataStr, false, v2TIMOfflinePushInfo, 60, new V2TIMCallback() {
            @Override
            public void onSuccess() {
                KLog.d("????????????????????????");
                Bundle bundle = new Bundle();
                bundle.putString(AppConstant.VideoStatus, "1");
                bundle.putString(AppConstant.Video_mRoomId, "" + mRoomId);
                context.startActivityMy(Rout.VideoCallingActivity, bundle);
            }

            @Override
            public void onError(int i, String s) {
                KLog.d("???????????????????????????" + i + s);
            }
        });
        return inviteID;
    }


    //??????????????????
    public void sendLeftTextMsg(String hello) {
        if (TextUtils.isEmpty(hello)) {
            return;
        }
        SpannableStringBuilder replace = ChatUiHelper.handlerEmojiText(hello, true);

        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_RECEIVE_TEXT);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setSentStatus(MsgSendStatus.SENT);

        //????????????
        mAdapter.addData(mMessgae);
        //???????????????????????????
        updateMsg(mMessgae);

        KLog.d("??????????????????" + hello);
    }

    //??????????????????
    public void sendLeftTextMsg2(String hello) {
        if (TextUtils.isEmpty(hello)) {
            return;
        }
        SpannableStringBuilder replace = ChatUiHelper.handlerEmojiText(hello, true);

        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_RECEIVE_TEXT);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setSentStatus(MsgSendStatus.SENT);

        //????????????
        mAdapter.addData(0, mMessgae);

        KLog.d("??????????????????" + hello);
    }

    public void sendRightTextMsg(String hello) {

        if (TextUtils.isEmpty(hello)) {
            return;
        }

        SpannableStringBuilder replace = ChatUiHelper.handlerEmojiText(hello, true);

        Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
        mMessgae.setSenderId(mSenderId);

        //????????????
        mAdapter.addData(mMessgae);
        //???????????????????????????
        updateMsg(mMessgae);

        KLog.d("??????????????????" + hello);
    }

    public void sendRightTextMsg2(String hello) {
        if (TextUtils.isEmpty(hello)) {
            return;
        }

        SpannableStringBuilder replace = ChatUiHelper.handlerEmojiText(hello, true);
        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setSentStatus(MsgSendStatus.SENT);

        //????????????
        mAdapter.addData(0, mMessgae);

        KLog.d("???????????????right???" + hello);
    }

    public void takeLeftImgMsg(String imagePath) {

        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl(imagePath);
        // mImageMsgBody.setThumbPath(imagePath);
        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setType(ChatAdapter.TYPE_RECEIVE_IMAGE);

        //????????????
        mAdapter.addData(mMessgae);
        updateMsg(mMessgae);

    }

    public void takeLeftImgMsg2(String imagePath) {

        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl(imagePath);
        // mImageMsgBody.setThumbPath(imagePath);
        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setType(ChatAdapter.TYPE_RECEIVE_IMAGE);
        mMessgae.setSentStatus(MsgSendStatus.SENT);
        //????????????
        mAdapter.addData(0, mMessgae);
    }

    public void takeRightImgMsg(String imagePath) {

        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl(imagePath);
        // mImageMsgBody.setThumbPath(imagePath);
        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_SEND_IMAGE);

        //????????????
        mAdapter.addData(mMessgae);
        updateMsg(mMessgae);

    }

    public void takeRightImgMsg2(String imagePath) {

        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl(imagePath);
        // mImageMsgBody.setThumbPath(imagePath);
        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_SEND_IMAGE);
        mMessgae.setSentStatus(MsgSendStatus.SENT);

        //????????????
        mAdapter.addData(0, mMessgae);
    }


    //????????????
    public void sendLeftShopMessage(SendOffineMsgEntity.DataBean.ProductInfoBean hello) {

        final Message mMessgae = getBaseSendMessage(MsgType.KAPIAN);
        ShopMsgBody mImageMsgBody = new ShopMsgBody();
        mImageMsgBody.setGoodsName(hello.getItemName());
        mImageMsgBody.setGoodsCode(hello.getItemCode());
        mImageMsgBody.setGoodsIcon(hello.getShareImg());
        mImageMsgBody.setGoodsPrice(hello.getLastSalePrice());
        mImageMsgBody.setGoodsLinkApp(hello.getItemUrl());

        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setType(ChatAdapter.TYPE_KAPIAN_RECEIVE_TEXT);

        //????????????
        mAdapter.addData(mMessgae);
        //???????????????????????????
        updateMsg(mMessgae);

    }


    //???????????? ??????
    public void sRightShopMessage(ShopMsgBody shopMsgBody) {
        final Message mMessgae = getBaseSendMessage(MsgType.KAPIAN);

        mMessgae.setBody(shopMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_KAPIAN_SEND_TEXT);
        //????????????
        mAdapter.addData(mMessgae);

        //???????????????????????????
        updateMsg(mMessgae);
    }

    //???????????? ??????
    public void sRightShopMessage2(ShopMsgBody shopMsgBody) {
        final Message mMessgae = getBaseSendMessage(MsgType.KAPIAN);

        mMessgae.setBody(shopMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_KAPIAN_SEND_TEXT);
        mMessgae.setSentStatus(MsgSendStatus.SENT);
        //????????????
        mAdapter.addData(0, mMessgae);
    }

    //???????????? ??????
    public void sLeftShopMessage(ShopMsgBody shopMsgBody) {
        final Message mMessgae = getBaseSendMessage(MsgType.KAPIAN);

        mMessgae.setBody(shopMsgBody);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setType(ChatAdapter.TYPE_KAPIAN_RECEIVE_TEXT);
        //????????????
        mAdapter.addData(mMessgae);

        //???????????????????????????
        updateMsg(mMessgae);
    }

    //???????????? ??????
    public void sLeftShopMessage2(ShopMsgBody shopMsgBody) {
        final Message mMessgae = getBaseSendMessage(MsgType.KAPIAN);

        mMessgae.setBody(shopMsgBody);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setType(ChatAdapter.TYPE_KAPIAN_RECEIVE_TEXT);
        mMessgae.setSentStatus(MsgSendStatus.SENT);
        //????????????
        mAdapter.addData(0, mMessgae);
    }


    //????????????
    public void sendImageMessage(final LocalMedia media) {

        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl(media.getPath());
        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_SEND_IMAGE);
        //????????????
        mAdapter.addData(mMessgae);

        KLog.d("" + media.getPath());

        LogUtils.logd("????????????11???" + media.getPath());

        String cutPath;

        if (media.isCut()) {
            cutPath = media.getCutPath();
        } else {
            cutPath = media.getPath();
        }

        if (cutPath.contains("content://")) {
            Uri uri = Uri.parse(cutPath);
            cutPath = AppUtils.getFilePathByUri_BELOWAPI11(uri, context);
        }


        KLog.d("????????????" + cutPath);

        // ??????????????????
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createImageMessage("" + cutPath);
        // ??????????????????
        V2TIMManager.getMessageManager().sendMessage(v2TIMMessage, "" + fsUserId, null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null, new V2TIMSendCallback<V2TIMMessage>() {
            @Override
            public void onError(int code, String desc) {
                // ????????????????????????
                KLog.d("????????????????????????:" + code + "    ?????????" + desc);
            }

            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                // ????????????????????????
                KLog.d("????????????????????????");

                String faceUrl = v2TIMMessage.getFaceUrl();

                KLog.d("????????????????????????:" + faceUrl);

                //???????????????????????????
                updateMsg(mMessgae);

            }

            @Override
            public void onProgress(int progress) {
                // ?????????????????????0-100???
                // KLog.d("????????????????????????");
            }
        });

    }


    //????????????
    public void sendFileMessage(String from, String to, final String path) {
        final Message mMessgae = getBaseSendMessage(MsgType.FILE);
        FileMsgBody mFileMsgBody = new FileMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDisplayName(FileUtils.getFileName(path));
        mFileMsgBody.setSize(FileUtils.getFileLength(path));
        mMessgae.setBody(mFileMsgBody);
        //????????????
        mAdapter.addData(mMessgae);
        //???????????????????????????
        updateMsg(mMessgae);

    }


    //??????????????????
    public void sendDefaultMsg(String replace) {

//        replace="????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n" +
//                "?????????????????????????????????????????????????????????????????????????????? ";

        if (!TextUtils.isEmpty(replace)) {

//            int bstart=replace.indexOf("????????????");
//            int bend=bstart+"????????????".length();
//            SpannableStringBuilder style=new SpannableStringBuilder(replace);
//            style.setSpan(new BackgroundColorSpan(Color.RED),bstart,bend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }


        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_DF_TEXT);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setSentStatus(MsgSendStatus.SENT);
        //????????????
        mAdapter.addData(mMessgae);


    }

    //??????????????????
    public void sendTextDefaultMsg(String replace) {
        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setSentStatus(MsgSendStatus.SENT);
        //????????????
        mAdapter.addData(mMessgae);
    }


    //??????????????????
    public void sendCenterDefaultMsg(String replace) {
        final Message mMessgae = getBaseSendMessage(MsgType.CENTERMS);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_CENTER_TEXT);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setSentStatus(MsgSendStatus.DEFAULT);
        //????????????
        mAdapter.addData(mMessgae);
        rvChatList.scrollToPosition(mAdapter.getItemCount() - 1);
    }


    public Message getBaseSendMessage(MsgType msgType) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
        mMessgae.setSenderId(mSenderId);
        mMessgae.setTargetId(mTargetId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }

    public Message getBaseSendMessage22(MsgType msgType) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
        mMessgae.setSenderId(mSenderId);
        mMessgae.setTargetId(mTargetId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENT);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }

    //????????????
    public void sendAudioMessage(final String path, int time) {
        final Message mMessgae = getBaseSendMessage(MsgType.AUDIO);
        AudioMsgBody mFileMsgBody = new AudioMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDuration(time);
        mMessgae.setBody(mFileMsgBody);
        //????????????
        mAdapter.addData(mMessgae);
        //???????????????????????????
        updateMsg(mMessgae);
    }

    //??????????????????
    public void getMyTextMsg(String text) {
        SpannableStringBuilder replace = ChatUiHelper.handlerEmojiText(text, true);
        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(2);
        mMessgae.setSenderId(mTargetId);

        //????????????
        mAdapter.addData(mMessgae);
        updateMsg(mMessgae);
    }

    //????????????????????????
    public void sendVideoHintMsg(String content) {
        Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(content);
        mTextMsgBody.setVideo(true);
        mTextMsgBody.setCharsequence(content);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
        mMessgae.setSentStatus(MsgSendStatus.SENT);
        mAdapter.addData(mMessgae);
        updateMsg(mMessgae);
    }

    //????????????????????????
    public void sendVideoHintMsg2(String content) {
        Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(content);
        mTextMsgBody.setVideo(true);
        mTextMsgBody.setCharsequence(content);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
        mMessgae.setSentStatus(MsgSendStatus.SENT);
        mAdapter.addData(0, mMessgae);
    }

    //??????????????????
    public void takeImageMsg(V2TIMMessage msg) {

        V2TIMImageElem v2TIMImageElem = msg.getImageElem();

        if (v2TIMImageElem == null) {
            return;
        }

        // ?????????????????????????????????????????????????????????????????????????????????????????????(SDK????????????????????????????????????)
        // ??????????????????????????????????????????????????????????????????????????????720?????????
        // ?????????????????????????????????????????????????????????????????????????????????198?????????
        List<V2TIMImageElem.V2TIMImage> imageList = v2TIMImageElem.getImageList();

        KLog.d("??????????????????22???" + imageList.size());


        for (V2TIMImageElem.V2TIMImage v2TIMImage : imageList) {

            String uuid = v2TIMImage.getUUID(); // ?????? ID
            int imageType = v2TIMImage.getType(); // ????????????
            int size = v2TIMImage.getSize(); // ????????????????????????
            int width = v2TIMImage.getWidth(); // ????????????
            int height = v2TIMImage.getHeight(); // ????????????
            // ???????????????????????? imagePath?????????????????? uuid ?????????????????????????????????

            //String imagePath = AppApplicationMVVM.getInstance().getFilesDir().getAbsolutePath()+ "/image/download/"+ uuid;
            String imagePath = "/sdcard/im/image/" + "" + ImUtils.MyUserId + uuid;

            //String imagePath =  ""+ImConstant.myUserId + uuid;
            KLog.d("?????????????????????" + imagePath);

            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {


                v2TIMImage.downloadImage(imagePath, new V2TIMDownloadCallback() {
                    @Override
                    public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                        // ???????????????????????????????????? v2ProgressInfo.getCurrentSize()?????????????????? v2ProgressInfo.getTotalSize()
                    }

                    @Override
                    public void onError(int code, String desc) {
                        // ??????????????????
                        KLog.d("??????????????????:" + code + "   ?????????" + desc);

                    }

                    @Override
                    public void onSuccess() {
                        // ??????????????????

                        KLog.d("??????????????????");

                        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
                        ImageMsgBody mImageMsgBody = new ImageMsgBody();
                        // mImageMsgBody.setThumbUrl(imagePath);
                        mImageMsgBody.setThumbPath(imagePath);
                        mMessgae.setBody(mImageMsgBody);
                        mImageMsgBody.setUuid(uuid);
                        mMessgae.setSenderId(mTargetId);
                        mMessgae.setType(ChatAdapter.TYPE_RECEIVE_IMAGE);

                        mMessgae.setV2TIMImage(v2TIMImage);

                        //????????????
                        mAdapter.addData(mMessgae);
                        updateMsg(mMessgae);


                    }
                });
            } else {
                // ???????????????


                KLog.d("???????????????");

                final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);

                ImageMsgBody mImageMsgBody = new ImageMsgBody();
                mImageMsgBody.setThumbPath(imagePath);
                mImageMsgBody.setUuid(uuid);
                // mImageMsgBody.setThumbUrl(imagePath);
                mMessgae.setBody(mImageMsgBody);
                mMessgae.setSenderId(mTargetId);
                mMessgae.setType(ChatAdapter.TYPE_RECEIVE_IMAGE);
                mMessgae.setV2TIMImage(v2TIMImage);
                //????????????
                mAdapter.addData(mMessgae);
                updateMsg(mMessgae);


            }
        }

    }


    public void takeOcr(V2TIMMessage msg) {


        // ????????????
        V2TIMSoundElem v2TIMSoundElem = msg.getSoundElem();
        // ?????? ID,???????????????????????????????????? key
        String uuid = v2TIMSoundElem.getUUID();
        // ??????????????????
        int dataSize = v2TIMSoundElem.getDataSize();
        // ????????????
        int duration = v2TIMSoundElem.getDuration();
        // ???????????????????????? soundPath?????????????????? uuid ?????????????????????????????????
        String soundPath = "/sdcard/im/sound/" + "myUserID" + uuid;
        File imageFile = new File(soundPath);
        // ?????? soundPath ??????????????????????????????????????????
        if (!imageFile.exists()) {
            v2TIMSoundElem.downloadSound(soundPath, new V2TIMDownloadCallback() {
                @Override
                public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                    // ???????????????????????????????????? v2ProgressInfo.getCurrentSize()?????????????????? v2ProgressInfo.getTotalSize()
                }

                @Override
                public void onError(int code, String desc) {
                    // ????????????
                }

                @Override
                public void onSuccess() {
                    // ????????????
                }
            });
        } else {
            // ???????????????
        }


    }


    public void takeVideoMsg(V2TIMMessage msg) {


        // ????????????
        V2TIMVideoElem v2TIMVideoElem = msg.getVideoElem();
        // ???????????? ID,???????????????????????????????????? key
        String snapshotUUID = v2TIMVideoElem.getSnapshotUUID();
        // ????????????????????????
        int snapshotSize = v2TIMVideoElem.getSnapshotSize();
        // ???????????????
        int snapshotWidth = v2TIMVideoElem.getSnapshotWidth();
        // ???????????????
        int snapshotHeight = v2TIMVideoElem.getSnapshotHeight();
        // ?????? ID,???????????????????????????????????? key
        String videoUUID = v2TIMVideoElem.getVideoUUID();
        // ??????????????????
        int videoSize = v2TIMVideoElem.getVideoSize();
        // ????????????
        int duration = v2TIMVideoElem.getDuration();
        // ???????????????????????????????????????????????? uuid ?????????????????????????????????
        String snapshotPath = "/sdcard/im/snapshot/" + "myUserID" + snapshotUUID;
        File snapshotFile = new File(snapshotPath);
        if (!snapshotFile.exists()) {
            v2TIMVideoElem.downloadSnapshot(snapshotPath, new V2TIMDownloadCallback() {
                @Override
                public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                    // ???????????????????????????????????? v2ProgressInfo.getCurrentSize()?????????????????? v2ProgressInfo.getTotalSize()
                }

                @Override
                public void onError(int code, String desc) {
                    // ????????????
                }

                @Override
                public void onSuccess() {
                    // ????????????
                }
            });
        } else {
            // ???????????????
        }

        // ?????????????????????????????????????????? uuid ?????????????????????????????????
        String videoPath = "/sdcard/im/video/" + "myUserID" + snapshotUUID;
        File videoFile = new File(videoPath);
        if (!snapshotFile.exists()) {
            v2TIMVideoElem.downloadSnapshot(videoPath, new V2TIMDownloadCallback() {
                @Override
                public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                    // ???????????????????????????????????? v2ProgressInfo.getCurrentSize()?????????????????? v2ProgressInfo.getTotalSize()
                }

                @Override
                public void onError(int code, String desc) {
                    // ????????????
                }

                @Override
                public void onSuccess() {
                    // ????????????
                }
            });
        } else {
            // ???????????????
        }


    }


    //????????????
    public void sendVedioMessage(final LocalMedia media) {
        final Message mMessgae = getBaseSendMessage(MsgType.VIDEO);
        //?????????????????????
        String vedioPath = media.getPath();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(vedioPath);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
        String imgname = System.currentTimeMillis() + ".jpg";
        String urlpath = Environment.getExternalStorageDirectory() + "/" + imgname;
        File f = new File(urlpath);
        try {
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            KLog.d("????????????????????????????????????" + e.toString());
            e.printStackTrace();
        }
        VideoMsgBody mImageMsgBody = new VideoMsgBody();
        mImageMsgBody.setExtra(urlpath);
        mMessgae.setBody(mImageMsgBody);
        //????????????
        mAdapter.addData(mMessgae);
        //???????????????????????????
        updateMsg(mMessgae);

    }


    public void updateMsg(Message mMessgae) {

        if (mMessgae == null) {
            return;
        }

        rvChatList.scrollToPosition(mAdapter.getItemCount() - 1);
        //??????2??????????????????
        new Handler(context.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = 0;
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                //?????????????????????
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    Message mAdapterMessage = mAdapter.getData().get(i);
                    if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())) {
                        position = i;
                    }
                }
                mAdapter.notifyItemChanged(position);
            }
        }, 200);


//        //????????? haven ???????????????????????????
//        V2TIMManager.getMessageManager().markC2CMessageAsRead("user0", new V2TIMCallback() {
//            @Override
//            public void onError(int code, String desc) {
//                // ????????????????????????
//            }
//            @Override
//            public void onSuccess() {
//                // ????????????????????????
//            }
//        });
    }

    public void updateFailedMsg(Message mMessgae) {
        if (mMessgae == null) {
            return;
        }

        rvChatList.scrollToPosition(mAdapter.getItemCount() - 1);

        new Handler(context.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                int position = 0;
                mMessgae.setSentStatus(MsgSendStatus.FAILED);
                if(mAdapter!=null){
                    //?????????????????????
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        Message mAdapterMessage = mAdapter.getData().get(i);
                        if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())) {
                            position = i;
                        }
                    }

                    mAdapter.notifyItemChanged(position);
                }

            }
        }, 200);




    }


    public onNoOnclickListener noOnclickListener;//????????????????????????????????????
    public onYesOnclickListener yesOnclickListener;//????????????????????????????????????
    public onYesMsgOnclickListener yesMsgOnclickListener;//????????????????????????????????????

    public void setNoOnclickListener(onNoOnclickListener onNoOnclickListener) {

        this.noOnclickListener = onNoOnclickListener;
    }

    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {

        this.yesOnclickListener = onYesOnclickListener;
    }


    public interface onYesOnclickListener {
        void onYesClick(int st);
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }


    public void setYesMsgOnclickListener(onYesMsgOnclickListener onYesMsgOnclickListener) {

        this.yesMsgOnclickListener = onYesMsgOnclickListener;
    }


    public interface onYesMsgOnclickListener {
        void onYesMsgClick(boolean isMsgOk, int msgType);
    }


}
