package com.dfcj.videoim.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.dfcj.videoim.adapter.ChatAdapter;
import com.dfcj.videoim.entity.AudioMsgBody;
import com.dfcj.videoim.entity.FileMsgBody;
import com.dfcj.videoim.entity.ImageMsgBody;
import com.dfcj.videoim.entity.Message;
import com.dfcj.videoim.entity.MsgSendStatus;
import com.dfcj.videoim.entity.MsgType;
import com.dfcj.videoim.entity.TextMsgBody;
import com.dfcj.videoim.entity.VideoMsgBody;
import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.ChatUiHelper;
import com.dfcj.videoim.util.other.LogUtils;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMImageElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
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
import java.util.List;
import java.util.UUID;

public class ImUtils {

    public static final String 	  mSenderId="right";
    public static final String     mTargetId="left";
    private Context context;
    private ChatAdapter mAdapter;
    public boolean isLogin=false;
    public boolean isKeFuLogin=true;

    RecyclerView rvChatList;

    public static   String fsUserId="user0";
    public static String MyUserId="gudada";


    //public static  String fsUserId="gudada";
    //public static String MyUserId="user0";


    public ImUtils(Context context){
        this.context=context;
    }

    public void initViewInfo(ChatAdapter mAdapter,RecyclerView rvChatList){
        this.mAdapter=mAdapter;
        this.rvChatList=rvChatList;


    }


