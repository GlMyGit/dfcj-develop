package com.dfcj.videoim.view.dialog;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.dfcj.videoim.R;
import com.dfcj.videoim.util.other.ScreenUtil;


public class UserXieYiDialog extends BaseDialogFragment {

    private TextView cancelTV;
    private TextView okTv,user_xieyi_title;
    private TextView user_xieyi_yinsi_tv,user_xieyi_us_tv;


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setLayout(ScreenUtil.getScreenWidth(activity), getDialog().getWindow().getAttributes().height);
    }



    @Override
    protected void initView(View view) {
        cancelTV=view.findViewById(R.id.user_xieyi_close_app_tv);
        okTv=view.findViewById(R.id.user_xieyi_ok_tv);
        user_xieyi_yinsi_tv=view.findViewById(R.id.user_xieyi_yinsi_tv);
        user_xieyi_us_tv=view.findViewById(R.id.user_xieyi_us_tv);
        user_xieyi_title=view.findViewById(R.id.user_xieyi_title);


        user_xieyi_title.setText("欢迎使用"+getString(R.string.app_name)+"APP");

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.user_xieyi;
    }

    @Override
    protected void setSubView() {

        //确定
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(1);
                }

            }
        });

        //隐私协议
        user_xieyi_yinsi_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(2);
                }

            }
        });
        //用户协议
        user_xieyi_us_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(3);
                }

            }
        });

    }

    @Override
    protected void initEvent() {
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    @Override
    protected void onCancel() {

    }

    //private String yesStr, noStr;
    public onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    public onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    public void setNoOnclickListener(onNoOnclickListener onNoOnclickListener) {

        this.noOnclickListener = onNoOnclickListener;
    }

    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {

        this.yesOnclickListener = onYesOnclickListener;
    }


    public interface onYesOnclickListener {
        void onYesClick(int st);
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }



}
