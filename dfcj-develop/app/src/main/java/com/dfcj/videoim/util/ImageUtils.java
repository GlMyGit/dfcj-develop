package com.dfcj.videoim.util;

import android.content.Context;
import android.text.TextUtils;

import com.dfcj.videoim.view.dialog.QuanXianDialog;
import com.wzq.mvvmsmart.utils.KLog;

import java.io.File;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class ImageUtils {



    public  void initCompressorRxJava(Context conn,String path){

        Luban.with(conn)
                .load(path)
                .ignoreBy(100)
                .setTargetDir(AppUtils.getPath())
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件

                        //返回的图片为file
                        //图片显示
                        String path1 = file.getPath();
                        KLog.d("压缩成功了path1:"+path1);
                        if(yesOnclickListener!=null){
                            yesOnclickListener.onYesClick(path1);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();


    }


    public  onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    public onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    public void setNoOnclickListener(onNoOnclickListener onNoOnclickListener) {

        this.noOnclickListener = onNoOnclickListener;
    }

    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {

        this.yesOnclickListener = onYesOnclickListener;
    }

    public interface onYesOnclickListener {
        void onYesClick(String paht);
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }




}

