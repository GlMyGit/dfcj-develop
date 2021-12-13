package com.dfcj.videoim.view.widght;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;


public class CircleImageView extends androidx.appcompat.widget.AppCompatImageView {
    private float width;
    private float height;
    private float radius;
    private Paint mPaint;
    private Matrix mMatrix;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);//给画笔设置抗锯齿
        mMatrix = new Matrix();
        //该方法千万别放到onDraw()方法里面调用，否则会不停的重绘的，因为该方法调用了invalidate() 方法
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//禁用硬加速

    }

    /**
     * 测量控件的宽高，并获取其内切圆的半径
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
       // width = getMeasuredWidth();
      //  height = getMeasuredHeight();
       // radius = Math.min(width, height) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
       // paint.setShader(initBitmapShader());//将着色器设置给画笔
       // canvas.drawCircle(width / 2, height / 2, radius, paint);//使用画笔在画布上画圆

        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            //通过BitmapShader的方式实现
            drawRoundByShaderMode(canvas, bitmap);
        } else {
            super.onDraw(canvas);
        }


    }

    private void drawRoundByShaderMode(Canvas canvas, Bitmap bitmap) {
        //获取到Bitmap的宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        //获取到ImageView的宽高
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        //根据Bitmap生成一个BitmapShader，这里有个TileMode设置有三个参数可以选择：
        //CLAMP, MIRROR, REPEAT 在后面会细说。
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mMatrix.reset();

        float minScale = Math.min(viewWidth / (float) bitmapWidth, viewHeight / (float) bitmapHeight);
        mMatrix.setScale(minScale, minScale);//将Bitmap保持比列根据ImageView的大小进行缩放
        bitmapShader.setLocalMatrix(mMatrix); //将矩阵变化设置到BitmapShader,其实就是作用到Bitmap
        mPaint.setShader(bitmapShader);

        //绘制圆形
        canvas.drawCircle(bitmapWidth / 2, bitmapHeight / 2, Math.min(bitmapWidth / 2, bitmapHeight / 2), mPaint);
    }


}