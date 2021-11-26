package com.dfcj.videoim.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.AppApplicationMVVM;
import com.dfcj.videoim.appconfig.Rout;
import com.wzq.mvvmsmart.utils.KLog;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoLinKTextViewUtil {

    private volatile static AutoLinKTextViewUtil autoLinKTextViewUtil;


    private AutoLinKTextViewUtil() {
    }

    public static AutoLinKTextViewUtil getInstance() {
        if (autoLinKTextViewUtil == null) {
            synchronized (AutoLinKTextViewUtil.class) {
                if (autoLinKTextViewUtil == null) {
                    autoLinKTextViewUtil = new AutoLinKTextViewUtil();
                }
            }
        }
        return autoLinKTextViewUtil;
    }

    public void interceptHyperLink(TextView textView) {
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable spannable = (Spannable) textView.getText();
            URLSpan[] urlSpans = spannable.getSpans(0, end, URLSpan.class);
            if (urlSpans.length == 0) {
                return;
            }

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            // 循环遍历并拦截 所有http://开头的链接
            for (URLSpan uri : urlSpans) {
                String url = uri.getURL();
                if (url.indexOf("http://") == 0 || url.indexOf("https://") == 0) {
                    CustomUrlSpan customUrlSpan = new CustomUrlSpan(textView.getContext(), url,
                            new CustomUrlSpan.OnClickInterface() {
                                @Override
                                public void onClick(View widget, String url, Context context) {//处理链接地址的点击事情
                                    if (!TextUtils.isEmpty(url)) {

                                        KLog.d("点击的链接是："+url);

                                       /* Toast.makeText(widget.getContext(), url, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(widget.getContext(), WebViewActivity.class);
                                        intent.putExtra("url", url);
                                        widget.getContext().startActivity(intent);
                                        */

                                    }
                                }
                            });
                    spannableStringBuilder.setSpan(customUrlSpan, spannable.getSpanStart(uri),
                            spannable.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            textView.setText(spannableStringBuilder);
        }
    }







    LinkedList<String> mStringList = new LinkedList<String>();
    LinkedList<UrlInfo> mUrlInfos = new LinkedList<UrlInfo>();
    private String pattern =
            "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?|(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";

    Pattern r = Pattern.compile(pattern);
    Matcher m;
    int flag= Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

    class UrlInfo {
        public int start;
        public int end;
    }


    public SpannableStringBuilderForAllvers identifyUrl(CharSequence text) {
        mStringList.clear();
        mUrlInfos.clear();

        CharSequence contextText;
        CharSequence clickText;
        text = text == null ? "" : text;
        //以下用于拼接本来存在的spanText
        SpannableStringBuilderForAllvers span = new SpannableStringBuilderForAllvers(text);
        ClickableSpan[] clickableSpans = span.getSpans(0, text.length(), ClickableSpan.class);
        if (clickableSpans.length > 0) {
            int start=0;
            int end=0;
            for (int i=0;i<clickableSpans.length;i++){
                start=span.getSpanStart(clickableSpans[0]);
                end=span.getSpanEnd(clickableSpans[i]);
            }
            //可点击文本后面的内容页
            contextText = text.subSequence(end, text.length());
            //可点击文本
            clickText = text.subSequence(start,
                    end);



        } else {
            contextText = text;
            clickText = null;
        }
        m = r.matcher(contextText);
        //匹配成功
        while (m.find()) {
            //得到网址数
            UrlInfo info = new UrlInfo();
            info.start = m.start();
            info.end = m.end();
            mStringList.add(m.group());
            mUrlInfos.add(info);


            //得到网址数m.group()
            if (m.start() < m.end()) {
                span.setSpan(new LinkClickSpan(m.group(), m.group(), mUrlSpanClickListener),
                        m.start(), m.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }


        }
        return joinText(clickText, contextText);
    }




    public SpannableStringBuilderForAllvers identifyUrl22(CharSequence text) {
        CharSequence contextText;
        CharSequence clickText;
        text = text == null ? "" : text;
        //以下用于拼接本来存在的spanText
        SpannableStringBuilderForAllvers span = new SpannableStringBuilderForAllvers(text);
        ClickableSpan[] clickableSpans = span.getSpans(0, text.length(), ClickableSpan.class);
        if (clickableSpans.length > 0) {
            int start = 0;
            int end = 0;
            for (int i = 0; i < clickableSpans.length; i++) {
                start = span.getSpanStart(clickableSpans[0]);
                end = span.getSpanEnd(clickableSpans[i]);
            }
            //可点击文本后面的内容页
            contextText = text.subSequence(end, text.length());
            //可点击文本
            clickText = text.subSequence(start, end);
        } else {
            contextText = text;
            clickText = null;
        }
        m = r.matcher(contextText);
        //匹配成功
        while (m.find()) {
            //得到网址数m.group()
            if (m.start() < m.end()) {


                span.setSpan(new LinkClickSpan(m.group(), m.group(), mUrlSpanClickListener),
                        m.start(), m.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        return span;
    }






    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";
    private static final String FTP = "ftp://";

    public static boolean hasNetUrlHead(String url) {
        return (!TextUtils.isEmpty(url)) && (url.startsWith(HTTP) || url.startsWith(HTTPS) || url.startsWith(FTP));
    }

    private UrlSpanClickListener mUrlSpanClickListener = new UrlSpanClickListener() {
        @Override
        public void onClick(View view, String url, String content) {

            KLog.d("消息点击了url111:");

            if (TextUtils.isEmpty(url)) {
                return;
            }
            Matcher url_matcher = Patterns.WEB_URL.matcher(url);
            if (url_matcher.matches()) {
                String tempUrl;
                if (hasNetUrlHead(url)) {
                    tempUrl = url;
                } else {
                    tempUrl = HTTPS + url;
                }
                //通过webview打开相应的url


                KLog.d("消息点击了url:"+tempUrl);

                Bundle bundle = new Bundle();
                bundle.putString("clickUrl", tempUrl);

                ARouter.getInstance().build(Rout.WebViewActivity)
                        .with(bundle)
                        .withTransition(R.anim.push_left_in,R.anim.push_left_out)
                        .navigation();



                //Intent intent=new Intent(AppApplicationMVVM.getInstance(),)
                //WebViewActivity.presentWeb(Utilities.getApplicationContext(), WebViewActivity.class, WebCommonFragment.class, content, bundle);
            }
        }
    };






    public interface UrlSpanClickListener {
        void onClick(View view, String url, String content);
    }

    public static class LinkClickSpan extends ClickableSpan {
        private int mColor = AppApplicationMVVM.getInstance().getResources().getColor(R.color.color_5ABDFC);
        private int mColor2 = AppApplicationMVVM.getInstance().getResources().getColor(R.color.red);
        private String mUrl;
        private String mContent;
        UrlSpanClickListener mClickListener;

        public LinkClickSpan(String url, String content, UrlSpanClickListener onClickListener) {
            super();
            mUrl = url;
            mContent = content;
            mClickListener = onClickListener;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(mColor2);
            ds.linkColor = mColor;
            ds.setUnderlineText(false);//设置是否下划线
            ds.clearShadowLayer();
        }

        @Override
        public void onClick(View widget) {
            if (mClickListener != null) {
                mClickListener.onClick(widget, mUrl, mContent);
            }
        }

    }



    /** 拼接文本 */
    private SpannableStringBuilderForAllvers joinText(CharSequence clickSpanText,
                                                      CharSequence contentText) {
        SpannableStringBuilderForAllvers spanBuilder;
        if (clickSpanText != null) {
            spanBuilder = new SpannableStringBuilderForAllvers(clickSpanText);

        } else {
            spanBuilder = new SpannableStringBuilderForAllvers();
        }
        if (mStringList.size() > 0) {
            //只有一个网址
            if (mStringList.size() == 1) {
                String preStr = contentText.toString().substring(0, mUrlInfos.get(0).start);
                spanBuilder.append(preStr);
                String url = mStringList.get(0);
                int start = spanBuilder.length();
                spanBuilder.append(url, new UnderlineSpan(), flag);
                int end = spanBuilder.length();
                if (start >= 0 && end > 0 && end > start) {
                    spanBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, flag);
                }
                String nextStr = contentText.toString().substring(mUrlInfos.get(0).end);
                spanBuilder.append(nextStr);
            } else {
                //有多个网址
                for (int i = 0; i < mStringList.size(); i++) {
                    if (i == 0) {
                        //拼接第1个span的前面文本
                        String headStr =
                                contentText.toString().substring(0, mUrlInfos.get(0).start);
                        spanBuilder.append(headStr);
                    }
                    if (i == mStringList.size() - 1) {
                        //拼接最后一个span的后面的文本
                        int start = spanBuilder.length();
                        spanBuilder.append(mStringList.get(i), new UnderlineSpan(),
                                flag);
                        int end = spanBuilder.length();
                        if (start >= 0 && end > 0 && end > start) {
                            spanBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, flag);
                        }
                        String footStr = contentText.toString().substring(mUrlInfos.get(i).end);
                        spanBuilder.append(footStr);
                    }
                    if (i != mStringList.size() - 1) {
                        //拼接两两span之间的文本
                        int start = spanBuilder.length();
                        spanBuilder.append(mStringList.get(i), new UnderlineSpan(), flag);
                        int end = spanBuilder.length();
                        if (start >= 0 && end > 0 && end > start) {
                            spanBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, flag);
                        }
                        String betweenStr = contentText.toString()
                                .substring(mUrlInfos.get(i).end,
                                        mUrlInfos.get(i + 1).start);
                        spanBuilder.append(betweenStr);
                    }
                }
            }
        } else {
            spanBuilder.append(contentText);
        }
        return spanBuilder;
    }

    public class SpannableStringBuilderForAllvers extends SpannableStringBuilder{

        public SpannableStringBuilderForAllvers() {
            super("");
        }
        public SpannableStringBuilderForAllvers(CharSequence text) {
            super(text, 0, text.length());
        }
        public SpannableStringBuilderForAllvers(CharSequence text, int start, int end){
            super(text,start,end);
        }

        @Override
        public SpannableStringBuilder append(CharSequence text) {
            if (text == null) {
                return this;
            }
            int length = length();
            return (SpannableStringBuilderForAllvers)replace(length, length, text, 0, text.length());
        }

        /**该方法在原API里面只支持API21或者以上，这里适应低版本*/
        @Override
        public SpannableStringBuilderForAllvers append(CharSequence text, Object what, int flags) {
            if (text == null) {
                return this;
            }
            int start = length();
            append(text);
            setSpan(what, start, length(), flags);
            return this;
        }
    }



}
