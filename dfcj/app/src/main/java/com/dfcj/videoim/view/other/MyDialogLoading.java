package com.dfcj.videoim.view.other;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.dfcj.videoim.R;


public class MyDialogLoading extends Dialog {

    private TextView loadingLabel;

    public MyDialogLoading(Context context) {
        super(context, R.style.Dialog);
        setContentView(R.layout.mydialog_loading_layout);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        loadingLabel = (TextView) findViewById(R.id.myloading_text);
    }

    public void setDialogLabel(String label) {
        loadingLabel.setText(label);
    }

}
