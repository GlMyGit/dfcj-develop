package com.dfcj.videoim.http;

import android.content.Context;
import android.text.TextUtils;

import com.dfcj.videoim.BuildConfig;
import com.dfcj.videoim.api.ApiConstants;
import com.dfcj.videoim.util.other.GsonUtil;
import com.dfcj.videoim.util.other.LogUtils;
import com.google.gson.Gson;
import com.wzq.mvvmsmart.http.cookie.CookieJarImpl;
import com.wzq.mvvmsmart.http.cookie.store.PersistentCookieStore;
import com.wzq.mvvmsmart.http.interceptor.BaseInterceptor;
import com.wzq.mvvmsmart.http.interceptor.CacheInterceptor;
import com.wzq.mvvmsmart.http.interceptor.logging.Level;
import com.wzq.mvvmsmart.http.interceptor.logging.LoggingInterceptor;
import com.wzq.mvvmsmart.utils.KLog;
import com.wzq.mvvmsmart.utils.Utils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient封装单例类, 实现网络请求
 */
public class RetrofitClient {
    //超时时间
    private static final int DEFAULT_TIMEOUT = 20;
    //缓存时间
    private static final int CACHE_TIMEOUT = 100 * 1024 * 1024;
    /**
     * 设缓存有效期为两天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;//秒
    /**
     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
     */
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    /**
     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
     */
    private static final String CACHE_CONTROL_AGE = "max-age=0";


    private static Context mContext = Utils.getContext();

    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;

    private Cache cache = null;
    private File httpCacheDirectory;

//    private static class SingletonHolder {
//        private static RetrofitClient INSTANCE = new RetrofitClient();
//    }

    public static RetrofitClient getInstance(int hostType,Map<String, String> headers) {
        return  new RetrofitClient(hostType,headers);
    }

    public static RetrofitClient getInstance(int hostType) {
        return  new RetrofitClient(hostType,null);
    }

    //  私有构造
    private RetrofitClient(int hostType, Map<String, String> headers) {


        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(mContext.getCacheDir(), "my_dfcj_cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, CACHE_TIMEOUT);   // okhttp的cache类;
            }
        } catch (Exception e) {
            KLog.e("Could not create http cache", e);
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();  // https证书认证,封装了认证方法,可根据自己公司进行调整;
        OkHttpClient.Builder  okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(mContext)))
                //.cache(cache)
                .addInterceptor(new BaseInterceptor(headers))   // 添加header的拦截器
                .addInterceptor(new CacheInterceptor(mContext)) //无网络状态智能读取缓存
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)   // https的证书校验
//                .addInterceptor(new LoggingInterceptor  //  日志拦截器
//                        .Builder()//构建者模式
//                        .loggable(BuildConfig.DEBUG) //是否开启日志打印
//                        .setLevel(Level.BASIC) //打印的等级
//                        .log(Platform.INFO) // 打印类型
//                       // .request("Request") // request的Tag
//                       // .response("Response")// Response的Tag
//                        //.addHeader("log-header", "I am the log request header.") // 添加打印头, 注意 key 和 value 都不能是中文
//                        .build()
//                )
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为15s
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                ;
        //开启Log
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    if (TextUtils.isEmpty(message)) {return;}
                    String s = message.substring(0, 1);
                    //如果收到响应是json才打印
                    if ("{".equals(s) || "[".equals(s)) {
                        LogUtils.logd("请求响应日志: " + message);
                    }

                }
            });
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient.addInterceptor(logInterceptor);
        }

        OkHttpClient okHttpClient2= okHttpClient.build();

        Gson gson= GsonUtil.newGson22();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient2)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  //  支持返回一个Observable泛型的接收对象:
                .baseUrl(ApiConstants.getHost(hostType))//给retrofit设置baseurl(baseurl是改变的,通过传入hostType动态设置)
                .build();

    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    /**
     * execute your customer API
     * For example:
     * MyApiService service =
     * RetrofitClient.getInstance(MainActivity.this).create(MyApiService.class);
     * <p>
     * RetrofitClient.getInstance(MainActivity.this)
     * .execute(service.lgon("name", "password"), subscriber)
     *  @param subscriber
     */

    public static <T> T execute(Observable<T> observable, Observer<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return null;
    }
}
