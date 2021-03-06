package com.dfcj.videoim.ui.video;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.dfcj.videoim.BR;
import com.dfcj.videoim.MainActivity;
import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.base.BaseActivity;
import com.dfcj.videoim.databinding.VideoCallLayoutBinding;
import com.dfcj.videoim.entity.BitRateBean;
import com.dfcj.videoim.entity.EventMessage;
import com.dfcj.videoim.im.ImConstant;
import com.dfcj.videoim.im.ImUtils;
import com.dfcj.videoim.im.video.GenerateUserSig;
import com.dfcj.videoim.im.video.VideoConstant;
import com.dfcj.videoim.service.FloatVideoWindowService;
import com.dfcj.videoim.util.other.EventBusUtils;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.liteav.device.TXDeviceManager;
import com.tencent.liteav.renderer.TXCGLSurfaceView;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;
import com.wzq.mvvmsmart.utils.KLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ????????????
 */
@Route(path = Rout.VideoCallingActivity)
public class VideoCallingActivity extends BaseActivity<VideoCallLayoutBinding, VideoCallingViewModel> {

    private static final int VIDEO_OVERTIME = 60 * 1000;//??????????????????????????????

    private String mRoomId = "96635124";
    private String mUserId = "" + SharedPrefsUtils.getValue(AppConstant.MYUSERID);
    private TRTCCloud mTRTCCloud;
    private TXDeviceManager mTXDeviceManager;
    protected static final int REQ_PERMISSION_CODE = 0x1000;
    protected int mGrantedCount = 0;
    private boolean mIsFrontCamera = true;
    private List<String> mRemoteUidList;
    private List<TXCloudVideoView> mRemoteViewList;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    //private FloatingView mFloatingView;
    private int mUserCount = 0;

    private boolean mServiceBound = false;

    private Map<String, BitRateBean> mBitRateMap;
    private int mQualityFlag = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_960_540;
    private int mFPSFlag = VideoConstant.VIDEO_FPS;
    private int mBitRateFlag = VideoConstant.LIVE_540_960_VIDEO_BITRATE;
    private String vStatus;

    private ImUtils imUtils;

    private int myIndex = 0;


    private RelativeLayout remote_rl;//?????? trtc_view_to_layout
    private RelativeLayout local_rl;//??????

    //???????????????
    private TXCloudVideoView remote_sv;//??????
    // ???????????????
    private TXCloudVideoView local_sv;//??????
    private int screenWidth;
    private int screenHeight;

