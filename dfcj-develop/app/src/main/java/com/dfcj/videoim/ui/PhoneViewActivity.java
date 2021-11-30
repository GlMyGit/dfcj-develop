package com.dfcj.videoim.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dfcj.videoim.BR;
import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.base.BaseActivity;
import com.dfcj.videoim.base.BaseViewModel;
import com.dfcj.videoim.databinding.PhoneViewActivtiyBinding;
import com.dfcj.videoim.util.other.GlideUtils;
import com.wzq.mvvmsmart.utils.KLog;


@Route(path = Rout.PhoneViewActivity)
public class PhoneViewActivity extends BaseActivity<PhoneViewActivtiyBinding, BaseViewModel> {

    String thumbImgUrl;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.phone_view_activtiy;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            thumbImgUrl=extras.getString("thumbImgUrl");
        }
    }

    @Override
    public void initData() {
        super.initData();

        binding.titleTop.unifiedHeadTitleTx.setText("");
        binding.titleTop.unifiedHeadBackImg.setImageResource(R.drawable.ser_pic8);


        KLog.d("接收的图片："+thumbImgUrl);


        Glide.with(this).load(thumbImgUrl).error(R.drawable.default_img_failed).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                //这里可以给错误提示,暂时未写
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                //  图片记载完成 ,隐藏loading

                binding.phoneViewLoad.setVisibility(View.GONE);

                return false;
            }
        }).into(binding.phoneView);

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        binding.titleTop.unifiedHeadBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
