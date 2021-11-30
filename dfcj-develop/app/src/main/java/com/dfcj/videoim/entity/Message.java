package com.dfcj.videoim.entity;


import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.dfcj.videoim.im.ImageBean;
import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMImageElem;

public  class Message  implements MultiItemEntity {

     private String uuid;
      private String msgId;
     private MsgType msgType;
     private MsgBody body;
     private MsgSendStatus sentStatus;
     private String senderId;
     private String targetId;
     private long sentTime;




    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public MsgBody getBody() {
        return body;
    }

    public void setBody(MsgBody body) {
        this.body = body;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public MsgSendStatus getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(MsgSendStatus sentStatus) {
        this.sentStatus = sentStatus;
    }



    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    @Override
    public int getItemType() {
        return type;
    }






    private V2TIMImageElem.V2TIMImage v2TIMImage;

    public void setV2TIMImage(V2TIMImageElem.V2TIMImage v2TIMImage) {
        this.v2TIMImage = v2TIMImage;
    }

    public V2TIMImageElem.V2TIMImage getV2TIMImage() {
        return v2TIMImage;
    }


    public String getUrl() {
        if (v2TIMImage != null) {
            return v2TIMImage.getUrl();
        }
        return "";
    }


    public int getSize() {
        if (v2TIMImage != null) {
            return v2TIMImage.getSize();
        }
        return 0;
    }

    public int getHeight() {
        if (v2TIMImage != null) {
            return v2TIMImage.getHeight();
        }
        return 0;
    }

    public int getWidth() {
        if (v2TIMImage != null) {
            return v2TIMImage.getWidth();
        }
        return 0;
    }

    public void downloadImage(String path, ImageBean.ImageDownloadCallback callback) {
        if (v2TIMImage != null) {
            v2TIMImage.downloadImage(path, new V2TIMDownloadCallback() {
                @Override
                public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                    if (callback != null) {
                        callback.onProgress(progressInfo.getCurrentSize(), progressInfo.getTotalSize());
                    }
                }

                @Override
                public void onSuccess() {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onError(int code, String desc) {
                    if (callback != null) {
                        callback.onError(code, desc);
                    }
                }
            });
        }
    }


    public interface ImageDownloadCallback {
        void onProgress(long currentSize, long totalSize);

        void onSuccess();

        void onError(int code, String desc);
    }


}
