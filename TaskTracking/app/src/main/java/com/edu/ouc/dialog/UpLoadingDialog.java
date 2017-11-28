package com.edu.ouc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.ouc.tasktracking.R;

/**
 * Created by JHC on 2017/11/27.
 */

public class UpLoadingDialog extends Dialog {
    private TextView tv;
    /**
     * style很关键
     */
    public UpLoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_uploadsummary);
        tv = (TextView) findViewById(R.id.tv);
        tv.setText("正在提交数据.....");
        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.llo_uploadsummary);
        linearLayout.getBackground().setAlpha(210);
    }

}
