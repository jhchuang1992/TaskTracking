package com.edu.ouc.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by JHC on 2017/11/28.
 * 班长给员工派发任务时的员工列表重写listview
 */

public class PaiFaListView extends ListView {
    public PaiFaListView(Context context) {
        super(context);
    }
    public PaiFaListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaiFaListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
