package com.dfcj.videoim.rx;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.AppApplicationMVVM;
import com.dfcj.videoim.base.BaseApplication;
import com.dfcj.videoim.util.other.NetWorkUtils;
import com.dfcj.videoim.view.other.MyDialogLoading;
import com.wzq.mvvmsmart.utils.KLog;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class RxSubscriber<T> implements Observer<T> {

    private Context mContext;
    public MyDialogLoading dialogLoading;
    private String msg;
    private boolean showDialog = true;

    public RxSubscriber(Context context, String msg, boolean showDialog) {
        this.mContext = context;
        this.msg = msg;
        this.showDialog = showDialog;
        dialogLoading = new MyDialogLoading(AppApplicationMVVM.getInstance());
        dialogLoading.setDialogLabel(msg);
    }

    public RxSubscriber() {

    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
       /* if (showDialog && dialogLoading != null) {
            try {
                //显示dialog
                dialogLoading.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    @Override
    public void onError(Throwable e) {
       /* if (showDialog) {
            dialogLoading.dismiss();
        }*/

        //无网络
        if (!NetWorkUtils.isNetConnected(AppApplicationMVVM.getInstance())) {
            _onError(AppApplicationMVVM.getInstance().getString(R.string.no_net));
        }
        else if (e instanceof ServerException) { //自定义服务器错误
            _onError(e.getMessage());
        }
        else { //其它
            _onError(AppApplicationMVVM.getInstance().getString(R.string.net_error));
        }

        KLog.d("接口返回错误:"+e.getMessage());


    }

    @Override
    public void onComplete() {
      /*  if (showDialog && dialogLoading != null) {
            dialogLoading.dismiss();
        }*/

    }


    //子类中实现
    protected abstract void _onNext(T t);

    //子类中实现
    protected abstract void _onError(String message);


}
