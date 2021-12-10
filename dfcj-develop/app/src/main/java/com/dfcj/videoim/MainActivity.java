package com.dfcj.videoim;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.util.StringUtil;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.dfcj.videoim.adapter.ChatAdapter;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.entity.BitRateBean;
import com.dfcj.videoim.entity.ChangeCustomerServiceEntity;
import com.dfcj.videoim.entity.CustomMsgEntity;
import com.dfcj.videoim.entity.EventMessage;
import com.dfcj.videoim.entity.HistoryMsgEntity;
import com.dfcj.videoim.entity.LoginBean;
import com.dfcj.videoim.entity.Message;
import com.dfcj.videoim.entity.MsgBodyBean;
import com.dfcj.videoim.entity.RoomIdEntity;
import com.dfcj.videoim.entity.SendOffineMsgEntity;
import com.dfcj.videoim.entity.ShopMsgBody;
import com.dfcj.videoim.entity.TrtcRoomEntity;
import com.dfcj.videoim.entity.UserInfoEntity;
import com.dfcj.videoim.entity.upLoadImgEntity;
import com.dfcj.videoim.im.ImConstant;
import com.dfcj.videoim.im.ImUtils;
import com.dfcj.videoim.im.ocr.OcrUtil;
import com.dfcj.videoim.ui.video.VideoCallingActivity;
import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.ChatUiHelper;
import com.dfcj.videoim.util.ImageUtils;
import com.dfcj.videoim.util.MyDialogUtil;
import com.dfcj.videoim.util.PermissionUtil;
import com.dfcj.videoim.util.PictureFileUtil;
import com.dfcj.videoim.util.other.EventBusUtils;
import com.dfcj.videoim.util.other.GsonUtil;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.dfcj.videoim.view.dialog.OcrMsgEditDialog;
import com.dfcj.videoim.view.dialog.QuanXianDialog;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.base.BaseActivity;

import com.dfcj.videoim.databinding.MainLayoutBinding;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMFaceElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageListGetOption;
import com.tencent.imsdk.v2.V2TIMSignalingListener;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.iot.speech.asr.listener.MessageListener;
import com.wzq.mvvmsmart.base.AppManagerMVVM;
import com.wzq.mvvmsmart.event.StateLiveData;
import com.wzq.mvvmsmart.utils.KLog;
import com.wzq.mvvmsmart.utils.ToastUtils;
import com.zzhoujay.richtext.RichText;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;


@Route(path = Rout.toMain)
public class MainActivity extends BaseActivity<MainLayoutBinding, MainActivityViewModel> {

    // @Autowired
    //int age;

    private String myOcrVal = "";
    public static final String mSenderId = "right";
    public static final String mTargetId = "left";
    public static final String mCenterId = "center";
    public static final int REQUEST_CODE_IMAGE = 0000;
    public static final int REQUEST_CODE_VEDIO = 1111;
    public static final int REQUEST_CODE_IMAGE_VIDEO = 9999;

    private float y;
    private ChatAdapter mAdapter;

    private ImUtils imUtils;
    private OcrUtil ocrUtil;

    public boolean isVideo = true;
    private ChatUiHelper mUiHelper;
    private boolean isSendMsg = false;
    private String loadEventMsg;
    private String cloudCustomData;
    private boolean isVidesClick = false;
    private ImageUtils imageUtils;
    private String mRoomId;
    private String myEventId;

    public int historyPage = 1;
    private List<HistoryMsgEntity.DataDTO.DataDTO2> historyMsgEntityList = new ArrayList<>();

    //设定app传递数据
    private String token;
    private ShopMsgBody shopMsgBody;
    private boolean showShopCard = true;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.main_layout;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViews() {
        super.initViews();
        setAppValue();

        initHttp();

        //第一次设置缓存位置
        RichText.initCacheDir(this);

        imUtils = new ImUtils(mContext);
        imageUtils = new ImageUtils();
        ocrUtil = new OcrUtil(mContext);
        ocrUtil.initInfo(MainActivity.this);


    }

    @Override
    public void initData() {
        super.initData();
        binding.setPresenter(new Presenter());

        //requestPermisson();

        initRv();
        imUtils.initViewInfo(mAdapter, binding.rvChatList);

        initOnCLick();
        initChatUi();
        //takeMsgInfo();
        takeImagMsg();
        setOcrListener();
        //setVideoListener();
        ocrUtil.initOcr();

    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();

        getHistoryMessageList();

        setMyListener();

        getCustomerInfo();
    }

