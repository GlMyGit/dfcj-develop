package com.dfcj.videoim;

import android.app.Application;

import androidx.annotation.NonNull;

import com.dfcj.videoim.api.ApiService;
import com.dfcj.videoim.api.HostType;
import com.dfcj.videoim.appconfig.AppApplicationMVVM;
import com.dfcj.videoim.base.BaseRespose;
import com.dfcj.videoim.base.BaseViewModel;
import com.dfcj.videoim.entity.ChangeCustomerServiceEntity;
import com.dfcj.videoim.entity.LoginBean;
import com.dfcj.videoim.entity.SendOffineMsgEntity;
import com.dfcj.videoim.entity.TrtcRoomEntity;
import com.dfcj.videoim.entity.upLoadImgEntity;
import com.dfcj.videoim.http.RetrofitClient;
import com.dfcj.videoim.im.ImUtils;
import com.dfcj.videoim.rx.RxSubscriber;
import com.dfcj.videoim.ui.video.VideoCallingViewModel;
import com.dfcj.videoim.util.AppUtils;
import com.wzq.mvvmsmart.event.SingleLiveEvent;
import com.wzq.mvvmsmart.utils.RxUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivityViewModel extends BaseViewModel {

    //使用Observable
    public SingleLiveEvent<Boolean> requestCameraPermissions = new SingleLiveEvent<>();
    //使用LiveData
    public SingleLiveEvent<LoginBean> loadEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<SendOffineMsgEntity> sendOffineMsgEntity = new SingleLiveEvent<>();
    public SingleLiveEvent<ChangeCustomerServiceEntity> changeCustomerServiceEntity = new SingleLiveEvent<>();
    public SingleLiveEvent<upLoadImgEntity> upLoad_ImgEntity = new SingleLiveEvent<>();
    public SingleLiveEvent<TrtcRoomEntity> trtcRoomEntity = new SingleLiveEvent<>();


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LoginBean mLoginBean;


    public void requestLogin(){

        ApiService apiService = RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class);

        String systemModel = AppUtils.getSystemModel();

        Map<String, Object> params = new HashMap<>();

        params.put("customerCode",""+ ImUtils.MyUserId);
        params.put("osInfo",""+systemModel);
        params.put("osType","2");//1、iOS系统、2、Android系统、3、微信小程序4、Mobile 5、电脑端PC网页浏览器


        apiService.requestLogin(params)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new RxSubscriber<LoginBean>(AppApplicationMVVM.getInstance(),"加载中",true) {
                    @Override
                    protected void _onNext(LoginBean loginBean) {
                        if(loginBean!=null){
                            mLoginBean=loginBean;
                            loadEvent.postValue(loginBean);
                        }
                    }

                    @Override
                    protected void _onError(String message) {

                    }
                });



    }


    //发送离线消息
    public void sendOfflineMsg(String question){


        Map<String, Object> params = new HashMap<>();

        params.put("question",""+ question);
        params.put("userId",""+ImUtils.MyUserId);



        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .requestSendOffineMsg(params)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new RxSubscriber<SendOffineMsgEntity>(AppApplicationMVVM.getInstance(),"加载中",true) {
                    @Override
                    protected void _onNext(SendOffineMsgEntity bean) {

                        sendOffineMsgEntity.postValue(bean);

                    }

                    @Override
                    protected void _onError(String message) {

                    }
                });



    }


    //获取客服
    public void getImStaff(){


        String systemModel = AppUtils.getSystemModel();

        Map<String, Object> params = new HashMap<>();

        params.put("customerCode",""+ ImUtils.MyUserId);
        params.put("osInfo",""+systemModel);
        params.put("osType","2");//1、iOS系统、2、Android系统、3、微信小程序4、Mobile 5、电脑端PC网页浏览器


        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .requestChangeCustomerService(params)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new RxSubscriber<ChangeCustomerServiceEntity>(AppApplicationMVVM.getInstance(),"加载中",true) {
                    @Override
                    protected void _onNext(ChangeCustomerServiceEntity bean) {

                        changeCustomerServiceEntity.postValue(bean);

                    }

                    @Override
                    protected void _onError(String message) {

                    }
                });



    }



    //上传图片
    public void fileUpload(String params){


        File mFile = new File(params);

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), mFile);
        MultipartBody.Part partFile1 = MultipartBody.Part.createFormData("file", mFile.getName(), requestBody1);


        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .requestUploadImg(partFile1)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new RxSubscriber<upLoadImgEntity>(AppApplicationMVVM.getInstance(),"加载中",true) {
                    @Override
                    protected void _onNext(upLoadImgEntity bean) {

                        upLoad_ImgEntity.postValue(bean);

                    }

                    @Override
                    protected void _onError(String message) {

                    }
                });



    }




    //获取视频房间号
    public void getTrtcRoomId(String eventId){


        String systemModel = AppUtils.getSystemModel();

        Map<String, Object> params = new HashMap<>();

       // params.put("customerCode",""+ ImUtils.MyUserId);

        params.put("eventId",""+eventId);

        params.put("osInfo",""+systemModel);
        params.put("osType","2");//1、iOS系统、2、Android系统、3、微信小程序4、Mobile 5、电脑端PC网页浏览器


        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .requestTrtcRoomId(params)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new Observer<TrtcRoomEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                         stateLiveData.postLoading();
                    }

                    @Override
                    public void onNext(@NonNull TrtcRoomEntity bean) {
                        trtcRoomEntity.postValue(bean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        stateLiveData.postIdle();
                    }

                    @Override
                    public void onComplete() {
                        stateLiveData.postIdle();
                    }
                });



    }





}
