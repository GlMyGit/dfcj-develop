package com.dfcj.videoim.view.other;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class XiaYiYeNestedScrollView  extends NestedScrollView {


    private OnXiaYiYeScrollChanged mOnXiaYiYeScrollChanged;

    public XiaYiYeNestedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public XiaYiYeNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XiaYiYeNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnXiaYiYeScrollChanged != null) {
            mOnXiaYiYeScrollChanged.onScroll(l, t, oldl, oldt);
        }
    }

    public void setXiaYiYeOnScrollChanged(OnXiaYiYeScrollChanged onXiaYiYeScrollChanged) {
        this.mOnXiaYiYeScrollChanged = onXiaYiYeScrollChanged;
    }

    public interface OnXiaYiYeScrollChanged {
        /**
         * 滑动的方法
         *
         * @param left    左边
         * @param top     上边
         * @param oldLeft 之前的左边
         * @param oldTop  之前的上边
         */
        void onScroll(int left, int top, int oldLeft, int oldTop);
    }



}
