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
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.dfcj.videoim.BR;
import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.base.BaseActivity;
import com.dfcj.videoim.databinding.VideoCallLayoutBinding;
import com.dfcj.videoim.entity.BitRateBean;
import com.dfcj.videoim.im.ImConstant;
import com.dfcj.videoim.im.ImUtils;
import com.dfcj.videoim.im.video.GenerateUserSig;
import com.dfcj.videoim.im.video.VideoConstant;
import com.dfcj.videoim.service.FloatVideoWindowService;
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
 * 视频聊天
 */
@Route(path = Rout.VideoCallingActivity)
public class VideoCallingActivity extends BaseActivity<VideoCallLayoutBinding,VideoCallingViewModel> {


    private String   mRoomId="1256732";
    private String   mUserId="70409924";
    private TRTCCloud mTRTCCloud;
    private TXDeviceManager mTXDeviceManager;
    protected static final int REQ_PERMISSION_CODE = 0x1000;
    protected int  mGrantedCount       = 0;
    private boolean  mIsFrontCamera = true;
    private List<String>                    mRemoteUidList;
    private List<TXCloudVideoView>          mRemoteViewList;
    private static final int                OVERLAY_PERMISSION_REQ_CODE = 1234;
    //private FloatingView mFloatingView;
    private int                             mUserCount = 0;

    private boolean mServiceBound = false;

    private Map<String, BitRateBean> mBitRateMap;
    private int   mQualityFlag= TRTCCloudDef.TRTC_VIDEO_RESOLUTION_960_540;
    private int   mFPSFlag= VideoConstant.VIDEO_FPS;
    private int   mBitRateFlag= VideoConstant.LIVE_540_960_VIDEO_BITRATE;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.video_call_layout;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViews() {
        super.initViews();
        //应用运行时，保持屏幕高亮，不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRemoteUidList = new ArrayList<>();
        mRemoteViewList = new ArrayList<>();
        mRemoteViewList.add(binding.trtcViewTo);

        mBitRateMap = new HashMap<>();
        mBitRateMap.put(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360 + "", new BitRateBean(200, 1000, 800));
        //mBitRateMap.put(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_960_540 + "", new BitRateBean(400, 1600, 900));
        mBitRateMap.put(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1280_720 + "", new BitRateBean(500, 2000, 1250));
        mBitRateMap.put(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1920_1080 + "", new BitRateBean(800, 3000, 1900));

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

    }


