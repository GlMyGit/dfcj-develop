package com.dfcj.videoim.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.AppApplicationMVVM;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.dfcj.videoim.view.dialog.BaseDialogFragment;
import com.dfcj.videoim.view.dialog.OcrMsgEditDialog;
import com.dfcj.videoim.view.dialog.QuanXianDialog;
import com.dfcj.videoim.view.dialog.SelectImDialog;
import com.dfcj.videoim.view.dialog.WhellViewDialog;
import com.tencent.aai.model.type.EngineModelType;

import java.util.ArrayList;

public class MyDialogUtil {


    public static SelectImDialog showSelectImDialog(AppCompatActivity appCompatActivity){

        Bundle bundle3 = new Bundle();
        bundle3.putBoolean(SelectImDialog.DIALOG_BACK, true);
        bundle3.putBoolean(SelectImDialog.DIALOG_CANCELABLE_TOUCH_OUT_SIDE, true);

        SelectImDialog fxdialog = SelectImDialog.newInstance(SelectImDialog.class, bundle3);
        fxdialog.show(appCompatActivity.getSupportFragmentManager(), OcrMsgEditDialog.class.getName());


        fxdialog.setYesOnclickListener(new SelectImDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(int sel) {


                if(sel==1){//会话
                    SharedPrefsUtils.putValue(AppConstant.CHAT_TYPE, "1");

                }else{//视频
                    SharedPrefsUtils.putValue(AppConstant.CHAT_TYPE, "2");
                }

                ARouter.getInstance().build(Rout.toMain)
                        .withTransition(R.anim.push_left_in,R.anim.push_left_out)
                        .navigation(appCompatActivity);

            }
        });

        return fxdialog;

    }



    public static OcrMsgEditDialog ocrEditMsgDialog(String onc){

        Bundle bundle3 = new Bundle();
        bundle3.putBoolean(OcrMsgEditDialog.DIALOG_BACK, true);
        bundle3.putBoolean(OcrMsgEditDialog.DIALOG_CANCELABLE_TOUCH_OUT_SIDE, true);
        bundle3.putString("mainOcrContentTvVal",""+onc);

        OcrMsgEditDialog fxdialog = OcrMsgEditDialog.newInstance(OcrMsgEditDialog.class, bundle3);

        return fxdialog;

    }

    public static QuanXianDialog authorityDialog(){

        Bundle bundle3 = new Bundle();
        bundle3.putBoolean(QuanXianDialog.DIALOG_BACK, true);
        bundle3.putBoolean(QuanXianDialog.DIALOG_CANCELABLE_TOUCH_OUT_SIDE, true);

        QuanXianDialog fxdialog = QuanXianDialog.newInstance(QuanXianDialog.class, bundle3);

        return fxdialog;

    }


    //语言选择
    public static void showMyLanguageDialog(){

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
     /*   xdialog.show(getSupportFragmentManager(), WhellViewDialog.class.getName());
        xdialog.setYesOnclickListener(new WhellViewDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(ArrayList<String> arrayList, int index) {
                String s = arrayList.get(index);

                binding.mainLayoutLanguageTv.setText(""+s);

                switch (index){

                    case 0:
                        ocrUtil.myLanguageTypt= EngineModelType.EngineModelType16K.getType();

                        break;
                    case 1:
                        ocrUtil.myLanguageTypt=EngineModelType.EngineModelType16KEN.getType();
                        break;
                    case 2:
                        ocrUtil.myLanguageTypt="16k_ca";
                        break;

                }


            }
        });*/


    }





}
