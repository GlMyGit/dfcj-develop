package com.dfcj.videoim.entity;

public class CustomMsgEntity {

//
//    自定义消息规范：msgType //1文本  2 富文本   3带网址  4图片地址  5视频 6商品
//"data":{
//    "msgType": 1,
//    "msgText":"string",
//    "imgUrl":"图片URL"
//    }
//
//



    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }


    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    private int msgType;//1文本  2 富文本   3带网址  4图片地址  5视频 6商品

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    private String imgUrl;
    private String msgText;





}
