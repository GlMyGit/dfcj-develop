package com.dfcj.videoim.api;



/**
 * des:retorfit api
 * on 2016.06.15:47
 */
public class Api {
//    //读超时长，单位：毫秒
//    public static final int READ_TIME_OUT = 30000;
//    //连接时长，单位：毫秒
//    public static final int CONNECT_TIME_OUT = 30000;
//
//    public Retrofit retrofit;
//    public ApiService movieService;
//
//    //管理各种类型Host的Api
//    //private static HashMap<String,Api> hashMap = new HashMap(HostType.TYPE_COUNT);
//    private static SparseArray<Api> sRetrofitManager = new SparseArray<>(HostType.TYPE_COUNT);
//
//    /*************************缓存设置*********************/
///*
//   1. noCache 不使用缓存，全部走网络
//
//    2. noStore 不使用缓存，也不存储缓存
//
//    3. onlyIfCached 只使用缓存
//
//    4. maxAge 设置最大失效时间，失效则不使用 需要服务器配合
//
//    5. maxStale 设置最大失效时间，失效则不使用 需要服务器配合 感觉这两个类似 还没怎么弄清楚，清楚的同学欢迎留言
//
//    6. minFresh 设置有效时间，依旧如上
//
//    7. FORCE_NETWORK 只走网络
//
//    8. FORCE_CACHE 只走缓存*/
//
//    /**
//     * 设缓存有效期为两天
//     */
//    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;//秒
//    /**
//     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
//     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
//     */
//    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
//    /**
//     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
//     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
//     */
//    private static final String CACHE_CONTROL_AGE = "max-age=0";
//
//
//    //构造方法私有
//    private Api(int hostType) {
//
//        //缓存
////        LogUtils.loge("===1==="+BaseApplication.getAppContext().getCacheDir());// /data/user/0/com.zlkj.loansteward/cache
////        LogUtils.loge("===2==="+BaseApplication.getAppContext().getExternalCacheDir());// /storage/emulated/0/Android/data/com.zlkj.loansteward/cache
////        File cacheFile = new File(BaseApplication.getAppContext().getCacheDir(), "cache");
//        File cacheFile = new File(BaseApplication.getAppContext().getExternalCacheDir(), "cache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
//        //增加头部信息
//        Interceptor headerInterceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request build = chain.request().newBuilder()
//                        .addHeader("Content-Type", "application/json")
//                        .addHeader("Authorization", ""+ SharedPrefsUtils.getValue(AppConstant.USERTOKEN))
//                        .addHeader("App-Id", ""+ AppConfig.PRODUCT_ID)
//                        .addHeader("channel-Id", ""+ AppConfig.CHANNEL_ID)
//                        .build();
//                return chain.proceed(build);
//            }
//        };
//
//        //设置可访问所有的https网站
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, BaseApplication.getAppContext(),
//                null, null);
//
//        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
//                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
//                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
//                .addInterceptor(mRewriteCacheControlInterceptor)//okhttp配置缓存策略
//                .addNetworkInterceptor(mRewriteCacheControlInterceptor)//okhttp配置缓存策略
//                .addInterceptor(headerInterceptor)//okhttp增加头部信息
//                //.addInterceptor(BuildConfig.DEBUG==true?null:null)//okhttp添加log信息
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                })//校验https连接
//                .sslSocketFactory(sslParams.sSLSocketFactory)//配置可访问所有的https网站
//                .cache(cache)//okhttp添加缓存信息
//                ;
//
//        //开启Log
//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//                @Override
//                public void log(String message) {
//
//                 //  LogUtils.logd("httpUrl: " + message);
//
//                    if (TextUtils.isEmpty(message)) return;
//                    String s = message.substring(0, 1);
//                    //如果收到响应是json才打印
//                    if ("{".equals(s) || "[".equals(s)) {
//                        LogUtils.logd("收到响应: " + message);
//                    }
//
//                   // String s2 = message.substring(0, 3);
//                    //如果收到响应是json才打印
//                    //if ("http".equals(s2)) {
//                       // LogUtils.logd("httpUrl: " + message);
//                   // }
//
//                }
//            });
//            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            okHttpClient.addInterceptor(logInterceptor);
//        }
//
//        OkHttpClient okHttpClient2= okHttpClient.build();
//        Gson gson= GsonUtil.newGson();
////        Gson gson = new GsonBuilder()
////
////                .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
////                .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
////                .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
////                .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
////                .registerTypeAdapter(Long.class, new LongDefault0Adapter())
////                .registerTypeAdapter(long.class, new LongDefault0Adapter())
////                .registerTypeAdapter(String.class, new StringConverter())
////                .registerTypeAdapterFactory(new GsonUtil.NullStringToEmptyAdapterFactory())
////                .setPrettyPrinting() //对json结果格式化.
////                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
////                .serializeNulls()
////                .create();
//
//        retrofit = new Retrofit.Builder()
//                .client(okHttpClient2)//给retrofit设置okhttpClient
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .baseUrl(ApiConstants.getHost(hostType))//给retrofit设置baseurl(baseurl是改变的,通过传入hostType动态设置)
//                .build();
//
//        movieService = retrofit.create(ApiService.class);//retrofit获取ApiService
//    }
//
//    public class StringConverter implements JsonSerializer<String>,
//            JsonDeserializer<String> {
//
//        @Override
//        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            return json.getAsJsonPrimitive().getAsString();
//        }
//
//        @Override
//        public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
//            if ( src == null ) {
//                return new JsonPrimitive("");
//            } else {
//                return new JsonPrimitive(src.toString());
//            }
//        }
//    }
//
//
//
//        public static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
//        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
//            Class<T> rawType = (Class<T>) type.getRawType();
//            if (rawType != String.class) {
//                return null;
//            }
//            return (TypeAdapter<T>) new StringNullAdapter();
//        }
//    }
//
//    public static class StringNullAdapter extends TypeAdapter<String> {
//        @Override
//        public String read(JsonReader reader) throws IOException {
//            if (reader.peek() == JsonToken.NULL) {
//                reader.nextNull();
//                return "";
//            }
//            return reader.nextString();
//        }
//
//        @Override
//        public void write(JsonWriter writer, String value) throws IOException {
//            if (value == null) {
//                writer.value("");
//                return;
//            }
//            writer.value(value);
//        }
//    }
//
//    /**
//     * @param hostType
//     *
//     */
//    public static ApiService getDefault(int hostType) {
//        Api retrofitManager = sRetrofitManager.get(hostType);
//        if (retrofitManager == null) {//=null
//            retrofitManager = new Api(hostType);
//            sRetrofitManager.put(hostType, retrofitManager);
//        }
//        return retrofitManager.movieService;
//    }
//
//
//    /**
//     * 根据网络状况获取缓存的策略
//     */
//    @NonNull
//    public static String getCacheControl() {
//        return NetWorkUtils.isNetConnected(BaseApplication.getAppContext()) ?
//                CACHE_CONTROL_AGE ://只请求服务器
//                CACHE_CONTROL_CACHE;//只查询缓存而不会请求服务器
//    }
//
//    /**
//     * 云端响应头拦截器，用来配置缓存策略
//     * Dangerous interceptor that rewrites the server's cache-control header.
//     */
//    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            if (!NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {//没有网络只走缓存
//                request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)//构造CacheControl
//                        .build();
//            }
//            Response originalResponse = chain.proceed(request);
//            if (NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
//                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
//                String cacheControl = request.cacheControl().toString();
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", cacheControl)
//                        .removeHeader("Pragma")
//                        .build();
//            } else {
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
//                        .removeHeader("Pragma")
//                        .build();
//            }
//        }
//    };
}