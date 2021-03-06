package com.dfcj.videoim.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.AppApplicationMVVM;
import com.dfcj.videoim.im.emo.EmojiAdapter;
import com.dfcj.videoim.im.emo.EmojiBean;
import com.dfcj.videoim.im.emo.EmojiVpAdapter;
import com.dfcj.videoim.view.widght.IndicatorView;
import com.dfcj.videoim.view.widght.RecordButton;
import com.wzq.mvvmsmart.utils.KLog;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUiHelper {

    private static Context context = AppApplicationMVVM.getInstance();

    private static String[] emojiFilters = context.getResources().getStringArray(R.array.emoji_filter_key);
    private static String[] emojiFilters_values = context.getResources().getStringArray(R.array.emoji_filter_value);
    private static final int drawableWidth = ScreenUtil.getPxByDp(context,32);
    private static LruCache<String, Bitmap> drawableCache = new LruCache(1024);
    private static LruCache<String, Bitmap> drawableCache22 = new LruCache(1024);
    private static ArrayList<EmojiBean> mListEmoji = new ArrayList<>();

    private static final String SHARE_PREFERENCE_NAME = "com.chat.ui";
    private static final String SHARE_PREFERENCE_TAG = "soft_input_height";
    private Activity mActivity;
    private LinearLayout mContentLayout;//??????????????????
    private RelativeLayout mBottomLayout;//????????????
    private LinearLayout mEmojiLayout;//????????????
    private LinearLayout mAddLayout;//????????????
    private Button mSendBtn;//????????????
    private View mAddButton;//????????????
    private Button mAudioButton;//????????????
    private ImageView mAudioIv;//????????????


    private EditText mEditText;
    private InputMethodManager mInputManager;
    private SharedPreferences mSp;
    private ImageView mIvEmoji;

    public ChatUiHelper() {

    }

    public static ChatUiHelper with(Activity activity) {
        ChatUiHelper mChatUiHelper = new ChatUiHelper();
        //   AndroidBug5497Workaround.assistActivity(activity);
        mChatUiHelper.mActivity = activity;
        mChatUiHelper.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatUiHelper.mSp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mChatUiHelper;
    }

    public static final int EVERY_PAGE_SIZE = 21;
   // private List<EmojiBean> mListEmoji;
    //private List<String> mListEmoji;

    public ChatUiHelper bindEmojiData() {

       // mListEmoji = EmojiDao.getInstance().getEmojiBean();

        if(mListEmoji!=null){
            mListEmoji.clear();
        }


        for (int i = 0; i < emojiFilters.length; i++) {
          /* String vbal= "/emoji/" + emojiFilters[i] + "@2x.png";
            EmojiBean  sdf=new EmojiBean();
            sdf.setEmojiFilters(vbal);
            mListEmoji.add(sdf);*/

            loadAssetBitmap(i,emojiFilters[i], "emoji/" + emojiFilters[i] + "@2x.png", true);

        }

        KLog.d("????????????????????????"+ Arrays.asList(mListEmoji).size());
        LinearLayout homeEmoji = mActivity.findViewById(R.id.home_emoji);
        ViewPager vpEmoji = mActivity.findViewById(R.id.vp_emoji);
        final IndicatorView indEmoji = mActivity.findViewById(R.id.ind_emoji);
        LinearLayout.LayoutParams layoutParams12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        //???RecyclerView??????ViewPager??????
        int pageSize = EVERY_PAGE_SIZE;
        EmojiBean mEmojiBean = new EmojiBean();
        mEmojiBean.setId(999);
        mEmojiBean.setUnicodeInt(000);
        int deleteCount = (int) Math.ceil(mListEmoji.size() * 1.0 / EVERY_PAGE_SIZE);//??????????????????????????????
//        KLog.d("" + deleteCount);
        //???????????????
        for (int i = 1; i < deleteCount + 1; i++) {
            if (i == deleteCount) {
                mListEmoji.add(mListEmoji.size(), mEmojiBean);
            } else {
                mListEmoji.add(i * EVERY_PAGE_SIZE - 1, mEmojiBean);
            }
//            KLog.d("????????????" + i);

        }


        int pageCount = (int) Math.ceil((mListEmoji.size()) * 1.0 / pageSize);//???????????????
       // KLog.d("???????????????:" + pageCount);
        List<View> viewList = new ArrayList<View>();
        for (int index = 0; index < pageCount; index++) {
            //????????????????????????recycleview
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.item_emoji_vprecy, vpEmoji, false);
            recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 7));
            EmojiAdapter entranceAdapter;
            if (index == pageCount - 1) {
                //?????????????????????
                List<EmojiBean> lastPageList = mListEmoji.subList(index * EVERY_PAGE_SIZE, mListEmoji.size());
                entranceAdapter = new EmojiAdapter(lastPageList, index, EVERY_PAGE_SIZE);
            } else {
                entranceAdapter = new EmojiAdapter(mListEmoji.subList(index * EVERY_PAGE_SIZE, (index + 1) * EVERY_PAGE_SIZE), index, EVERY_PAGE_SIZE);
            }

            entranceAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    EmojiBean mEmojiBean = (EmojiBean) adapter.getData().get(position);
                    if (mEmojiBean.getId() == 999) {
                        //??????????????????
                        mEditText.dispatchKeyEvent(new KeyEvent(
                                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    } else {

                        Bitmap bitmap = ((EmojiBean) adapter.getData().get(position)).getIcon();
                        String emotionName  = ((EmojiBean) adapter.getData().get(position)).getFilter();

                        // ????????????????????????,??????????????????????????????????????????
//                        int curPosition = mEditText.getSelectionStart();
//                        StringBuilder sb = new StringBuilder(mEditText.getText().toString());
//                        sb.insert(curPosition, emotionName );
//                        // ??????????????????,????????????????????????
//                        mEditText.append(AppUtils.getEmotionContent(bitmap,
//                                context, mEditText, sb.toString()));
//                        // ??????????????????????????????????????????
                      //  mEditText.setSelection(curPosition + emotionName.length());


                        SpannableString spannableString = new SpannableString(emotionName);
                        ImageSpan span = new ImageSpan(context, bitmap);
                        spannableString.setSpan(span, 0, 0 + emotionName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        mEditText.append(spannableString);


                    }

                }
            });

            recyclerView.setAdapter(entranceAdapter);
            viewList.add(recyclerView);
        }
        EmojiVpAdapter adapter = new EmojiVpAdapter(viewList);
        vpEmoji.setAdapter(adapter);
        indEmoji.setIndicatorCount(vpEmoji.getAdapter().getCount());
        indEmoji.setCurrentIndicator(vpEmoji.getCurrentItem());
        vpEmoji.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indEmoji.setCurrentIndicator(position);
            }
        });
        return this;
    }




    private static EmojiBean loadAssetBitmap(int s,String filter, String assetPath, boolean isEmoji) {
        InputStream is = null;
        try {



            EmojiBean emoji = new EmojiBean();
            Resources resources = context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = DisplayMetrics.DENSITY_XXHIGH;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            context.getAssets().list("");
            is = context.getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(0, 0, drawableWidth, drawableWidth), options);
            if (bitmap != null) {
                drawableCache.put(filter, bitmap);

                emoji.setIcon(bitmap);
                emoji.setFilter(filter);
                emoji.setId(s);
                if (isEmoji) {
                   // emojiList.add(emoji);
                    mListEmoji.add(emoji);
                }

            }
            return emoji;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static Bitmap getImgBitMapUrl(String assetPath){

        InputStream is = null;
        try {
            Resources resources = context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = DisplayMetrics.DENSITY_XXHIGH;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            context.getAssets().list("");
            is = context.getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(0, 0, drawableWidth, drawableWidth), options);
            if (bitmap != null) {

            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }




    public static SpannableStringBuilder handlerEmojiText( String content, boolean typing) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "\\[(\\S+?)\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        boolean imageFound = false;
        while (m.find()) {
            String emojiName = m.group();
            Bitmap bitmap = drawableCache.get(emojiName);
            if (bitmap != null) {
                imageFound = true;
                sb.setSpan(new ImageSpan(context, bitmap),
                        m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        // ????????????????????????????????????????????????????????????????????????????????????
        if (!imageFound && typing) {
            return    new SpannableStringBuilder(content);
        }

        return sb;


      /*  int selection = comment.getSelectionStart();
        comment.setText(sb);
        if (comment instanceof EditText) {
            ((EditText) comment).setSelection(selection);
        }*/
    }




    //????????????????????????
    public ChatUiHelper bindContentLayout(LinearLayout bottomLayout) {
        mContentLayout = bottomLayout;
        return this;
    }


    //???????????????
    public ChatUiHelper bindEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mBottomLayout.isShown()) {
                    lockContentHeight();//?????????????????????????????????????????????????????????
                    hideBottomLayout(true);//????????????????????????????????????
                    mIvEmoji.setImageResource(R.drawable.g_pic102);
                    //???????????????????????????????????????
                    mEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unlockContentHeightDelayed();
                        }
                    }, 200L);
                }
                return false;
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditText.getText().toString().trim().length() > 0) {
                    mSendBtn.setVisibility(View.VISIBLE);
                    mAddButton.setVisibility(View.GONE);
                } else {
                    mSendBtn.setVisibility(View.GONE);
                    mAddButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return this;
    }

    //??????????????????
    public ChatUiHelper bindBottomLayout(RelativeLayout bottomLayout) {
        mBottomLayout = bottomLayout;
        return this;
    }


    //??????????????????
    public ChatUiHelper bindEmojiLayout(LinearLayout emojiLayout) {
        mEmojiLayout = emojiLayout;
        return this;
    }

    //??????????????????
    public ChatUiHelper bindAddLayout(LinearLayout addLayout) {
        mAddLayout = addLayout;
        return this;
    }

    //??????????????????
    public ChatUiHelper bindttToSendButton(Button sendbtn) {
        mSendBtn = sendbtn;
        return this;
    }


    //??????????????????????????????
    public ChatUiHelper bindAudioBtn(RecordButton audioBtn) {
        mAudioButton = audioBtn;
        return this;
    }

    //??????????????????????????????
    public ChatUiHelper bindAudioIv(ImageView audioIv) {
        mAudioIv = audioIv;
       /* audioIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????????????????????
                if (mAudioButton.isShown()) {
                    hideAudioButton();
                    mEditText.requestFocus();
                    showSoftInput();

                } else {
                    mEditText.clearFocus();
                    showAudioButton();
                    hideEmotionLayout();
                    hideMoreLayout();
                }
            }
        });
*/
        // UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
        return this;
    }

    private void hideAudioButton() {
        mAudioButton.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
        mAudioIv.setImageResource(R.drawable.g_pic101);
    }


    private void showAudioButton() {
        mAudioButton.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
       // mAudioIv.setImageResource(R.drawable.ic_keyboard);
        mAudioIv.setImageResource(R.drawable.g_pic123);
        if (mBottomLayout.isShown()) {
            hideBottomLayout(false);
        } else {
            hideSoftInput();
        }
    }


    //??????????????????????????????
    public ChatUiHelper bindToEmojiButton(ImageView emojiBtn) {
        mIvEmoji = emojiBtn;
        emojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.clearFocus();
                if (!mEmojiLayout.isShown()) {
                    if (mAddLayout.isShown()) {
                        showEmotionLayout();
                        hideMoreLayout();
                        hideAudioButton();
                        return;
                    }
                } else if (mEmojiLayout.isShown() && !mAddLayout.isShown()) {
                    mIvEmoji.setImageResource(R.drawable.g_pic102);
                    if (mBottomLayout.isShown()) {
                        lockContentHeight();//?????????????????????????????????????????????????????????
                        hideBottomLayout(true);//????????????????????????????????????
                        unlockContentHeightDelayed();//???????????????????????????????????????
                    } else {
                        if (isSoftInputShown()) {//??????
                            lockContentHeight();
                            showBottomLayout();
                            unlockContentHeightDelayed();
                        } else {
                            showBottomLayout();//?????????????????????????????????????????????
                        }
                    }


                    return;
                }
                showEmotionLayout();
                hideMoreLayout();
                hideAudioButton();
                if (mBottomLayout.isShown()) {
                    lockContentHeight();//?????????????????????????????????????????????????????????
                    hideBottomLayout(true);//????????????????????????????????????
                    unlockContentHeightDelayed();//???????????????????????????????????????
                } else {
                    if (isSoftInputShown()) {//??????
                        lockContentHeight();
                        showBottomLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showBottomLayout();//?????????????????????????????????????????????
                    }
                }
            }
        });
        return this;
    }


    //????????????????????????
    public ChatUiHelper bindToAddButton(View addButton) {
        mAddButton = addButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mEditText.clearFocus();
                 hideAudioButton();
                 if (mBottomLayout.isShown()){
                  if (mAddLayout.isShown()){
                      lockContentHeight();//?????????????????????????????????????????????????????????
                      hideBottomLayout(true);//????????????????????????????????????
                      unlockContentHeightDelayed();//???????????????????????????????????????
                  }else{
                      showMoreLayout();
                      hideEmotionLayout();
                  }
              }else{
                  if (isSoftInputShown()) {//??????
                      hideEmotionLayout();
                      showMoreLayout();
                      lockContentHeight();
                      showBottomLayout();
                      unlockContentHeightDelayed();
                  } else {
                       showMoreLayout();
                       hideEmotionLayout();
                       showBottomLayout();//?????????????????????????????????????????????
                  }
              }
            }
        });
        return this;
    }


    private void hideMoreLayout() {
        mAddLayout.setVisibility(View.GONE);
    }

    private void showMoreLayout() {
        mAddLayout.setVisibility(View.VISIBLE);
    }


    /**
     * ??????????????????
     *
     * @param showSoftInput ?????????????????????
     */
    public void hideBottomLayout(boolean showSoftInput) {
        if (mBottomLayout.isShown()) {
            mBottomLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    private void showBottomLayout() {
        int softInputHeight = getSupportSoftInputHeight();
         if (softInputHeight == 0) {
            softInputHeight = mSp.getInt(SHARE_PREFERENCE_TAG, dip2Px(220));//?????????????????? 270??????
        }
        hideSoftInput();
        mBottomLayout.getLayoutParams().height = softInputHeight;
        mBottomLayout.setVisibility(View.VISIBLE);
    }


    private void showEmotionLayout() {
        mEmojiLayout.setVisibility(View.VISIBLE);
     //   mIvEmoji.setImageResource(R.drawable.ic_keyboard);
        mIvEmoji.setImageResource(R.drawable.g_pic123);
    }

    private void hideEmotionLayout() {
        mEmojiLayout.setVisibility(View.GONE);
        mIvEmoji.setImageResource(R.drawable.g_pic102);
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    public int dip2Px(int dip) {
        float density = mActivity.getApplicationContext().getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }


    /**
     * ???????????????
     */
    public void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }


    /**
     * ????????????????????????
     *
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /*  *
         * decorView???window???????????????view????????????window?????????getDecorView?????????decorView???
         * ??????decorView???????????????????????????????????????????????????????????????????????????*/
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //?????????????????????
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        //????????????????????????
        int softInputHeight = screenHeight - r.bottom;

        if (isNavigationBarExist(mActivity)) {
            softInputHeight = softInputHeight - getNavigationHeight(mActivity);
        }
        //??????????????????
        if (softInputHeight > 0) {
            mSp.edit().putInt(SHARE_PREFERENCE_TAG, softInputHeight).apply();
        }
        return softInputHeight;
    }


    public void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    /**
     * ?????????????????????????????????
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentLayout.getLayoutParams();
        params.height = mContentLayout.getHeight();
        params.weight = 0.0F;
    }

    /**
     * ??????????????????????????????
     */
    public void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentLayout.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    private static final String NAVIGATION = "navigationBarBackground";

    // ??????????????????View??????????????????????????????????????????????????????
    //????????? onWindowFocusChanged??????????????????????????????????????????
    public boolean isNavigationBarExist(@NonNull Activity activity) {
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();
                if (vp.getChildAt(i).getId() != View.NO_ID &&
                        NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                     return true;
                }
            }
        }
        return false;
    }


    public int getNavigationHeight(Context activity) {
        if (activity == null) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            //??????NavigationBar?????????
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }


}
