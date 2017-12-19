package com.edu.ouc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.ouc.R;

/**
 * Created by JHC on 2017/11/27.
 */

public class UpLoadingDialog extends Dialog {
    public String dialogText="加载中......";

    public String getDialogText() {
        return dialogText;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

    public TextView tv;
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
        tv.setText(getDialogText());
        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.llo_uploadsummary);
        linearLayout.getBackground().setAlpha(210);
    }

}
