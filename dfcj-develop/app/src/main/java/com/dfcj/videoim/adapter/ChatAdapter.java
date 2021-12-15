package com.dfcj.videoim.adapter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dfcj.videoim.MainActivity;
import com.dfcj.videoim.R;
import com.dfcj.videoim.appconfig.AppConstant;
import com.dfcj.videoim.appconfig.Rout;
import com.dfcj.videoim.entity.AudioMsgBody;
import com.dfcj.videoim.entity.FileMsgBody;
import com.dfcj.videoim.entity.ImageMsgBody;
import com.dfcj.videoim.entity.Message;
import com.dfcj.videoim.entity.MsgBody;
import com.dfcj.videoim.entity.MsgSendStatus;
import com.dfcj.videoim.entity.MsgType;
import com.dfcj.videoim.entity.ShopMsgBody;
import com.dfcj.videoim.entity.TextMsgBody;
import com.dfcj.videoim.entity.VideoMsgBody;
import com.dfcj.videoim.listener.ShopManager;
import com.dfcj.videoim.util.AppUtils;
import com.dfcj.videoim.util.AutoLinKTextViewUtil;
import com.dfcj.videoim.util.other.GlideUtils;
import com.dfcj.videoim.util.other.SharedPrefsUtils;
import com.dfcj.videoim.view.myview.ClickableSpanTextView;
import com.wzq.mvvmsmart.utils.KLog;
import com.zzhoujay.richtext.RichText;


import java.io.File;
import java.util.List;

public class ChatAdapter extends BaseMultiItemQuickAdapter<Message, BaseViewHolder> {


    public static final int TYPE_SEND_TEXT = 1;
    public static final int TYPE_RECEIVE_TEXT = 2;
    public static final int TYPE_SEND_IMAGE = 3;
    public static final int TYPE_RECEIVE_IMAGE = 4;
    public static final int TYPE_SEND_VIDEO = 5;
    public static final int TYPE_RECEIVE_VIDEO = 6;
    public static final int TYPE_SEND_FILE = 7;
    public static final int TYPE_RECEIVE_FILE = 8;
    public static final int TYPE_SEND_AUDIO = 9;
    public static final int TYPE_RECEIVE_AUDIO = 10;
    public static final int TYPE_KAPIAN_SEND_TEXT = 11;//左边卡片
    public static final int TYPE_KAPIAN_RECEIVE_TEXT = 12;//右边卡片
    public static final int TYPE_CENTER_TEXT = 13;//中间文本
    public static final int TYPE_DF_TEXT = 14;//默认机器人文本


    private static final int SEND_TEXT = R.layout.item_text_send;
    private static final int RECEIVE_TEXT = R.layout.item_text_receive;
    private static final int SEND_IMAGE = R.layout.item_image_send;
    private static final int RECEIVE_IMAGE = R.layout.item_image_receive;
    private static final int SEND_VIDEO = R.layout.item_video_send;
    private static final int RECEIVE_VIDEO = R.layout.item_video_receive;
    private static final int SEND_FILE = R.layout.item_file_send;
    private static final int RECEIVE_FILE = R.layout.item_file_receive;
    private static final int RECEIVE_AUDIO = R.layout.item_audio_receive;
    private static final int SEND_AUDIO = R.layout.item_audio_send;
    private static final int KAPIAN_SEND_TEXT = R.layout.kapian_item_text_send;
    private static final int KAPIAN_RECEIVE_TEXT = R.layout.kapian_item_text_receive;
    private static final int CENTER_TEXT = R.layout.center_item_msg;
    private static final int DF_TEXT = R.layout.df_item_text_receive;


    public ChatAdapter(List<Message> data) {
        // super(context);
        super(data);
        addItemType(TYPE_SEND_TEXT, SEND_TEXT);
        addItemType(TYPE_RECEIVE_TEXT, RECEIVE_TEXT);
        addItemType(TYPE_SEND_IMAGE, SEND_IMAGE);
        addItemType(TYPE_RECEIVE_IMAGE, RECEIVE_IMAGE);
        addItemType(TYPE_SEND_VIDEO, SEND_VIDEO);
        addItemType(TYPE_RECEIVE_VIDEO, RECEIVE_VIDEO);
        addItemType(TYPE_SEND_FILE, SEND_FILE);
        addItemType(TYPE_RECEIVE_FILE, RECEIVE_FILE);
        addItemType(TYPE_SEND_AUDIO, SEND_AUDIO);
        addItemType(TYPE_RECEIVE_AUDIO, RECEIVE_AUDIO);
        addItemType(TYPE_KAPIAN_SEND_TEXT, KAPIAN_SEND_TEXT);
        addItemType(TYPE_KAPIAN_RECEIVE_TEXT, KAPIAN_RECEIVE_TEXT);
        addItemType(TYPE_CENTER_TEXT, CENTER_TEXT);
        addItemType(TYPE_DF_TEXT, DF_TEXT);


    }

    @Override
    protected void convert(BaseViewHolder helper, Message item) {
        setContent(helper, item);
        setStatus(helper, item);
        setOnClick(helper, item);

    }