    private int beforRemoteweith;
    private int beforLocalweith;
    private int beforRemoteheigth;
    private int beforLocalheigth;
    private int StateAB = 0;
    private int StateBA = 1;
    private int mSate;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.video_call_layout;
    }

    @Override
    public int initVariableId() {
        return BR._all;
    }

    @Override
    public void initViews() {
        super.initViews();
        //????????????????????????????????????????????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRemoteUidList = new ArrayList<>();
        mRemoteViewList = new ArrayList<>();
        mRemoteViewList.add(binding.trtcViewTo);

        mBitRateMap = new HashMap<>();
        mBitRateMap.put(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360 + "", new BitRateBean(200, 1000, 800));
        //mBitRateMap.put(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_960_540 + "", new BitRateBean(400, 1600, 900));
        mBitRateMap.put(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1280_720 + "", new BitRateBean(500, 2000, 1250));
        mBitRateMap.put(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1920_1080 + "", new BitRateBean(800, 3000, 1900));

        //??????????????????
        binding.staffName.setText(ImUtils.fsUserId);
        Glide.with(this).
                load(SharedPrefsUtils.getValue(AppConstant.STAFF_IMGE))
                .error(R.drawable.ic_head_default_left).into(binding.staffIcon);

        imUtils = new ImUtils(mContext);
        timer.start();
    }

    @Override
    protected boolean isRegisteredEventBus() {
        return true;
    }

    @Override
    public void initData() {
        super.initData();

        binding.setPresenter(new Presenter());

        if (checkPermission()) {
            enterRoom();
        }
        meiyan();

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        setMyListener();

        initVis();

    }

    @Override
    public void initParam() {
        super.initParam();
        getTrtcRoom();
    }

    //?????????????????????
    private void getTrtcRoom() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String string = extras.getString(AppConstant.Video_mRoomId);
            vStatus = extras.getString(AppConstant.VideoStatus);
            if (string != null) {
                mRoomId = string;
            }
        }

    }


    //??????
    private void meiyan() {


        if (mTRTCCloud == null) {
            return;
        }

        TXBeautyManager beautyManager = mTRTCCloud.getBeautyManager();


        binding.videoMeiyanSeekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                KLog.d("??????1???" + i);

                beautyManager.setBeautyLevel(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.videoMeiyanSeekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                KLog.d("??????2???" + i);
                beautyManager.setWhitenessLevel(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.videoMeiyanSeekbar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                KLog.d("??????3???" + i);
                beautyManager.setRuddyLevel(i);//???????????????????????????0 - 9???0???????????????9?????????????????????
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    /**
     * ????????????Video??????
     */
    private void startVideoService() {

        //?????????Activity
        moveTaskToBack(true);
        //???????????????????????????
        ImConstant.mVideoViewLayout = binding.txcvvMainMine;
        KLog.d("??????????????????");
        Intent floatVideoIntent = new Intent(this, FloatVideoWindowService.class);
        floatVideoIntent.putExtra("userId", "" + ImUtils.MyUserId);
        mServiceBound = bindService(floatVideoIntent, mVideoCallServiceConnection, Context.BIND_AUTO_CREATE);
        KLog.d("??????????????????2222");
    }

    /**
     * ??????????????????????????? ??????????????????????????????
     */
    private ServiceConnection mVideoCallServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // ???????????????????????????
            KLog.d("777777777777");
            FloatVideoWindowService.MyBinder binder = (FloatVideoWindowService.MyBinder) service;
            binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public class Presenter {

        //?????????????????????
        public void switchCamear() {
            mIsFrontCamera = !mIsFrontCamera;
            mTXDeviceManager.switchCamera(mIsFrontCamera);

        }

        //??????/??????
        public void closeVideo() {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }


            if (available) {//??????
                //????????????
                String callDuration = (String) binding.videoCallTopTv.getText();

                imUtils.sendTextMsg(callDuration, AppConstant.SEND_VIDEO_TYPE_END);
                EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_END, callDuration));

            } else {//??????
                imUtils.sendTextMsg("????????????", AppConstant.SEND_VIDEO_TYPE_CANCEL);
                EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_CANCEL));
            }
            closeActivity(VideoCallingActivity.this);
        }

        //????????????
        public void videoSwitch() {
            if (myIndex % 2 == 0) {

                zoomRemoteout(beforRemoteweith, beforRemoteheigth, local_sv,
                        remote_sv);
                zoomlocalViewint(beforLocalweith, beforLocalheigth);

            } else {

                zoomlocalViewout(beforRemoteweith, beforRemoteheigth, local_sv,
                        remote_sv);
                zoomRemoteViewint(beforLocalweith, beforLocalheigth);

            }

            myIndex += 1;
        }

        //??????
        public void videoMeiYan() {
            binding.videoMeiyanLayout.setVisibility(View.VISIBLE);

        }

        //
        public void closeVideoMeiYan() {

            binding.videoMeiyanLayout.setVisibility(View.GONE);

        }


        //????????????
        public void clickMeiYan() {

            binding.videoMeiyanTopLayoutTv1.setTextColor(getResources().getColor(R.color.black));
            binding.videoMeiyanTopLayoutTv2.setTextColor(getResources().getColor(R.color.color_a3a3a3));

            binding.meiyanSetLayout.setVisibility(View.VISIBLE);
            binding.videoCallQingxiLayout.setVisibility(View.GONE);
            binding.videoMeiyanTopLayout1View.setVisibility(View.VISIBLE);
            binding.videoMeiyanTopLayout2View.setVisibility(View.INVISIBLE);
        }

        //??????
        public void clickClear() {

            binding.videoMeiyanTopLayoutTv1.setTextColor(getResources().getColor(R.color.color_a3a3a3));
            binding.videoMeiyanTopLayoutTv2.setTextColor(getResources().getColor(R.color.black));

            binding.meiyanSetLayout.setVisibility(View.GONE);
            binding.videoCallQingxiLayout.setVisibility(View.VISIBLE);
            binding.videoMeiyanTopLayout2View.setVisibility(View.VISIBLE);
            binding.videoMeiyanTopLayout1View.setVisibility(View.INVISIBLE);


        }


    }


    private void setMyListener() {

        binding.closeAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // closeActivity(VideoCallingActivity.this);

                startVideoService();


            }
        });

        binding.videoCallQingxiRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (!radioGroup.isPressed()) {
                    return;
                }

                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.meiyan_qx_radio1) {
                    mQualityFlag = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
                    setVideoEncoderParam(true);
                } else if (checkedRadioButtonId == R.id.meiyan_qx_radio2) {
                    mQualityFlag = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1280_720;
                    setVideoEncoderParam(true);
                } else if (checkedRadioButtonId == R.id.meiyan_qx_radio3) {
                    mQualityFlag = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1920_1080;
                    setVideoEncoderParam(true);
                }
            }
        });


    }


    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(nonRoot);

    }

    private void enterRoom() {

        mTRTCCloud = TRTCCloud.sharedInstance(getApplicationContext());
        mTRTCCloud.setListener(new TRTCCloudImplListener(VideoCallingActivity.this));
        mTXDeviceManager = mTRTCCloud.getDeviceManager();

        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.sdkAppId = SharedPrefsUtils.getValue(AppConstant.SDKAppId, 0);
        trtcParams.userId = mUserId;
        trtcParams.strRoomId = mRoomId;
        trtcParams.userSig = SharedPrefsUtils.getValue(AppConstant.SDKUserSig);
       // trtcParams.role = TRTCCloudDef.TRTCRoleAnchor;

      //  mTRTCCloud.startLocalPreview(mIsFrontCamera, binding.txcvvMainMine);
       // mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT);

        //setMixTranscodingConfigInfo();
        //trtcParams.userDefineRecordId = mRoomId;
        //trtcParams.userSig = GenerateUserSig.genTestUserSig(trtcParams.userId);

        mTRTCCloud.startLocalPreview(mIsFrontCamera, binding.txcvvMainMine);
        mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT);
        mTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);

