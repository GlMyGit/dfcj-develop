package com.dfcj.videoim.ui.video;

import android.app.Application;

import androidx.annotation.NonNull;

import com.dfcj.videoim.MainActivityViewModel;
import com.dfcj.videoim.api.ApiService;
import com.dfcj.videoim.api.HostType;
import com.dfcj.videoim.appconfig.AppApplicationMVVM;
import com.dfcj.videoim.base.BaseViewModel;
import com.dfcj.videoim.entity.ChangeCustomerServiceEntity;
import com.dfcj.videoim.entity.TrtcRoomEntity;
import com.dfcj.videoim.http.RetrofitClient;
import com.dfcj.videoim.im.ImUtils;
import com.dfcj.videoim.rx.RxSubscriber;
import com.dfcj.videoim.util.AppUtils;
import com.wzq.mvvmsmart.event.SingleLiveEvent;
import com.wzq.mvvmsmart.utils.RxUtils;

import java.util.HashMap;
import java.util.Map;

public class VideoCallingViewModel  extends BaseViewModel {





    public VideoCallingViewModel(@NonNull Application application) {
        super(application);
    }






}
