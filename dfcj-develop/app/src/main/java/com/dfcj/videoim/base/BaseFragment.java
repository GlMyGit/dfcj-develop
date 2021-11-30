package com.dfcj.videoim.base;


import androidx.databinding.ViewDataBinding;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dfcj.videoim.http.MaterialDialogUtils;
import com.dfcj.videoim.view.other.MyDialogLoading;
import com.wzq.mvvmsmart.base.BaseFragmentMVVM;


public abstract class BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends BaseFragmentMVVM<V, VM> {
    public final String TAG = getClass().getSimpleName();

    private MyDialogLoading dialog;

    public void showLoading(String title) {

        dialog = new MyDialogLoading(getActivity());
        dialog.setDialogLabel(title);
        dialog.show();

    }

    public void dismissLoading() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
