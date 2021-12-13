package com.dfcj.videoim.util.other;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.AppApplicationMVVM;
import com.dfcj.videoim.util.BitmapUtil;
import com.dfcj.videoim.util.CornerTransform;
import com.dfcj.videoim.util.ImageSize;

import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/5/23.
 */

public class GlideUtils {

    private static final String TAG = "ImageLoaderUtils";

    private static final String IMAGE_DOWNLOAD_DIR_SUFFIX = "/image/download/";
    private static String appDir = "";




    /**
     * 根据图片 UUID 和 类型得到图片文件路径
     * @param uuid 图片 UUID
     * @param imageType 图片类型 V2TIMImageElem.V2TIM_IMAGE_TYPE_THUMB , V2TIMImageElem.V2TIM_IMAGE_TYPE_ORIGIN ,
     *                  V2TIMImageElem.V2TIM_IMAGE_TYPE_LARGE
     * @return 图片文件路径
     */
    public static String generateImagePath(String uuid, int imageType) {
        return AppApplicationMVVM.getInstance()+IMAGE_DOWNLOAD_DIR_SUFFIX + uuid + "_" + imageType;
    }


    public static void loadCornerImageWithoutPlaceHolder(Context mContext,ImageView imageView, String filePath, RequestListener listener, float radius) {
        CornerTransform transform = new CornerTransform(mContext, radius);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .transform(transform);
        Glide.with(mContext)
                .load(filePath)
                .apply(options)
                .listener(listener)
                .into(imageView);
    }






    public static void loadChatImage(final Context mContext, String imgUrl,final ImageView imageView) {


        final RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_img_failed)// 正在加载中的图片
                .error(R.drawable.default_img_failed); // 加载失败的图片

        Glide.with(mContext)
                .load(imgUrl) // 图片地址
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ImageSize imageSize = BitmapUtil.getImageSize(((BitmapDrawable)resource).getBitmap() );
                        RelativeLayout.LayoutParams imageLP =(RelativeLayout.LayoutParams )(imageView.getLayoutParams());
                        imageLP.width = imageSize.getWidth();
                        imageLP.height = imageSize.getHeight();
                        imageView.setLayoutParams(imageLP);