    private void setAppValue() {
        token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMDA4MDEwMDAwNDQiLCJpc3MiOiJvY2otc3RhcnNreSIsImxvZ2lkIjoiNjg3NDYyNzk0OTA5MDMxMjE5MiIsImV4cCI6MTY0NjgxNTAyNywiaWF0IjoxNjM5MDM5MDI3LCJkZXZpY2VpZCI6IiJ9.hNcsiYer2GsAA_TbqPT8vyNrW1rfdVfV4YTbMs-Rrho";

        shopMsgBody = new ShopMsgBody();
        shopMsgBody.setGoodsIcon("https://t7.baidu.com/it/u=793426911,3641399153&fm=218&app=126&f=JPEG?w=121&h=75&s=DEA0546E36517A77458B2750020030FA");
        shopMsgBody.setGoodsCode("1001001");
        shopMsgBody.setGoodsName("资生堂悦薇珀翡紧颜亮肤乳（滋润型）100ml");
        shopMsgBody.setGoodsPrice("20.00");

        binding.mainShopLayout.setVisibility(showShopCard ? View.VISIBLE : View.GONE);

        Glide.with(MainActivity.this)
                .load(shopMsgBody.getGoodsIcon())
                .error(R.drawable.default_img_failed)
                .into(binding.mainLayoutShopImg);
        binding.mianLayoutShopNumTv.setText(shopMsgBody.getGoodsCode());
        binding.mianLayoutShopTitleTv.setText(shopMsgBody.getGoodsName());
        binding.mianLayoutShopPriceTv.setText(shopMsgBody.getGoodsPrice());

    }


