package com.dfcj.videoim.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.dfcj.videoim.BR;
import com.dfcj.videoim.R;
import com.dfcj.videoim.base.BaseFragment;
import com.dfcj.videoim.databinding.MainFragmentBinding;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wzq.mvvmsmart.utils.ToastUtils;

import io.reactivex.functions.Consumer;

public class MainFragment extends BaseFragment<MainFragmentBinding, MainViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.main_fragment;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        binding.setPresenter(new Presenter());
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        binding.button.setOnClickListener(view -> {
            viewModel.getDate();
        });

        viewModel.liveData.observe(this,itemsEntities -> {
                    if(viewModel.pageNum==1){

                    }
        });


    }

    /**
     * 封装布局中的点击事件儿;
     */
    public class Presenter {

        //网络访问点击事件
        public void netWorkClick() {
//            NavHostFragment
//                    .findNavController(MainFragment.this)
//                    .navigate(R.id.action_homeFragment_to_netWorkFragment);
        }

        //权限申请
        public void permissionsClick(){

        }

        //全局异常捕获
        public void exceptionClick() {
            //伪造一个异常
           // Integer.parseInt("test");
        }

        //文件下载
        public void fileDownLoadClick() {
           // viewModel.loadUrlEvent.setValue("http://gdown.baidu.com/data/wisegame/a2cd8828b227b9f9/neihanduanzi_692.apk");
        }

    }


    /**
     * 请求相机权限
     */
    @SuppressLint("CheckResult")
    private void requestCameraPermissions() {
        ToastUtils.showShort("请求相机权限");
        //请求打开相机权限
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            ToastUtils.showShort("相机权限已经打开，直接跳入相机");
                        } else {
                            ToastUtils.showShort("权限被拒绝");
                        }
                    }
                });
    }


}