    private int getMsgStatus(Message entity) {
        boolean isSend = entity.getSenderId().equals(MainActivity.mSenderId);

        if (MsgType.TEXT == entity.getMsgType()) {
            return isSend ? TYPE_SEND_TEXT : TYPE_RECEIVE_TEXT;
        } else if (MsgType.IMAGE == entity.getMsgType()) {
            return isSend ? TYPE_SEND_IMAGE : TYPE_RECEIVE_IMAGE;
        } else if (MsgType.VIDEO == entity.getMsgType()) {
            return isSend ? TYPE_SEND_VIDEO : TYPE_RECEIVE_VIDEO;
        } else if (MsgType.FILE == entity.getMsgType()) {
            return isSend ? TYPE_SEND_FILE : TYPE_RECEIVE_FILE;
        } else if (MsgType.AUDIO == entity.getMsgType()) {
            return isSend ? TYPE_SEND_AUDIO : TYPE_RECEIVE_AUDIO;
        }
        return 0;

    }

    private void setStatus(BaseViewHolder helper, Message item) {
        MsgBody msgContent = item.getBody();
        if (msgContent instanceof TextMsgBody
                || msgContent instanceof AudioMsgBody || msgContent instanceof VideoMsgBody
                || msgContent instanceof FileMsgBody||  msgContent instanceof ShopMsgBody) {
            //只需要设置自己发送的状态
            MsgSendStatus sentStatus = item.getSentStatus();
            boolean isSend = item.getSenderId().equals(MainActivity.mSenderId);
            if (isSend) {
                if (sentStatus == MsgSendStatus.SENDING) {//发送中
                    helper.setVisible(R.id.chat_item_progress, true).setVisible(R.id.chat_item_fail, false);
                } else if (sentStatus == MsgSendStatus.FAILED) {//发送失败
                    helper.setVisible(R.id.chat_item_progress, false).setVisible(R.id.chat_item_fail, true);
                } else if (sentStatus == MsgSendStatus.SENT) {//发送成功
                    helper.setVisible(R.id.chat_item_progress, false).setVisible(R.id.chat_item_fail, false);
                }
            }
        } else if (msgContent instanceof ImageMsgBody) {
            boolean isSend = item.getSenderId().equals(MainActivity.mSenderId);
            if (isSend) {
                MsgSendStatus sentStatus = item.getSentStatus();
                if (sentStatus == MsgSendStatus.SENDING) {//发送中
                    helper.setVisible(R.id.chat_item_progress, false).setVisible(R.id.chat_item_fail, false);
                } else if (sentStatus == MsgSendStatus.FAILED) {//发送失败
                    helper.setVisible(R.id.chat_item_progress, false).setVisible(R.id.chat_item_fail, true);
                } else if (sentStatus == MsgSendStatus.SENT) {//发送成功
                    helper.setVisible(R.id.chat_item_progress, false).setVisible(R.id.chat_item_fail, false);
                }
            } else {

            }
        }

    }

    private String mImagePath = null;

