package com.dfcj.videoim.util;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.base.BaseActivity;
import com.dfcj.videoim.util.other.LogUtils;
import com.dfcj.videoim.util.other.ScreenUtil;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wzq.mvvmsmart.utils.KLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/3/28.
 */

public class AppUtils {

    public static SpannableString getEmotionContent(Bitmap emotion_map_type,final Context context, final TextView tv, String source) {
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();

        String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        while (matcherEmotion.find()) {
            // ??????????????????????????????
            String key = matcherEmotion.group();
            // ??????????????????????????????
            int start = matcherEmotion.start();
            // ??????????????????????????????????????????
           // Integer imgRes = EmotionUtils.getImgByName(emotion_map_type,key);
            if (emotion_map_type != null) {
                // ??????????????????
                int size = (int) tv.getTextSize()*13/10;
              //  Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(emotion_map_type, size, size, true);

                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }


    public static String checkString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int len = str.length();
        int i = 0, j = 0;
        char[] strChar = str.toCharArray();
        for (; i < len; i++) {
            if (' ' == strChar[i] || '\t' == strChar[i] || '\n' == strChar[i]) {
                continue;
            }
            if (i != j) {
                strChar[j] = strChar[i];
            }
            j++;
        }
        strChar[j] = 0;
        return new String(Arrays.copyOf(strChar, j));
    }


    /**
     * ??????EditText????????????????????????
     *
     * @param editText EditText?????????
     */
    public static void setEditTextInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ") || source.toString().contentEquals("\n")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static Bitmap returnBitMap(final String url){
        final Bitmap[] bitmap = new Bitmap[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;

                try {
                    imageurl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return bitmap[0];
    }




    /**
     *
     * ???????????????????????????????????????
     * <p>
     *
     * @param input
     * @return boolean
     */
    public static boolean hasSpecialChars(String input) {
        boolean flag = false;
        if ((input != null) && (input.length() > 0)) {
            char c;
            for (int i = 0; i <= input.length() - 1; i++) {
                c = input.charAt(i);
                switch (c) {
                    case '>':
                        flag = true;
                        break;
                    case '<':
                        flag = true;
                        break;
                    case '"':
                        flag = true;
                        break;
                    case '&':
                        flag = true;
                        break;
                }
            }
        }
        return flag;
    }





    /**
     * ???????????????
     * @param tv
     */
    public static void interceptHyperLink(TextView tv,Context conn) {
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = tv.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable spannable = (Spannable) tv.getText();
            URLSpan[] urlSpans = spannable.getSpans(0, end, URLSpan.class);
            if (urlSpans.length == 0) {
                return;
            }

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            // ????????????????????? ??????http://???????????????
            for (URLSpan uri : urlSpans) {
                String url = uri.getURL();
                if (url.indexOf("http://" )== 0 || url.indexOf("https://" )== 0) {

                    CustomUrlSpan customUrlSpan = new CustomUrlSpan(conn,url);
                    spannableStringBuilder.setSpan(customUrlSpan, spannable.getSpanStart(uri),
                            spannable.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            tv.setText(spannableStringBuilder);
        }
    }

    public static  String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }



    public static class CustomUrlSpan extends ClickableSpan {

        private Context context;
        private String url;
        public CustomUrlSpan(Context context,String url){
            this.context = context;
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            // ?????????????????????????????????????????????
            KLog.d("??????????????????");
            /*Intent intent = new Intent(context,WebViewActivity.class);
            intent.putExtra(WebViewActivity.WEB_URL,url);
            context.startActivity(intent);*/
        }
    }


    public static  void readPermission(BaseActivity asct,String parm){

        new RxPermissions(asct)
                .request(parm)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {

                        }else{

                        }
                    }
                });

    }

