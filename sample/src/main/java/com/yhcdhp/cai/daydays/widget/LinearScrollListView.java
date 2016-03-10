package com.yhcdhp.cai.daydays.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * scrollview嵌套gridview可以正常显示
 */
public class LinearScrollListView extends ListView {
    public LinearScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearScrollListView(Context context) {
        super(context);
    }

    public LinearScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
} 