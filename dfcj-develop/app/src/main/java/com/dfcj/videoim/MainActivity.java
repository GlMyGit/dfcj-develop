package com.dfcj.videoim;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.dfcj.videoim.adapter.ChatAdapter;
import com.dfcj.videoim.entity.Message;
import com.dfcj.videoim.im.ImUtils;
import com.dfcj.videoim.im.ocr.OcrUtil;
import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.ChatUiHelper;
import com.dfcj.videoim.util.PictureFileUtil;
import com.dfcj.videoim.view.dialog.OcrMsgEditDialog;
import com.dfcj.videoim.view.dialog.QuanXianDialog;
import com.dfcj.videoim.view.dialog.WhellViewDialog;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.base.BaseActivity;

import com.dfcj.videoim.databinding.MainLayoutBinding;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.aai.model.type.EngineModelType;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMFaceElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.tencent.iot.speech.asr.listener.MessageListener;
import com.wzq.mvvmsmart.utils.KLog;
import com.wzq.mvvmsmart.utils.ToastUtils;
import com.zzhoujay.richtext.RichText;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;


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


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.main_layout;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        super.initData();
        binding.setPresenter(new Presenter());


    }

    @Override
    public void initViews() {
        super.initViews();

        //第一次设置缓存位置
        RichText.initCacheDir(this);

        imUtils = new ImUtils(mContext);
        imUtils.initTencentImLogin();

        ocrUtil=new OcrUtil(mContext);
        ocrUtil.initInfo(MainActivity.this);


    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        requestPermisson();


        initRv();

        imUtils.initViewInfo(mAdapter,binding.rvChatList);


        initOnCLick();
        initChatUi();

        //takeMsgInfo();
        takeImagMsg();


        setOcrListener();

        ocrUtil.initOcr();


        imUtils.sendDefaultMsg("");


    }



    private void requestPermisson(){

        new RxPermissions(this)
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
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

        binding.swipeChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeChat.setRefreshing(false);
            }
        });


    }


    /**
     * 封装布局中的点击事件儿;
     */
    public class Presenter {


        public void btn_send() {

            if (TextUtils.isEmpty(binding.etContent.getText().toString().trim())) {

                ToastUtils.showShort("请输入内容");

                return;
            }

            KLog.d("输入的内容："+binding.etContent.getText().toString().trim());

            if(TextUtils.equals("1",binding.etContent.getText().toString().trim())){

                if(!imUtils.isLogin){
                    imUtils.loginIm();
                    imUtils.sendTextDefaultMsg("1");
                }

                if(imUtils.isKeFuLogin){
                    imUtils.sendCenterDefaultMsg("客服001号将为您服务");
                }

            }else{

                if(imUtils.isLogin) {
                    imUtils.sendTextMsg(binding.etContent.getText().toString().trim());

                }else{
                    imUtils.sendCenterDefaultMsg("客服繁忙");
                }

            }

            binding.etContent.setText("");

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

            imUtils.sendTextMsg(binding.mainOcrContentTv.getText().toString().trim());
            binding.mainOcrContentTv.setText("");

        }

        //相册
        public void toImgPic(){
            KLog.d("相册点击");

            boolean permission1 = AppUtils.isPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            boolean permission2 = AppUtils.isPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);

            if(!permission1){

            }

            if(!permission2){

            }

            PictureFileUtil.openGalleryPic(MainActivity.this,REQUEST_CODE_IMAGE);
        }

        //相机
        public void toImgVideoPic(){
            KLog.d("拍照点击");

            boolean permission = AppUtils.isPermission(mContext, Manifest.permission.CAMERA);

            if(!permission){

            }

            PictureFileUtil.openCameraInfo(MainActivity.this,REQUEST_CODE_IMAGE_VIDEO);

        }

        //视频
        public void toVideo(){
            KLog.d("视频点击");
            startActivityMy(Rout.VideoCallingActivity);

        }

        //ocr 编辑
        public void ocrEditClick(){
            KLog.d("视频点击");
            ocrEditMsgDialog();

        }

        public void languageSel(){
            KLog.d("语言选择点击");

            showMyLanguageDialog();

        }

    }

    private void ocrEditMsgDialog(){

        Bundle bundle3 = new Bundle();
        bundle3.putBoolean(OcrMsgEditDialog.DIALOG_BACK, true);
        bundle3.putBoolean(OcrMsgEditDialog.DIALOG_CANCELABLE_TOUCH_OUT_SIDE, true);
        bundle3.putString("mainOcrContentTvVal",""+binding.mainOcrContentTv.getText().toString());

        OcrMsgEditDialog fxdialog = OcrMsgEditDialog.newInstance(OcrMsgEditDialog.class, bundle3);
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

        Bundle bundle3 = new Bundle();
        bundle3.putBoolean(QuanXianDialog.DIALOG_BACK, true);
        bundle3.putBoolean(QuanXianDialog.DIALOG_CANCELABLE_TOUCH_OUT_SIDE, true);

        QuanXianDialog fxdialog = QuanXianDialog.newInstance(QuanXianDialog.class, bundle3);
        fxdialog.show(getSupportFragmentManager(), QuanXianDialog.class.getName());

    }

    //相机权限
    private void showMyLanguageDialog(){

        ArrayList mylist=new ArrayList();
        mylist.add("普通话");
        mylist.add("英语");
        mylist.add("粤语");

        Bundle bundle = new Bundle();
        bundle.putBoolean(WhellViewDialog.DIALOG_BACK, true);
        bundle.putBoolean(WhellViewDialog.DIALOG_CANCELABLE_TOUCH_OUT_SIDE, true);
        bundle.putString("myTitle","请选择语言");
        bundle.putStringArrayList("ListVal",mylist);

        WhellViewDialog xdialog = WhellViewDialog.newInstance(WhellViewDialog.class, bundle);
        xdialog.show(getSupportFragmentManager(), WhellViewDialog.class.getName());
        xdialog.setYesOnclickListener(new WhellViewDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(ArrayList<String> arrayList, int index) {
                String s = arrayList.get(index);

                binding.mainLayoutLanguageTv.setText(""+s);

                switch (index){

                    case 0:
                        ocrUtil.myLanguageTypt=EngineModelType.EngineModelType16K.getType();

                        break;
                    case 1:
                        ocrUtil.myLanguageTypt=EngineModelType.EngineModelType16KEN.getType();
                        break;
                    case 2:
                        ocrUtil.myLanguageTypt="16k_ca";
                        break;

                }


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
                        // setText("松开发送");

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


        binding.etContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                   // binding.bottomLayout.setVisibility(View.GONE);

                } else {
                    // 此处为失去焦点时的处理内容


                }


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
                case REQUEST_CODE_IMAGE_VIDEO:

                    List<LocalMedia> result2 = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = result2.get(0);
                    KLog.d("获取图片路径成功:"+  localMedia.getPath());

                    imUtils.sendImageMessage(localMedia);

                    break;
                case REQUEST_CODE_IMAGE:
                    // 图片选择结果回调
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListPic) {
                        KLog.d("获取图片路径成功:"+  media.getPath());
                        // imUtils.sendImageMessage(media);

                        imUtils.sendCustomerTextMsg(media);

                    }
                    break;
                case REQUEST_CODE_VEDIO:
                    break;
            }
        }

    }


    //接收图片类型消息
    private void takeImagMsg(){

        V2TIMManager.getMessageManager().addAdvancedMsgListener(new V2TIMAdvancedMsgListener() {
            @Override
            public void onRecvNewMessage(V2TIMMessage msg) {
                // super.onRecvNewMessage(msg);

                int elemType = msg.getElemType();


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

                    V2TIMCustomElem v2TIMCustomElem = msg.getCustomElem();
                    byte[] customData = v2TIMCustomElem.getData();

                    try {

                        String str = new String(customData, "UTF8");

                        KLog.d("自定义消息接收内容："+str);

                        imUtils.takeCustomerImageMsg(msg);


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


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


       /* if(!TextUtils.isEmpty(msgs)){

            String s = msgs.replaceAll(" ", "");


            String[] split = s.split("。");

            if(split!=null && split.length>0){

                StringBuffer sb = new StringBuffer();

                for (int i = 0; i < split.length; i++) {
                    sb.append(split[i]);
                }

               // binding.mainOcrContentTv.setText("");
                binding.mainOcrContentTv.setText(sb);
            }

        }*/



      //  binding.mainOcrContentTv.setText(msgs);

    }

    public void setOcrListener(){

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


}