    public static  void readPermissionStatus(BaseActivity asct,String parm){

        boolean permission = isPermission(asct, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(!permission){
            new RxPermissions(asct)
                    .request(parm)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {

                            }
                        }
                    });
        }

    }



    public static boolean isPermission(Context conn,String param){

        boolean isf=false;

            //Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(conn,param ) != PackageManager.PERMISSION_GRANTED) {
            //??????????????????  ??????WRITE_EXTERNAL_STORAGE??????
            isf=false;

        }else{

            //???????????????
            isf=true;

        }


        return  isf;


    }



    public static Bitmap getBitmapFromPath(String path){
        if (!new File(path).exists()) {
            return null;
        }
        //????????????10M?????????
        byte[] buf = new byte[1024*1024*10];
        Bitmap bitmap = null;

        try {
            FileInputStream fis = new FileInputStream(path);
            int len = fis.read(buf, 0, buf.length);
            bitmap = BitmapFactory.decodeByteArray(buf, 0, len);
            if (bitmap == null) {
                return null;
            }
            fis.close();
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }


    /**
     * ??????????????????
     * http://bbs.3gstdy.com
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Uri ??? ????????????
     *
     * @param uri
     * @return
     */
    public static String getFilePathByUri_BELOWAPI11(Uri uri, Context context) {
        // ??? content:// ??????????????????  content://media/external/file/960
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            String path = null;
            String[] projection = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        return null;
    }




    //?????????????????????????????????   context???????????????Activity.??????tiis
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());


                LogUtils.logd("?????????:"+result);

                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());

                LogUtils.logd("?????????222:"+mode);

                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }




    /** ?????? ??????????????????????????????
         *   OP_SYSTEM_ALERT_WINDOW=24
                 * @param context
         * @return true ??????  false??????
         */
    public static boolean getAppOps(Context context) {
        try {
            @SuppressLint("WrongConstant") Object object = context.getSystemService("appops");
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();

            LogUtils.logd("?????????2220:"+arrayOfObject1[0]);
            LogUtils.logd("?????????2221:"+arrayOfObject1[1]);
            LogUtils.logd("?????????2223:"+arrayOfObject1[2]);

            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }









    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Context conn) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) conn.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(conn.getPackageName());
        }
        return isIgnoring;
    }


    /**
     * ????????????????????????
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    private int progressStart = 0; //??????????????? ????????? progressStart
    public void startProgressVal(ProgressBar pro_bar,double ps,Handler handler) {

        progressStart = pro_bar.getProgress();

        final int interval_time = 10;//???????????? 100??????

        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //???????????????
                        if (progressStart < ps) { //????????????100

                            progressStart+=5;
                            pro_bar.setProgress(progressStart);
                            handler.postDelayed(this, interval_time);

                        } else {

                            handler.removeCallbacks(this); //???????????????,???????????????
                        }
                    }
                }
                , interval_time); //????????????


    }




    public static  void setProgressAnimation(int precent, final ProgressBar pb_plan,int times){

        boolean IsVisibleToUser=true;                     //??????????????????????????????
        HashMap<Object, Object> AnimMap = new HashMap<>();	//????????????????????? ValueAnimator

        if (IsVisibleToUser) {
            ValueAnimator anim = ValueAnimator.ofInt(0, precent);
            //text.setText(precent+"%");
            anim.setDuration(times / 100 * precent);				//????????????????????????
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    pb_plan.setProgress(animatedValue);
                }
            });
            anim.start();
            AnimMap.put(pb_plan, anim);
        }

    }

    public static  void setSuoFangAnimation(Context conn,View mys){

        try {
            Animation scaleAnimation = AnimationUtils.loadAnimation(conn, R.anim.suofang);
            mys.startAnimation(scaleAnimation);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public static  void setGifImg(Context conn, GifImageView fgimg,int drawableimg,int defaultimgs){
//
//        try {
//
//            if(fgimg!=null){
//                GifDrawable gifFromResource = new GifDrawable(conn.getResources(), drawableimg);
//
///*gifDrawable.start(); //????????????
//        gifDrawable.stop(); //????????????
//        gifDrawable.reset(); //???????????????????????????
//        gifDrawable.isRunning(); //??????????????????
//        gifDrawable.setLoopCount( 2 ); //???????????????????????????????????????????????????
//        gifDrawable.getCurrentLoop(); //???????????????????????????
//        gifDrawable.getCurrentPosition() ; //????????????????????????????????????????????????
//        gifDrawable.getDuration() ; //????????????????????????????????????
//        gifDrawable.recycle();//????????????*/
//
//                fgimg.setImageDrawable(gifFromResource);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            if(fgimg!=null && defaultimgs!=0) {
//                fgimg.setImageResource(defaultimgs);
//            }
//        }
//
//
//    }
//
//
//    public static  void setGifImg(Context conn, GifImageView fgimg,int drawableimg,int defaultimgs,int looper){
//
//        try {
//
//            if(fgimg!=null){
//                GifDrawable gifFromResource = new GifDrawable(conn.getResources(), drawableimg);
//
//                gifFromResource.setLoopCount(looper);//???????????????????????? ?????????????????? app:loopCount="1000"
//
//                fgimg.setImageDrawable(gifFromResource);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            if(fgimg!=null && defaultimgs!=0) {
//                fgimg.setImageResource(defaultimgs);
//            }
//        }
//    }
//
//    public static  GifDrawable setGifImg55(Context conn, GifImageView fgimg,int drawableimg,int defaultimgs,int looper){
//        GifDrawable gifFromResource=null;
//        try {
//
//            if(fgimg!=null){
//                 gifFromResource = new GifDrawable(conn.getResources(), drawableimg);
//
//                gifFromResource.setLoopCount(looper);//???????????????????????? ?????????????????? app:loopCount="1000"
//
//                fgimg.setImageDrawable(gifFromResource);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            if(fgimg!=null && defaultimgs!=0) {
//                fgimg.setImageResource(defaultimgs);
//            }
//        }
//
//        return gifFromResource;
//
//    }


    public static String timestampToTimeForService(int timestamp) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp* 1000));
        return time;
    }


    /**
     * ????????????????????????
     *
     * @param content
     * @param context
     */
    public static void copyContentToClipboard(String content, Context context) {
        //???????????????????????????
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // ?????????????????????ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // ???ClipData?????????????????????????????????
        cm.setPrimaryClip(mClipData);
    }


    /**
     * ???????????????????????????????????? ROUND_HALF_UP
     */
    public  static  double format1(double format) {
        BigDecimal bg = new BigDecimal(format);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }


    /**
     * ??????????????????2???   ROUND_DOWN??????????????????????????????????????????(???????????????????????????????????????1????????????)???
     *
     * @return
     */
    public static String keepTwo(double b) {
        //  DecimalFormat  format = new DecimalFormat ("0.00");
        // String str = format.format(b);

        BigDecimal bigDecimal = new BigDecimal(b);
        double bg = bigDecimal.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();


        return ""+bg;
    }

    public static double keepTwo2(double b) {
        //  DecimalFormat  format = new DecimalFormat ("0.00");
        // String str = format.format(b);

        BigDecimal bigDecimal = new BigDecimal(b);
        double bg = bigDecimal.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

        return bg;
    }

    public static double keepTwo3(double b) {
        //  DecimalFormat  format = new DecimalFormat ("0.00");
        // String str = format.format(b);

        BigDecimal bigDecimal = new BigDecimal(b);
        double bg = bigDecimal.setScale(4, BigDecimal.ROUND_DOWN).doubleValue();

        return bg;
    }


    public static Drawable getWallpaperDrawable(Context conn) {
        Drawable wallpaperDrawable;
        PackageManager pm = conn.getApplicationContext().getPackageManager();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(conn);
        if (wallpaperManager.getWallpaperInfo() != null) {
            /*
             * Wallpaper info is not equal to null, that is if the live wallpaper
             * is set, then get the drawable image from the package for the
             * live wallpaper
             */
            wallpaperDrawable = wallpaperManager.getWallpaperInfo().loadThumbnail(pm);
        } else {
            /*
             * Else, if static wallpapers are set, then directly get the
             * wallpaper image
             */
            wallpaperDrawable = wallpaperManager.getDrawable();
        }
        return wallpaperDrawable;
    }

    //????????????
    private static final int MIN_DELAY_TIME = 1000;  // ??????????????????????????????1000ms
    private static long lastClickTime;
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }


    /**
     * ??????????????????????????? dp ????????? ????????? px(??????)
     */
    public static int dip2pxAd(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dipAd(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static  boolean isShenHe(){
        //1 ?????????  0 ?????????
        if(TextUtils.equals(""+ SharedPrefsUtils.getValue(AppConstant.checkStatus),"1")){
            return true;
        }else{
            return false;
        }
    }

    public static  boolean isLogin(){

        //????????????
        if(!TextUtils.isEmpty(SharedPrefsUtils.getValue(AppConstant.MYUSERID))){
            return true;
        }else{
            return false;
        }
    }

    public static  String  getDayForm(){

        Calendar calendar=Calendar.getInstance();
        //???
        int year = calendar.get(Calendar.YEAR);
        //???
        int month = calendar.get(Calendar.MONTH)+1;
        //???
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return  ""+year+month+day;

    }



    public static  String  getDayForm(Calendar calendar){

        //???
        int year = calendar.get(Calendar.YEAR);
        //???
        int month = calendar.get(Calendar.MONTH)+1;
        //???
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return  ""+year+month+day;

    }


    public static  String getToDayTime(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH,-1);//???????????????  30????????????  365?????????


        //???
        int year = calendar.get(Calendar.YEAR);
        //???
        int month = calendar.get(Calendar.MONTH)+1;
        //???
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        return  ""+year+month+day;
    }

    /**
     * ??????2????????????????????????  ?????????????????????
     * ?????????2011-02-02 ???  2017-03-02 ?????? 6??????1?????????0???
     * @param fromDate
     * @param toDate
     * @return
     */
    /**
     * ???????????????????????????????????????
     * @param date1
     * @param date2
     * @return
     */
    public static int daysBetween(Date date1,Date date2){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * ???????????????????????????????????????
     * @param smdate ???????????????
     * @param bdate  ???????????????
     * @return ????????????
     * @throws ParseException
     */
    public static int daysBetween22(Date smdate,Date bdate)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        long between_days=0;
        try {

            smdate=sdf.parse(sdf.format(smdate));
            bdate=sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
             between_days=(time2-time1)/(1000*3600*24);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        return Integer.parseInt(String.valueOf(between_days));
    }


    public static String getTypes(int bubbleType0){

        String mybubbleName="";
        //0??????????????? 1?????? 2????????? 3????????? 4????????? 5????????? 6?????????(??????) 7?????????(?????????)
        switch (bubbleType0){
            case 0:
                mybubbleName="?????????";
                break;
            case 1:
                mybubbleName="????????????";
                break;
            case 2:
                mybubbleName="?????????";
                break;
            case 3:
                mybubbleName="?????????";
                break;
            case 4:
                mybubbleName="?????????";
                break;
            case 5:
                mybubbleName="????????????";
                break;
            case 6:
                mybubbleName="????????????";
                break;
            case 7:
                mybubbleName="????????????";
                break;
        }

        return  mybubbleName;

    }


    public static void startShakeByPropertyAnim(View view, float scaleSmall, float scaleLarge, float shakeDegrees, long duration) {
        if (view == null) {
            return;
        }
        //TODO ????????????????????????

        //??????????????????
        PropertyValuesHolder scaleXValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1.0f),
                Keyframe.ofFloat(0.25f, scaleSmall),
                Keyframe.ofFloat(0.5f, scaleLarge),
                Keyframe.ofFloat(0.75f, scaleLarge),
                Keyframe.ofFloat(1.0f, 1.0f)
        );
        PropertyValuesHolder scaleYValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1.0f),
                Keyframe.ofFloat(0.25f, scaleSmall),
                Keyframe.ofFloat(0.5f, scaleLarge),
                Keyframe.ofFloat(0.75f, scaleLarge),
                Keyframe.ofFloat(1.0f, 1.0f)
        );

        //??????????????????
        PropertyValuesHolder rotateValuesHolder = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(0.1f, -shakeDegrees),
                Keyframe.ofFloat(0.2f, shakeDegrees),
                Keyframe.ofFloat(0.3f, -shakeDegrees),
                Keyframe.ofFloat(0.4f, shakeDegrees),
                Keyframe.ofFloat(0.5f, -shakeDegrees),
                Keyframe.ofFloat(0.6f, shakeDegrees),
                Keyframe.ofFloat(0.7f, -shakeDegrees),
                Keyframe.ofFloat(0.8f, shakeDegrees),
                Keyframe.ofFloat(0.9f, -shakeDegrees),
                Keyframe.ofFloat(1.0f, 0f)
        );

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scaleXValuesHolder, scaleYValuesHolder, rotateValuesHolder);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }


    public static void startShakeByViewAnim(View view, float scaleSmall, float scaleLarge, float shakeDegrees, long duration) {
        if (view == null) {
            return;
        }
        //TODO ????????????????????????

        //????????????
        //Animation scaleAnim = new ScaleAnimation(scaleSmall, scaleLarge, scaleSmall, scaleLarge);
        //????????????
        Animation rotateAnim = new RotateAnimation(-shakeDegrees, shakeDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        // scaleAnim.setDuration(duration);
        rotateAnim.setDuration(duration / 10);
        rotateAnim.setRepeatMode(Animation.REVERSE);
        rotateAnim.setRepeatCount(1);

        AnimationSet smallAnimationSet = new AnimationSet(false);
        // smallAnimationSet.addAnimation(scaleAnim);
        smallAnimationSet.addAnimation(rotateAnim);

        view.startAnimation(smallAnimationSet);
    }



    //CycleTimes?????????????????????
    public static Animation shakeAnimation(int CycleTimes) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 10);
        translateAnimation.setInterpolator(new CycleInterpolator(CycleTimes));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }


    public static   void initLocation(Context mcnn,Activity myact,TextView tvLocation){
        final String[] myads = {""};
        new Thread(new Runnable() {
            @Override
            public void run() {
                String locationService = Context.LOCATION_SERVICE;// ????????????????????????
                LocationManager locationManager = (LocationManager) mcnn.getSystemService(locationService);
                String networkProvider = LocationManager.NETWORK_PROVIDER;
                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(networkProvider);
                String address22 = getAddress(location, mcnn);
                //myads[0] =address22;
                myact.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvLocation.setText(""+address22);
                    }
                });
            }
        }).start();
    }

    private static String   getAddress(Location location,Context mc){

        List<Address> result = new ArrayList<>();
        String addressLine = "";
        try {
            if (location != null) {
                Geocoder gc=new Geocoder(mc, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (!result.isEmpty()) {
                    try {
                        addressLine = result.get(0).getAddressLine(0) + result.get(0).getAddressLine(1);
                    } catch (Exception e) {
                        addressLine = result.get(0).getAddressLine(0);
                    }
                }
                LogUtils.logd("?????????"+addressLine);
                LogUtils.logd("??????2???"+result.get(0).getAddressLine(0));
                // LogUtils.logd("??????3???"+result.get(0).getAddressLine(1));
                // LogUtils.logd("??????4???"+result.get(0).getCountryName());
                // LogUtils.logd("??????5???"+result.get(0).getFeatureName());
                // LogUtils.logd("??????6???"+result.get(0).getSubAdminArea());
                LogUtils.logd("??????7???"+result.get(0).getLocality());//???
                LogUtils.logd("??????8???"+result.get(0).getSubLocality());//???

            }
            addressLine=addressLine.replace("null","");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressLine;
    }




    public static boolean isNumeric2(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    public static String getIMEIDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }*/
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = mTelephony.getImei();
                }else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        LogUtils.logd("deviceId", deviceId);
        return deviceId;
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {

        // String imeiDeviceId = getIMEIDeviceId(context);

        String deviceId="";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            deviceId = "";

        } else {

            TelephonyManager mTelephony2 = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // assert mTelephony2 != null;
            if (mTelephony2!=null && mTelephony2.getDeviceId() != null)
            {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = mTelephony2.getImei();
                }else {
                    deviceId = mTelephony2.getDeviceId();
                }

            } else {
                deviceId ="";
            }

        }

        LogUtils.logd("deviceId", ""+deviceId);
        return deviceId;

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return null;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return null;
            }
           @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
            return imei;
        } catch (Exception e) {
            return null;
        }*/
        //  return imeiDeviceId;
    }


    public static Map<String, RequestBody> mapToRequestBody(Map<String,Object> mmap){

        Map<String, RequestBody> reMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : mmap.entrySet()) {
            String key = entry.getKey();
            String value = ""+entry.getValue();
            RequestBody s1 = RequestBody.create(MediaType.parse("text/plain"), value + "");
            reMap.put(""+key,s1);
        }

        return reMap;

    }

    public static Map<String, RequestBody> generateRequestBody(TreeMap<String, String> requestDataMap) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (String key : requestDataMap.keySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                    requestDataMap.get(key) == null ? "" : requestDataMap.get(key));
            requestBodyMap.put(key, requestBody);
        }
        return requestBodyMap;
    }


    /**
     * ??????????????????
     *
     * @return  ????????????
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }



    /**
     * ???????????????????????????
     *
     * @return ["????????????CTCC":3]["????????????CUCC:2]["????????????CMCC":1]["other":0]["???sim???":-1]
     */
    public static String getSubscriptionOperatorType(Context mconn) {
        int opeType = -1;
        String types="";
        // No sim
        if (!hasSim(mconn)) {
            return types;
        }

        TelephonyManager tm = (TelephonyManager) mconn.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getNetworkOperator();
        // ????????????
        if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
            opeType = 2;
            types="????????????";
            // ????????????
        } else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
            opeType = 1;
            types="????????????";
            // ????????????
        } else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
            opeType = 3;
            types="????????????";
        } else {
            opeType = 0;
            types="";
        }
        return types;
    }


    /*  *//**
     * ?????????????????????????????????
     *
     * @return ["????????????CTCC":3]["????????????CUCC:2]["????????????CMCC":1]["other":0]["???sim???":-1]["?????????????????????":-2]
     *//*
    public static int getCellularOperatorType() {
        int opeType = -1;
        // No sim
        if (!hasSim()) {
            return opeType;
        }
        // Mobile data disabled
        if (!isMobileDataEnabled(MobSDK.getContext())) {
            opeType = -2;
            return opeType;
        }
        // Check cellular operator
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        // ????????????
        if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
            opeType = 2;
            // ????????????
        } else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
            opeType = 1;
            // ????????????
        } else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
            opeType = 3;
        } else {
            opeType = 0;
        }
        return opeType;
    }*/

    /**
     * ????????????????????????????????????
     *
     * @param context
     * @return
     */
    public static boolean isMobileDataEnabled(Context context) {
        try {
            Method method = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return (Boolean) method.invoke(connectivityManager);
        } catch (Throwable t) {
            Log.d("isMobileDataEnabled", "Check mobile data encountered exception");
            return false;
        }
    }



    /**
     * ?????????????????????sim???
     */
    public static boolean hasSim(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        if (TextUtils.isEmpty(operator)) {
            return false;
        }
        return true;
    }



    //??????????????????
    public static final String NETWORN_NONE = "?????????";
    //wifi??????
    public static final String NETWORN_WIFI = "wifi";
    //??????????????????????????????
    public static final String NETWORN_2G = "2G";
    public static final String NETWORN_3G = "3G";
    public static final String NETWORN_4G = "4G";
    public static final String NETWORN_5G = "5G";
    public static final String NETWORN_MOBILE = "NETWORN_MOBILE";



    /**
     * ??????????????????????????????
     *
     * @param context
     * @return
     */
    public static String getNetworkState(Context context) {
        //???????????????????????????
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //????????????????????????
        if (null == connManager) {
            return NETWORN_NONE;
        }
        //?????????????????????????????????????????????????????????
        @SuppressLint("MissingPermission") NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORN_NONE;
        }
        // ?????????????????????????????????wifi
        @SuppressLint("MissingPermission") NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORN_WIFI;
                }
            }
        }
        // ????????????wifi??????????????????????????????????????????????????????2g???3g???4g???
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //?????????2g??????
                        case TelephonyManager.NETWORK_TYPE_GPRS: // ??????2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // ??????2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // ??????2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORN_2G;
                        //?????????3g??????
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // ??????3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORN_3G;
                        //?????????4g??????
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORN_4G;
                        case TelephonyManager.NETWORK_TYPE_NR: //?????????20 ???????????????android 10.0???????????????
                            return NETWORN_5G;
                        default:
                            //???????????? ?????? ?????? ??????3G??????
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORN_3G;
                            } else {
                                return NETWORN_MOBILE;
                            }
                    }
                }
            }
        }
        return NETWORN_NONE;
    }

    /**
     * get huawei network type
     * @return networkType
     */
    public static int getHwNetworkType(Context ctx) {
        int networkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && ctx.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                ServiceState ss;
                int subId = SubscriptionManager.getDefaultDataSubscriptionId();
                if (subId < 0) {
                    ss = tm.getServiceState();
                } else {
                    Class<TelephonyManager> classTm = TelephonyManager.class;
                    Method method = classTm
                            .getDeclaredMethod("getServiceStateForSubscriber", new Class[]{int.class});
                    method.setAccessible(true);
                    Object objSs = method.invoke(tm, subId);
                    ss = (ServiceState) objSs;
                }

                Method hwGetNetworkMethod = ServiceState.class.getMethod("getHwNetworkType");
                hwGetNetworkMethod.setAccessible(true);
                Integer hwNetType = (Integer) hwGetNetworkMethod.invoke(ss);
                if (hwNetType != null) {
                    networkType = hwNetType;
                }
            } catch (Exception e) {

            }
        }
        return networkType;
    }


    public static int sp2px(Context context,int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }


    /**
     * ??????????????????????????? px(??????) ????????? ????????? dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(float pxValue, Context context) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * ??????????????????????????? dp ????????? ????????? px(??????)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //????????????????????????
    public boolean areNotificationsEnabled(Context mContext) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        if (Build.VERSION.SDK_INT >= 24) {
            return NotificationManagerCompat.from(mContext).areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOps =
                    (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = mContext.getApplicationInfo();
            String pkg = mContext.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE,
                        Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
                        == AppOpsManager.MODE_ALLOWED);
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
                    | InvocationTargetException | IllegalAccessException | RuntimeException e) {
                return true;
            }
        } else {
            return true;
        }
    }


    //RxPermissions?????????
    private void getReadPhoneStatus(FragmentActivity act){

//        new RxPermissions(act).requestEach(Manifest.permission.READ_PHONE_STATE).subscribe(new Consumer<Permission>() {
//            @Override
//            public void accept(Permission permission) throws Exception {
//                if (permission.granted) {
//                    //??????
//
//                }
//                else if (permission.shouldShowRequestPermissionRationale) {
//                    //????????????????????????????????????
//
//                }
//                else {
//                    // "????????????????????????????????????????????????APP??????????????????????????????"
//                    //todo nothing
//                }
//            }
//        });
//
//        new RxPermissions(act).request(Manifest.permission.READ_PHONE_STATE).subscribe(new Consumer<Boolean>() {
//            @Override
//            public void accept(Boolean aBoolean) throws Exception {
//
//            }
//        });


    }




    public static Spannable setTextOnSize(Context mContext, String balance_money){

        Spannable builder = new SpannableString(""+balance_money) ;

        builder.setSpan(new AbsoluteSizeSpan(ScreenUtil.sp2px(mContext,40)),0,balance_money.indexOf("."),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        builder.setSpan(new AbsoluteSizeSpan(ScreenUtil.sp2px(mContext,20)),balance_money.indexOf("."), balance_money.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return builder;

    }


    public  static void closeKeyboard(final Activity mcontext) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                View view =mcontext.getWindow().peekDecorView();
                if (view != null) {
                    //??????????????????
                    InputMethodManager inputmanger = (InputMethodManager)mcontext.getSystemService(mcontext.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });

    }

    //????????? map ???json??????
    public static String simpleMapToJsonStr(Map<String ,String > map){
        if(map==null||map.isEmpty()){
            return "";
        }

        String jsonStr = "{";
        Set<?> keySet = map.keySet();
        for (Object key : keySet) {
            jsonStr += "\""+key+"\":\""+map.get(key)+"\",";
        }
        jsonStr = jsonStr.substring(0,jsonStr.length()-1);
        jsonStr += "}";
        return jsonStr;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {


        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean areIsNotificationsEnabled(Context mContext) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        if (Build.VERSION.SDK_INT >= 24) {
            return NotificationManagerCompat.from(mContext).areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOps =
                    (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = mContext.getApplicationInfo();
            String pkg = mContext.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE,
                        Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
                        == AppOpsManager.MODE_ALLOWED);
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
                    | InvocationTargetException | IllegalAccessException | RuntimeException e) {
                return true;
            }
        } else {
            return true;
        }
    }



    public static void gotoSet(Context mcont) {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0??????
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", mcont.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", mcont.getPackageName());
            intent.putExtra("app_uid", mcont.getApplicationInfo().uid);
        } else {
            // ??????
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", mcont.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mcont.startActivity(intent);

    }


    public static void setMyViewIsInVisibity(View myvs){
        if(myvs!=null){
            myvs.setVisibility(View.INVISIBLE);
        }
    }

    public static void setMyViewIsVisibity(View myvs){
        if(myvs!=null){
            myvs.setVisibility(View.VISIBLE);
        }
    }

    public static void setMyViewIsGone(View myvs){
        if(myvs!=null){
            myvs.setVisibility(View.GONE);
        }
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    //????????????Dialog
    public static Dialog newDialong(View relayoutview, Context context, int syle){
        Dialog dialog=new Dialog(context,syle);
        dialog.setCanceledOnTouchOutside(true);//dialog???????????????????????????dialog?????????????????????????????????dialog??????
        dialog.setCancelable(true);//dialog?????????????????????????????????????????????dialog?????????
        dialog.setContentView(relayoutview);

        return  dialog;
    }

    //????????????Dialog
    public static Dialog newDialongByview(Context context, int relayoutview,int syle){
        Dialog dialog=new Dialog(context,syle);
        dialog.setCanceledOnTouchOutside(true);//dialog???????????????????????????dialog?????????????????????????????????dialog??????
        dialog.setCancelable(true);//dialog?????????????????????????????????????????????dialog?????????
        dialog.setContentView(relayoutview);

        return  dialog;
    }

    //???????????? 2.0.0
    public static String getVersionNameInfo(Context context) {
        return getPackageInfo(context).versionName;
    }

    //???????????? 200
    public static int getVersionCodeInfo(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }



    public static void autoIncrement(final TextView target, final float start,
                                     final float end, long duration) {

        ValueAnimator animator = ValueAnimator.ofFloat(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private FloatEvaluator evalutor = new FloatEvaluator();
            private DecimalFormat format = new DecimalFormat("####");

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float fraction = animation.getAnimatedFraction();
                float currentValue = evalutor.evaluate(fraction, start, end);
                target.setText(format.format(currentValue));
            }
        });
        animator.setDuration(duration);
        animator.start();

    }



    /**
     * ????????????????????????
     *
     * @param paramContext
     * @return
     */
    public static boolean checkEnable(Context paramContext) {
        boolean i = false;
        @SuppressLint("WrongConstant") NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService("connectivity")).getActiveNetworkInfo();
        if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
            return true;
        return false;
    }

    /**
     * ???ip????????????????????????ip??????
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * ????????????ip??????
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {

        String networkState = getNetworkState(context);
        if(networkState.equals(NETWORN_NONE)){
            return "";
        }else if(networkState.equals(NETWORN_WIFI)){
            try {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int i = wifiInfo.getIpAddress();
                return int2ip(i);
            } catch (Exception ex) {
                //  return " ??????IP?????????!!!!????????????WIFI,???????????????????????????!\n" + ex.getMessage();
                return "";
            }

        }else if(networkState.equals(NETWORN_2G)||networkState.equals(NETWORN_3G)||
                networkState.equals(NETWORN_4G)||networkState.equals(NETWORN_5G)||networkState.equals(NETWORN_MOBILE)){
            String localIpAddress = getLocalIpAddress();
            return localIpAddress;
        }

        return "";

    }


    //GPRS????????????ip
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            // Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }


}
