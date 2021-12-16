package com.dfcj.videoim.entity;

import com.google.gson.annotations.SerializedName;

public class MsgBodyBean {
    @SerializedName("MsgType")
    private String msgType;
    @SerializedName("MsgContent")
    private MsgContentDTO msgContent;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public MsgContentDTO getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(MsgContentDTO msgContent) {
        this.msgContent = msgContent;
    }

    public static class MsgContentDTO {
        @SerializedName("Data")
        private String data;
        private String Text;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getText() {
            return Text;
        }

        public void setText(String text) {
            Text = text;
        }
    }
}
