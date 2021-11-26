package com.dfcj.videoim;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.base.BaseActivity;

import com.dfcj.videoim.rx.AppManager;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.dfcj.videoim.view.dialog.UserXieYiDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class SplashActivity extends BaseActivity {

//
//    @Override
//    public int getLayoutId() {
//        return ;
//    }
//
//    @Override
//    protected void initInjector() {
//
//    }
//
//    @Override
//    public void initView() {
//
//        ChannelUtil.setChannidInfo(mContext);
//
//
//    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.splash_layout;
    }

    @Override
    public int initVariableId() {
        return 0;
    }

    @Override
    public void initData() {


        String valueProtocol = SharedPrefsUtils.getValue(AppConstant.xieyistatus);
        if(TextUtils.isEmpty(valueProtocol)){
            checkUserProtocol();
        }else{
           updateApp();
        }


    }



    private void updateApp() {


    }


    private  void  checkUserProtocol(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            new RxPermissions(this).request(Manifest.permission.ACTIVITY_RECOGNITION
            ).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {

                }
            });
        }

        new RxPermissions(this).request(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
             //   Manifest.permission.BODY_SENSORS,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
               // Manifest.permission.ACCESS_FINE_LOCATION
        ).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        });



        Bundle bundle3 = new Bundle();
        bundle3.putBoolean(UserXieYiDialog.DIALOG_BACK, false);
        bundle3.putBoolean(UserXieYiDialog.DIALOG_CANCELABLE_TOUCH_OUT_SIDE, false);

        UserXieYiDialog cdialog = UserXieYiDialog.newInstance(UserXieYiDialog.class, bundle3);
        cdialog.show(getSupportFragmentManager(), UserXieYiDialog.class.getName());

        cdialog.setYesOnclickListener(new UserXieYiDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(int st) {
                if(st==1){//同意协议
                    SharedPrefsUtils.putValue(AppConstant.xieyistatus,"同意了");
                    updateApp();
                }else  if(st==2){//隐私协议
                    Bundle mb=new Bundle();
                    mb.putString("proStatus","1");
                  //  startActivity(Rout.ProtocolActivity,mb);
                }else  if(st==3){//用户协议
                    Bundle mb=new Bundle();
                    mb.putString("proStatus","2");
//                    startActivity(Rout.ProtocolActivity,mb);
                }
            }
        });

        cdialog.setNoOnclickListener(new UserXieYiDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                //退出APP
                //updateApp();
                AppManager.getAppManager().finishAllActivity();
                System.exit(0);
            }
        });







    }







}
