package com.dfcj.videoim;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.dfcj.videoim.entity.ChangeCustomerServiceEntity;
import com.dfcj.videoim.entity.CloudCustomDataBean;
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
import com.tencent.imsdk.v2.V2TIMSignalingListener;
import com.tencent.imsdk.v2.V2TIMTextElem;
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

    //??????app????????????
    // private String token;
    // private ShopMsgBody shopMsgBody;
    // private String token;
    //private ShopMsgBody shopMsgBody;
    private boolean showShopCard = false;//????????????????????????

    private String token;//??????token
    private String chatType;//????????????
    private ShopMsgBody shopMsgBody;//????????????
    private boolean isService = false;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.main_layout;
    }

    @Override
    public int initVariableId() {
        return BR._all;
    }

    @Override
    public void initViews() {
        super.initViews();
        initAppValue();

        initHttp();

        //???????????????????????????
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

        setMyListener();
        getCustomerInfo();

        showVideoIm();

    }


    //????????????????????????
    private void showVideoIm() {

        switch (chatType) {
            case "1"://??????
                break;
            case "2"://??????
                KLog.d("????????????11");
                showVideoClick2();
                break;
        }
        SharedPrefsUtils.putValue(AppConstant.CHAT_TYPE, "0");
    }

    private void initAppValue() {
        /*token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMDA4MDEwMDAwNDQiLCJpc3MiOiJvY2otc3RhcnNreSIsImxvZ2lkIjoiNjg3NDYyNzk0OTA5MDMxMjE5MiIsImV4cCI6MTY0NjgxNTAyNywiaWF0IjoxNjM5MDM5MDI3LCJkZXZpY2VpZCI6IiJ9.hNcsiYer2GsAA_TbqPT8vyNrW1rfdVfV4YTbMs-Rrho";
        shopMsgBody = new ShopMsgBody();
        shopMsgBody.setGoodsIcon("https://t7.baidu.com/it/u=793426911,3641399153&fm=218&app=126&f=JPEG?w=121&h=75&s=DEA0546E36517A77458B2750020030FA");
        shopMsgBody.setGoodsCode("1001001");
        shopMsgBody.setGoodsName("???????????????????????????????????????????????????100ml");
        shopMsgBody.setGoodsPrice("20.00");*/

        SharedPrefsUtils.putValue(AppConstant.USERTOKEN, "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMDA4MDEwMDAwNDQiLCJpc3MiOiJvY2otc3RhcnNreSIsImxvZ2lkIjoiNjg3NDYyNzk0OTA5MDMxMjE5MiIsImV4cCI6MTY0NjgxNTAyNywiaWF0IjoxNjM5MDM5MDI3LCJkZXZpY2VpZCI6IiJ9.hNcsiYer2GsAA_TbqPT8vyNrW1rfdVfV4YTbMs-Rrho");

        token = SharedPrefsUtils.getValue(AppConstant.USERTOKEN);
        chatType = "" + SharedPrefsUtils.getValue(AppConstant.CHAT_TYPE);
        String shopMsgBodyStr = SharedPrefsUtils.getValue(AppConstant.SHOP_MSG_BODY_DATA);
        if (!ObjectUtils.isEmpty(shopMsgBodyStr)) {
            shopMsgBody = GsonUtil.newGson22().fromJson(shopMsgBodyStr, ShopMsgBody.class);

            binding.mainShopLayout.setVisibility(View.VISIBLE);
            binding.mianLayoutShopNumTv.setText(shopMsgBody.getGoodsCode());
            binding.mianLayoutShopTitleTv.setText(shopMsgBody.getGoodsName());
            binding.mianLayoutShopPriceTv.setText(shopMsgBody.getGoodsPrice());
            Glide.with(MainActivity.this)
                    .load(shopMsgBody.getGoodsIcon())
                    .error(R.drawable.default_img_failed)
                    .into(binding.mainLayoutShopImg);
        }
    }


    private void setMyListener() {

        imUtils.setYesOnclickListener(new ImUtils.onYesOnclickListener() {
            @Override
            public void onYesClick(int st) {
                if (st == 1) {
                    KLog.d("onYesClick");
                }
            }
        });

        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.goods_layout) {

                }
            }
        });
    }

    //??????????????????
    private void getCustomerInfo() {
        viewModel.getCustomerInfo(token);
    }

    //??????
    private void login() {
        viewModel.requestLogin();
    }

    //???????????????????????????
    private void sendOffineMsg(String msg) {

        imUtils.sendTextDefaultMsg("" + msg);

        viewModel.sendOfflineMsg("" + msg);


    }


    private void requestPermisson() {

        new RxPermissions(this)
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//????????????
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
                // OnScrollListener.SCROLL_STATE_IDLE; //??????????????????
                // ????????????????????????
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //???????????????????????????
                    if (!binding.rvChatList.canScrollVertically(1)) { // ????????????
//                        KLog.d("????????????");

                    } else if (!binding.rvChatList.canScrollVertically(-1)) { // ????????????
                        KLog.d("?????????");

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
     * ?????????????????????????????????;
     */
    public class Presenter {

        //??????
        public void btn_send() {
            String sdMsg = binding.etContent.getText().toString().trim();
            if (TextUtils.isEmpty(sdMsg)) {
                ToastUtils.showShort("???????????????");
                return;
            }
            KLog.d("??????????????????" + sdMsg);

            if (!isSendMsg) {//?????????????????????
                sendOffineMsg("" + sdMsg);

            } else {//???????????????
                imUtils.sendTextMsg(sdMsg, AppConstant.SEND_MSG_TYPE_TEXT);

            }

            binding.etContent.setText("");
        }

        //????????????
        public void send_shop() {
            imUtils.sendTextMsg(GsonUtil.newGson22().toJson(shopMsgBody), AppConstant.SEND_MSG_TYPE_CARD);
        }

        //????????????
        public void close_shop() {
            binding.mainShopLayout.setVisibility(View.GONE);
        }

        //?????????
        public void transferToLabor() {
            getChangeToLable2();
            /*if (!isSendMsg) {
                getChangeToLable();
            }*/
        }

        //????????????
        public void ivAudioClick() {

            binding.ocrLayout.setVisibility(View.VISIBLE);
            binding.mainInputMsgLayout.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.GONE);


            mUiHelper.hideBottomLayout(false);
            mUiHelper.hideSoftInput();

        }

        //??????????????????
        public void closeOcr() {

            myOcrVal = "";

            binding.ocrLayout.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.GONE);
            binding.mainInputMsgLayout.setVisibility(View.VISIBLE);
        }

        //????????????
        public void speechRecognition() {
            ocrUtil.startRecording();
        }

        //????????????
        public void clearOcr() {
            setOcrStop();
        }

        //????????????
        public void sendOcr() {

            if (TextUtils.isEmpty(binding.mainOcrContentTv.getText().toString().trim())) {
                ToastUtils.showShort("???????????????????????????");
                return;
            }

            imUtils.sendTextMsg(binding.mainOcrContentTv.getText().toString().trim(), AppConstant.SEND_MSG_TYPE_TEXT);
            binding.mainOcrContentTv.setText("");
        }

        //??????
        public void toImgPic() {
            KLog.d("????????????");

            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

            PictureFileUtil.openGalleryPic(MainActivity.this, REQUEST_CODE_IMAGE);
        }

        //??????
        public void toImgVideoPic() {
            KLog.d("????????????");

            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.CAMERA);
            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);


            PictureFileUtil.openCameraInfo(MainActivity.this, REQUEST_CODE_IMAGE_VIDEO);

        }

        //?????? ??????
        public void toVideo() {

            KLog.d("????????????22");
            showVideoClick();

        }

        //ocr ??????
        public void ocrEditClick() {
            KLog.d("????????????");
            ocrEditMsgDialog();

        }

        public void languageSel() {
            KLog.d("??????????????????");

            //showMyLanguageDialog();

        }

    }


    //?????????
    private void showVideoClick() {
        PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        PermissionUtil.getIsPrmission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        boolean permission3 = AppUtils.isPermission(mContext, Manifest.permission.CAMERA);
        boolean permission4 = AppUtils.isPermission(mContext, Manifest.permission.RECORD_AUDIO);

        if (!permission3 || !permission4) {
            sPermission();
            return;
        }

        KLog.d("isSendMsg:" + isSendMsg);
        KLog.d("imUtils.isLogin:" + imUtils.isLogin);

        if (imUtils.isLogin) {
            if (isSendMsg) {
                getTrtcRoomId();
            } else {
                imUtils.sendCenterDefaultMsg(loadEventMsg);
            }
        }
    }

    private void showVideoClick2() {
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
                getChangeToLable2();
            } else {
                imUtils.sendCenterDefaultMsg(loadEventMsg);
            }
        }
    }

    //???????????????
    private void getTrtcRoomId() {
        String CloudCustomData = SharedPrefsUtils.getValue(AppConstant.CloudCustomData);
        KLog.d("??????eventId?????????:" + CloudCustomData);
        if (ObjectUtils.isEmpty(CloudCustomData)) {
            KLog.d("??????eventId??????");
        } else {
            CloudCustomDataBean cloudCustomDataBean = GsonUtil.GsonToBean(CloudCustomData, CloudCustomDataBean.class);
            viewModel.getTrtcRoomId(cloudCustomDataBean.getEventId());
        }
    }


    //??????????????????????????????
    private void getChangeToLable() {
        // imUtils.sendTextDefaultMsg("1");
        viewModel.getImStaff();
    }

    //??????????????????
    private void getChangeToLable2() {
        viewModel.getTrtcStaff();
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
                    binding.mainOcrContentTv.setHint("??????????????????~");
                }
            }
        });

    }


    //????????????
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

        //??????????????????,??????????????????
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

        //??????????????????????????????
        binding.rvChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mUiHelper.hideBottomLayout(false);
                mUiHelper.hideSoftInput();
                binding.etContent.clearFocus();
                binding.ivEmo.setImageResource(R.drawable.g_pic102);
                return false;
            }
        });

        //????????????
        binding.mainOcrBtnImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                y = motionEvent.getY();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:

                        KLog.d("????????????");

                        if (!TextUtils.isEmpty(binding.mainOcrContentTv.getText().toString())) {
                            myOcrVal = binding.mainOcrContentTv.getText().toString();
                        }

                        binding.mainOcrSelImg.setVisibility(View.GONE);
                        binding.mainOcrCancelTv.setVisibility(View.VISIBLE);
                        binding.mainOcrSendTv.setVisibility(View.VISIBLE);
                        binding.mainlayoutPtLayout.setVisibility(View.GONE);
                        binding.mainOcrContentTv.setVisibility(View.VISIBLE);

                        binding.mainOcrCkTv.setText("????????????");

                        ocrUtil.startRecording();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        KLog.d("????????????");
                        ocrUtil.stopOcr();

                        break;
                }

                return true;
            }
        });


    }


    private void setOcrStop() {

        binding.mainOcrCkTv.setText("????????????");
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
                case REQUEST_CODE_IMAGE_VIDEO://??????

                    List<LocalMedia> result2 = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = result2.get(0);
                    KLog.d("????????????????????????:" + localMedia.getPath());

                    uploadImg(localMedia);

                    break;
                case REQUEST_CODE_IMAGE://??????
                    // ????????????????????????
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);

                    for (LocalMedia media : selectListPic) {
                        KLog.d("????????????????????????:" + media.getPath());
                        // imUtils.sendImageMessage(media);

                        uploadImg(media);
                    }
                    break;
                case REQUEST_CODE_VEDIO:
                    break;
            }
        }

    }

    //?????????????????????
    private void uploadImg(LocalMedia media) {
        showLoading("?????????");

        String path2 = media.getPath();
        KLog.d("????????????????????????path:" + path2);

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


    //????????????????????????
    private void takeImagMsg() {

        V2TIMManager.getMessageManager().addAdvancedMsgListener(new V2TIMAdvancedMsgListener() {
            @Override
            public void onRecvNewMessage(V2TIMMessage msg) {
                // super.onRecvNewMessage(msg);
                KLog.d("????????????");
                KLog.d("??????????????????" + msg.getNickName());
                SharedPrefsUtils.putValue(AppConstant.STAFF_NAME, msg.getNickName());
                SharedPrefsUtils.putValue(AppConstant.STAFF_IMGE, msg.getFaceUrl());

                int elemType = msg.getElemType();
                KLog.d("???????????????" + elemType);

                if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE) {
                    KLog.d("????????????");
                    imUtils.takeImageMsg(msg);

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT) {
                    KLog.d("????????????");
                    V2TIMTextElem v2TIMTextElem = msg.getTextElem();
                    String text = v2TIMTextElem.getText();
                    KLog.d("?????????" + text);
                    imUtils.getMyTextMsg(text);

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
                    KLog.d("???????????????");
                    sendZiDingYiMsg(msg);

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_SOUND) {
                    KLog.d("????????????");

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO) {
                    KLog.d("????????????");

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_FILE) {
                    KLog.d("????????????");

                } else if (elemType == V2TIMMessage.V2TIM_ELEM_TYPE_FACE) {
                    KLog.d("????????????");
                    // ????????????
                    V2TIMFaceElem v2TIMFaceElem = msg.getFaceElem();
                    // ?????????????????????
                    int index = v2TIMFaceElem.getIndex();
                    // ?????????????????????
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
        String value = SharedPrefsUtils.getValue(AppConstant.USERTOKEN);
        KLog.d("??????token:" + value);
        SharedPrefsUtils.putValue(AppConstant.SHOP_MSG_BODY_DATA, "");
        SharedPrefsUtils.clearShardInfo();

        SharedPrefsUtils.putValue(AppConstant.USERTOKEN, value);

    }

    //??????ocr????????????
    private void setOcrEditVal(String msgs) {
        String s = msgs.replaceAll(" ", "");

        if (!TextUtils.isEmpty(myOcrVal)) {
            binding.mainOcrContentTv.setText(myOcrVal + s);
        } else {
            binding.mainOcrContentTv.setText(s);
        }
    }

    public void setOcrListener() {
        //??????????????????
        imUtils.setYesMsgOnclickListener(new ImUtils.onYesMsgOnclickListener() {
            @Override
            public void onYesMsgClick(boolean isMsgOk, int msgType) {
                if (isMsgOk) {
                    switch (msgType) {
                        case AppConstant.SEND_MSG_TYPE_CARD:
                            binding.mainShopLayout.setVisibility(View.GONE);
                            break;
                        case AppConstant.SEND_VIDEO_TYPE_START://????????????
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
                KLog.d("??????????????????" + msg);

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
                KLog.d("????????????");
            }

            @Override
            public void onInviteeAccepted(String inviteID, String invitee, String data) {
                super.onInviteeAccepted(inviteID, invitee, data);
                KLog.d("????????????????????????");
            }

            @Override
            public void onInviteeRejected(String inviteID, String invitee, String data) {
                super.onInviteeRejected(inviteID, invitee, data);
                KLog.d("????????????????????????");
                EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_REFUSE));
                closeActivity(ActivityUtils.getTopActivity());
            }

            @Override
            public void onInvitationCancelled(String inviteID, String inviter, String data) {
                super.onInvitationCancelled(inviteID, inviter, data);
                KLog.d("???????????????");
                EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_CANCEL));
            }

            @Override
            public void onInvitationTimeout(String inviteID, List<String> inviteeList) {
                super.onInvitationTimeout(inviteID, inviteeList);
                KLog.d("????????????" + inviteID);
                EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_OVERTIME));
            }
        });
    }


    //?????????????????????SDK?????????
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
                        KLog.d("????????????????????????");

                        if (v2TIMMessages != null) {
                            KLog.d("????????????????????????:" + v2TIMMessages.size());
                            for (int i = 0; i < v2TIMMessages.size(); i++) {

                                V2TIMMessage v2TIMMessage = v2TIMMessages.get(i);
                                //String userID = v2TIMMessage.getUserID();
                                //boolean self = v2TIMMessage.isSelf();//??????????????????????????????

                                int elemType = v2TIMMessage.getElemType();
                                KLog.d("elemType:" + elemType);

                                switch (elemType) {
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_TEXT://??????
                                        break;
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM://?????????
                                        sendZiDingYiMsg(v2TIMMessage);
                                        break;
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE://??????
                                        break;
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_SOUND://??????
                                        break;
                                    case V2TIMMessage.V2TIM_ELEM_TYPE_FACE:// ????????????
                                        break;
                                }

                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        KLog.d("????????????????????????:" + s);
                    }
                });

    }*/


    @Override
    public void onReceiveEvent(EventMessage event) {
        super.onReceiveEvent(event);
        switch (event.getCodeInt()) {
            case AppConstant.SEND_VIDEO_TYPE_END:
                if (ObjectUtils.isEmpty(event.getCode())) {
                    imUtils.sendVideoHintMsg("????????????" + event.getData());
                } else {
                    imUtils.sendVideoHintMsg2("????????????" + event.getData());
                }
                break;

            case AppConstant.SEND_VIDEO_TYPE_OVERTIME:
                if (ObjectUtils.isEmpty(event.getCode())) {
                    imUtils.sendVideoHintMsg("???????????????");
                } else {
                    imUtils.sendVideoHintMsg2("???????????????");
                }
                break;

            case AppConstant.SEND_VIDEO_TYPE_REFUSE:
                if (ObjectUtils.isEmpty(event.getCode())) {
                    imUtils.sendVideoHintMsg("????????????");
                } else {
                    imUtils.sendVideoHintMsg2("????????????");
                }
                break;

            case AppConstant.SEND_VIDEO_TYPE_CANCEL:
                if (ObjectUtils.isEmpty(event.getCode())) {
                    imUtils.sendVideoHintMsg("????????????");
                } else {
                    imUtils.sendVideoHintMsg2("????????????");
                }
                /*//????????????????????????
                V2TIMManager.getSignalingManager().cancel(inviteID, "", new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        imUtils.sendVideoHintMsg("????????????");
                    }

                    @Override
                    public void onError(int i, String s) {
                        //8010 ???????????? ID ???????????????????????????
                        if (8010 == i) {
                            EventBusUtils.post(new EventMessage(VIDEO_CONNECT_SUCCESS, event.getData()));
                        }
                    }
                });*/
                break;
        }
    }

    //?????????????????????????????????????????????
    private void sendZiDingYiMsg(V2TIMMessage msg) {

        V2TIMCustomElem v2TIMCustomElem = msg.getCustomElem();
        String cloudCustomData = msg.getCloudCustomData();
        KLog.d("?????????????????????cloudCustomData???" + cloudCustomData);

        byte[] customData = v2TIMCustomElem.getData();
        String description = v2TIMCustomElem.getDescription();
        byte[] extension = v2TIMCustomElem.getExtension();

        if (customData == null) {
            return;
        }

        try {
            String str = new String(customData, "UTF8");
            KLog.d("??????????????????????????????" + description);
            KLog.d("??????????????????????????????" + str);

            if (!TextUtils.isEmpty(str)) {

                CustomMsgEntity customMsgEntity = GsonUtil.newGson22().fromJson(str, CustomMsgEntity.class);
                int msgType = customMsgEntity.getMsgType();
                String msgText = "";
                if (customMsgEntity.getMsgText() instanceof String) {
                    msgText = (String) customMsgEntity.getMsgText();
                } else {
                    msgText = GsonUtil.newGson22().toJson(customMsgEntity.getMsgText());
                }
                //???????????????101??????  102 ????????????  103????????????
                //??????????????????201??????????????????  202????????????????????????   203????????????????????????   204????????????????????????  205?????????60s?????????
                if (msgType == AppConstant.SEND_MSG_TYPE_TEXT) {//??????
                    if (msg.isSelf()) {
                        imUtils.sendRightTextMsg(msgText);
                    } else {
                        imUtils.sendLeftTextMsg(msgText);
                    }
                } else if (msgType == AppConstant.SEND_MSG_TYPE_IMAGE) {//??????
                    if (msg.isSelf()) {
                        imUtils.takeRightImgMsg(msgText);
                    } else {
                        imUtils.takeLeftImgMsg(msgText);
                    }

                } else if (msgType == AppConstant.SEND_MSG_TYPE_CARD) {//??????
                    ShopMsgBody shopMsgBod = GsonUtil.newGson22().fromJson(msgText, ShopMsgBody.class);
                    imUtils.sLeftShopMessage(shopMsgBod);

                } else if (msgType == AppConstant.SEND_VIDEO_TYPE_END) {//????????????
                    closeVideoActivity();
                    EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_END, msgText));

                } else if (msgType == AppConstant.SEND_VIDEO_TYPE_REFUSE) {//????????????
                    closeVideoActivity();
                    EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_REFUSE));

                } else if (msgType == AppConstant.SEND_MSG_TYPE_SERVICE) {//????????????

                    KLog.d("cloudCustomData55:" + cloudCustomData);


                    SharedPrefsUtils.putValue(AppConstant.CloudCustomData, cloudCustomData);
                    SharedPrefsUtils.putValue(AppConstant.STAFF_CODE, msg.getUserID());
                    isSendMsg = true;
                    setVideoStatus(true);

                    imUtils.sendCenterDefaultMsg("??????" + msg.getNickName() + "???????????????");
                    imUtils.sendLeftTextMsg(msgText);


                } else if (msgType == AppConstant.SEND_MSG_TYPE_CLOSE) {//????????????

                    SharedPrefsUtils.putValue(AppConstant.STAFF_CODE, "");
                    SharedPrefsUtils.putValue(AppConstant.CloudCustomData, "");
                    isSendMsg = false;
                    setVideoStatus(false);

                    imUtils.sendLeftTextMsg(msgText);
                }
            }

            V2TIMManager.getMessageManager().markC2CMessageAsRead(ImUtils.MyUserId, new V2TIMCallback() {
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

    //????????????????????????????????????????????????????????????
    private void sendZiDingYiMsg(CustomMsgEntity customMsgEntity, boolean isSelf) {
        // KLog.d("?????????????????????cloudCustomData???" + GsonUtil.GsonString(customMsgEntity) + isSelf);

        if (customMsgEntity == null) {
            return;
        }

        int msgType = customMsgEntity.getMsgType();

        String msgText = "";
        if (customMsgEntity.getMsgText() instanceof String) {
            msgText = (String) customMsgEntity.getMsgText();
        } else {
            KLog.d("?????????????????????msgText????????????" + msgText);
            return;
        }

        KLog.d("tttttttt:" + msgType);
        switch (msgType) {
            case AppConstant.SEND_MSG_TYPE_TEXT://??????
                if (isSelf) {
                    imUtils.sendRightTextMsg2(msgText);
                } else {
                    imUtils.sendLeftTextMsg2(msgText);
                }
                break;


            case AppConstant.SEND_MSG_TYPE_IMAGE://??????
                if (isSelf) {
                    imUtils.takeRightImgMsg2(msgText);
                } else {
                    imUtils.takeLeftImgMsg2(msgText);
                }
                break;
            case AppConstant.SEND_MSG_TYPE_CARD://??????
                ShopMsgBody shopMsgBod = GsonUtil.newGson22().fromJson(msgText, ShopMsgBody.class);
                if (isSelf) {
                    imUtils.sRightShopMessage2(shopMsgBod);
                } else {
                    imUtils.sLeftShopMessage2(shopMsgBod);
                }
                break;
            case AppConstant.SEND_VIDEO_TYPE_START://????????????
                break;
            case AppConstant.SEND_VIDEO_TYPE_END://????????????
            case AppConstant.SEND_VIDEO_TYPE_CANCEL://????????????
            case AppConstant.SEND_VIDEO_TYPE_REFUSE://????????????
            case AppConstant.SEND_VIDEO_TYPE_OVERTIME://????????????
                EventBusUtils.post(new EventMessage<>(msgType + "", msgType, msgText));
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
                KLog.e("???????????????--??????loading");
                showLoading("?????????");
            }
            if (stateEnum.equals(StateLiveData.StateEnum.Success)) {
                KLog.e("??????????????????--??????loading");
                dismissLoading();
            }
            if (stateEnum.equals(StateLiveData.StateEnum.Idle)) {
                KLog.e("????????????--??????loading");
                dismissLoading();
            }
        });

        //??????????????????
        viewModel.userInfoEntity.observe(this, new Observer<UserInfoEntity>() {
            @Override
            public void onChanged(UserInfoEntity userInfoEntity) {
                if (ObjectUtils.isEmpty(userInfoEntity) || ObjectUtils.isEmpty(userInfoEntity.getData())) {
                    ToastUtils.showShort("????????????????????????");
                    return;
                }
                if (userInfoEntity.getSuccess()) {

                    SharedPrefsUtils.putValue(AppConstant.MYUSERID, "" + userInfoEntity.getData().getCustNo());
                    SharedPrefsUtils.putValue(AppConstant.MyUserName, "" + userInfoEntity.getData().getCustName());
                    SharedPrefsUtils.putValue(AppConstant.MyUserIcon, "" + userInfoEntity.getData().getCustFaceUrl());

                    KLog.d("Yonghu1:" + userInfoEntity.getData().getCustNo());
                    KLog.d("Yonghu22:" + ImUtils.MyUserId);

                    if (ObjectUtils.isEmpty(userInfoEntity.getData().getCustFaceUrl())) {
                        SharedPrefsUtils.putValue(AppConstant.MyUserIcon, "http://wwwww");
                    }

                    login();
                    viewModel.getCustImRecord(historyPage, TimeUtils.getNowMills() + "");
                } else {
                    ToastUtils.showShort("????????????????????????");
                }
            }
        });


        //??????
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

                imUtils.loginIm();

                //0???????????????1????????????2?????????
                switch (loginBean.getData().getStatus()) {
                    case 0:
                        isSendMsg = true;

                        Map<String, Object> value = new HashMap<>();
                        value.put("eventId", Long.parseLong(loginBean.getData().getEventId()));

                        cloudCustomData = new Gson().toJson(value);
                        KLog.d("cloudCustomData33:" + cloudCustomData);
                        myEventId = "" + loginBean.getData().getEventId();
                        SharedPrefsUtils.putValue(AppConstant.CloudCustomData, cloudCustomData);
                        SharedPrefsUtils.putValue(AppConstant.STAFF_CODE, loginBean.getData().getStaffCode());

                        setVideoStatus(true);

                        break;
                    case 1:

                        isSendMsg = false;
                        imUtils.initTencentImLogin();

                        loadEventMsg = loginBean.getData().getMsg();
                        imUtils.sendDefaultMsg("" + loadEventMsg);

                        setVideoStatus(false);

                        break;
                    case 2:

                        isSendMsg = false;

                        loadEventMsg = loginBean.getData().getMsg();
                        imUtils.sendCenterDefaultMsg("" + loadEventMsg);

                        setVideoStatus(false);

                        break;

                }


            }


        });

        //??????????????????
        viewModel.historyMsgEntity.observe(this, new Observer<HistoryMsgEntity>() {
            @Override
            public void onChanged(HistoryMsgEntity historyMsgEntity) {
                if (ObjectUtils.isEmpty(historyMsgEntity) ||
                        ObjectUtils.isEmpty(historyMsgEntity.getData())) {
                    return;
                }

                binding.mainTopProgress.setVisibility(View.GONE);
                historyMsgEntityList = historyMsgEntity.getData().getData();

                //??????????????????????????????
                for (HistoryMsgEntity.DataDTO.DataDTO2 dataDTO2 : historyMsgEntityList) {

                    SharedPrefsUtils.putValue(AppConstant.STAFF_IMGE, dataDTO2.getStaffFaceUrl());
                    SharedPrefsUtils.putValue(AppConstant.STAFF_NAME, dataDTO2.getStaffNick());


                    SharedPrefsUtils.putValue(AppConstant.MyUserName, "" + dataDTO2.getCustNick());
                    SharedPrefsUtils.putValue(AppConstant.MyUserIcon, "" + dataDTO2.getCustFaceUrl());


                    List<HistoryMsgEntity.DataDTO.DataDTO2.MsgBody> msgBody = GsonUtil.GsonToList(dataDTO2.getMsgBody(), HistoryMsgEntity.DataDTO.DataDTO2.MsgBody.class);
                    String str = GsonUtil.GsonString(msgBody.get(0));
                    MsgBodyBean msgBodyBean = GsonUtil.newGson22().fromJson(str, MsgBodyBean.class);

                    if ("TIMTextElem".equals(msgBodyBean.getMsgType())) {
                        //????????????

                        if (StringUtils.equals(dataDTO2.getFromAccount(), "" + SharedPrefsUtils.getValue(AppConstant.MYUSERID))) {
                            imUtils.sendRightTextMsg2(msgBodyBean.getMsgContent().getText());
                        } else {
                            imUtils.sendLeftTextMsg2(msgBodyBean.getMsgContent().getText());
                        }

                    } else {
                        //???????????????

                        String data = msgBodyBean.getMsgContent().getData();
                        CustomMsgEntity customMsgEntity = GsonUtil.newGson22().fromJson(data, CustomMsgEntity.class);

                        sendZiDingYiMsg(customMsgEntity, StringUtils.equals(dataDTO2.getFromAccount(), "" + SharedPrefsUtils.getValue(AppConstant.MYUSERID)));
                    }


                }
                if (historyPage == 1 && historyMsgEntityList.size() > 0) {
                    binding.rvChatList.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                }

            }
        });

        //??????????????????(??????)
        viewModel.changeCustomerServiceEntity.observe(this, new Observer<ChangeCustomerServiceEntity>() {
            @Override
            public void onChanged(ChangeCustomerServiceEntity changeCustomerServiceEntity) {
                if (changeCustomerServiceEntity != null) {
                    imUtils.loginIm();
                    String code = changeCustomerServiceEntity.getCode();

                    switch (code) {
                        case "18790301"://?????????
                        case "18790303"://???????????????
                            isSendMsg = false;

                            String message = changeCustomerServiceEntity.getMessage();
                            imUtils.sendCenterDefaultMsg("" + message);

                            setVideoStatus(false);

                            break;
                        case "99990000"://???????????????
                            //  String staffCode = changeCustomerServiceEntity.getData().getStaffCode();
                            myEventId = "" + changeCustomerServiceEntity.getData().getEventId();

                            if (changeCustomerServiceEntity.getData() != null) {

                                Map<String, Object> value = new HashMap<>();
                                value.put("eventId", changeCustomerServiceEntity.getData().getEventId());
                                cloudCustomData = new Gson().toJson(value);

                                KLog.d("cloudCustomData11:" + cloudCustomData);

                                SharedPrefsUtils.putValue(AppConstant.CloudCustomData, cloudCustomData);
                                SharedPrefsUtils.putValue(AppConstant.STAFF_CODE, changeCustomerServiceEntity.getData().getStaffCode());
                            }
                            setVideoStatus(true);
                            isSendMsg = true;
                            break;
                    }


                }

            }
        });

        //??????????????????
        viewModel.changeCustomerServiceEntity2.observe(this, new Observer<ChangeCustomerServiceEntity>() {
            @Override
            public void onChanged(ChangeCustomerServiceEntity changeCustomerServiceEntity) {
                if (changeCustomerServiceEntity != null) {
                    String code = changeCustomerServiceEntity.getCode();

                    switch (code) {
                        case "18790301"://?????????
                        case "18790303"://???????????????
                            isSendMsg = false;

                            String message = changeCustomerServiceEntity.getMessage();
                            imUtils.sendCenterDefaultMsg("" + message);

                            setVideoStatus(false);

                            break;
                        case "99990000"://???????????????
                            //String staffCode = changeCustomerServiceEntity.getData().getStaffCode();
                            //myEventId = "" + changeCustomerServiceEntity.getData().getEventId();

                            if (changeCustomerServiceEntity.getData() != null) {

                                Map<String, Object> value = new HashMap<>();
                                value.put("eventId", changeCustomerServiceEntity.getData().getEventId());
                                cloudCustomData = new Gson().toJson(value);

                                KLog.d("cloudCustomData11:" + cloudCustomData);

                                SharedPrefsUtils.putValue(AppConstant.CloudCustomData, cloudCustomData);
                                SharedPrefsUtils.putValue(AppConstant.STAFF_CODE, changeCustomerServiceEntity.getData().getStaffCode());

                                setVideoStatus(true);
                                isSendMsg = true;

                                String roomId = changeCustomerServiceEntity.getData().getRoomId();
                                imUtils.sendTextMsg("" + roomId, AppConstant.SEND_VIDEO_TYPE_START);
                            }
                            break;
                    }


                }

            }
        });

        //?????????????????????
        viewModel.sendOffineMsgEntity.observe(this, new Observer<SendOffineMsgEntity>() {
            @Override
            public void onChanged(SendOffineMsgEntity sendOffineMsgEntity) {


                if (sendOffineMsgEntity != null) {


                    String code = sendOffineMsgEntity.getCode();

                    switch (code) {
                        case "18790301"://?????????
                        case "18790303"://???????????????
                            isSendMsg = false;

                            String message = sendOffineMsgEntity.getMessage();
                            imUtils.sendCenterDefaultMsg("" + message);

                            setVideoStatus(false);

                            break;
                        case "99990000"://???????????????
                            //  String staffCode = changeCustomerServiceEntity.getData().getStaffCode();


                            if (sendOffineMsgEntity.getData() != null) {

                                Integer showType = sendOffineMsgEntity.getData().getShowType();
                                //0?????????I?????? 1?????????????????? 2??????
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


                                } else if (showType == 2) {

                                    myEventId = "" + sendOffineMsgEntity.getData().getDistributeStaffInfo().getEventId();

                                    if (sendOffineMsgEntity.getData() != null) {

                                        Map<String, Object> value = new HashMap<>();
                                        value.put("eventId", sendOffineMsgEntity.getData().getDistributeStaffInfo().getEventId());
                                        cloudCustomData = new Gson().toJson(value);

                                        KLog.d("cloudCustomData11:" + cloudCustomData);

                                        SharedPrefsUtils.putValue(AppConstant.CloudCustomData, cloudCustomData);
                                        SharedPrefsUtils.putValue(AppConstant.STAFF_CODE, sendOffineMsgEntity.getData().getDistributeStaffInfo().getStaffCode());

                                        //  String roomId = sendOffineMsgEntity.getData().getDistributeStaffInfo().getRoomId();


                                    }

                                    setVideoStatus(true);
                                    isSendMsg = true;


                                }


                            }


                            break;
                    }


//                    //???????????????1163????????????
//                    String menu = sendOffineMsgEntity.getData().getMenu();
//                    if ("1163".equals(menu)) {
//                        getChangeToLable();
//                    }


                }


            }
        });


        //???????????????
        viewModel.trtcRoomEntity.observe(this, new Observer<TrtcRoomEntity>() {
            @Override
            public void onChanged(TrtcRoomEntity trtcRoomEntity) {
                if (trtcRoomEntity != null && trtcRoomEntity.getData() != null) {

                    mRoomId = trtcRoomEntity.getData();
                    KLog.d("????????????" + mRoomId);

                    RoomIdEntity roomIdEntity = new RoomIdEntity();
                    roomIdEntity.setRoomId(mRoomId);
                    roomIdEntity.setHelloText("???????????????");
                    imUtils.sendTextMsg("" + mRoomId, AppConstant.SEND_VIDEO_TYPE_START);

                    //inviteID = imUtils.callVideo(MainActivity.this, mRoomId);
                }
            }
        });

        //????????????
        viewModel.upLoad_ImgEntity.observe(MainActivity.this, new Observer<upLoadImgEntity>() {
            @Override
            public void onChanged(upLoadImgEntity upLoadImgEntity) {

                if (upLoadImgEntity != null && upLoadImgEntity.getData() != null) {

                    String data = upLoadImgEntity.getData();

                    if (!TextUtils.isEmpty(data)) {
                        // dismissLoading();
                        imUtils.sendTextMsg(data, AppConstant.SEND_MSG_TYPE_IMAGE);
                    }

                }

            }
        });
    }

    //??????????????????
    private void closeVideoActivity() {
        AppManagerMVVM.getAppManager().finishActivity(VideoCallingActivity.class);
    }


    //???????????????????????????
    private void setVideoStatus(boolean fg) {

        if (fg) {
            binding.ivVideo.setImageResource(R.drawable.g_pic112);
        } else {

            binding.ivVideo.setImageResource(R.drawable.g_pic124);

        }

        isVidesClick = fg;

        KLog.d("????????????????????????" + isVidesClick);


    }


}
