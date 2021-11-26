package com.dfcj.videoim.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.dfcj.videoim.base.BaseViewModel;
import com.dfcj.videoim.data.source.http.service.MyApiService;
import com.dfcj.videoim.entity.DemoBean;
import com.dfcj.videoim.http.RetrofitClient;
import com.wzq.mvvmsmart.event.SingleLiveEvent;
import com.wzq.mvvmsmart.http.BaseResponse;
import com.wzq.mvvmsmart.utils.KLog;
import com.wzq.mvvmsmart.utils.RxUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class MainViewModel extends BaseViewModel {
    //使用Observable
    public SingleLiveEvent<Boolean> requestCameraPermissions = new SingleLiveEvent<>();
    //使用LiveData
    public SingleLiveEvent<String> loadUrlEvent = new SingleLiveEvent<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> liveData;

    public int pageNum = 1;

    public void getDate(){

        MyApiService  apiService = RetrofitClient.getInstance().create(MyApiService.class);


        apiService.demoGet(1)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainViewModel.this)
        .subscribe(new Observer<BaseResponse<DemoBean>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                stateLiveData.postLoading();
            }

            @Override
            public void onNext(@NonNull BaseResponse<DemoBean> response) {
                //请求成功
                if (response.getCode() == 1) {
                    liveData.postValue(response.toString());
                }else {
                    KLog.e("数据返回null");
                    stateLiveData.postError();
                }

            }

            @Override
            public void onError(@NonNull Throwable e) {
                //关闭对话框
                stateLiveData.postIdle();
            }

            @Override
            public void onComplete() {
                //关闭对话框
                stateLiveData.postIdle();
            }
        });

        apiService.getJsonFile()
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainViewModel.this)
        .subscribe(new Observer<BaseResponse<Object>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                KLog.e("开始请求...");
            }

            @Override
            public void onNext(@NonNull BaseResponse<Object> response) {
                KLog.e("返回数据: " + response.toString());
                //请求成功
                if (response.getCode() == 1) {
                    liveData.postValue(response.toString());
                }


            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                KLog.e("错误onError" + throwable.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });   //  请求与ViewModel周期同步


    }


}