    private void setMyListener() {

        imUtils.setYesOnclickListener(new ImUtils.onYesOnclickListener() {
            @Override
            public void onYesClick(int st) {
                if (st == 1) {
                    binding.ivVideo.setImageResource(R.drawable.selector_ctype_video);
                    isVidesClick = true;
                    KLog.d("onYesClick");
                }
            }
        });

        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                switch (view.getId()) {
                    case R.id.goods_layout:
                        break;
                }
            }
        });


    }

    //获取顾客信息
    private void getCustomerInfo() {
        viewModel.getCustomerInfo(token);

    }


    //登录
    private void login() {
        viewModel.requestLogin();


    }


    //未连接客服回复消息
    private void sendOffineMsg(String msg) {

        imUtils.sendTextDefaultMsg("" + msg);

        viewModel.sendOfflineMsg("" + msg);


    }


    private void requestPermisson() {

        new RxPermissions(this)
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.READ_PHONE_STATE

                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {

                        }
                    }
                });

    }


    private void initRv() {

        mAdapter = new ChatAdapter(new ArrayList<Message>());
        //binding.setAdapter(mAdapter);
        binding.setAdapter(mAdapter);
        binding.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initOnCLick() {
        binding.rvChatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // OnScrollListener.SCROLL_STATE_IDLE; //停止滑动状态
                // 记录当前滑动状态
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //当前状态为停止滑动
                    if (!binding.rvChatList.canScrollVertically(1)) { // 到达底部
                        KLog.d("到达底部");

                    } else if (!binding.rvChatList.canScrollVertically(-1)) { // 到达顶部
                        KLog.d("到顶了");

                        if (historyMsgEntityList.size() > 19 && binding.mainTopProgress.getVisibility() == View.GONE) {
                            binding.mainTopProgress.setVisibility(View.VISIBLE);
                            viewModel.getCustImRecord(historyPage++, TimeUtils.getNowMills() + "");
                        }
                    }
                }
            }
        });


    }


    /**
     * 封装布局中的点击事件儿;
     */
    public class Presenter {

        //发送
        public void btn_send() {
            String sdMsg = binding.etContent.getText().toString().trim();
            if (TextUtils.isEmpty(sdMsg)) {
                ToastUtils.showShort("请输入内容");
                return;
            }
            KLog.d("输入的内容：" + sdMsg);

            if (!isSendMsg) {//未登录链接会话
                sendOffineMsg("" + sdMsg);

            } else {//已链接会话
                imUtils.sendTextMsg(sdMsg, AppConstant.SEND_MSG_TYPE_TEXT);

            }

            binding.etContent.setText("");
        }

        //发送商品
        public void send_shop() {
            imUtils.sendTextMsg(GsonUtil.newGson22().toJson(shopMsgBody), AppConstant.SEND_MSG_TYPE_CARD);
        }

        //关闭商品
        public void close_shop() {
            binding.mainShopLayout.setVisibility(View.GONE);
        }

        //转人工
        public void transferToLabor() {
            if (!isSendMsg) {
                getChangeToLable();
            }
        }

        //语音点击
        public void ivAudioClick() {

            binding.ocrLayout.setVisibility(View.VISIBLE);
            binding.mainInputMsgLayout.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.GONE);


            mUiHelper.hideBottomLayout(false);
            mUiHelper.hideSoftInput();

        }

        //语音界面取消
        public void closeOcr() {

            myOcrVal = "";

            binding.ocrLayout.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.GONE);
            binding.mainInputMsgLayout.setVisibility(View.VISIBLE);
        }

        //语音识别
        public void speechRecognition() {
            ocrUtil.startRecording();
        }

        //取消语音
        public void clearOcr() {
            setOcrStop();
        }

        //语音发送
        public void sendOcr() {

            if (TextUtils.isEmpty(binding.mainOcrContentTv.getText().toString().trim())) {
                ToastUtils.showShort("请使用语言输入内容");
                return;
            }

            imUtils.sendTextMsg(binding.mainOcrContentTv.getText().toString().trim(), AppConstant.SEND_MSG_TYPE_TEXT);
            binding.mainOcrContentTv.setText("");
        }

        //相册
        public void toImgPic() {
            KLog.d("相册点击");

            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

            PictureFileUtil.openGalleryPic(MainActivity.this, REQUEST_CODE_IMAGE);
        }

        //相机
        public void toImgVideoPic() {
            KLog.d("拍照点击");

            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.CAMERA);
            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);


            PictureFileUtil.openCameraInfo(MainActivity.this, REQUEST_CODE_IMAGE_VIDEO);

        }

        //视频 点击
        public void toVideo() {
            KLog.d("视频点击");
            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

            boolean permission3 = AppUtils.isPermission(mContext, Manifest.permission.CAMERA);
            boolean permission4 = AppUtils.isPermission(mContext, Manifest.permission.RECORD_AUDIO);

            if (!permission3 || !permission4) {
                sPermission();
                return;
            }

            if (imUtils.isLogin) {
                if (isSendMsg) {
                    getTrtcRoomId();
                } else {
                    imUtils.sendCenterDefaultMsg(loadEventMsg);
                }
            }
        }

        //ocr 编辑
        public void ocrEditClick() {
            KLog.d("视频点击");
            ocrEditMsgDialog();

        }

        public void languageSel() {
            KLog.d("语言选择点击");

            //showMyLanguageDialog();

        }

    }

    //获取房间号
    private void getTrtcRoomId() {
        viewModel.getTrtcRoomId(myEventId);

    }


    //获取人工客服
    private void getChangeToLable() {
        // imUtils.sendTextDefaultMsg("1");
        viewModel.getImStaff();


    }


    private void ocrEditMsgDialog() {


        OcrMsgEditDialog fxdialog = MyDialogUtil.ocrEditMsgDialog(binding.mainOcrContentTv.getText().toString());
        fxdialog.show(getSupportFragmentManager(), OcrMsgEditDialog.class.getName());
        fxdialog.setYesOnclickListener(new OcrMsgEditDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String tl) {
                if (!TextUtils.isEmpty(tl)) {
                    binding.mainOcrContentTv.setText(tl);

                } else {
                    binding.mainOcrContentTv.setText("");
                    binding.mainOcrContentTv.setHint("请说点什么吧~");
                }
            }
        });

    }


    //相机权限
    private void authorityDialog() {

        QuanXianDialog fxdialog = MyDialogUtil.authorityDialog();
        fxdialog.show(getSupportFragmentManager(), QuanXianDialog.class.getName());
        fxdialog.setYesOnclickListener(new QuanXianDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                PermissionUtil.gotoPermission(mContext);
            }
        });

    }


    private void initChatUi() {

        mUiHelper = ChatUiHelper.with(this);
        mUiHelper.bindContentLayout(binding.llContent)
                .bindttToSendButton(binding.btnSend)
                .bindEditText(binding.etContent)
                .bindBottomLayout(binding.bottomLayout)
                .bindEmojiLayout(binding.rlEmotion)
                .bindAddLayout(binding.llAdd)
                .bindToAddButton(binding.ivAdd)
                .bindToEmojiButton(binding.ivEmo)
                .bindAudioBtn(binding.btnAudio)
                .bindAudioIv(binding.ivAudio);
        //.bindEmojiData();

        mUiHelper.bindEmojiData();

        //底部布局弹出,聊天列表上滑
        binding.rvChatList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    binding.rvChatList.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAdapter.getItemCount() > 0) {
                                binding.rvChatList.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                    });
                }
            }
        });

        //点击空白区域关闭键盘
        binding.rvChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mUiHelper.hideBottomLayout(false);
                mUiHelper.hideSoftInput();
                binding.etContent.clearFocus();
                binding.ivEmo.setImageResource(R.drawable.ic_emoji);
                return false;
            }
        });

        //语音识别
        binding.mainOcrBtnImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                y = motionEvent.getY();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:

                        KLog.d("手指按下");

                        if (!TextUtils.isEmpty(binding.mainOcrContentTv.getText().toString())) {
                            myOcrVal = binding.mainOcrContentTv.getText().toString();
                        }

                        binding.mainOcrSelImg.setVisibility(View.GONE);
                        binding.mainOcrCancelTv.setVisibility(View.VISIBLE);
                        binding.mainOcrSendTv.setVisibility(View.VISIBLE);
                        binding.mainlayoutPtLayout.setVisibility(View.GONE);
                        binding.mainOcrContentTv.setVisibility(View.VISIBLE);

                        binding.mainOcrCkTv.setText("按住说话");

                        ocrUtil.startRecording();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        KLog.d("手指抬起");
                        ocrUtil.stopOcr();

                        break;
                }

                return true;
            }
        });


    }


    private void setOcrStop() {

        binding.mainOcrCkTv.setText("按住说话");
        binding.mainOcrSelImg.setVisibility(View.VISIBLE);
        binding.mainOcrCancelTv.setVisibility(View.GONE);
        binding.mainOcrSendTv.setVisibility(View.GONE);
        binding.mainlayoutPtLayout.setVisibility(View.VISIBLE);
        binding.mainOcrContentTv.setVisibility(View.GONE);
        binding.mainOcrContentTv.setText("");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE_VIDEO://相机

                    List<LocalMedia> result2 = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = result2.get(0);
                    KLog.d("获取图片路径成功:" + localMedia.getPath());

                    uploadImg(localMedia);

                    break;
                case REQUEST_CODE_IMAGE://相册
                    // 图片选择结果回调
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);

                    for (LocalMedia media : selectListPic) {
                        KLog.d("获取图片路径成功:" + media.getPath());
                        // imUtils.sendImageMessage(media);

                        uploadImg(media);
                    }
                    break;
                case REQUEST_CODE_VEDIO:
                    break;
            }
        }

    }

    //上传图片到服务
    private void uploadImg(LocalMedia media) {
        showLoading("发送中");

        String path2 = media.getPath();
        KLog.d("获取图片路径成功path:" + path2);

        initCompressorRxJava(path2);


    }

    private void initCompressorRxJava(String path) {

        imageUtils.initCompressorRxJava(this, path);
        imageUtils.setYesOnclickListener(new ImageUtils.onYesOnclickListener() {
            @Override
            public void onYesClick(String paht) {
                viewModel.fileUpload(paht);
            }
        });

    }


    //接收各种类型消息
    private void takeImagMsg() {

        V2TIMManager.getMessageManager().addAdvancedMsgListener(new V2TIMAdvancedMsgListener() {
            @Override
            public void onRecvNewMessage(V2TIMMessage msg) {
                // super.onRecvNewMessage(msg);
                KLog.d("消息接收");
                KLog.d("消息接收昵称" + msg.getNickName());
                SharedPrefsUtils.putValue(AppConstant.STAFF_IMGE, msg.getFaceUrl());

                int elemType = msg.getElemType();
                KLog.d("消息接收：" + elemType);

                if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE) {
                    KLog.d("图片消息");
                    imUtils.takeImageMsg(msg);

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT) {
                    KLog.d("文本消息");
                    V2TIMTextElem v2TIMTextElem = msg.getTextElem();
                    String text = v2TIMTextElem.getText();
                    KLog.d("内容：" + text);
                    imUtils.getMyTextMsg(text);

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
                    KLog.d("自定义消息");
                    sendZiDingYiMsg(msg);

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_SOUND) {
                    KLog.d("语音消息");

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO) {
                    KLog.d("视频消息");

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_FILE) {
                    KLog.d("文件消息");

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_FACE) {
                    KLog.d("表情消息");
                    // 表情消息
                    V2TIMFaceElem v2TIMFaceElem = msg.getFaceElem();
                    // 表情所在的位置
                    int index = v2TIMFaceElem.getIndex();
                    // 表情自定义数据
                    byte[] data = v2TIMFaceElem.getData();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (ocrUtil.aaiClient != null) {
            ocrUtil.aaiClient.release();
        }
        super.onDestroy();


        if (imUtils != null) {
            imUtils.isLogin = false;
        }


        if (ocrUtil != null) {
            ocrUtil.cancelOcr();
        }


        if (ocrUtil.handler != null) {
            ocrUtil.handler.removeCallbacksAndMessages(null);
        }

        RichText.recycle();
        SharedPrefsUtils.clearShardInfo();
    }

    //设置ocr识别文字
    private void setOcrEditVal(String msgs) {
        String s = msgs.replaceAll(" ", "");

        if (!TextUtils.isEmpty(myOcrVal)) {
            binding.mainOcrContentTv.setText(myOcrVal + s);
        } else {
            binding.mainOcrContentTv.setText(s);
        }
    }

    public void setOcrListener() {
        //消息发送监听
        imUtils.setYesMsgOnclickListener(new ImUtils.onYesMsgOnclickListener() {
            @Override
            public void onYesMsgClick(boolean isMsgOk, int msgType) {
                if (isMsgOk) {
                    switch (msgType) {
                        case AppConstant.SEND_MSG_TYPE_CARD:
                            binding.mainShopLayout.setVisibility(View.GONE);
                            break;
                        case AppConstant.SEND_VIDEO_TYPE_START://开始视频
                            Bundle bundle = new Bundle();
                            bundle.putString(AppConstant.VideoStatus, "1");
                            bundle.putString(AppConstant.Video_mRoomId, "" + mRoomId);
                            startActivityMy(Rout.VideoCallingActivity, bundle);
                            break;
                        case AppConstant.SEND_VIDEO_TYPE_END:
                        case AppConstant.SEND_VIDEO_TYPE_CANCEL:
                            EventBusUtils.post(new EventMessage(msgType));
                            break;
                    }
                }
            }
        });


        ocrUtil.setMessageList(new MessageListener() {
            @Override
            public void onMessage(String msg) {

            }
        });

        ocrUtil.setOnFinishedRecordListener(new OcrUtil.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String msg, int time) {
                KLog.d("录音返回的：" + msg);

                if (time == 3) {
                    if (!TextUtils.isEmpty(msg) || msg.length() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setOcrEditVal(msg.trim());
                            }
                        });
                    }
                }

            }
        });

    }

    private void setVideoListener() {
        V2TIMManager.getSignalingManager().addSignalingListener(new V2TIMSignalingListener() {
            @Override
            public void onReceiveNewInvitation(String inviteID, String inviter, String groupID, List<String> inviteeList, String data) {
                super.onReceiveNewInvitation(inviteID, inviter, groupID, inviteeList, data);
                KLog.d("收到邀请");
            }

            @Override
            public void onInviteeAccepted(String inviteID, String invitee, String data) {
                super.onInviteeAccepted(inviteID, invitee, data);
                KLog.d("被邀请者接受邀请");
            }

            @Override
            public void onInviteeRejected(String inviteID, String invitee, String data) {
                super.onInviteeRejected(inviteID, invitee, data);
                KLog.d("被邀请者拒绝邀请");
                EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_REFUSE));
                closeActivity(ActivityUtils.getTopActivity());
            }

            @Override
            public void onInvitationCancelled(String inviteID, String inviter, String data) {
                super.onInvitationCancelled(inviteID, inviter, data);
                KLog.d("邀请被取消");
                EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_CANCEL));
            }

            @Override
            public void onInvitationTimeout(String inviteID, List<String> inviteeList) {
                super.onInvitationTimeout(inviteID, inviteeList);
                KLog.d("邀请超时" + inviteID);
                EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_OVERTIME));
            }
        });
    }


    //获取历史消息（SDK返回）
    /*private void getHistoryMessageList() {

//        static final int 	V2TIM_GET_CLOUD_OLDER_MSG = 1
//        static final int 	V2TIM_GET_CLOUD_NEWER_MSG = 2
//        static final int 	V2TIM_GET_LOCAL_OLDER_MSG = 3
//        static final int 	V2TIM_GET_LOCAL_NEWER_MSG = 4


        V2TIMMessageListGetOption optionBackward = new V2TIMMessageListGetOption();

        optionBackward.setGetType(V2TIMMessageListGetOption.V2TIM_GET_CLOUD_OLDER_MSG);
        optionBackward.setCount(20);
        optionBackward.setUserID("" + ImUtils.fsUserId);

        V2TIMManager.getMessageManager().getHistoryMessageList(optionBackward,
                new V2TIMValueCallback<List<V2TIMMessage>>() {
                    @Override
                    public void onSuccess(List<V2TIMMessage> v2TIMMessages) {
                        KLog.d("获取历史消息成功");

                        if (v2TIMMessages != null) {
                            KLog.d("获取历史消息成功:" + v2TIMMessages.size());
                            for (int i = 0; i < v2TIMMessages.size(); i++) {

                                V2TIMMessage v2TIMMessage = v2TIMMessages.get(i);
                                //String userID = v2TIMMessage.getUserID();
                                //boolean self = v2TIMMessage.isSelf();//消息发送者是否是自己

                                int elemType = v2TIMMessage.getElemType();
                                KLog.d("elemType:" + elemType);

                                switch (elemType) {
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_TEXT://文本
                                        break;
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM://自定义
                                        sendZiDingYiMsg(v2TIMMessage);
                                        break;
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE://图片
                                        break;
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_SOUND://语音
                                        break;
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_FACE:// 表情消息
                                        break;
                                }

                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        KLog.d("获取历史消息失败:" + s);
                    }
                });

    }*/


    //获取历史消息（后台接口返回）
    private void getHistoryMessageList() {

    }

    @Override
    public void onReceiveEvent(EventMessage event) {
        super.onReceiveEvent(event);
        switch (event.getCodeInt()) {
            case AppConstant.SEND_VIDEO_TYPE_END:
                imUtils.sendVideoHintMsg("通话时长" + event.getData());
                break;
            case AppConstant.SEND_VIDEO_TYPE_OVERTIME:
                imUtils.sendVideoHintMsg("对方无应答");
                break;
            case AppConstant.SEND_VIDEO_TYPE_REFUSE:
                imUtils.sendVideoHintMsg("拒绝邀请");
                break;
            case AppConstant.SEND_VIDEO_TYPE_CANCEL:
                imUtils.sendVideoHintMsg("取消视频");
                /*//信令方式视频提示
                V2TIMManager.getSignalingManager().cancel(inviteID, "", new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        imUtils.sendVideoHintMsg("取消视频");
                    }

                    @Override
                    public void onError(int i, String s) {
                        //8010 信令请求 ID 无效或已经被处理过
                        if (8010 == i) {
                            EventBusUtils.post(new EventMessage(VIDEO_CONNECT_SUCCESS, event.getData()));
                        }
                    }
                });*/
                break;
        }
    }

    //接收自定义消息（处理实时回调）
    private void sendZiDingYiMsg(V2TIMMessage msg) {

        V2TIMCustomElem v2TIMCustomElem = msg.getCustomElem();
        String cloudCustomData = msg.getCloudCustomData();
        KLog.d("自定义消息接收cloudCustomData：" + cloudCustomData);

        byte[] customData = v2TIMCustomElem.getData();
        String description = v2TIMCustomElem.getDescription();
        byte[] extension = v2TIMCustomElem.getExtension();

        if (customData == null) {
            return;
        }

        try {
            String str = new String(customData, "UTF8");
            KLog.d("自定义消息接收内容：" + description);
            KLog.d("自定义消息接收内容：" + str);

            if (!TextUtils.isEmpty(str)) {

                CustomMsgEntity customMsgEntity = GsonUtil.newGson22().fromJson(str, CustomMsgEntity.class);
                int msgType = customMsgEntity.getMsgType();
                String msgText = "";
                if (customMsgEntity.getMsgText() instanceof String) {
                    msgText = (String) customMsgEntity.getMsgText();
                } else {
                    msgText = GsonUtil.newGson22().toJson(customMsgEntity.getMsgText());
                }
                //会话枚举：101文本  102 图片地址  103商品卡片
                //视频用枚举：201发起邀请视频  202已接听，结束视频   203未接听，顾客取消   204未接听，客服拒绝  205超时（60s）挂断
                if (msgType == AppConstant.SEND_MSG_TYPE_TEXT) {//文本
                    if (msg.isSelf()) {
                        imUtils.sendRightTextMsg(msgText);
                    } else {
                        imUtils.sendLeftTextMsg(msgText);
                    }
                } else if (msgType == AppConstant.SEND_MSG_TYPE_IMAGE) {//图片
                    /*String msgText = customMsgEntity.getImgUrl();

                    if (msg.isSelf()) {
                        imUtils.takeRightImgMsg(msgText);
                    } else {
                        imUtils.takeLeftImgMsg(msgText);
                    }*/

                } else if (msgType == AppConstant.SEND_MSG_TYPE_CARD) {//卡片
                    ShopMsgBody shopMsgBod = GsonUtil.newGson22().fromJson(msgText, ShopMsgBody.class);
                    imUtils.sLeftShopMessage(shopMsgBod);

                } else if (msgType == AppConstant.SEND_VIDEO_TYPE_END) {//结束视频
                    closeVideoActivity();
                    EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_END, msgText));

                } else if (msgType == AppConstant.SEND_VIDEO_TYPE_REFUSE) {//拒绝视频
                    closeVideoActivity();
                    EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_REFUSE));

                } else if (msgType == AppConstant.SEND_MSG_TYPE_SERVICE) {
                    SharedPrefsUtils.putValue(AppConstant.CloudCustomData, cloudCustomData);
                    SharedPrefsUtils.putValue(AppConstant.STAFF_CODE, msg.getUserID());
                    imUtils.sendCenterDefaultMsg(msg.getNickName() + "将为你服务");

                    imUtils.sendLeftTextMsg(msgText);
                }
            }

            V2TIMManager.getMessageManager().markC2CMessageAsRead(imUtils.MyUserId, new V2TIMCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(int i, String s) {
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    //接收自定义消息（处理接口拉取的历史数据）
    private void sendZiDingYiMsg(CustomMsgEntity customMsgEntity, boolean isSelf) {
        KLog.d("自定义消息接收cloudCustomData：" + GsonUtil.GsonString(customMsgEntity) + isSelf);
        int msgType = customMsgEntity.getMsgType();

        String msgText = "";
        if (customMsgEntity.getMsgText() instanceof String) {
            msgText = (String) customMsgEntity.getMsgText();
        } else {
            KLog.d("自定义消息接收msgText数据异常" + msgText);
            return;
        }

        switch (msgType) {
            case AppConstant.SEND_MSG_TYPE_TEXT://文本
                if (isSelf) {
                    imUtils.sendRightTextMsg2(msgText);
                    KLog.d("test+我发的");
                } else {
                    imUtils.sendLeftTextMsg2(msgText);
                    KLog.d("test+你发的");
                }
                break;
            case AppConstant.SEND_MSG_TYPE_IMAGE://图片
                break;
            case AppConstant.SEND_MSG_TYPE_CARD://卡片
                ShopMsgBody shopMsgBod = GsonUtil.newGson22().fromJson(msgText, ShopMsgBody.class);
                if (isSelf) {
                    imUtils.sRightShopMessage(shopMsgBod);
                } else {
                    imUtils.sLeftShopMessage(shopMsgBod);
                }
                break;
            case AppConstant.SEND_VIDEO_TYPE_START://开始视频
                break;
            case AppConstant.SEND_VIDEO_TYPE_END://结束视频
            case AppConstant.SEND_VIDEO_TYPE_CANCEL://取消视频
            case AppConstant.SEND_VIDEO_TYPE_REFUSE://拒绝视频
            case AppConstant.SEND_VIDEO_TYPE_OVERTIME://视频超时
                EventBusUtils.post(new EventMessage<>(msgType, msgText));
                break;
        }
    }

    public void sPermission() {
        new RxPermissions(MainActivity.this)
                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                        } else {
                            authorityDialog();
                        }
                    }
                });
    }


    private void initHttp() {

        viewModel.stateLiveData.stateEnumMutableLiveData.observe(this, stateEnum -> {
            if (stateEnum.equals(StateLiveData.StateEnum.Loading)) {
                KLog.e("请求数据中--显示loading");
                showLoading("加载中");
            }
            if (stateEnum.equals(StateLiveData.StateEnum.Success)) {
                KLog.e("数据获取成功--关闭loading");
                dismissLoading();
            }
            if (stateEnum.equals(StateLiveData.StateEnum.Idle)) {
                KLog.e("空闲状态--关闭loading");
                dismissLoading();
            }
        });

        //获取用户信息
        viewModel.userInfoEntity.observe(this, new Observer<UserInfoEntity>() {
            @Override
            public void onChanged(UserInfoEntity userInfoEntity) {
                if (ObjectUtils.isEmpty(userInfoEntity) || ObjectUtils.isEmpty(userInfoEntity.getData())) {
                    ToastUtils.showShort("获取用户信息失败");
                    return;
                }
                if (userInfoEntity.getSuccess()) {
                    SharedPrefsUtils.putValue(AppConstant.MYUSERID, userInfoEntity.getData().getCustNo());
                    SharedPrefsUtils.putValue(AppConstant.MyUserName, userInfoEntity.getData().getCustName());
                    SharedPrefsUtils.putValue(AppConstant.MyUserIcon, userInfoEntity.getData().getCustFaceUrl());
                    if (ObjectUtils.isEmpty(userInfoEntity.getData().getCustFaceUrl())) {
                        SharedPrefsUtils.putValue(AppConstant.MyUserIcon, "https://t7.baidu.com/it/u=793426911,3641399153&fm=218&app=126&f=JPEG?w=121&h=75&s=DEA0546E36517A77458B2750020030FA");
                    }

                    login();
                    viewModel.getCustImRecord(historyPage, TimeUtils.getNowMills() + "");
                } else {
                    ToastUtils.showShort("获取用户信息失败");
                }
            }
        });


        //登录
        viewModel.loadEvent.observe(this, new Observer<LoginBean>() {
            @Override
            public void onChanged(LoginBean loginBean) {
                if (loginBean == null || loginBean.getData() == null) {
                    return;
                }

                ImConstant.SDKAPPID = loginBean.getData().getSdkAppId();
                if (ImConstant.SDKAPPID > 0) {
                    SharedPrefsUtils.putValue(AppConstant.SDKAppId, ImConstant.SDKAPPID);
                }

                SharedPrefsUtils.putValue(AppConstant.SDKUserSig, loginBean.getData().getUserSig());

                //0正在会话，1未会话，2排队中
                switch (loginBean.getData().getStatus()) {
                    case 0:
                        isSendMsg = true;
                        imUtils.loginIm();

                        Map<String, Object> value = new HashMap<>();
                        value.put("eventId", Integer.parseInt(loginBean.getData().getEventId()));
                        cloudCustomData = GsonUtil.newGson22().toJson(value);

                        myEventId = "" + loginBean.getData().getEventId();
                        SharedPrefsUtils.putValue(AppConstant.CloudCustomData, cloudCustomData);
                        SharedPrefsUtils.putValue(AppConstant.STAFF_CODE, loginBean.getData().getStaffCode());
                        break;
                    case 1:
                        isSendMsg = false;
                        imUtils.initTencentImLogin();

                        loadEventMsg = loginBean.getData().getMsg();
                        imUtils.sendDefaultMsg("" + loadEventMsg);
                        break;
                    case 2:
                        isSendMsg = false;
                        imUtils.loginIm();

                        loadEventMsg = loginBean.getData().getMsg();
                        imUtils.sendCenterDefaultMsg("" + loadEventMsg);
                        break;

                }


            }


        });

        //获取历史信息
        viewModel.historyMsgEntity.observe(this, new Observer<HistoryMsgEntity>() {
            @Override
            public void onChanged(HistoryMsgEntity historyMsgEntity) {
                if (ObjectUtils.isEmpty(historyMsgEntity) ||
                        ObjectUtils.isEmpty(historyMsgEntity.getData())) {
                    return;
                }

                binding.mainTopProgress.setVisibility(View.GONE);
                historyMsgEntityList = historyMsgEntity.getData().getData();

                //循环消息模拟发送赋值
                for (HistoryMsgEntity.DataDTO.DataDTO2 dataDTO2 : historyMsgEntityList) {

                    List<HistoryMsgEntity.DataDTO.DataDTO2.MsgBody> msgBody = GsonUtil.GsonToList(dataDTO2.getMsgBody(), HistoryMsgEntity.DataDTO.DataDTO2.MsgBody.class);

                    String str = GsonUtil.GsonString(msgBody.get(0));
                    MsgBodyBean msgBodyBean = GsonUtil.newGson22().fromJson(str, MsgBodyBean.class);

                    String data = msgBodyBean.getMsgContent().getData();
                    CustomMsgEntity customMsgEntity = GsonUtil.newGson22().fromJson(data, CustomMsgEntity.class);

                    sendZiDingYiMsg(customMsgEntity, StringUtils.equals(dataDTO2.getFromAccount(), ImUtils.MyUserId));
                }
            }
        });

        //获取人工客服
        viewModel.changeCustomerServiceEntity.observe(this, new Observer<ChangeCustomerServiceEntity>() {
            @Override
            public void onChanged(ChangeCustomerServiceEntity changeCustomerServiceEntity) {
                if (changeCustomerServiceEntity != null) {
                    imUtils.loginIm();
                    String code = changeCustomerServiceEntity.getCode();

                    switch (code) {
                        case "18790301"://排队中
                        case "18790303"://客服下班了
                            isSendMsg = false;

                            String message = changeCustomerServiceEntity.getMessage();
                            imUtils.sendCenterDefaultMsg("" + message);

                            break;
                        case "99990000"://有客服接入
                            //  String staffCode = changeCustomerServiceEntity.getData().getStaffCode();
                            myEventId = "" + changeCustomerServiceEntity.getData().getEventId();

                            if (changeCustomerServiceEntity.getData() != null) {
                                Gson gson = new Gson();
                                cloudCustomData = gson.toJson(changeCustomerServiceEntity.getData());

                                SharedPrefsUtils.putValue(AppConstant.CloudCustomData, cloudCustomData);
                                SharedPrefsUtils.putValue(AppConstant.STAFF_CODE, changeCustomerServiceEntity.getData().getStaffCode());
                            }

                            isSendMsg = true;
                            break;
                    }


                }

            }
        });

        //未回复客服消息
        viewModel.sendOffineMsgEntity.observe(this, new Observer<SendOffineMsgEntity>() {
            @Override
            public void onChanged(SendOffineMsgEntity sendOffineMsgEntity) {

                if (sendOffineMsgEntity != null && sendOffineMsgEntity.getData() != null) {

                    Integer showType = sendOffineMsgEntity.getData().getShowType();
                    //0代表小I回复 1代表返回商品
                    if (showType == 0) {

                        String msgType = sendOffineMsgEntity.getData().getMsg().getMsgType();
                        String content = sendOffineMsgEntity.getData().getMsg().getContent();
                        if (!TextUtils.isEmpty(content)) {
                            imUtils.sendLeftTextMsg("" + content);
                        }


                    } else if (showType == 1) {

                        SendOffineMsgEntity.DataBean.ProductInfoBean productInfo = sendOffineMsgEntity.getData().getProductInfo();

                        if (productInfo != null) {
                            imUtils.sendLeftShopMessage(productInfo);
                        }


                    }


                }


            }
        });


        //获取房间号
        viewModel.trtcRoomEntity.observe(this, new Observer<TrtcRoomEntity>() {
            @Override
            public void onChanged(TrtcRoomEntity trtcRoomEntity) {
                if (trtcRoomEntity != null) {

                    mRoomId = trtcRoomEntity.getData();
                    KLog.d("房间号：" + mRoomId);

                    RoomIdEntity roomIdEntity = new RoomIdEntity();
                    roomIdEntity.setRoomId(mRoomId);
                    roomIdEntity.setHelloText("视频连接中");
                    imUtils.sendTextMsg("" + mRoomId, AppConstant.SEND_VIDEO_TYPE_START);

                    //inviteID = imUtils.callVideo(MainActivity.this, mRoomId);
                }
            }
        });

        //上传图片
        viewModel.upLoad_ImgEntity.observe(MainActivity.this, new Observer<upLoadImgEntity>() {
            @Override
            public void onChanged(upLoadImgEntity upLoadImgEntity) {

                String data = upLoadImgEntity.getData();

                if (!TextUtils.isEmpty(data)) {
                    dismissLoading();
                    imUtils.sendTextMsg(data, AppConstant.SEND_MSG_TYPE_IMAGE);
                }
            }
        });
    }

    private void closeVideoActivity() {
        AppManagerMVVM.getAppManager().finishActivity(VideoCallingActivity.class);
    }
}
