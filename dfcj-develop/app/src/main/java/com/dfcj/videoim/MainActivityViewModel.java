package com.dfcj.videoim;

import android.app.Application;

import androidx.annotation.NonNull;

import com.dfcj.videoim.api.ApiService;
import com.dfcj.videoim.api.HostType;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.base.BaseViewModel;
import com.dfcj.videoim.entity.ChangeCustomerServiceEntity;
import com.dfcj.videoim.entity.HistoryMsgEntity;
import com.dfcj.videoim.entity.LoginBean;
import com.dfcj.videoim.entity.SendOffineMsgEntity;
import com.dfcj.videoim.entity.TrtcRoomEntity;
import com.dfcj.videoim.entity.UserInfoEntity;
import com.dfcj.videoim.entity.upLoadImgEntity;
import com.dfcj.videoim.http.RetrofitClient;
import com.dfcj.videoim.im.ImUtils;
import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.wzq.mvvmsmart.event.SingleLiveEvent;
import com.wzq.mvvmsmart.utils.RxUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivityViewModel extends BaseViewModel {

    //使用Observable
    public SingleLiveEvent<Boolean> requestCameraPermissions = new SingleLiveEvent<>();
    //使用LiveData
    public SingleLiveEvent<UserInfoEntity> userInfoEntity = new SingleLiveEvent<>();
    public SingleLiveEvent<LoginBean> loadEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<SendOffineMsgEntity> sendOffineMsgEntity = new SingleLiveEvent<>();
    public SingleLiveEvent<ChangeCustomerServiceEntity> changeCustomerServiceEntity = new SingleLiveEvent<>();
    public SingleLiveEvent<upLoadImgEntity> upLoad_ImgEntity = new SingleLiveEvent<>();
    public SingleLiveEvent<TrtcRoomEntity> trtcRoomEntity = new SingleLiveEvent<>();
    public SingleLiveEvent<HistoryMsgEntity> historyMsgEntity = new SingleLiveEvent<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LoginBean mLoginBean;

    public void getCustomerInfo(String token) {
        Map<String, Object> params = new HashMap<>();
        params.put("token", token);

        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .getCustomerInfo(params)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new Observer<UserInfoEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // stateLiveData.postLoading();
                    }

                    @Override
                    public void onNext(@NonNull UserInfoEntity bean) {
                        userInfoEntity.postValue(bean);
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


    //登录
    public void requestLogin() {

        Map<String, Object> params = new HashMap<>();
        params.put("customerCode", "" + SharedPrefsUtils.getValue(AppConstant.MYUSERID));
        params.put("faceUrl", SharedPrefsUtils.getValue(AppConstant.MyUserIcon));
        params.put("nick", SharedPrefsUtils.getValue(AppConstant.MyUserName));
        params.put("osInfo", AppUtils.getSystemModel());
        params.put("osType", "2");//1、iOS系统、2、Android系统、3、微信小程序4、Mobile 5、电脑端PC网页浏览器

        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .requestLogin(params)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new Observer<LoginBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // stateLiveData.postLoading();
                    }

                    @Override
                    public void onNext(@NonNull LoginBean bean) {
                        loadEvent.postValue(bean);
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


    //发送离线消息 智能客服 小I机器人
    public void sendOfflineMsg(String question) {
        Map<String, Object> params = new HashMap<>();

        params.put("question", "" + question);
        params.put("userId", "" + SharedPrefsUtils.getValue(AppConstant.MYUSERID));

        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .requestSendOffineMsg(params)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new Observer<SendOffineMsgEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // stateLiveData.postLoading();
                    }

                    @Override
                    public void onNext(@NonNull SendOffineMsgEntity bean) {
                        sendOffineMsgEntity.postValue(bean);
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


    //获取客服
    public void getImStaff() {
        String systemModel = AppUtils.getSystemModel();

        Map<String, Object> params = new HashMap<>();
        params.put("customerCode", "" + SharedPrefsUtils.getValue(AppConstant.MYUSERID));
        params.put("faceUrl", SharedPrefsUtils.getValue(AppConstant.MyUserIcon));
        params.put("nick", "" + SharedPrefsUtils.getValue(AppConstant.MyUserName));
        params.put("osInfo", "" + systemModel);
        params.put("osType", "2");//1、iOS系统、2、Android系统、3、微信小程序4、Mobile 5、电脑端PC网页浏览器

        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .requestChangeCustomerService(params)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new Observer<ChangeCustomerServiceEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // stateLiveData.postLoading();
                    }

                    @Override
                    public void onNext(@NonNull ChangeCustomerServiceEntity bean) {
                        changeCustomerServiceEntity.postValue(bean);
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


    //上传图片
    public void fileUpload(String params) {
        File mFile = new File(params);

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), mFile);
        MultipartBody.Part partFile1 = MultipartBody.Part.createFormData("file", mFile.getName(), requestBody1);

        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .requestUploadImg(partFile1)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new Observer<upLoadImgEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        stateLiveData.postLoading();
                    }

                    @Override
                    public void onNext(@NonNull upLoadImgEntity bean) {
                        upLoad_ImgEntity.postValue(bean);
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


    //获取视频房间号
    public void getTrtcRoomId(String eventId) {
        String systemModel = AppUtils.getSystemModel();

        Map<String, Object> params = new HashMap<>();
        params.put("customerCode", "" + SharedPrefsUtils.getValue(AppConstant.MYUSERID));
        params.put("eventId", "" +eventId);
        params.put("osInfo", "" + systemModel);
        params.put("osType", "2");//1、iOS系统、2、Android系统、3、微信小程序4、Mobile 5、电脑端PC网页浏览器

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

    //获取历史接口
    public void getCustImRecord(int page, String time) {
        Map<String, Object> params = new HashMap<>();

        Map<String, Object> params2 = new HashMap<>();
        params2.put("pageNo", page);
        params2.put("pageSize", 20);

        params.put("page", params2);
        params.put("custCode", SharedPrefsUtils.getValue(AppConstant.MYUSERID));
        params.put("startDate", time);

        RetrofitClient.getInstance(HostType.lOAN_STEWARD_MAIN_HOST).create(ApiService.class)
                .requestQueryCustImRecord(params)
                .compose(RxUtils.observableToMain()) //线程调度,compose操作符是直接对当前Observable进行操作（可简单理解为不停地.方法名（）.方法名（）链式操作当前Observable）
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(MainActivityViewModel.this)    //  请求与ViewModel周期同步
                .subscribe(new Observer<HistoryMsgEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //stateLiveData.postLoading();
                    }

                    @Override
                    public void onNext(@NonNull HistoryMsgEntity entity) {
                        historyMsgEntity.postValue(entity);
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
