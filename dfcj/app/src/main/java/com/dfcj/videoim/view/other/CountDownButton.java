package com.dfcj.videoim.view.other;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.widget.TextView;


/**
 * 倒计时
 */

public class CountDownButton extends CountDownTimer
{
    public static final int TIME_COUNT_FUTURE = 60000;
    public static final int TIME_COUNT_INTERVAL = 1000;

    private Context mContext;
    private TextView mButton;
    private String mOriginalText;

     private int mOriginalBackgroundend;
     private int mTickBackgroundstart;
    // private int mOriginalTextColor;

    private int colortop;
    private int colorbottom;

    public CountDownButton()
    {
        super(TIME_COUNT_FUTURE, TIME_COUNT_INTERVAL);
    }

    public CountDownButton(long millisInFuture, long countDownInterval)
    {
        super(millisInFuture, countDownInterval);
    }

    public void init(Context context, TextView register_btn_sendverificationcode,int daojishiimg,int endimg,int daojishi,int endcolor)
    {
        this.mContext = context;
        this.mButton = (TextView) register_btn_sendverificationcode;
        this.mOriginalText = mButton.getText().toString();
        this.mOriginalBackgroundend =endimg;
        this.colortop=endcolor;
        this.colorbottom=daojishi;
        // context.getResources().getDrawable(R.color.unChecked);
         this.mTickBackgroundstart = daojishiimg;
        // this.mOriginalTextColor = mButton.getCurrentTextColor();
    }

    public void setTickDrawable(Drawable tickDrawable)
    {
        // this.mTickBackground = tickDrawable;
    }

    @Override
    public void onFinish()  //结束
    {
        if (mContext != null && mButton != null)
        {
            mButton.setText(mOriginalText);
            mButton.setTextColor(mContext.getResources().getColor(colortop));
             mButton.setBackgroundResource(mOriginalBackgroundend);
            mButton.setClickable(true);
            mButton.setEnabled(true);
        }
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        if (mContext != null && mButton != null)
        {
            mButton.setClickable(false);
            mButton.setEnabled(false);
            mButton.setBackgroundResource(mTickBackgroundstart);
            mButton.setTextColor(mContext.getResources().getColor(colorbottom));
            mButton.setText(millisUntilFinished / 1000 + "s");
        }
    }

   /* public void init(MobilePhoneBindingActivity context,
            ImageView phonebinding_btn_sendverificationcode)
    {
        // TODO Auto-generated method stub

    }*/
}