    //美颜
    private void meiyan(){


        TXBeautyManager beautyManager = mTRTCCloud.getBeautyManager();


        binding.videoMeiyanSeekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                KLog.d("进度1："+i);

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
                KLog.d("进度2："+i);
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
                KLog.d("进度3："+i);
                beautyManager.setRuddyLevel(i);//红润级别，取值范围0 - 9；0表示关闭，9表示效果最明显
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
     * 开启悬浮Video服务
     */
    private void startVideoService() {

        //最小化Activity
        moveTaskToBack(true);
        //开启服务显示悬浮框
        ImConstant.mVideoViewLayout = binding.txcvvMainMine;
        KLog.d("服务开启开始");
        Intent floatVideoIntent = new Intent(this, FloatVideoWindowService.class);
        floatVideoIntent.putExtra("userId", ""+ ImUtils.MyUserId);
        mServiceBound = bindService(floatVideoIntent, mVideoCallServiceConnection, Context.BIND_AUTO_CREATE);
        KLog.d("服务开启开始2222");
    }

    /**
     * 定义服务绑定的回调 开启视频通话服务连接
     */
    private ServiceConnection mVideoCallServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取服务的操作对象
            KLog.d("777777777777");
            FloatVideoWindowService.MyBinder binder = (FloatVideoWindowService.MyBinder) service;
            binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public class Presenter {

        //切换前后摄像头
        public void switchCamear() {
            mIsFrontCamera = !mIsFrontCamera;
            mTXDeviceManager.switchCamera(mIsFrontCamera);

        }

        //
        public void closeVideo() {
            closeActivity(VideoCallingActivity.this);
        }

        //视频切换
        public void videoSwitch() {

        }

        //美颜
        public void videoMeiYan(){
            binding.videoMeiyanLayout.setVisibility(View.VISIBLE);

        }

        //
        public void closeVideoMeiYan(){

            binding.videoMeiyanLayout.setVisibility(View.GONE);

        }


        //美颜点击
        public void clickMeiYan(){

            binding.videoMeiyanTopLayoutTv1.setChecked(true);
            binding.videoMeiyanTopLayoutTv2.setChecked(false);

            binding.meiyanSetLayout.setVisibility(View.VISIBLE);
            binding.videoCallQingxiLayout.setVisibility(View.GONE);
            binding.videoMeiyanTopLayout1View.setVisibility(View.VISIBLE);
            binding.videoMeiyanTopLayout2View.setVisibility(View.INVISIBLE);

        }

        //清晰
        public void clickClear(){

            binding.videoMeiyanTopLayoutTv1.setChecked(false);
            binding.videoMeiyanTopLayoutTv2.setChecked(true);

            binding.meiyanSetLayout.setVisibility(View.GONE);
            binding.videoCallQingxiLayout.setVisibility(View.VISIBLE);
            binding.videoMeiyanTopLayout2View.setVisibility(View.VISIBLE);
            binding.videoMeiyanTopLayout1View.setVisibility(View.INVISIBLE);


        }



    }



    private void setMyListener(){

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

                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.meiyan_qx_radio1:
                        mQualityFlag = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
                        setVideoEncoderParam(true);
                        break;
                    case R.id.meiyan_qx_radio2:
                        mQualityFlag = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1280_720;
                        setVideoEncoderParam(true);
                        break;
                    case R.id.meiyan_qx_radio3:
                        mQualityFlag = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1920_1080;
                        setVideoEncoderParam(true);
                        break;

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
        trtcParams.sdkAppId = GenerateUserSig.SDKAPPID;
        trtcParams.userId = mUserId;
        trtcParams.roomId = Integer.parseInt(mRoomId);
        trtcParams.userSig = GenerateUserSig.genTestUserSig(trtcParams.userId);

        mTRTCCloud.startLocalPreview(mIsFrontCamera, binding.txcvvMainMine);
        mTRTCCloud.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH);
        mTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);


    }


    private void setVideoEncoderParam(boolean isSwitchQuality){
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

        //不显示悬浮框
        if (mServiceBound) {
            unbindService(mVideoCallServiceConnection);
            mServiceBound = false;
        }


        TXCloudVideoView txCloudVideoView = binding.txcvvMainMine;

        if(ImUtils.MyUserId.equals("gudada")){
            TXCGLSurfaceView mTXCGLSurfaceView=txCloudVideoView.getGLSurfaceView();
            if (mTXCGLSurfaceView!=null && mTXCGLSurfaceView.getParent() != null) {
                ((ViewGroup) mTXCGLSurfaceView.getParent()).removeView(mTXCGLSurfaceView);
                txCloudVideoView.addVideoView(mTXCGLSurfaceView);
            }
        }else{
            TextureView mTextureView=txCloudVideoView.getVideoView();
            if (mTextureView!=null && mTextureView.getParent() != null) {
                ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
                txCloudVideoView.addVideoView(mTextureView);
            }
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exitRoom();
        //解绑 不显示悬浮框
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

    private class TRTCCloudImplListener extends TRTCCloudListener {

        private WeakReference<VideoCallingActivity> mContext;

        public TRTCCloudImplListener(VideoCallingActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }

        @Override
        public void onUserVideoAvailable(String userId, boolean available) {
            KLog.d( "onUserVideoAvailable userId " + userId + ", mUserCount " + mUserCount + ",available " + available);
            int index = mRemoteUidList.indexOf(userId);
            if (available) {
                if (index != -1) {
                    return;
                }
                mRemoteUidList.add(userId);
                refreshRemoteVideoViews();

                getVideoOpen();


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
                    mTRTCCloud.startRemoteView(remoteUid, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SMALL,mRemoteViewList.get(i));
                } else {
                    mRemoteViewList.get(i).setVisibility(View.GONE);
                }
            }


            if(mRemoteUidList.size()<=0){
                closeActivity(VideoCallingActivity.this);
            }

        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            KLog.d("sdk callback onError");
            VideoCallingActivity activity = mContext.get();
            if (activity != null) {
                Toast.makeText(activity, "onError: " + errMsg + "[" + errCode+ "]" , Toast.LENGTH_SHORT).show();
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    activity.exitRoom();
                }
            }
        }
    }


    private void getVideoOpen(){

        binding.videoCallCenterLayout.setVisibility(View.GONE);
        binding.videoCallTopTv2.setVisibility(View.GONE);
        binding.videoCallTopTv.setVisibility(View.VISIBLE);
        binding.videoCallQiehuanImg.setVisibility(View.VISIBLE);
        binding.trtcViewToLayout.setVisibility(View.VISIBLE);
        binding.txcvvMainMine.setVisibility(View.VISIBLE);

        binding.videoCallMeiyanImg.setVisibility(View.VISIBLE);

        binding.videoCallCloseTv.setText("挂断");

        binding.videoCallTopTv.setBase(SystemClock.elapsedRealtime());//计时器清零
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



}
