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
import com.tencent.imsdk.v2.V2TIMVideoElem;
import com.wzq.mvvmsmart.utils.KLog;

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

    //public static String fsUserId = "106584";//客服
    public static String fsUserId = SharedPrefsUtils.getValue(AppConstant.STAFF_CODE);//客服
    public static String MyUserId = SharedPrefsUtils.getValue(AppConstant.MYUSERID);//顾客

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
        KLog.d("sdk初始化调用了:" + ImConstant.SDKAPPID);

        // 1. 从 IM 控制台获取应用 SDKAppID，详情请参考 SDKAppID。
        // 2. 初始化 config 对象
        V2TIMSDKConfig config = new V2TIMSDKConfig();
        // 3. 指定 log 输出级别，详情请参考 SDKConfig。
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO);
        // 4. 初始化 SDK 并设置 V2TIMSDKListener 的监听对象。
        // initSDK 后 SDK 会自动连接网络，网络连接状态可以在 V2TIMSDKListener 回调里面监听。
        V2TIMManager.getInstance().initSDK(context, ImConstant.SDKAPPID, config);
        V2TIMManager.getInstance().addIMSDKListener(new V2TIMSDKListener() {
            @Override
            public void onConnecting() {
                super.onConnecting();
                // 正在连接到腾讯云服务器
                KLog.d("链接客服系统中");
            }

            @Override
            public void onConnectSuccess() {
                super.onConnectSuccess();
                // 已经成功连接到腾讯云服务器
                KLog.d("链接客服系统成功");
            }

            @Override
            public void onConnectFailed(int code, String error) {
                super.onConnectFailed(code, error);
                // 连接腾讯云服务器失败
                KLog.d("连接腾讯云服务器失败");
                ActivityUtils.finishActivity(MainActivity.class);
            }

            @Override
            public void onKickedOffline() {
                super.onKickedOffline();
                //用户被踢下线
                KLog.d("用户被踢下线");
                ActivityUtils.finishActivity(MainActivity.class);
            }

            @Override
            public void onSelfInfoUpdated(V2TIMUserFullInfo info) {
                super.onSelfInfoUpdated(info);
                //用户的资料发生了更新
                KLog.d("用户的资料发生了更新");

            }
        });


    }


    //登录
    public void loginIm() {
        initTencentImLogin();
        V2TIMManager.getInstance().login("" + MyUserId, ImConstant.genTestUserSig("" + MyUserId), new V2TIMCallback() {
            @Override
            public void onSuccess() {
                KLog.d("登录成功");

                isLogin = true;

                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(1);
                }
            }

            @Override
            public void onError(int i, String s) {
                KLog.d("登录失败:" + s);
                isLogin = false;


                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(2);
                }

            }
        });

    }


    private SpannableStringBuilder replace = null;
    private Message mMessgae = null;

    //所有的通用消息发送消息   //msgType  1文本  2 富文本   3带网址  4图片地址  5视频  6商品
    public void sendTextMsg(String hello, int msgType) {
        if (TextUtils.isEmpty(hello)) {
            return;
        }

        String cloudCustomData = SharedPrefsUtils.getValue(AppConstant.CloudCustomData);

        KLog.d("消息的内容：" + hello);
        KLog.d("消息的内容cloudCustomData：" + cloudCustomData);

        try {

            CustomMsgEntity customMsgEntity = new CustomMsgEntity();
            customMsgEntity.setMsgType(msgType);

            if (msgType == AppConstant.SEND_MSG_TYPE_TEXT) {//文本
                replace = ChatUiHelper.handlerEmojiText(hello, true);
                KLog.d("消息的内容replace：" + replace);

                mMessgae = getBaseSendMessage(MsgType.TEXT);
                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(replace.toString());
                mTextMsgBody.setCharsequence(replace);
                mMessgae.setBody(mTextMsgBody);
                mTextMsgBody.setVideo(false);
                mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);

                //开始发送
                mAdapter.addData(mMessgae);
                customMsgEntity.setMsgText("" + replace);

            } else if (msgType == AppConstant.SEND_MSG_TYPE_IMAGE) {//图片
                customMsgEntity.setImgUrl(hello);

                mMessgae = getBaseSendMessage(MsgType.IMAGE);
                ImageMsgBody mImageMsgBody = new ImageMsgBody();
                mImageMsgBody.setThumbUrl(hello);
                // mImageMsgBody.setThumbPath(imagePath);
                mMessgae.setBody(mImageMsgBody);
                mMessgae.setSenderId(mSenderId);
                mMessgae.setType(ChatAdapter.TYPE_SEND_IMAGE);
                //开始发送
                mAdapter.addData(mMessgae);

            } else if (msgType == AppConstant.SEND_MSG_TYPE_CARD) {//卡片
                customMsgEntity.setMsgText("" + hello);
                mMessgae = getBaseSendMessage(MsgType.KAPIAN);
                //发送商品需要打开此代码
                ShopMsgBody shopMsgBody = GsonUtil.newGson22().fromJson(hello, ShopMsgBody.class);
                mMessgae.setBody(shopMsgBody);
                mMessgae.setSenderId(mSenderId);
                mMessgae.setType(ChatAdapter.TYPE_KAPIAN_SEND_TEXT);
                //开始发送
                mAdapter.addData(mMessgae);

            } else if (msgType == AppConstant.SEND_VIDEO_TYPE_START) {//开始视频
                mMessgae = getBaseSendMessage(MsgType.TEXT);

                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(hello);
                mTextMsgBody.setVideo(true);
                mTextMsgBody.setCharsequence(hello);
                mMessgae.setBody(mTextMsgBody);

                mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
                customMsgEntity.setMsgText("" + hello);

            } else if (msgType == AppConstant.SEND_VIDEO_TYPE_CANCEL) {//取消视频
                mMessgae = getBaseSendMessage(MsgType.TEXT);
                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(hello);
                mTextMsgBody.setVideo(true);
                mTextMsgBody.setCharsequence(hello);
                mMessgae.setBody(mTextMsgBody);
                mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
                customMsgEntity.setMsgText("" + hello);

            } else if (msgType == AppConstant.SEND_VIDEO_TYPE_END) {//结束视频
                mMessgae = getBaseSendMessage(MsgType.TEXT);
                TextMsgBody mTextMsgBody = new TextMsgBody();
                mTextMsgBody.setMessage(hello);
                mTextMsgBody.setVideo(true);
                mTextMsgBody.setCharsequence(hello);
                mMessgae.setBody(mTextMsgBody);
                mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
                customMsgEntity.setMsgText("" + hello);

            } else if (msgType == AppConstant.SEND_VIDEO_TYPE_OVERTIME) {//视频超时
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

            V2TIMMessage customMessage = V2TIMManager.getMessageManager().createCustomMessage(strs);
            customMessage.setCloudCustomData(cloudCustomData);

            //是否只有在线用户才能收到，如果设置为 true ，接收方历史消息拉取不到，常被用于实现“对方正在输入”或群组里的非重要提示等弱提示功能
            KLog.d("发送成功：" + fsUserId);

            V2TIMManager.getMessageManager().sendMessage(customMessage, fsUserId, null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT,
                    false, new V2TIMOfflinePushInfo(), new V2TIMSendCallback<V2TIMMessage>() {
                        @Override
                        public void onProgress(int i) {
                        }

                        @Override
                        public void onSuccess(V2TIMMessage v2TIMMessage) {
                            KLog.d("发送成功：");
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
                            KLog.d("发送失败：" + s);
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


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


//        V2TIMManager.getInstance().sendC2CTextMessage(hello, ""+fsUserId, new V2TIMValueCallback<V2TIMMessage>() {
//            @Override
//            public void onSuccess(V2TIMMessage v2TIMMessage) {
//                updateMsg(mMessgae);
//                KLog.d("消息发送成功");
//
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                ToastUtils.showShort("发送失败");
//
//                KLog.d("发送消息失败："+s + "        --"+i);
//
//            }
//        });


    }

    public String callVideo(BaseActivity context, String mRoomId) {
        Map<String, String> data = new HashMap<>();
        data.put("roomId", mRoomId);
        data.put("userName", MyUserId);
        data.put("userImg", "");
        data.put("msgText", "开始视频");
        data.put("msgType", "5");
        String dataStr = GsonUtil.GsonString(data);
        V2TIMOfflinePushInfo v2TIMOfflinePushInfo = new V2TIMOfflinePushInfo();
        String inviteID = V2TIMManager.getSignalingManager().invite(fsUserId, dataStr, false, v2TIMOfflinePushInfo, 60, new V2TIMCallback() {
            @Override
            public void onSuccess() {
                KLog.d("拨打视频通话成功");
                Bundle bundle = new Bundle();
                bundle.putString(AppConstant.VideoStatus, "1");
                bundle.putString(AppConstant.Video_mRoomId, "" + mRoomId);
                context.startActivityMy(Rout.VideoCallingActivity, bundle);
            }

            @Override
            public void onError(int i, String s) {
                KLog.d("拨打视频通话异常：" + i + s);
            }
        });
        return inviteID;
    }


    //客服消息回复
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

        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

        KLog.d("消息的内容：" + hello);
    }

    //客服消息回复
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

        //开始发送
        mAdapter.addData(0, mMessgae);

        KLog.d("消息的内容：" + hello);
    }

    public void sendRightTextMsg(String hello) {

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

        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

        KLog.d("消息的内容：" + hello);
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

        //开始发送
        mAdapter.addData(0, mMessgae);

        KLog.d("消息的内容：" + hello);
    }

    public void takeLeftImgMsg(String imagePath) {

        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl(imagePath);
        // mImageMsgBody.setThumbPath(imagePath);
        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setType(ChatAdapter.TYPE_RECEIVE_IMAGE);

        //开始发送
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

        //开始发送
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

        //开始发送
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

        //开始发送
        mAdapter.addData(0, mMessgae);
    }


    //商品消息
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

        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }


    //商品消息 右边
    public void sRightShopMessage(ShopMsgBody shopMsgBody) {
        final Message mMessgae = getBaseSendMessage(MsgType.KAPIAN);

        mMessgae.setBody(shopMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_KAPIAN_SEND_TEXT);
        //开始发送
        mAdapter.addData(mMessgae);

        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }

    //商品消息 右边
    public void sRightShopMessage2(ShopMsgBody shopMsgBody) {
        final Message mMessgae = getBaseSendMessage(MsgType.KAPIAN);

        mMessgae.setBody(shopMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_KAPIAN_SEND_TEXT);
        //开始发送
        mAdapter.addData(0, mMessgae);
    }

    //商品消息 左边
    public void sLeftShopMessage(ShopMsgBody shopMsgBody) {
        final Message mMessgae = getBaseSendMessage(MsgType.KAPIAN);

        mMessgae.setBody(shopMsgBody);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setType(ChatAdapter.TYPE_KAPIAN_RECEIVE_TEXT);
        //开始发送
        mAdapter.addData(mMessgae);

        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }

    //商品消息 左边
    public void sLeftShopMessage2(ShopMsgBody shopMsgBody) {
        final Message mMessgae = getBaseSendMessage(MsgType.KAPIAN);

        mMessgae.setBody(shopMsgBody);
        mMessgae.setSenderId(mTargetId);
        mMessgae.setType(ChatAdapter.TYPE_KAPIAN_RECEIVE_TEXT);
        //开始发送
        mAdapter.addData(0, mMessgae);
    }


    //图片消息
    public void sendImageMessage(final LocalMedia media) {

        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl(media.getPath());
        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_SEND_IMAGE);
        //开始发送
        mAdapter.addData(mMessgae);

        KLog.d("" + media.getPath());

        LogUtils.logd("拍照结果11：" + media.getPath());

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


        KLog.d("新地址：" + cutPath);

        // 创建图片消息
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createImageMessage("" + cutPath);
        // 发送图片消息
        V2TIMManager.getMessageManager().sendMessage(v2TIMMessage, "" + fsUserId, null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null, new V2TIMSendCallback<V2TIMMessage>() {
            @Override
            public void onError(int code, String desc) {
                // 图片消息发送失败
                KLog.d("图片消息发送失败:" + code + "    详情：" + desc);
            }

            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                // 图片消息发送成功
                KLog.d("图片消息发送成功");

                String faceUrl = v2TIMMessage.getFaceUrl();

                KLog.d("图片消息发送成功:" + faceUrl);

                //模拟两秒后发送成功
                updateMsg(mMessgae);

            }

            @Override
            public void onProgress(int progress) {
                // 图片上传进度（0-100）
                // KLog.d("图片消息发送失败");
            }
        });

    }


    //文件消息
    public void sendFileMessage(String from, String to, final String path) {
        final Message mMessgae = getBaseSendMessage(MsgType.FILE);
        FileMsgBody mFileMsgBody = new FileMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDisplayName(FileUtils.getFileName(path));
        mFileMsgBody.setSize(FileUtils.getFileLength(path));
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }


    //默认客服消息
    public void sendDefaultMsg(String replace) {

//        replace="您好，先由机器人客服东东为您服务，请简要、完整地输入您的问题，如：我的商品今天会发货吗？\n" +
//                "若有其他问题，请在聊天对话框输入具体服务对应的数字： ";

        if (!TextUtils.isEmpty(replace)) {

//            int bstart=replace.indexOf("人工客服");
//            int bend=bstart+"人工客服".length();
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
        //开始发送
        mAdapter.addData(mMessgae);


    }

    //默认本地消息
    public void sendTextDefaultMsg(String replace) {
        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setSentStatus(MsgSendStatus.DEFAULT);
        //开始发送
        mAdapter.addData(mMessgae);
    }


    //中间默认消息
    public void sendCenterDefaultMsg(String replace) {
        final Message mMessgae = getBaseSendMessage(MsgType.CENTERMS);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_CENTER_TEXT);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setSentStatus(MsgSendStatus.DEFAULT);
        //开始发送
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

    //语音消息
    public void sendAudioMessage(final String path, int time) {
        final Message mMessgae = getBaseSendMessage(MsgType.AUDIO);
        AudioMsgBody mFileMsgBody = new AudioMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDuration(time);
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }

    //接受文本消息
    public void getMyTextMsg(String text) {
        SpannableStringBuilder replace = ChatUiHelper.handlerEmojiText(text, true);
        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(2);
        mMessgae.setSenderId(mTargetId);

        //开始发送
        mAdapter.addData(mMessgae);
        updateMsg(mMessgae);
    }

    //发送视频提示消息
    public void sendVideoHintMsg(String content) {
        Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(content);
        mTextMsgBody.setVideo(true);
        mTextMsgBody.setCharsequence(content);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);

        mAdapter.addData(mMessgae);
        updateMsg(mMessgae);
    }

    //发送视频提示消息
    public void sendVideoHintMsg2(String content) {
        Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(content);
        mTextMsgBody.setVideo(true);
        mTextMsgBody.setCharsequence(content);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_SEND_TEXT);

        mAdapter.addData(0, mMessgae);
    }

    //接收图片消息
    public void takeImageMsg(V2TIMMessage msg) {

        V2TIMImageElem v2TIMImageElem = msg.getImageElem();

        if (v2TIMImageElem == null) {
            return;
        }

        // 一个图片消息会包含三种格式大小的图片，分别为原图、大图、微缩图(SDK内部自动生成大图和微缩图)
        // 大图：是将原图等比压缩，压缩后宽、高中较小的一个等于720像素。
        // 缩略图：是将原图等比压缩，压缩后宽、高中较小的一个等于198像素。
        List<V2TIMImageElem.V2TIMImage> imageList = v2TIMImageElem.getImageList();

        KLog.d("收到图片路径22：" + imageList.size());


        for (V2TIMImageElem.V2TIMImage v2TIMImage : imageList) {

            String uuid = v2TIMImage.getUUID(); // 图片 ID
            int imageType = v2TIMImage.getType(); // 图片类型
            int size = v2TIMImage.getSize(); // 图片大小（字节）
            int width = v2TIMImage.getWidth(); // 图片宽度
            int height = v2TIMImage.getHeight(); // 图片高度
            // 设置图片下载路径 imagePath，这里可以用 uuid 作为标识，避免重复下载

            //String imagePath = AppApplicationMVVM.getInstance().getFilesDir().getAbsolutePath()+ "/image/download/"+ uuid;
            String imagePath = "/sdcard/im/image/" + "" + ImUtils.MyUserId + uuid;

            //String imagePath =  ""+ImConstant.myUserId + uuid;
            KLog.d("收到图片路径：" + imagePath);

            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {


                v2TIMImage.downloadImage(imagePath, new V2TIMDownloadCallback() {
                    @Override
                    public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                        // 图片下载进度：已下载大小 v2ProgressInfo.getCurrentSize()；总文件大小 v2ProgressInfo.getTotalSize()
                    }

                    @Override
                    public void onError(int code, String desc) {
                        // 图片下载失败
                        KLog.d("图片下载失败:" + code + "   详情：" + desc);

                    }

                    @Override
                    public void onSuccess() {
                        // 图片下载完成

                        KLog.d("图片下载完成");

                        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
                        ImageMsgBody mImageMsgBody = new ImageMsgBody();
                        // mImageMsgBody.setThumbUrl(imagePath);
                        mImageMsgBody.setThumbPath(imagePath);
                        mMessgae.setBody(mImageMsgBody);
                        mImageMsgBody.setUuid(uuid);
                        mMessgae.setSenderId(mTargetId);
                        mMessgae.setType(ChatAdapter.TYPE_RECEIVE_IMAGE);

                        mMessgae.setV2TIMImage(v2TIMImage);

                        //开始发送
                        mAdapter.addData(mMessgae);
                        updateMsg(mMessgae);


                    }
                });
            } else {
                // 图片已存在


                KLog.d("图片已存在");

                final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);

                ImageMsgBody mImageMsgBody = new ImageMsgBody();
                mImageMsgBody.setThumbPath(imagePath);
                mImageMsgBody.setUuid(uuid);
                // mImageMsgBody.setThumbUrl(imagePath);
                mMessgae.setBody(mImageMsgBody);
                mMessgae.setSenderId(mTargetId);
                mMessgae.setType(ChatAdapter.TYPE_RECEIVE_IMAGE);
                mMessgae.setV2TIMImage(v2TIMImage);
                //开始发送
                mAdapter.addData(mMessgae);
                updateMsg(mMessgae);


            }
        }

    }


    public void takeOcr(V2TIMMessage msg) {


        // 语音消息
        V2TIMSoundElem v2TIMSoundElem = msg.getSoundElem();
        // 语音 ID,内部标识，可用于外部缓存 key
        String uuid = v2TIMSoundElem.getUUID();
        // 语音文件大小
        int dataSize = v2TIMSoundElem.getDataSize();
        // 语音时长
        int duration = v2TIMSoundElem.getDuration();
        // 设置语音文件路径 soundPath，这里可以用 uuid 作为标识，避免重复下载
        String soundPath = "/sdcard/im/sound/" + "myUserID" + uuid;
        File imageFile = new File(soundPath);
        // 判断 soundPath 下有没有已经下载过的语音文件
        if (!imageFile.exists()) {
            v2TIMSoundElem.downloadSound(soundPath, new V2TIMDownloadCallback() {
                @Override
                public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                    // 下载进度回调：已下载大小 v2ProgressInfo.getCurrentSize()；总文件大小 v2ProgressInfo.getTotalSize()
                }

                @Override
                public void onError(int code, String desc) {
                    // 下载失败
                }

                @Override
                public void onSuccess() {
                    // 下载完成
                }
            });
        } else {
            // 文件已存在
        }


    }


    public void takeVideoMsg(V2TIMMessage msg) {


        // 视频消息
        V2TIMVideoElem v2TIMVideoElem = msg.getVideoElem();
        // 视频截图 ID,内部标识，可用于外部缓存 key
        String snapshotUUID = v2TIMVideoElem.getSnapshotUUID();
        // 视频截图文件大小
        int snapshotSize = v2TIMVideoElem.getSnapshotSize();
        // 视频截图宽
        int snapshotWidth = v2TIMVideoElem.getSnapshotWidth();
        // 视频截图高
        int snapshotHeight = v2TIMVideoElem.getSnapshotHeight();
        // 视频 ID,内部标识，可用于外部缓存 key
        String videoUUID = v2TIMVideoElem.getVideoUUID();
        // 视频文件大小
        int videoSize = v2TIMVideoElem.getVideoSize();
        // 视频时长
        int duration = v2TIMVideoElem.getDuration();
        // 设置视频截图文件路径，这里可以用 uuid 作为标识，避免重复下载
        String snapshotPath = "/sdcard/im/snapshot/" + "myUserID" + snapshotUUID;
        File snapshotFile = new File(snapshotPath);
        if (!snapshotFile.exists()) {
            v2TIMVideoElem.downloadSnapshot(snapshotPath, new V2TIMDownloadCallback() {
                @Override
                public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                    // 下载进度回调：已下载大小 v2ProgressInfo.getCurrentSize()；总文件大小 v2ProgressInfo.getTotalSize()
                }

                @Override
                public void onError(int code, String desc) {
                    // 下载失败
                }

                @Override
                public void onSuccess() {
                    // 下载完成
                }
            });
        } else {
            // 文件已存在
        }

        // 设置视频文件路径，这里可以用 uuid 作为标识，避免重复下载
        String videoPath = "/sdcard/im/video/" + "myUserID" + snapshotUUID;
        File videoFile = new File(videoPath);
        if (!snapshotFile.exists()) {
            v2TIMVideoElem.downloadSnapshot(videoPath, new V2TIMDownloadCallback() {
                @Override
                public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                    // 下载进度回调：已下载大小 v2ProgressInfo.getCurrentSize()；总文件大小 v2ProgressInfo.getTotalSize()
                }

                @Override
                public void onError(int code, String desc) {
                    // 下载失败
                }

                @Override
                public void onSuccess() {
                    // 下载完成
                }
            });
        } else {
            // 文件已存在
        }


    }


    //视频消息
    public void sendVedioMessage(final LocalMedia media) {
        final Message mMessgae = getBaseSendMessage(MsgType.VIDEO);
        //生成缩略图路径
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
            KLog.d("视频缩略图路径获取失败：" + e.toString());
            e.printStackTrace();
        }
        VideoMsgBody mImageMsgBody = new VideoMsgBody();
        mImageMsgBody.setExtra(urlpath);
        mMessgae.setBody(mImageMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }


    public void updateMsg(Message mMessgae) {

        if (mMessgae == null) {
            return;
        }

        rvChatList.scrollToPosition(mAdapter.getItemCount() - 1);
        //模拟2秒后发送成功
        new Handler(context.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = 0;
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                //更新单个子条目
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    Message mAdapterMessage = mAdapter.getData().get(i);
                    if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())) {
                        position = i;
                    }
                }
                mAdapter.notifyItemChanged(position);
            }
        }, 500);


//        //将来自 haven 的消息均标记为已读
//        V2TIMManager.getMessageManager().markC2CMessageAsRead("user0", new V2TIMCallback() {
//            @Override
//            public void onError(int code, String desc) {
//                // 设置消息已读失败
//            }
//            @Override
//            public void onSuccess() {
//                // 设置消息已读成功
//            }
//        });
    }

    public void updateFailedMsg(Message mMessgae) {
        if (mMessgae == null) {
            return;
        }

        int position = 0;
        mMessgae.setSentStatus(MsgSendStatus.FAILED);

        //更新单个子条目
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            Message mAdapterMessage = mAdapter.getData().get(i);
            if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())) {
                position = i;
            }
        }

        mAdapter.notifyItemChanged(position);
    }


    public onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    public onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器
    public onYesMsgOnclickListener yesMsgOnclickListener;//确定按钮被点击了的监听器

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
