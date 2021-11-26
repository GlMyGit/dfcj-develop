package com.dfcj.videoim.push.um;

import android.content.Intent;
import android.os.Bundle;

import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.ChannelUtil;
import com.dfcj.videoim.util.other.LogUtils;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

public class HwPushActivity extends UmengNotifyClickActivity {

    private static String TAG = "DebugLog";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mipush);

        ChannelUtil.setChannidInfo(this);

        goToNextAc();


    }

    private void goToNextAc(){

//        Observable.timer(1500, TimeUnit.MILLISECONDS)
//                .compose(RxSchedulers.<Long>io_main())
//                .subscribe(new Action1<Long>() {
//                    @Override
//                    public void call(Long aLong) {
//                        AppManager.getAppManager().finishAllActivity();
//                        if (AppUtils.isShenHe()) {
//                            String myextrval=""+ ChannelUtil.getApplicationMetaValue2(HwPushActivity.this);
//                            if(TextUtils.equals(myextrval,"hw") || TextUtils.equals(myextrval,"toutiao")){
//                                ARouter.getInstance().build(Rout.toMain)
//                                        .withTransition(R.anim.push_left_in,R.anim.push_left_out)
//                                        .navigation();
//                            }else{
//                                ARouter.getInstance().build(Rout.toMainTwo)
//                                        .withTransition(R.anim.push_left_in,R.anim.push_left_out)
//                                        .navigation();
//                            }
//
//                        }else{
//                            ARouter.getInstance().build(Rout.SpAdActivity)
//                                    .withTransition(R.anim.push_left_in,R.anim.push_left_out)
//                                    .navigation();
//                        }
//                        finish();
//                    }
//                });

    }


    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);//此方法必须调用，否则无法统计打开数
        LogUtils.logd("执行了push");
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        LogUtils.logd(""+body);

       /* if (!TextUtils.isEmpty(body)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent1=new Intent(HwPushActivity.this, MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
            });

        }
*/
    }


}
