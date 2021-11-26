package com.dfcj.videoim;

import android.app.Application;

import androidx.annotation.NonNull;

import com.dfcj.videoim.base.BaseViewModel;
import com.wzq.mvvmsmart.event.SingleLiveEvent;

public class MainActivityViewModel extends BaseViewModel {

    //使用Observable
    public SingleLiveEvent<Boolean> requestCameraPermissions = new SingleLiveEvent<>();
    //使用LiveData
    public SingleLiveEvent<String> loadUrlEvent = new SingleLiveEvent<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }






}