                        Glide.with(mContext)
                                .load(resource)
                                .apply(options) // 参数
                                .into(imageView);
                    }
                });
    }

    public static void loadChatImage33(final Context mContext, Object imgUrl,final ImageView imageView) {


        final RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_img_failed)// 正在加载中的图片
                .error(R.drawable.default_img_failed); // 加载失败的图片

        Glide.with(mContext)
                .load(imgUrl) // 图片地址
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ImageSize imageSize = BitmapUtil.getImageSize(((BitmapDrawable)resource).getBitmap() );
                        RelativeLayout.LayoutParams imageLP =(RelativeLayout.LayoutParams )(imageView.getLayoutParams());
                        imageLP.width = imageSize.getWidth();
                        imageLP.height = imageSize.getHeight();
                        imageView.setLayoutParams(imageLP);

                        Glide.with(mContext)
                                .load(resource)
                                .apply(options) // 参数
                                .into(imageView);
                    }
                });
    }



    public static void loadChatImage2(final Context mContext, Url imgUrl, final ImageView imageView) {


        final RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_img_failed)// 正在加载中的图片
                .error(R.drawable.default_img_failed); // 加载失败的图片

        Glide.with(mContext)
                .load(imgUrl) // 图片地址
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ImageSize imageSize = BitmapUtil.getImageSize(((BitmapDrawable)resource).getBitmap() );
                        RelativeLayout.LayoutParams imageLP =(RelativeLayout.LayoutParams )(imageView.getLayoutParams());
                        imageLP.width = imageSize.getWidth();
                        imageLP.height = imageSize.getHeight();
                        imageView.setLayoutParams(imageLP);

                        Glide.with(mContext)
                                .load(resource)
                                .apply(options) // 参数
                                .into(imageView);
                    }
                });
    }

    public static void loadChatImage4(final Context mContext, String imgUrl,final ImageView imageView) {


        final RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_img_failed)// 正在加载中的图片
                .error(R.drawable.default_img_failed); // 加载失败的图片

        Glide.with(mContext)
                .load(imgUrl) // 图片地址
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        /*ImageSize imageSize = BitmapUtil.getImageSize(((BitmapDrawable)resource).getBitmap() );
                        RelativeLayout.LayoutParams imageLP =(RelativeLayout.LayoutParams )(imageView.getLayoutParams());
                        imageLP.width = imageSize.getWidth();
                        imageLP.height = imageSize.getHeight();
                        imageView.setLayoutParams(imageLP);*/

                        Glide.with(mContext)
                                .load(resource)
                                .apply(options) // 参数
                                .into(imageView);
                    }
                });
    }

    public static void load(Context context, ImageView imageView, Object url,int radius,int errorImg){
        //RequestOptions 设置请求参数，通过apply方法设置
        RequestOptions options = new RequestOptions()
                // 但不保证所有图片都按序加载
                // 枚举Priority.IMMEDIATE，Priority.HIGH，Priority.NORMAL，Priority.LOW
                // 默认为Priority.NORMAL
                // 如果没设置fallback，model为空时将显示error的Drawable，
                // 如果error的Drawable也没设置，就显示placeholder的Drawable
                .priority(Priority.NORMAL) //指定加载的优先级，优先级越高越优先加载，
                // 缓存原始数据
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(errorImg)
                .skipMemoryCache(true)
                .transform(new RoundedCorners(radius));
        // 图片加载库采用Glide框架
        Glide.with(context).asDrawable().load(url).apply(options)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(imageView);

    }

    //圆形图片
    public static void loadCircleCrop(Context context, ImageView imageView, String url ,int errorImg){
        //RequestOptions 设置请求参数，通过apply方法设置
        RequestOptions options = new RequestOptions()
                // 但不保证所有图片都按序加载
                // 枚举Priority.IMMEDIATE，Priority.HIGH，Priority.NORMAL，Priority.LOW
                // 默认为Priority.NORMAL
                // 如果没设置fallback，model为空时将显示error的Drawable，
                // 如果error的Drawable也没设置，就显示placeholder的Drawable
                .priority(Priority.NORMAL) //指定加载的优先级，优先级越高越优先加载，
                // 缓存原始数据
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(errorImg)
                .skipMemoryCache(false)
                .centerCrop()
                .transform(new CircleCrop());
        // 图片加载库采用Glide框架
        Glide.with(context).asDrawable().load(url).apply(options)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(imageView);

    }



    /**
     * 圆角图片加载
     * @author leibing
     * @createTime 2016/8/15
     * @lastModify 2016/8/15
     * @param context 上下文
     * @param imageView 图片显示控件
     * @param url 图片链接
     * @return
     */
    public static void loadImages(Context context, ImageView imageView, String url){
        //RequestOptions 设置请求参数，通过apply方法设置
        RequestOptions options = new RequestOptions()
                // 但不保证所有图片都按序加载
                // 枚举Priority.IMMEDIATE，Priority.HIGH，Priority.NORMAL，Priority.LOW
                // 默认为Priority.NORMAL
                // 如果没设置fallback，model为空时将显示error的Drawable，
                // 如果error的Drawable也没设置，就显示placeholder的Drawable
                .priority(Priority.NORMAL) //指定加载的优先级，优先级越高越优先加载，
                // 缓存原始数据
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop();
        // 图片加载库采用Glide框架
        Glide.with(context).load(url).apply(options)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(imageView);

    }



    /**
     * 圆角图片加载
     * @author leibing
     * @createTime 2016/8/15
     * @lastModify 2016/8/15
     * @param context 上下文
     * @param imageView 图片显示控件
     * @param url 图片链接
     * @param defaultImage 默认占位图片
     * @param errorImage 加载失败后图片
     * @param radius 图片圆角半径
     * @return
     */
    public static void load(Context context, ImageView imageView, String url, int defaultImage,
                            int errorImage , int radius){
        //RequestOptions 设置请求参数，通过apply方法设置
        RequestOptions options = new RequestOptions()
                // 但不保证所有图片都按序加载
                // 枚举Priority.IMMEDIATE，Priority.HIGH，Priority.NORMAL，Priority.LOW
                // 默认为Priority.NORMAL
                // 如果没设置fallback，model为空时将显示error的Drawable，
                // 如果error的Drawable也没设置，就显示placeholder的Drawable
                .priority(Priority.NORMAL) //指定加载的优先级，优先级越高越优先加载，
                .placeholder(defaultImage)
                .error(errorImage)
                // 缓存原始数据
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .transform(new CornersTranform(context, radius));
        // 图片加载库采用Glide框架
        Glide.with(context).load(url).apply(options)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(imageView);

    }

    /**
     * 加载resoures下的文件
     * @param context
     * @param imageView
     * @param url
     * @param defaultImage
     * @param errorImage
     */
    public static void loadImgId(Context context, final ImageView imageView, int url, int defaultImage,
                                 int errorImage, int radius) {
        RequestOptions options = new RequestOptions()
                // 但不保证所有图片都按序加载
                // 枚举Priority.IMMEDIATE，Priority.HIGH，Priority.NORMAL，Priority.LOW
                // 默认为Priority.NORMAL
                // 如果没设置fallback，model为空时将显示error的Drawable，
                // 如果error的Drawable也没设置，就显示placeholder的Drawable
                .priority(Priority.NORMAL) //指定加载的优先级，优先级越高越优先加载，
                .placeholder(defaultImage)
                .error(errorImage)
                // 缓存原始数据
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .transform(new CornersTranform(context, radius));
        // 图片加载库采用Glide框架
        Glide.with(context).load(url)
                .apply(options)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(imageView);
    }

    /**
     * 加载圆形头像
     * @param context
     * @param imageView
     * @param url
     * @param defaultImage
     * @param errorImage
     */
    public static void loadCircle(Context context, final ImageView imageView, String url, int defaultImage,
                                  int errorImage) {
        RequestOptions options = new RequestOptions()
                // 但不保证所有图片都按序加载
                // 枚举Priority.IMMEDIATE，Priority.HIGH，Priority.NORMAL，Priority.LOW
                // 默认为Priority.NORMAL
                // 如果没设置fallback，model为空时将显示error的Drawable，
                // 如果error的Drawable也没设置，就显示placeholder的Drawable
                .priority(Priority.NORMAL) //指定加载的优先级，优先级越高越优先加载，
                .dontAnimate() //防止设置placeholder导致第一次不显示网络图片,只显示默认图片的问题
                .placeholder(defaultImage)
                .error(errorImage)
                // 缓存原始数据
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .transform(new GlideCircleTransform());
        // 图片加载库采用Glide框架
        Glide.with(context).load(url)
                .apply(options)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                    }
                });
    }




//
//    页面销毁时， 在onstop方法中取消图片加载请求：
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Glide.with(this).pauseRequests();
//    }

//    在onresume方法中恢复图片加载请求：
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Glide.with(this).resumeRequests();
//    }
//
}
