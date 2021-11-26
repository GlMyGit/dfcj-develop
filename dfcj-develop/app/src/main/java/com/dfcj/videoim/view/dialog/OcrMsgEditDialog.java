package com.dfcj.videoim.view.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dfcj.videoim.R;
import com.dfcj.videoim.util.other.ScreenUtil;

public class OcrMsgEditDialog extends BaseDialogFragment{


    private TextView t1;
    private EditText t2;
    private ImageView img1,img2;
    private String mainOcrContentTvVal;

    @Override
    protected int getLayoutResId() {
        return R.layout.ocr_msg_edit_dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setLayout(ScreenUtil.getScreenWidth(activity), getDialog().getWindow().getAttributes().height);

        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        //设置dialog的位置在底部
        lp.gravity = Gravity.CENTER;
        //设置dialog的动画
        lp.windowAnimations = R.style.FragmentDialogAnimation;
        getDialog().getWindow().setAttributes(lp);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainOcrContentTvVal = bundle.getString("mainOcrContentTvVal");
    }

    @Override
    protected void initView(View view) {

        t1 = view.findViewById(R.id.ocr_msg_edit_tv1);
        t2 =  view.findViewById(R.id.ocr_msg_edit_tv2);
        img1 = view.findViewById(R.id.ocr_msg_edit_img1);
        img2= view.findViewById(R.id.ocr_msg_edit_img2);


        t2.setText(""+mainOcrContentTvVal);


    }

    @Override
    protected void setSubView() {

    }

    @Override
    protected void initEvent() {


        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String trim = t2.getText().toString().trim();
                if(yesOnclickListener!=null){
                    yesOnclickListener.onYesClick(trim);
                }
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
        void onYesClick(String tl);
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
