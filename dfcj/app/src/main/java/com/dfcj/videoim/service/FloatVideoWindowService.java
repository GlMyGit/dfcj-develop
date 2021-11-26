package com.dfcj.videoim.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.dfcj.videoim.R;
import com.dfcj.videoim.entity.EventMessage;
import com.dfcj.videoim.im.ImConstant;
import com.dfcj.videoim.im.ImUtils;
import com.dfcj.videoim.ui.video.VideoCallingActivity;
import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.ScreenUtil;
import com.tencent.liteav.renderer.TXCGLSurfaceView;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wzq.mvvmsmart.utils.KLog;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FloatVideoWindowService extends Service {



    private String currentBigUserId;
    //浮动布局view
    private View mFloatingLayout;
    //容器父布局
    private TXCloudVideoView mTXCloudVideoView;
    private String TAG = getClass().getSimpleName();

    private TXCloudVideoView mTXCloudVideoView2;


    @Override
    public void onCreate() {
        super.onCreate();
        KLog.d("进来onCreate33333");
        initWindow();//设置悬浮窗基本参数（位置、宽高等）
        KLog.d("进来onCreate");
        mTXCloudVideoView2=ImConstant.mVideoViewLayout;
        KLog.d("进来onCreate2:"+mTXCloudVideoView2);
        EventBus.getDefault().register(this);

    }

    /**
     * 接收到分发的事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage event) {

        KLog.d("进来oonReceiveEvent");

        if (event.getCode().equals("1000")) {
            findVideoView(""+event.getData().toString());
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        KLog.d("进来onbind");
        currentBigUserId = intent.getStringExtra("userId");
        initFloating();//悬浮框点击事件的处理
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public FloatVideoWindowService getService() {
            return FloatVideoWindowService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatWindow.destroy();
        EventBus.getDefault().unregister(this);
        if (mFloatingLayout != null) {
            // 移除悬浮窗口
            mFloatingLayout = null;
            ImConstant.isShowFloatWindow = false;
        }
    }

    /**
     * 设置悬浮框基本参数（位置、宽高等）
     */
    private void initWindow() {
        mFloatingLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.alert_float_video_layout, null);
    }

    private void initFloating() {
        mTXCloudVideoView = mFloatingLayout.findViewById(R.id.float_videoview);
        findVideoView(currentBigUserId);
        ImConstant.isShowFloatWindow = true;
        mFloatingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在这里实现点击重新回到Activity
                Intent intent = new Intent(FloatVideoWindowService.this, VideoCallingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        FloatWindow
                .with(getApplicationContext())
                .setView(mFloatingLayout)
                .setWidth((int) AppUtils.dip2px(getApplicationContext(),96))                               //设置控件宽高
                .setHeight((int) AppUtils.dip2px(getApplicationContext(),136))
                .setMoveType(MoveType.slide,0,0)
                .setX(100)                                   //设置控件初始位置
                .setY(Screen.height,0.3f)
                .setDesktopShow(true)                        //桌面显示
                .setViewStateListener(mViewStateListener)    //监听悬浮控件状态改变
//                .setPermissionListener(mPermissionListener)  //监听权限申请结果
                .build();

    }
    ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int i, int i1) {
            if(mFloatingLayout == null){
                return;
            }
            int screenWidth = ScreenUtil.getScreenWidth(getApplicationContext());
            if(i == 0){
                mFloatingLayout.setBackgroundResource(R.drawable.custom_divider_white2);
            } else {
                if(i == screenWidth - mFloatingLayout.getWidth()){
                    mFloatingLayout.setBackgroundResource(R.drawable.custom_divider_white2);
                } else {
                    mFloatingLayout.setBackgroundResource(R.drawable.custom_divider_white2);
                }
            }
        }

        @Override
        public void onShow() {

        }

        @Override
        public void onHide() {

        }

        @Override
        public void onDismiss() {

        }

        @Override
        public void onMoveAnimStart() {

        }

        @Override
        public void onMoveAnimEnd() {

        }

        @Override
        public void onBackToDesktop() {

        }
    };

    private void findVideoView(String userId) {

        mTXCloudVideoView.removeVideoView();

        KLog.d("123123123312");


        if (ImUtils.MyUserId.equals(userId)) {

            KLog.d("66666666");

            TXCGLSurfaceView mTXCGLSurfaceView = mTXCloudVideoView2.getGLSurfaceView();
            if (mTXCGLSurfaceView != null && mTXCGLSurfaceView.getParent() != null) {
                ((ViewGroup) mTXCGLSurfaceView.getParent()).removeView(mTXCGLSurfaceView);
                mTXCloudVideoView.addVideoView(mTXCGLSurfaceView);
            }
        } else {

            KLog.d("99999999");


            TextureView mTextureView = mTXCloudVideoView2.getVideoView();
            if (mTextureView != null && mTextureView.getParent() != null) {
                ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
                mTXCloudVideoView.addVideoView(mTextureView);
            }
        }



    }


}
