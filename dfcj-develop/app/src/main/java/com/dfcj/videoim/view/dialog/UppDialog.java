package com.dfcj.videoim.view.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.dfcj.videoim.R;
import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.other.ScreenUtil;

public class UppDialog extends BaseDialogFragment {

    private TextView update_info_gx_text,versionchecklib_version_dialog_cancel,versionchecklib_version_dialog_commit;
    private View update_info_xian;
    private String mContent;
    private String isUppCompulsory;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setLayout(ScreenUtil.getScreenWidth(activity), getDialog().getWindow().getAttributes().height);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContent= bundle.getString("mContent");
        isUppCompulsory= bundle.getString("isUppCompulsory");
    }

    @Override
    protected void initView(View view) {

        update_info_gx_text=view.findViewById(R.id.update_info_gx_text);
        versionchecklib_version_dialog_cancel=view.findViewById(R.id.versionchecklib_version_dialog_cancel);
        versionchecklib_version_dialog_commit=view.findViewById(R.id.versionchecklib_version_dialog_commit);
        update_info_xian=view.findViewById(R.id.update_info_xian);


        update_info_gx_text.setText(""+mContent);


        //强制更新 1是 0否
        if("1".equals(isUppCompulsory)){
            AppUtils.setMyViewIsGone(update_info_xian);
            AppUtils.setMyViewIsGone(versionchecklib_version_dialog_cancel);
            versionchecklib_version_dialog_commit.setText("立即更新");
        }else{
            AppUtils.setMyViewIsVisibity(update_info_xian);
            AppUtils.setMyViewIsVisibity(versionchecklib_version_dialog_cancel);
            versionchecklib_version_dialog_commit.setText("更新");
        }

    }



    @Override
    protected int getLayoutResId() {
        return R.layout.upp1_dialog;
    }

    @Override
    protected void setSubView() {

    }

    @Override
    protected void initEvent() {

        versionchecklib_version_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });

        versionchecklib_version_dialog_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(yesOnclickListener!=null){
                    yesOnclickListener.onYesClick();
                }
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
