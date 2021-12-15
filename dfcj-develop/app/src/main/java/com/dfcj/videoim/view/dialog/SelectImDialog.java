package com.dfcj.videoim.view.dialog;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dfcj.videoim.R;
import com.dfcj.videoim.util.other.ScreenUtil;

//相机权限
public class SelectImDialog extends BaseDialogFragment{


    private TextView t1,t2;
    private ImageView select_im_close;
   // private ImageView img1,img2;

    @Override
    protected int getLayoutResId() {
        return R.layout.select_im_dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setLayout(ScreenUtil.getScreenWidth(activity), getDialog().getWindow().getAttributes().height);

    }

    @Override
    protected void initView(View view) {

        t1 = view.findViewById(R.id.authority_tv1);
        t2 =  view.findViewById(R.id.authority_tv2);
        select_im_close =  view.findViewById(R.id.select_im_close);



    }

    @Override
    protected void setSubView() {

    }

    @Override
    protected void initEvent() {


        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(yesOnclickListener!=null){
                    yesOnclickListener.onYesClick(2);
                }
            }
        });

        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(yesOnclickListener!=null){
                    yesOnclickListener.onYesClick(1);
                }
            }
        });

        select_im_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });



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
        void onYesClick(int sel);
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
