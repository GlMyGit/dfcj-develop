package com.dfcj.videoim.view.other;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;

public class MyRecyclerViewGlideLayoutManager extends GridLayoutManager {

    private boolean isScrollEnabled = true;

    public MyRecyclerViewGlideLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyRecyclerViewGlideLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public MyRecyclerViewGlideLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
