package com.dfcj.videoim.view.dialog;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.dfcj.videoim.R;
import com.dfcj.videoim.util.other.LogUtils;
import com.dfcj.videoim.util.other.ScreenUtil;


public class Upp2Dialog extends BaseDialogFragment {

    private TextView upp2_progress_tv,versionchecklib_loading_dialog_cancel;
    private ProgressBar update_info_gx_progress;
    private DownloadManager manager;
    private String downLoadUrl="";

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setLayout(ScreenUtil.getScreenWidth(activity), getDialog().getWindow().getAttributes().height);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downLoadUrl= bundle.getString("downLoadUrl");
    }

    @Override
    protected void initView(View view) {

        upp2_progress_tv=view.findViewById(R.id.upp2_progress_tv);
        versionchecklib_loading_dialog_cancel=view.findViewById(R.id.versionchecklib_loading_dialog_cancel);
        update_info_gx_progress=view.findViewById(R.id.update_info_gx_progress);

       // downLoadUrl="https://imtt.dd.qq.com/16891/apk/36A738553F15B2DD57F04DDDDD3066C3.apk?fsname=com.qiyi.video_12.2.3_800120253.apk&csr=1bbd";
        update_info_gx_progress.setProgress(0);
        update_info_gx_progress.setMax(100);
        LogUtils.logd("下载3");
        setInfo();

    }

    private void setInfo(){
        LogUtils.logd("下载2");
//        UpdateConfiguration configuration = new UpdateConfiguration();
//        //下载完成自动跳动安装页面
//        configuration.setJumpInstallPage(true);
//        configuration.setOnDownloadListener(listenerAdapter);
//        manager = DownloadManager.getInstance(getContext());
//        manager.setApkName(""+getString(R.string.app_name)+".apk")
//                .setApkUrl(downLoadUrl)
//                .setConfiguration(configuration)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .download();

    }

    private void startUpdate3() {
//        /*
//         * 整个库允许配置的内容
//         * 非必选
//         */
//        UpdateConfiguration configuration = new UpdateConfiguration()
//                //输出错误日志
//                .setEnableLog(true)
//                //设置自定义的下载
//                //.setHttpManager()
//                //下载完成自动跳动安装页面
//                .setJumpInstallPage(true)
//                //设置对话框背景图片 (图片规范参照demo中的示例图)
//                //.setDialogImage(R.drawable.ic_dialog)
//                //设置按钮的颜色
//                //.setDialogButtonColor(Color.parseColor("#E743DA"))
//                //设置对话框强制更新时进度条和文字的颜色
//                //.setDialogProgressBarColor(Color.parseColor("#E743DA"))
//                //设置按钮的文字颜色
//                .setDialogButtonTextColor(Color.WHITE)
//                //设置是否显示通知栏进度
//                .setShowNotification(true)
//                //设置是否提示后台下载toast
//                .setShowBgdToast(false)
//                //设置是否上报数据
//                .setUsePlatform(true)
//                //设置强制更新
//                .setForcedUpgrade(false)
//                //设置对话框按钮的点击监听
//                .setButtonClickListener(new OnButtonClickListener() {
//                    @Override
//                    public void onButtonClick(int id) {
//
//                    }
//                })
//                //设置下载过程的监听
//                .setOnDownloadListener(listenerAdapter);
//
//        manager = DownloadManager.getInstance(getActivity());
//        manager.setApkName("ESFileExplorer.apk")
//                .setApkUrl(downLoadUrl)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setShowNewerToast(true)
//                .setConfiguration(configuration)
//                .setApkDescription("更新")
////                .setApkMD5("DC501F04BBAA458C9DC33008EFED5E7F")
//                .download();
    }

//    private OnDownloadListenerAdapter listenerAdapter = new OnDownloadListenerAdapter() {
//        /**
//         * 下载中
//         *
//         * @param max      总进度
//         * @param progress 当前进度
//         */
//        @Override
//        public void downloading(int max, int progress) {
//
//            int curr = (int) (progress / (double) max * 100.0);
//          //  LogUtils.logd("下载1"+progress+"    进度："+curr);
//            update_info_gx_progress.setProgress(curr);
//            upp2_progress_tv.setText(""+curr+"%");
//        }
//    };



    @Override
    protected int getLayoutResId() {
        return R.layout.upp2_layout;
    }

    @Override
    protected void setSubView() {

    }

    @Override
    protected void initEvent() {

      /*  versionchecklib_version_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });*/

    }

    @Override
    protected void onCancel() {

    }


    public onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    public onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器
    public onYesOnclickListener2 yesOnclickListener2;//确定按钮被点击了的监听器

    public void setNoOnclickListener(onNoOnclickListener onNoOnclickListener) {

        this.noOnclickListener = onNoOnclickListener;
    }

    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {

        this.yesOnclickListener = onYesOnclickListener;
    }


    public interface onYesOnclickListener {
        void onYesClick();
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }


    public void setYesOnclickListener2(onYesOnclickListener2 onYesOnclickListener2) {

        this.yesOnclickListener2 = onYesOnclickListener2;
    }


    public interface onYesOnclickListener2 {
        void onYesClick2();
    }




}