    private void setContent(BaseViewHolder helper, Message item) {

        if (item.getMsgType().equals(MsgType.TEXT)) {

            TextMsgBody msgBody = (TextMsgBody) item.getBody();

            CharSequence charsequence = msgBody.getCharsequence();

            ClickableSpanTextView tv = helper.getView(R.id.chat_item_content_text);

            boolean b = AppUtils.hasSpecialChars(charsequence.toString());

            if (msgBody.isVideo()) {
                helper.setGone(R.id.chat_item_content_video_text, false);
            } else {
                helper.setGone(R.id.chat_item_content_video_text, true);
            }


            if (b) {

                String replace = charsequence.toString().replace("\\n", "<br>");
                String replace2 = replace.replace("\\r", "<br>");

                // KLog.d("格式化后："+ charsequence.toString());

                RichText.from(replace2)
                        .into(tv);

            } else {

                tv.setText(AutoLinKTextViewUtil.getInstance().identifyUrl22(msgBody.getCharsequence()));

            }

            if (item.getSenderId().equals(MainActivity.mSenderId)) {
                helper.setText(R.id.kapian_top_tv, SharedPrefsUtils.getValue(AppConstant.MyUserName));
            } else {
                helper.setText(R.id.kapian_top_tv, SharedPrefsUtils.getValue(AppConstant.STAFF_NAME));
            }
        } else if (item.getMsgType().equals(MsgType.IMAGE)) {

            ImageMsgBody msgBody = (ImageMsgBody) item.getBody();
            if (TextUtils.isEmpty(msgBody.getThumbPath())) {

                GlideUtils.loadChatImage(getContext(), msgBody.getThumbUrl(), (ImageView) helper.getView(R.id.bivPic));
              //  KLog.d("图片接收33333");
              //  KLog.d("图片接收33333:" + msgBody.getThumbUrl());

            } else {

                File file = new File(msgBody.getThumbPath());
                if (file.exists()) {
                  //  KLog.d("图片接收11111");
                 //   KLog.d("图片接收11111:" + msgBody.getThumbPath());

                    GlideUtils.loadChatImage(getContext(), msgBody.getThumbPath(), (ImageView) helper.getView(R.id.bivPic));

                } else {

                   // KLog.d("图片接收2222:" + msgBody.getThumbPath());
                    //  KLog.d("图片接收2222url:"+msgBody.getThumbUrl());

                    //  ImageView contentImage= ( (ImageView) helper.getView(R.id.bivPic));

                   // GlideUtils.loadChatImage(getContext(), msgBody.getThumbPath(), (ImageView) helper.getView(R.id.bivPic));


                    Glide.with(getContext()).
                            load(msgBody.getThumbPath())
                            .placeholder(R.drawable.default_img_failed).into((ImageView) helper.getView(R.id.bivPic));

                    // GlideUtils.loadChatImage(getContext(),msgBody.getThumbUrl(),(ImageView) helper.getView(R.id.bivPic));

                }
            }

            ((ImageView) helper.getView(R.id.bivPic)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String thumbUrl = msgBody.getThumbUrl();

                    KLog.d("点击的图片地址：" + thumbUrl);
                    if (!TextUtils.isEmpty(thumbUrl)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("thumbImgUrl", thumbUrl);
                        ARouter.getInstance().build(Rout.PhoneViewActivity)
                                .with(bundle)
                                .navigation();
                    }


                }
            });


        } else if (item.getMsgType().equals(MsgType.KAPIAN)) {//商品卡片信息

            ShopMsgBody msgBody = (ShopMsgBody) item.getBody();

            helper.setText(R.id.chat_item_content_text1, "" + msgBody.getGoodsCode());
            helper.setText(R.id.chat_item_content_text, "" + msgBody.getGoodsName());
            helper.setText(R.id.chat_item_price_text, "" + msgBody.getGoodsPrice());

            Glide.with(getContext()).
                    load(msgBody.getGoodsIcon())
                    .error(R.drawable.default_img_failed).into((ImageView) helper.getView(R.id.chat_item_img));

            if (item.getSenderId().equals(MainActivity.mSenderId)) {
                helper.setText(R.id.kapian_top_tv, SharedPrefsUtils.getValue(AppConstant.MyUserName));
            } else {
                helper.setText(R.id.kapian_top_tv, SharedPrefsUtils.getValue(AppConstant.STAFF_NAME));
            }

            helper.getView(R.id.goods_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ShopManager.instance().getShopClickListener() != null) {
                        ShopManager.instance().goodsChange(msgBody);
                    }
                }
            });

        } else if (item.getMsgType().equals(MsgType.CENTERMS)) {//中间信息

            TextMsgBody msgBody = (TextMsgBody) item.getBody();

            helper.setText(R.id.center_item_tv, "" + msgBody.getMessage());

        } else if (item.getMsgType().equals(MsgType.VIDEO)) {
            VideoMsgBody msgBody = (VideoMsgBody) item.getBody();
            File file = new File(msgBody.getExtra());
            if (file.exists()) {
                GlideUtils.loadChatImage(getContext(), msgBody.getExtra(), (ImageView) helper.getView(R.id.bivPic));
            } else {
                GlideUtils.loadChatImage(getContext(), msgBody.getExtra(), (ImageView) helper.getView(R.id.bivPic));
            }
        } else if (item.getMsgType().equals(MsgType.FILE)) {
            FileMsgBody msgBody = (FileMsgBody) item.getBody();
            helper.setText(R.id.msg_tv_file_name, msgBody.getDisplayName());
            helper.setText(R.id.msg_tv_file_size, msgBody.getSize() + "B");
        } else if (item.getMsgType().equals(MsgType.AUDIO)) {
            AudioMsgBody msgBody = (AudioMsgBody) item.getBody();
            helper.setText(R.id.tvDuration, msgBody.getDuration() + "\"");
        }


        if (item.getMsgType().equals(MsgType.TEXT) || item.getMsgType().equals(MsgType.IMAGE) || item.getMsgType().equals(MsgType.KAPIAN)) {

            //头像
            if (item.getSenderId().equals(MainActivity.mSenderId)) {
                Glide.with(getContext())
                        .load(SharedPrefsUtils.getValue(AppConstant.MyUserIcon))
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()).error(R.drawable.g_pic108))
                        .into((ImageView) helper.getView(R.id.chat_item_header));
            } else {
                Glide.with(getContext())
                        .load(SharedPrefsUtils.getValue(AppConstant.STAFF_IMGE))
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()).error(R.drawable.g_pic107))
                        .into((ImageView) helper.getView(R.id.chat_item_header));
            }

        }

    }


    private void setOnClick(BaseViewHolder helper, Message item) {
        MsgBody msgContent = item.getBody();
        if (msgContent instanceof ShopMsgBody) {
            //helper.addOnClickListener(R.id.rlAudio);
            // addChildClickViewIds(R.id.rlAudio);
            addChildClickViewIds(R.id.goods_layout);
        }
    }


}
