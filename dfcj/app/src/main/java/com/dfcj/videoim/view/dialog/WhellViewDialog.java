package com.dfcj.videoim.view.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.dfcj.videoim.R;
import com.dfcj.videoim.util.other.ScreenUtil;

import java.util.ArrayList;

//
public class WhellViewDialog extends BaseDialogFragment{


    private TextView whellview_cancel_tv,whellview_title_tv,whellview_ok_tv;
    private WheelView whellview_view;
   // private ImageView img1,img2;
    private ArrayList<String>  mylist;
    private int myIndex=0;
    private String myTitle="请选择";

    @Override
    protected int getLayoutResId() {
        return R.layout.whellview_dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow().setLayout(ScreenUtil.getScreenWidth(activity), getDialog().getWindow().getAttributes().height);

        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置dialog的位置在底部
        lp.gravity = Gravity.BOTTOM;
        //设置dialog的动画
        lp.windowAnimations = R.style.FragmentDialogAnimation;
        getDialog().getWindow().setAttributes(lp);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mylist=bundle.getStringArrayList("ListVal");
        myTitle= bundle.getString("myTitle");
    }

    @Override
    protected void initView(View view) {

        whellview_cancel_tv = view.findViewById(R.id.whellview_cancel_tv);
        whellview_title_tv =  view.findViewById(R.id.whellview_title_tv);
        whellview_ok_tv =  view.findViewById(R.id.whellview_ok_tv);
        whellview_view =  view.findViewById(R.id.whellview_view);


        if(!TextUtils.isEmpty(myTitle)){
            whellview_title_tv.setText(""+myTitle);
        }

        whellview_view.setCyclic(false);//是否循环滚动
        whellview_view.setCurrentItem(0);

        whellview_view.setTextSize(16);
        //whellview_view.setDividerType(WheelView.DividerType.CIRCLE);

        if(mylist!=null){

            whellview_view.setAdapter(new ArrayWheelAdapter(mylist));
            whellview_view.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    //Toast.makeText(MainActivity.this, "" + mOptionsItems.get(index), Toast.LENGTH_SHORT).show();

                    myIndex=index;
                }
            });

        }



    }

    @Override
    protected void setSubView() {

    }

    @Override
    protected void initEvent() {


        whellview_cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        whellview_ok_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(yesOnclickListener!=null){
                    yesOnclickListener.onYesClick(mylist,myIndex);
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
        void onYesClick(ArrayList<String>  arrayList,int index);
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
