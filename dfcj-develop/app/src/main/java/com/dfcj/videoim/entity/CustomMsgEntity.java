package com.dfcj.videoim.entity;

public class CustomMsgEntity {
    private int msgType;//1文本  2 富文本   3带网址  4图片地址  5视频  6商品卡片    7结束视频   8取消  9拒绝
    private Object msgText;
    private Object imgUrl;
    private String userName;
    private String userImg;
    private String data;

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public Object getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(Object imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Object getMsgText() {
        return msgText;
    }

    public void setMsgText(Object msgText) {
        this.msgText = msgText;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
