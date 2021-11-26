package com.dfcj.videoim.view.other;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;

public class MyRecyclerViewLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public MyRecyclerViewLayoutManager(Context context) {
        super(context);
    }

    public MyRecyclerViewLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyRecyclerViewLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }



}
