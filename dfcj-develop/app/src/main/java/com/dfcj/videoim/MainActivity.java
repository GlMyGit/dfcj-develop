package com.dfcj.videoim;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.dfcj.videoim.adapter.ChatAdapter;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.entity.ChangeCustomerServiceEntity;
import com.dfcj.videoim.entity.CustomMsgEntity;
import com.dfcj.videoim.entity.LoginBean;
import com.dfcj.videoim.entity.Message;
import com.dfcj.videoim.entity.RoomIdEntity;
import com.dfcj.videoim.entity.SendOffineMsgEntity;
import com.dfcj.videoim.entity.ShopMsgBody;
import com.dfcj.videoim.entity.TrtcRoomEntity;
import com.dfcj.videoim.entity.upLoadImgEntity;
import com.dfcj.videoim.im.ImConstant;
import com.dfcj.videoim.im.ImUtils;
import com.dfcj.videoim.im.ocr.OcrUtil;
import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.ChatUiHelper;
import com.dfcj.videoim.util.ImageUtils;
import com.dfcj.videoim.util.MyDialogUtil;
import com.dfcj.videoim.util.PermissionUtil;
import com.dfcj.videoim.util.PictureFileUtil;
import com.dfcj.videoim.util.other.GsonUtil;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.dfcj.videoim.view.dialog.BaseDialogFragment;
import com.dfcj.videoim.view.dialog.OcrMsgEditDialog;
import com.dfcj.videoim.view.dialog.QuanXianDialog;
import com.dfcj.videoim.view.dialog.WhellViewDialog;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.base.BaseActivity;

import com.dfcj.videoim.databinding.MainLayoutBinding;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.aai.model.type.EngineModelType;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMFaceElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageListGetOption;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.iot.speech.asr.listener.MessageListener;
import com.wzq.mvvmsmart.utils.KLog;
import com.wzq.mvvmsmart.utils.ToastUtils;
import com.zzhoujay.richtext.RichText;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


@Route(path = Rout.toMain)
public class MainActivity extends BaseActivity<MainLayoutBinding, MainActivityViewModel> {

    // @Autowired
    //int age;

    private String myOcrVal="";
    public static final String 	  mSenderId="right";
    public static final String     mTargetId="left";
    public static final String     mCenterId="center";
    public static final int       REQUEST_CODE_IMAGE=0000;
    public static final int       REQUEST_CODE_VEDIO=1111;
    public static final int       REQUEST_CODE_IMAGE_VIDEO=9999;
    private static long startTime;
    private float y ;
    private ChatAdapter mAdapter;

    private ImUtils imUtils;
    private OcrUtil ocrUtil;

    public boolean isVideo=true;
    private ChatUiHelper mUiHelper;
    private boolean isSendMsg=false;
    private String cloudCustomData;
    private  boolean isVidesClick=false;
    private ImageUtils imageUtils;
    private String mRoomId;

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

        //第一次设置缓存位置
        RichText.initCacheDir(this);