    public void initTencentImLogin(){

        KLog.d("sdk初始化调用了");


        // 1. 从 IM 控制台获取应用 SDKAppID，详情请参考 SDKAppID。
        // 2. 初始化 config 对象
        V2TIMSDKConfig config = new V2TIMSDKConfig();
        // 3. 指定 log 输出级别，详情请参考 SDKConfig。
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO);
        // 4. 初始化 SDK 并设置 V2TIMSDKListener 的监听对象。
        // initSDK 后 SDK 会自动连接网络，网络连接状态可以在 V2TIMSDKListener 回调里面监听。
        V2TIMManager.getInstance().initSDK(context,ImConstant.SDKAPPID,config);
        V2TIMManager.getInstance().addIMSDKListener(new V2TIMSDKListener() {
            @Override
            public void onConnecting() {
                super.onConnecting();
                // 正在连接到腾讯云服务器
               // ToastUtils.showShort("链接客服系统中");

                KLog.d("链接客服系统中");

            }

            @Override
            public void onConnectSuccess() {
                super.onConnectSuccess();
                // 已经成功连接到腾讯云服务器
               // ToastUtils.showLong("链接客服系统成功");



            }

            @Override
            public void onConnectFailed(int code, String error) {
                super.onConnectFailed(code, error);
                // 连接腾讯云服务器失败
              //  ToastUtils.showLong("链接客服失败");
            }

            @Override
            public void onKickedOffline() {
                super.onKickedOffline();
                //用户被踢下线
              //  ToastUtils.showLong("用户被踢下线");
            }

            @Override
            public void onSelfInfoUpdated(V2TIMUserFullInfo info) {
                super.onSelfInfoUpdated(info);
                //用户的资料发生了更新
               // ToastUtils.showLong("用户的资料发生了更新");
            }


        });

//        V2TIMManager.getInstance().initSDK(context, ImConstant.SDKAPPID, config, new V2TIMSDKListener() {
//            // 5. 监听 V2TIMSDKListener 回调
//            @Override
//            public void onConnecting() {
//                // 正在连接到腾讯云服务器
//               // ToastUtils.showShort("链接客服系统中");
//                KLog.d("链接客服系统中");
//            }
//            @Override
//            public void onConnectSuccess() {
//                // 已经成功连接到腾讯云服务器
//               // ToastUtils.showLong("链接客服系统成功");
//                KLog.d("链接客服系统成功");
//                loginIm();
//
//            }
//            @Override
//            public void onConnectFailed(int code, String error) {
//                // 连接腾讯云服务器失败
//               // ToastUtils.showLong("链接客服失败");
//                KLog.d("链接客服失败");
//            }
//
//            @Override
//            public void onKickedOffline() {
//                super.onKickedOffline();
//                //用户被踢下线
//                KLog.d("用户被踢下线");
//            }
//
//            @Override
//            public void onSelfInfoUpdated(V2TIMUserFullInfo info) {
//                super.onSelfInfoUpdated(info);
//                //用户的资料发生了更新
//                KLog.d("用户的资料发生了更新");
//            }
//        });


    }


    //登录
    public void loginIm() {


        V2TIMManager.getInstance().login(""+MyUserId, ImConstant.genTestUserSig(""+MyUserId), new V2TIMCallback() {
            @Override
            public void onSuccess() {
                KLog.d("登录成功");


                isLogin=true;

            }

            @Override
            public void onError(int i, String s) {
                KLog.d("登录失败");
                isLogin=false;
            }
        });

    }


    //文本消息
    public void sendTextMsg(String hello)  {

        if(TextUtils.isEmpty(hello)){
            return;
        }

        SpannableStringBuilder replace = ChatUiHelper.handlerEmojiText(hello,true);


        final Message mMessgae=getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody=new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(1);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        //   updateMsg(mMessgae);

        KLog.d("消息的内容："+hello);

        V2TIMManager.getInstance().sendC2CTextMessage(hello, ""+fsUserId, new V2TIMValueCallback<V2TIMMessage>() {
            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                updateMsg(mMessgae);
                KLog.d("消息发送成功");

            }

            @Override
            public void onError(int i, String s) {
                ToastUtils.showShort("发送失败");

                KLog.d("发送消息失败："+s + "        --"+i);

            }
        });


    }


    //文本自定义消息
    public void sendCustomerTextMsg(LocalMedia localMedia)  {


        String hello="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic_source%2F53%2F0a%2Fda%2F530adad966630fce548cd408237ff200.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1640336391&t=58e47be5791d9c1285e3116135ead99d";

        if(TextUtils.isEmpty(hello)){
            return;
        }

        final Message mMessgae=getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody=new ImageMsgBody();
        mImageMsgBody.setThumbUrl(hello);
        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_SEND_IMAGE);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        //   updateMsg(mMessgae);

        KLog.d("文本自定义消息："+hello);

        try {

            byte[] strs = hello.getBytes("UTF8");


            V2TIMManager.getInstance().sendC2CCustomMessage(strs, fsUserId, new V2TIMValueCallback<V2TIMMessage>() {
                @Override
                public void onSuccess(V2TIMMessage v2TIMMessage) {
                    updateMsg(mMessgae);
                    KLog.d("文本自定义消息发送成功");
                }

                @Override
                public void onError(int i, String s) {
                    KLog.d("文本自定义发送消息失败："+s + "        --"+i);
                }
            });




        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


    //接收自定义文本消息
    public void takeCustomerImageMsg(V2TIMMessage msg){

        V2TIMCustomElem v2TIMCustomElem = msg.getCustomElem();
        byte[] customData = v2TIMCustomElem.getData();

        try {

            String str = new String(customData, "UTF8");

            KLog.d("自定义消息接收内容："+str);


          /*  final Message mMessgae=getBaseSendMessage(MsgType.IMAGE);
            ImageMsgBody mImageMsgBody=new ImageMsgBody();
            // mImageMsgBody.setThumbUrl(imagePath);
            mImageMsgBody.setThumbPath(str);
            mMessgae.setBody(mImageMsgBody);
            mMessgae.setSenderId(mTargetId);
            mMessgae.setType(ChatAdapter.TYPE_RECEIVE_IMAGE);

            //开始发送
            mAdapter.addData(mMessgae);
            updateMsg(mMessgae);*/


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }






    //图片消息
    public void sendImageMessage(final LocalMedia media) {

        final Message mMessgae=getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody=new ImageMsgBody();
        mImageMsgBody.setThumbUrl(media.getPath());
        mMessgae.setBody(mImageMsgBody);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setType(ChatAdapter.TYPE_SEND_IMAGE);
        //开始发送
        mAdapter.addData( mMessgae);

        KLog.d(""+media.getPath());

        LogUtils.logd("拍照结果11："+media.getPath());

        String cutPath;

        if (media.isCut()) {
            cutPath=media.getCutPath();
        }else{
            cutPath= media.getPath();
        }

        if (cutPath.contains("content://")) {
            Uri uri = Uri.parse(cutPath);
            cutPath = AppUtils.getFilePathByUri_BELOWAPI11(uri, context);
        }


        KLog.d("新地址："+cutPath);

        // 创建图片消息
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createImageMessage(""+cutPath);
        // 发送图片消息
        V2TIMManager.getMessageManager().sendMessage(v2TIMMessage, ""+fsUserId, null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null,  new V2TIMSendCallback<V2TIMMessage>() {
            @Override
            public void onError(int code, String desc) {
                // 图片消息发送失败
                KLog.d("图片消息发送失败:"+code +"    详情："+desc);
            }
            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                // 图片消息发送成功
                KLog.d("图片消息发送成功");

                String faceUrl = v2TIMMessage.getFaceUrl();

                KLog.d("图片消息发送成功:"+faceUrl);

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
        final Message mMessgae=getBaseSendMessage(MsgType.FILE);
        FileMsgBody mFileMsgBody=new FileMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDisplayName(FileUtils.getFileName(path));
        mFileMsgBody.setSize(FileUtils.getFileLength(path));
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }


    //默认客服消息
    public void sendDefaultMsg(String replace){

        replace="您好，先由机器人客服东东为您服务，请简要、完整地输入您的问题，如：我的商品今天会发货吗？\n" +
                "若有其他问题，请在聊天对话框输入具体服务对应的数字： ";

        final Message mMessgae=getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody=new TextMsgBody();
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
    public void sendTextDefaultMsg(String replace){


        final Message mMessgae=getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody=new TextMsgBody();
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
    public void sendCenterDefaultMsg(String replace){


        final Message mMessgae=getBaseSendMessage(MsgType.CENTERMS);
        TextMsgBody mTextMsgBody=new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(ChatAdapter.TYPE_CENTER_TEXT);
        mMessgae.setSenderId(mSenderId);
        mMessgae.setSentStatus(MsgSendStatus.DEFAULT);
        //开始发送
        mAdapter.addData(mMessgae);


    }




    public Message getBaseSendMessage(MsgType msgType){
        Message mMessgae=new Message();
        mMessgae.setUuid(UUID.randomUUID()+"");
        mMessgae.setSenderId(mSenderId);
        mMessgae.setTargetId(mTargetId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }

    //语音消息
    public void sendAudioMessage(  final String path,int time) {
        final Message mMessgae=getBaseSendMessage(MsgType.AUDIO);
        AudioMsgBody mFileMsgBody=new AudioMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDuration(time);
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }

    //接受文本消息
    public void getMyTextMsg(String text){

        SpannableStringBuilder replace = ChatUiHelper.handlerEmojiText(text,true);

        final Message mMessgae=getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody=new TextMsgBody();
        mTextMsgBody.setMessage(replace.toString());
        mTextMsgBody.setCharsequence(replace);
        mMessgae.setBody(mTextMsgBody);
        mMessgae.setType(2);
        mMessgae.setSenderId(mTargetId);
        //开始发送
        mAdapter.addData(mMessgae);

        updateMsg(mMessgae);


    }

    //接收图片消息
    public void takeImageMsg(V2TIMMessage msg){

        V2TIMImageElem v2TIMImageElem = msg.getImageElem();

        if (v2TIMImageElem == null) {
            return ;
        }

        // 一个图片消息会包含三种格式大小的图片，分别为原图、大图、微缩图(SDK内部自动生成大图和微缩图)
        // 大图：是将原图等比压缩，压缩后宽、高中较小的一个等于720像素。
        // 缩略图：是将原图等比压缩，压缩后宽、高中较小的一个等于198像素。
        List<V2TIMImageElem.V2TIMImage> imageList = v2TIMImageElem.getImageList();

        KLog.d("收到图片路径22："+imageList.size());


        for (V2TIMImageElem.V2TIMImage v2TIMImage : imageList) {

            String uuid = v2TIMImage.getUUID(); // 图片 ID
            int imageType = v2TIMImage.getType(); // 图片类型
            int size = v2TIMImage.getSize(); // 图片大小（字节）
            int width = v2TIMImage.getWidth(); // 图片宽度
            int height = v2TIMImage.getHeight(); // 图片高度
            // 设置图片下载路径 imagePath，这里可以用 uuid 作为标识，避免重复下载

            //String imagePath = AppApplicationMVVM.getInstance().getFilesDir().getAbsolutePath()+ "/image/download/"+ uuid;
            String imagePath = "/sdcard/im/image/" + ""+ImConstant.myUserId + uuid;

            //String imagePath =  ""+ImConstant.myUserId + uuid;
            KLog.d("收到图片路径："+imagePath);

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
                        KLog.d("图片下载失败:"+code  +"   详情："+desc);

                    }
                    @Override
                    public void onSuccess() {
                        // 图片下载完成

                        KLog.d("图片下载完成");

                        final Message mMessgae=getBaseSendMessage(MsgType.IMAGE);
                        ImageMsgBody mImageMsgBody=new ImageMsgBody();
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

                final Message mMessgae=getBaseSendMessage(MsgType.IMAGE);

                ImageMsgBody mImageMsgBody=new ImageMsgBody();
                mImageMsgBody.setThumbPath(imagePath);
                mImageMsgBody.setUuid(uuid);
                // mImageMsgBody.setThumbUrl(imagePath);
                mMessgae.setBody(mImageMsgBody);
                mMessgae.setSenderId(mTargetId);
                mMessgae.setType(ChatAdapter.TYPE_RECEIVE_IMAGE);
                mMessgae.setV2TIMImage(v2TIMImage);
                //开始发送
                mAdapter.addData( mMessgae);
                updateMsg(mMessgae);



            }
        }

    }


    public  void takeOcr(V2TIMMessage msg){


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


    public void takeVideoMsg(V2TIMMessage msg){


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
        final Message mMessgae=getBaseSendMessage(MsgType.VIDEO);
        //生成缩略图路径
        String vedioPath=media.getPath();
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
        }catch ( Exception e) {
            KLog.d("视频缩略图路径获取失败："+e.toString());
            e.printStackTrace();
        }
        VideoMsgBody mImageMsgBody=new VideoMsgBody();
        mImageMsgBody.setExtra(urlpath);
        mMessgae.setBody(mImageMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }




    public void updateMsg(final Message mMessgae) {
        rvChatList.scrollToPosition(mAdapter.getItemCount() - 1);
        //模拟2秒后发送成功
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int position=0;
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                //更新单个子条目
                for (int i=0;i<mAdapter.getData().size();i++){
                    Message mAdapterMessage=mAdapter.getData().get(i);
                    if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())){
                        position=i;
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


}
