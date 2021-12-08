package com.dfcj.videoim.base;

import android.app.Activity;
import android.os.Bundle;

import androidx.databinding.ViewDataBinding;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.launcher.ARouter;
import com.dfcj.videoim.R;
import com.dfcj.videoim.entity.EventMessage;
import com.dfcj.videoim.http.MaterialDialogUtils;
import com.dfcj.videoim.util.other.AntiShake;
import com.dfcj.videoim.util.other.EventBusUtils;
import com.dfcj.videoim.view.other.MyDialogLoading;
import com.gyf.immersionbar.ImmersionBar;
import com.wzq.mvvmsmart.base.BaseActivityMVVM;
import com.wzq.mvvmsmart.base.BaseViewModelMVVM;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * <p>作者：<p>
 * <p>创建时间：<p>
 * <p>文件描述：<p>
 */
public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModelMVVM> extends BaseActivityMVVM<V, VM> {
    public final String TAG = getClass().getSimpleName();


    public AntiShake clickUtil = new AntiShake();


    protected boolean isRegisteredEventBus() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        doBeforeSetcontentView();

        ARouter.getInstance().inject(this);

        if (isRegisteredEventBus()) {
            EventBusUtils.register(this);
        }


    }

    /**
     * 设置layout前 统一配置activity基本属性
     */
    private void doBeforeSetcontentView() {
        //设置昼夜主题
        //initTheme();
        // 把actvity放到application栈中管理
        /// AppManager.getAppManager().addActivity(this);
        // 无标题
        // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 竖屏
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 默认着色状态栏
        //  SetStatusBarColor();

        ImmersionBar.with(this)
                .statusBarDarkFont(true) //深色字体
//                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
//                .autoDarkModeEnable(true) //自动状态栏字体和导航栏图标变色，必须指定状态栏颜色和导航栏颜色才可以自动变色哦
//                .autoStatusBarDarkModeEnable(true,0.2f) //自动状态栏字体变色，必须指定状态栏颜色才可以自动变色哦
//                .autoNavigationBarDarkModeEnable(true,0.2f) //自动导航栏图标变色，必须指定导航栏颜色才可以自动变色哦
                .init();

    }



    private MyDialogLoading dialog;

    public void showLoading(String title) {

        dialog = new MyDialogLoading(this);
        dialog.setDialogLabel(title);
        dialog.show();

    }

    public void dismissLoading() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    /**
     * 接收到分发的事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage event) {
    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceiveStickyEvent(EventMessage event) {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisteredEventBus()) {
            EventBusUtils.unregister(this);
        }
    }

    /**
     * 关闭activity
     **/
    public void closeActivity(Activity cls) {
        cls.finish();
        cls.overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityMy(String path) {

        if(clickUtil.check()){
            return;
        }


        ARouter.getInstance().build(path)
                .withTransition(R.anim.push_left_in,R.anim.push_left_out)
                .navigation();
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityMy(String path,Bundle bundle) {

        if(clickUtil.check()){
            return;
        }

        ARouter.getInstance().build(path)
                .with(bundle)
                .withTransition(R.anim.push_left_in,R.anim.push_left_out)
                .navigation();
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    /**
     * 通过Class跳转界面
     **/
    public void startActivityMy(Class<?> cls) {
        startActivity(cls, null);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

}
