package com.dfcj.videoim.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.BR;
import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.base.BaseActivity;
import com.dfcj.videoim.base.BaseViewModel;
import com.dfcj.videoim.databinding.WebviewActivityBinding;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

@Route(path = Rout.WebViewActivity)
public class WebViewActivity extends BaseActivity<WebviewActivityBinding, BaseViewModel> {

    private AgentWeb mAgentWeb;
    private String clickUrl;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.webview_activity;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            clickUrl = mBundle.getString("clickUrl");
        }
    }


    @Override
    public void initData() {
        super.initData();

        setWebInfo();
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();


        binding.webTitle.unifiedHeadBackLayout.setOnClickListener(v ->{

            closeActivity(this);

        });

    }

    private void setWebInfo(){


        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(binding.webviewLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .createAgentWeb()
                .ready()
                .go(""+clickUrl);

    }


    private WebViewClient mWebViewClient=new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
        }
    };
    private WebChromeClient mWebChromeClient=new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }
    };


    @Override
    protected void onPause() {
        if(mAgentWeb!=null){
            mAgentWeb.getWebLifeCycle().onPause();
        }
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        if(mAgentWeb!=null){
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        if(mAgentWeb!=null){
            mAgentWeb.getWebLifeCycle().onResume();
        }
        super.onResume();

    }


}
