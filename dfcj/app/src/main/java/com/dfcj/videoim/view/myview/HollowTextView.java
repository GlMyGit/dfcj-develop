package com.dfcj.videoim.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class HollowTextView extends AppCompatTextView {

    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode;

    public HollowTextView(Context context) {
        this(context,null);
    }

    public HollowTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR );
        //使用DST_OUT和XOR模式也可以实现镂空字体
        //mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        //mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //需要在新的图层上绘制，绘制完后再再叠加到旧的图层上。
        canvas.saveLayer(0,0,canvas.getWidth(),canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),mPaint);
        getPaint().setXfermode(mPorterDuffXfermode);
        super.onDraw(canvas);
        canvas.restore();
    }

}