        imUtils = new ImUtils(mContext);
        imageUtils = new ImageUtils();
        ocrUtil=new OcrUtil(mContext);
        ocrUtil.initInfo(MainActivity.this);


    }

    @Override
    public void initData() {
        super.initData();
        binding.setPresenter(new Presenter());


        requestPermisson();

        initRv();

        imUtils.initViewInfo(mAdapter,binding.rvChatList);

        initOnCLick();
        initChatUi();

        //takeMsgInfo();
        takeImagMsg();

        setOcrListener();

        ocrUtil.initOcr();


    }



    @Override
    public void initViewObservable() {
        super.initViewObservable();



        imUtils.setYesOnclickListener(new ImUtils.onYesOnclickListener() {
            @Override
            public void onYesClick(int st) {

                if(st==1){
                    binding.ivVideo.setImageResource(R.drawable.selector_ctype_video);
                    isVidesClick=true;

                    getHistoryMessageList();
                }

            }
        });

        login();


    }

    //登录
    private void login(){

        viewModel.requestLogin();

        viewModel.loadEvent.observe(this, new Observer<LoginBean>() {
            @Override
            public void onChanged(LoginBean loginBean) {


                if(loginBean==null || loginBean.getData()==null){
                    return;
                }

                //0正在会话，1未会话，2排队中
                switch (loginBean.getData().getStatus()){
                    case 0:

                        imUtils.loginIm();
                        isSendMsg=true;

                        cloudCustomData="{"+"\"staffCode:\""+"\""+loginBean.getData().getStaffCode()+"\""
                                +","+"\"eventId:\""+loginBean.getData().getEventId()+"}";

                        SharedPrefsUtils.putValue(AppConstant.CloudCustomData,cloudCustomData);

                        break;

                    case 1:

                        isSendMsg=false;

                        String msg = loginBean.getData().getMsg();
                        int sdkAppId = loginBean.getData().getSdkAppId();
                        ImConstant.SDKAPPID=sdkAppId;
                        imUtils.initTencentImLogin();

                        imUtils.sendDefaultMsg(""+msg);

                        break;
                    case 2:

                        imUtils.loginIm();
                        isSendMsg=false;

                        String msg2 = loginBean.getData().getMsg();
                        imUtils.sendCenterDefaultMsg(""+msg2);



                        break;

                }





            }


        });

    }



    //未连接客服回复消息
    private void sendOffineMsg(String msg){


        imUtils.sendTextDefaultMsg(""+msg);

        viewModel.sendOfflineMsg(""+msg);

        viewModel.sendOffineMsgEntity.observe(this, new Observer<SendOffineMsgEntity>() {
            @Override
            public void onChanged(SendOffineMsgEntity sendOffineMsgEntity) {

                if(sendOffineMsgEntity!=null){

                    Integer showType = sendOffineMsgEntity.getData().getShowType();
                    //0代表小I回复 1代表返回商品
                    if(showType==0){

                        String msgType = sendOffineMsgEntity.getData().getMsg().getMsgType();
                        String content = sendOffineMsgEntity.getData().getMsg().getContent();
                        if(!TextUtils.isEmpty(content)){
                            imUtils.sendLeftTextMsg(""+content);
                        }


                    }else  if(showType==1){

                        SendOffineMsgEntity.DataBean.ProductInfoBean productInfo = sendOffineMsgEntity.getData().getProductInfo();

                        if(productInfo!=null){
                            imUtils.sendLeftShopMessage(productInfo);
                        }


                    }


                }


            }
        });

    }


    private void requestPermisson(){

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



    private void initRv(){

        mAdapter=new ChatAdapter(new ArrayList<Message>());
        //binding.setAdapter(mAdapter);
        binding.setAdapter(mAdapter);
        binding.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initOnCLick(){

        binding.rvChatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // OnScrollListener.SCROLL_STATE_IDLE; //停止滑动状态
                // 记录当前滑动状态
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //当前状态为停止滑动

                    if (! binding.rvChatList.canScrollVertically(1)) { // 到达底部
                        KLog.d("到达底部");

                    } else if (! binding.rvChatList.canScrollVertically(-1)) { // 到达顶部
                        KLog.d( "到顶了");

                      //  binding.mainTopProgress.setVisibility(View.VISIBLE);


                    }
                }

            }
        });


    }


    /**
     * 封装布局中的点击事件儿;
     */
    public class Presenter {


        public void btn_send() {

            String sdMsg=binding.etContent.getText().toString().trim();

            if (TextUtils.isEmpty(sdMsg)) {

                ToastUtils.showShort("请输入内容");

                return;
            }

            KLog.d("输入的内容："+sdMsg);


            if(!isSendMsg){
                //未登录链接会话

                sendOffineMsg(""+sdMsg);


            }else{

                //已链接会话
                imUtils.sendTextMsg(sdMsg,1);

            }


            binding.etContent.setText("");

        }


        //转人工
        public void transferToLabor(){

            if(!isSendMsg){
                getChangeToLable();
            }


        }

        //语音点击
        public void ivAudioClick(){

            binding.ocrLayout.setVisibility(View.VISIBLE);
            binding.mainInputMsgLayout.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.GONE);


            mUiHelper.hideBottomLayout(false);
            mUiHelper.hideSoftInput();

        }

        //语音界面取消
        public void closeOcr(){

            myOcrVal="";

            binding.ocrLayout.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.GONE);
            binding.mainInputMsgLayout.setVisibility(View.VISIBLE);
        }

        //语音识别
        public void speechRecognition(){
            ocrUtil.startRecording();
        }


        //取消语音
        public void clearOcr(){

            setOcrStop();
        }

        //语音发送
        public void sendOcr(){

            if (TextUtils.isEmpty(binding.mainOcrContentTv.getText().toString().trim())) {

                ToastUtils.showShort("请使用语言输入内容");

                return;
            }

            imUtils.sendTextMsg(binding.mainOcrContentTv.getText().toString().trim(),1);
            binding.mainOcrContentTv.setText("");

        }

        //相册
        public void toImgPic(){
            KLog.d("相册点击");

            PermissionUtil.getIsPrmission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.getIsPrmission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);

            PictureFileUtil.openGalleryPic(MainActivity.this,REQUEST_CODE_IMAGE);
        }

        //相机
        public void toImgVideoPic(){
            KLog.d("拍照点击");

            PermissionUtil.getIsPrmission(MainActivity.this,Manifest.permission.CAMERA);
            PermissionUtil.getIsPrmission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.getIsPrmission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);


            PictureFileUtil.openCameraInfo(MainActivity.this,REQUEST_CODE_IMAGE_VIDEO);

        }

        //视频 点击
        public void toVideo(){
            KLog.d("视频点击");

            PermissionUtil.getIsPrmission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.getIsPrmission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);

            boolean permission3 = AppUtils.isPermission(mContext, Manifest.permission.CAMERA);
            boolean permission4 = AppUtils.isPermission(mContext, Manifest.permission.RECORD_AUDIO);

            if(!permission3||!permission4){
                sPermission();
                return;
            }

            if(imUtils.isLogin){

                getTrtcRoomId();

            }

        }

        //ocr 编辑
        public void ocrEditClick(){
            KLog.d("视频点击");
            ocrEditMsgDialog();

        }

        public void languageSel(){
            KLog.d("语言选择点击");

            //showMyLanguageDialog();

        }

    }

    //获取房间号
    private void getTrtcRoomId(){

        viewModel.getTrtcRoomId();

        viewModel.trtcRoomEntity.observe(this, new Observer<TrtcRoomEntity>() {
            @Override
            public void onChanged(TrtcRoomEntity trtcRoomEntity) {
                if(trtcRoomEntity!=null){
                    mRoomId = trtcRoomEntity.getData();
                    KLog.d("房间号："+ mRoomId);
                   /* RoomIdEntity roomIdEntity=new RoomIdEntity();
                    roomIdEntity.setRoomId(mRoomId);
                    roomIdEntity.setHelloText("视频连接中");
                    String s = new Gson().toJson(roomIdEntity);*/
                    imUtils.sendTextMsg(""+ mRoomId,5);

                }
            }
        });

    }



    //获取人工客服
    private void getChangeToLable(){


        // imUtils.sendTextDefaultMsg("1");

        viewModel.getImStaff();
        viewModel.changeCustomerServiceEntity.observe(this, new Observer<ChangeCustomerServiceEntity>() {
            @Override
            public void onChanged(ChangeCustomerServiceEntity changeCustomerServiceEntity) {


                if(changeCustomerServiceEntity!=null){

                    imUtils.loginIm();

                    String code = changeCustomerServiceEntity.getFail().getCode();

                    switch (code){
                        case "18800301"://排队中
                            isSendMsg=false;

                            String message = changeCustomerServiceEntity.getFail().getMessage();
                            imUtils.sendCenterDefaultMsg(""+message);


                            break;
                        case "99990000"://有客服接入

                            //  String staffCode = changeCustomerServiceEntity.getData().getStaffCode();
                            // Integer eventId = changeCustomerServiceEntity.getData().getEventId();

                            if(changeCustomerServiceEntity.getData()!=null){
                                Gson gson=new Gson();
                                cloudCustomData = gson.toJson(changeCustomerServiceEntity.getData());


                                SharedPrefsUtils.putValue(AppConstant.CloudCustomData,cloudCustomData);

                            }


                            isSendMsg=true;
                            break;
                    }


                }

            }
        });


    }



    private void ocrEditMsgDialog(){


        OcrMsgEditDialog fxdialog = MyDialogUtil.ocrEditMsgDialog(binding.mainOcrContentTv.getText().toString());
        fxdialog.show(getSupportFragmentManager(), OcrMsgEditDialog.class.getName());
        fxdialog.setYesOnclickListener(new OcrMsgEditDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String tl) {
                if(!TextUtils.isEmpty(tl)){
                    binding.mainOcrContentTv.setText(tl);

                }else{
                    binding.mainOcrContentTv.setText("");
                    binding.mainOcrContentTv.setHint("请说点什么吧~");
                }
            }
        });

    }


    //相机权限
    private void authorityDialog(){

        QuanXianDialog fxdialog = MyDialogUtil.authorityDialog();
        fxdialog.show(getSupportFragmentManager(), QuanXianDialog.class.getName());
        fxdialog.setYesOnclickListener(new QuanXianDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                PermissionUtil.gotoPermission(mContext);
            }
        });

    }


    private void initChatUi(){

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

                        if(!TextUtils.isEmpty(binding.mainOcrContentTv.getText().toString())){
                            myOcrVal=binding.mainOcrContentTv.getText().toString();
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


    private void setOcrStop(){

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
                    KLog.d("获取图片路径成功:"+  localMedia.getPath());

                    uploadImg(localMedia);

                    break;
                case REQUEST_CODE_IMAGE://相册
                    // 图片选择结果回调
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);

                    for (LocalMedia media : selectListPic) {
                        KLog.d("获取图片路径成功:"+  media.getPath());
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
    private void uploadImg(LocalMedia media){


        showLoading("发送中");

       String path2=media.getPath();

        KLog.d("获取图片路径成功path:"+  path2);

        initCompressorRxJava(path2);

        viewModel.upLoad_ImgEntity.observe(MainActivity.this, new Observer<upLoadImgEntity>() {
            @Override
            public void onChanged(upLoadImgEntity upLoadImgEntity) {
                String data = upLoadImgEntity.getData();
                if(!TextUtils.isEmpty(data)){

                    dismissLoading();
                    imUtils.sendTextMsg(data,4);
                }
            }
        });


    }

    private void initCompressorRxJava(String path){

        imageUtils.initCompressorRxJava(this,path);
        imageUtils.setYesOnclickListener(new ImageUtils.onYesOnclickListener() {
            @Override
            public void onYesClick(String paht) {
                viewModel.fileUpload(paht);
            }
        });

    }



    //接收各种类型消息
    private void takeImagMsg(){

        V2TIMManager.getMessageManager().addAdvancedMsgListener(new V2TIMAdvancedMsgListener() {
            @Override
            public void onRecvNewMessage(V2TIMMessage msg) {
                // super.onRecvNewMessage(msg);

                KLog.d("消息接收");

                int elemType = msg.getElemType();

                KLog.d("消息接收："+elemType);

                if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE) {


                    imUtils.takeImageMsg(msg);


                }else if(elemType == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT){

                    KLog.d("文本消息");

                    // 文本消息
                    V2TIMTextElem v2TIMTextElem = msg.getTextElem();
                    String text = v2TIMTextElem.getText();

                    KLog.d("内容："+text);

                    imUtils.getMyTextMsg(text);


                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
                    // 自定义消息
                    KLog.d("自定义消息接收：");
                    sendZiDingYiMsg(msg);


                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_SOUND) {

                    KLog.d("语音消息");

                }else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO) {

                    KLog.d("视频消息");


                }else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_FILE) {


                }else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_FACE) {

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


        if(imUtils!=null){
            imUtils.isLogin=false;
        }


        if(ocrUtil!=null){
            ocrUtil.cancelOcr();
        }


        if(ocrUtil.handler!=null){
            ocrUtil.handler.removeCallbacksAndMessages(null);
        }

        RichText.recycle();

    }

    //设置ocr识别文字
    private void setOcrEditVal(String msgs){


        String s = msgs.replaceAll(" ", "");

        if(!TextUtils.isEmpty(myOcrVal)){
            binding.mainOcrContentTv.setText(myOcrVal+s);
        }else{
            binding.mainOcrContentTv.setText(s);
        }


    }

    public void setOcrListener(){


        //消息发送监听
        imUtils.setYesMsgOnclickListener(new ImUtils.onYesMsgOnclickListener() {
            @Override
            public void onYesMsgClick(boolean isMsgOk,int msgType) {
                if(isMsgOk){

                    switch (msgType){
                        case 5://视频消息

                            Bundle bundle=new Bundle();
                            bundle.putString(AppConstant.VideoStatus,"1");
                            bundle.putString(AppConstant.Video_mRoomId,""+mRoomId);
                            startActivityMy(Rout.VideoCallingActivity,bundle);

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
                KLog.d("录音返回的："+msg);

                if(time==3){
                    if(!TextUtils.isEmpty(msg) || msg.length()>0){
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


    //获取历史消息
    private void getHistoryMessageList(){

//        static final int 	V2TIM_GET_CLOUD_OLDER_MSG = 1
//        static final int 	V2TIM_GET_CLOUD_NEWER_MSG = 2
//        static final int 	V2TIM_GET_LOCAL_OLDER_MSG = 3
//        static final int 	V2TIM_GET_LOCAL_NEWER_MSG = 4


        V2TIMMessageListGetOption optionBackward = new V2TIMMessageListGetOption();

        optionBackward.setGetType(V2TIMMessageListGetOption.V2TIM_GET_CLOUD_OLDER_MSG);
        optionBackward.setCount(20);
        optionBackward.setUserID(""+ImUtils.fsUserId);

        V2TIMManager.getMessageManager().getHistoryMessageList(optionBackward,
                new V2TIMValueCallback<List<V2TIMMessage>>() {
                    @Override
                    public void onSuccess(List<V2TIMMessage> v2TIMMessages) {

                        KLog.d("获取历史消息成功");

                        if(v2TIMMessages!=null){

                            KLog.d("获取历史消息成功:"+v2TIMMessages.size());

                            for (int i = 0; i < v2TIMMessages.size(); i++) {

                                V2TIMMessage v2TIMMessage = v2TIMMessages.get(i);

                                //String userID = v2TIMMessage.getUserID();

                                // boolean self = v2TIMMessage.isSelf();//消息发送者是否是自己

                                int elemType = v2TIMMessage.getElemType();

                                KLog.d("elemType:"+elemType);


                                switch (elemType){

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
                        KLog.d("获取历史消息失败:"+s);
                    }
                });

    }


    //接收自定义消息
    private void sendZiDingYiMsg(V2TIMMessage msg){

        V2TIMCustomElem v2TIMCustomElem = msg.getCustomElem();

        String cloudCustomData = msg.getCloudCustomData();

        KLog.d("自定义消息接收cloudCustomData："+cloudCustomData);

        byte[] customData = v2TIMCustomElem.getData();
        String description = v2TIMCustomElem.getDescription();
        byte[] extension = v2TIMCustomElem.getExtension();

        try {

            String str = new String(customData, "UTF8");
            KLog.d("自定义消息接收内容："+description);
            KLog.d("自定义消息接收内容："+str);

            if(!TextUtils.isEmpty(str)){
                Gson gs=new Gson();
                CustomMsgEntity customMsgEntity = gs.fromJson(str, CustomMsgEntity.class);

                int msgType = customMsgEntity.getMsgType();

                //1文本  2 富文本   3带网址  4图片地址  5视频  6商品卡片
                if(msgType==1|| msgType==2 || msgType==3){
                    String msgText = customMsgEntity.getMsgText();

                    if(msg.isSelf()){

                        imUtils.sendRightTextMsg(msgText);

                    }else{

                        imUtils.sendLeftTextMsg(msgText);

                    }


                }else if(msgType==4){

                    String msgText = customMsgEntity.getImgUrl();

                    if(msg.isSelf()){
                        imUtils.takeRightImgMsg(msgText);


                    }else{

                        imUtils.takeLeftImgMsg(msgText);

                    }

                }else if(msgType==5){//视频消息


                    String msgText = customMsgEntity.getMsgText();


                    Bundle bundle=new Bundle();
                    bundle.putString(AppConstant.VideoStatus,"2");
                    bundle.putString(AppConstant.Video_mRoomId,""+msgText);
                    startActivityMy(Rout.VideoCallingActivity,bundle);



                }else if(msgType==6){//卡片消息

                    String msgText = customMsgEntity.getMsgText();

                    if(!TextUtils.isEmpty(msgText)){

                        ShopMsgBody shopMsgBody = new Gson().fromJson(msgText, ShopMsgBody.class);
                        if(msg.isSelf()){

                            imUtils.sRightShopMessage(shopMsgBody);


                        }else{

                            imUtils.sLeftShopMessage(shopMsgBody);
                        }

                    }


                }


            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }



    public  void sPermission(){

        new RxPermissions(MainActivity.this)
                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {

                        }else{
                            authorityDialog();
                        }
                    }
                });

    }



}