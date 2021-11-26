package com.dfcj.videoim.util;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class CustomUrlSpan extends ClickableSpan {


    private Context context;
    private String url;
    private OnClickInterface onClickInterface;
    public CustomUrlSpan(Context context,String url, OnClickInterface onClickInterface){
        this.context = context;
        this.url = url;
        this.onClickInterface = onClickInterface;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(true);//设置显示下划线
    }

    @Override
    public void onClick(View widget) {//链接地址点击事件监听
        if(onClickInterface != null){
            onClickInterface.onClick(widget, url, context);
        }
    }

    interface OnClickInterface{
        void onClick(View widget, String url, Context context);
    }


}