//        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
//        trtcParams.sdkAppId = SharedPrefsUtils.getValue(AppConstant.SDKAppId, 0);
//        trtcParams.userId = mUserId;
//        trtcParams.strRoomId = mRoomId;
//        trtcParams.userDefineRecordId = mRoomId;
//        trtcParams.userSig = SharedPrefsUtils.getValue(AppConstant.SDKUserSig);
//        trtcParams.role = TRTCCloudDef.TRTCRoleAnchor;

//        mTRTCCloud.startLocalPreview(mIsFrontCamera, binding.txcvvMainMine);
//        mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT);
//        mTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_LIVE);


       // mTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);



    }


    private void setMixTranscodingConfigInfo(){

        DisplayMetrics dm = getResources().getDisplayMetrics();



        TRTCCloudDef.TRTCTranscodingConfig  trtcTranscodingConfig=new TRTCCloudDef.TRTCTranscodingConfig();

        trtcTranscodingConfig.videoWidth      =  720;
        trtcTranscodingConfig.videoHeight     = 1280;
        trtcTranscodingConfig.videoBitrate    = 1500;
        trtcTranscodingConfig.videoFramerate  = 20;
        trtcTranscodingConfig.videoGOP        = 2;
        trtcTranscodingConfig.audioSampleRate = 48000;
        trtcTranscodingConfig.audioBitrate    = 64;
        trtcTranscodingConfig.audioChannels   = 2;
        trtcTranscodingConfig.streamId="abc123";
        // trtcTranscodingConfig.appId=SharedPrefsUtils.getValue(AppConstant.APP_ID,0);
        trtcTranscodingConfig.mode=TRTCCloudDef.TRTC_TranscodingConfigMode_Template_PresetLayout;
        trtcTranscodingConfig.mixUsers = new ArrayList<>();


        //???????????????
        TRTCCloudDef.TRTCMixUser mixUser = new TRTCCloudDef.TRTCMixUser();
        /*" $PLACE_HOLDER_REMOTE$" : ???????????????????????????????????????????????????
            "$PLACE_HOLDER_LOCAL_MAIN$" ??? ??????????????????????????????????????????????????????
            "$PLACE_HOLDER_LOCAL_SUB$" : ?????????????????????????????????????????????????????? */
        mixUser.userId = "$PLACE_HOLDER_LOCAL_MAIN$";
        //  mixUser.streamType = TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG;
        mixUser.zOrder = 0;//?????????????????????????????????????????????1 - 15??????????????????
        mixUser.x = 0;
        mixUser.y = 0;
        mixUser.width = 720;
        mixUser.height = 1280;
        mixUser.roomId = null;
        // mixUser.inputType = TRTCCloudDef.TRTC_MixInputType_AudioVideo;
        // mixUser.streamType=TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG;//???????????????TRTCVideoStreamTypeBig????????????????????????TRTCVideoStreamTypeSub??????
        trtcTranscodingConfig.mixUsers.add(mixUser);


        KLog.d("??????????????????x0:"+(dm.widthPixels-190-30));
        KLog.d("??????????????????x1:"+(dm.widthPixels));
        KLog.d("??????????????????x2:"+(dm.heightPixels));

        //?????????????????????
        TRTCCloudDef.TRTCMixUser remote = new TRTCCloudDef.TRTCMixUser();
        remote.userId = "$PLACE_HOLDER_REMOTE$";
        //remote.streamType=TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG;
        remote.zOrder = 1;
        remote.x      =720-190-30;
        remote.y      = 140;
        remote.width  = 190;
        remote.height = 340;
        remote.roomId = mRoomId;
        //remote.inputType = TRTCCloudDef.TRTC_MixInputType_AudioVideo;
        trtcTranscodingConfig.mixUsers.add(remote);

        // ??????????????????
        mTRTCCloud.setMixTranscodingConfig(trtcTranscodingConfig);

    }




    private void setVideoEncoderParam(boolean isSwitchQuality) {
        TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();
        encParam.videoResolution = mQualityFlag;
        encParam.videoFps = mFPSFlag;
        encParam.videoBitrate = mBitRateFlag;
        encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
        mTRTCCloud.setVideoEncoderParam(encParam);
    }


    @Override
    protected void onStop() {
        super.onStop();
        requestDrawOverLays();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //??????????????????
        if (mServiceBound) {
            unbindService(mVideoCallServiceConnection);
            mServiceBound = false;
        }


        TXCloudVideoView txCloudVideoView = binding.txcvvMainMine;

        if (ImUtils.MyUserId.equals("" + ImUtils.MyUserId)) {
            TXCGLSurfaceView mTXCGLSurfaceView = txCloudVideoView.getGLSurfaceView();
            if (mTXCGLSurfaceView != null && mTXCGLSurfaceView.getParent() != null) {
                ((ViewGroup) mTXCGLSurfaceView.getParent()).removeView(mTXCGLSurfaceView);
                txCloudVideoView.addVideoView(mTXCGLSurfaceView);
            }
        } else {
            TextureView mTextureView = txCloudVideoView.getVideoView();
            if (mTextureView != null && mTextureView.getParent() != null) {
                ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
                txCloudVideoView.addVideoView(mTextureView);
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exitRoom();
        //?????? ??????????????????
        if (mServiceBound) {
            unbindService(mVideoCallServiceConnection);
            mServiceBound = false;
        }


    }


    public void requestDrawOverLays() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N && !Settings.canDrawOverlays(VideoCallingActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + VideoCallingActivity.this.getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            showFloatingView();
        }
    }

    private void showFloatingView() {

    }

    private boolean available = false;

    private class TRTCCloudImplListener extends TRTCCloudListener {

        private WeakReference<VideoCallingActivity> mContext;

        public TRTCCloudImplListener(VideoCallingActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        @Override
        public void onRemoteVideoStatusUpdated(String userId, int streamType, int status, int reason, Bundle extraInfo) {
            super.onRemoteVideoStatusUpdated(userId, streamType, status, reason, extraInfo);

        }


        //?????????????????????/???????????????????????????
        @Override
        public void onUserVideoAvailable(String userId, boolean available) {
            KLog.d("onUserVideoAvailable userId " + userId + ", mUserCount " + mUserCount + ",available " + available);
            timer.cancel();

            int index = mRemoteUidList.indexOf(userId);
            if (available) {
                if (index != -1) {
                    return;
                }

                mRemoteUidList.add(userId);
                refreshRemoteVideoViews();
                getVideoOpen();
                VideoCallingActivity.this.available = available;
            } else {
                if (index == -1) {
                    return;
                }
                mTRTCCloud.stopRemoteView(userId);
                mRemoteUidList.remove(index);
                refreshRemoteVideoViews();
                binding.videoCallMeiyanImg.setVisibility(View.GONE);
            }

        }


        private void refreshRemoteVideoViews() {

            for (int i = 0; i < mRemoteViewList.size(); i++) {
                if (i < mRemoteUidList.size()) {
                    String remoteUid = mRemoteUidList.get(i);
                    mRemoteViewList.get(i).setVisibility(View.VISIBLE);
                    mTRTCCloud.startRemoteView(remoteUid, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SMALL, mRemoteViewList.get(i));
                } else {
                    mRemoteViewList.get(i).setVisibility(View.GONE);
                }
            }


//            if(mRemoteUidList.size()<=0){
//                closeActivity(VideoCallingActivity.this);
//            }


        }

        @Override
        public void onRemoteUserLeaveRoom(String userId, int reason) {
            super.onRemoteUserLeaveRoom(userId, reason);
            KLog.d("?????????????????????");
        }

        @Override
        public void onEnterRoom(long result) {
            super.onEnterRoom(result);
            if (result > 0) {
                KLog.d("????????????");
            } else {
                KLog.d("????????????");
            }
        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            KLog.d("sdk callback onError");
            VideoCallingActivity activity = mContext.get();
            if (activity != null) {
               // Toast.makeText(activity, "onError: " + errMsg + "[" + errCode + "]", Toast.LENGTH_SHORT).show();
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    activity.exitRoom();
                }
            }

        }
    }


    private void getVideoOpen() {

        binding.videoCallCenterLayout.setVisibility(View.GONE);
        binding.videoCallTopTv2.setVisibility(View.GONE);
        binding.videoCallTopTv.setVisibility(View.VISIBLE);
        binding.videoCallQiehuanImg.setVisibility(View.VISIBLE);
        binding.trtcViewToLayout.setVisibility(View.VISIBLE);
        binding.txcvvMainMine.setVisibility(View.VISIBLE);

        binding.videoCallMeiyanImg.setVisibility(View.VISIBLE);

        binding.videoCallCloseTv.setText("??????");

        binding.videoCallTopTv.setBase(SystemClock.elapsedRealtime());//???????????????
        binding.videoCallTopTv.start();

    }


    private void exitRoom() {
        if (mTRTCCloud != null) {
            mTRTCCloud.stopLocalAudio();
            mTRTCCloud.stopLocalPreview();
            mTRTCCloud.exitRoom();
            mTRTCCloud.setListener(null);
        }
        mTRTCCloud = null;
        TRTCCloud.destroySharedInstance();
    }


    protected boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(VideoCallingActivity.this,
                        permissions.toArray(new String[0]),
                        REQ_PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }

    //??????????????????
    private CountDownTimer timer = new CountDownTimer(VIDEO_OVERTIME, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            imUtils.sendTextMsg("???????????????", AppConstant.SEND_VIDEO_TYPE_OVERTIME);
            EventBusUtils.post(new EventMessage<>(AppConstant.SEND_VIDEO_TYPE_OVERTIME));
            closeActivity(VideoCallingActivity.this);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                for (int ret : grantResults) {
                    if (PackageManager.PERMISSION_GRANTED == ret) {
                        mGrantedCount++;
                    }
                }
                if (mGrantedCount == permissions.length) {

                } else {
                    Toast.makeText(this, getString(R.string.common_please_input_roomid_and_userid), Toast.LENGTH_SHORT).show();
                }
                mGrantedCount = 0;
                break;
            default:
                break;
        }
    }


    private void initVis() {

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 500;

        remote_rl = binding.trtcViewToLayout;
        local_rl = binding.trtcViewToZiji;


        remote_sv = binding.trtcViewTo;
        local_sv = binding.txcvvMainMine;


    }

    private void zoomRemoteout(int weith2, int heigth2, TXCloudVideoView localView,
                               TXCloudVideoView remoteView) {

        beforLocalheigth = localView.getMeasuredHeight();
        beforLocalweith = localView.getMeasuredWidth();
        beforRemoteheigth = remoteView.getMeasuredHeight();
        beforRemoteweith = remoteView.getMeasuredWidth();
        KLog.d("zoomRemoteout beforLocalheigth" + beforLocalheigth
                + "beforLocalweith" + beforLocalweith + "beforRemoteheigth"
                + beforRemoteheigth + "beforRemoteweith" + beforLocalweith);
        zoomOpera22(local_rl, local_sv, remote_sv, remote_rl, screenWidth,
                beforLocalheigth, RelativeLayout.CENTER_IN_PARENT);

    }

    //?????????????????????
    private void zoomOpera(View sourcView, TXCloudVideoView beforeview,
                           TXCloudVideoView afterview, View detView, int beforLocalweith,
                           int beforLocalHeigth, int rule) {

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        KLog.d("beforLocalheigth = " + beforLocalheigth
                + "; beforLocalweith = " + beforLocalweith);
        params1.addRule(rule, RelativeLayout.TRUE);

        afterview.setLayoutParams(params1);
        afterview.setBackgroundResource(android.R.color.transparent);
        params1 = new RelativeLayout.LayoutParams(beforLocalweith, beforLocalHeigth);
        params1.addRule(rule, RelativeLayout.TRUE);

        params1.setMargins(0, 200, 10, 0);

        detView.setLayoutParams(params1);

    }

    private void zoomOpera22(View sourcView, TXCloudVideoView beforeview,
                             TXCloudVideoView afterview, View detView, int beforLocalweith,
                             int beforLocalHeigth, int rule) {

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        KLog.d("beforLocalheigth = " + beforLocalheigth
                + "; beforLocalweith = " + beforLocalweith);
        params1.addRule(rule, RelativeLayout.TRUE);

        afterview.setLayoutParams(params1);
        afterview.setBackgroundResource(android.R.color.transparent);
        params1 = new RelativeLayout.LayoutParams(beforLocalweith, beforLocalHeigth);
        params1.addRule(rule, RelativeLayout.TRUE);

        detView.setLayoutParams(params1);

    }


    //?????????????????????
    private void zoomRemoteViewint(int weith2, int heigth2) {
        RelativeLayout paretview = (RelativeLayout) local_rl.getParent();
        paretview.removeView(remote_rl);
        paretview.removeView(local_rl);
        zoomOpera(local_rl, local_sv, remote_sv, remote_rl, beforLocalweith,
                beforLocalheigth, RelativeLayout.ALIGN_PARENT_RIGHT);
        KLog.d("paretview" + paretview.getChildCount());
        paretview.addView(local_rl);
        paretview.addView(remote_rl);

        remote_rl.bringToFront();


    }

    //?????????????????????
    private void zoomlocalViewout(int weith2, int heigth2,
                                  TXCloudVideoView localView, TXCloudVideoView remoteView) {
        beforLocalheigth = localView.getMeasuredHeight();
        beforLocalweith = localView.getMeasuredWidth();
        beforRemoteheigth = remoteView.getMeasuredHeight();
        beforRemoteweith = remoteView.getMeasuredWidth();
        KLog.d("zoomlocalViewout beforLocalheigth" + beforLocalheigth
                + "beforLocalweith" + beforLocalweith + "beforRemoteheigth"
                + beforRemoteheigth + "beforRemoteweith" + beforRemoteweith);
        zoomOpera22(remote_rl, remote_sv, local_sv, local_rl, beforRemoteweith,
                beforRemoteheigth, RelativeLayout.CENTER_IN_PARENT);

    }

    //?????????????????????
    private void zoomlocalViewint(int weith2, int heigth2) {
        RelativeLayout paretview = (RelativeLayout) local_rl.getParent();
        paretview.removeView(remote_rl);
        paretview.removeView(local_rl);
        zoomOpera(remote_rl, remote_sv, local_sv, local_rl, beforRemoteweith,
                beforRemoteheigth, RelativeLayout.ALIGN_PARENT_RIGHT);
        paretview.addView(remote_rl);
        paretview.addView(local_rl);

        local_rl.bringToFront();

    }


}
