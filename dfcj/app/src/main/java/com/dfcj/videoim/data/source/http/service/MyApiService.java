package com.dfcj.videoim.data.source.http.service;


import com.dfcj.videoim.entity.DemoBean;
import com.wzq.mvvmsmart.http.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface MyApiService {

    @GET("action/apiv2/banner")
    Observable<BaseResponse<DemoBean>> demoGet(@Query("catalog") int pageNum);

    @FormUrlEncoded
    @POST("action/apiv2/banner")
    Observable<BaseResponse<DemoBean>> demoPost(@Field("catalog") String catalog);

    @GET("getJsonFile")
    Observable<BaseResponse<Object>> getJsonFile();




}
