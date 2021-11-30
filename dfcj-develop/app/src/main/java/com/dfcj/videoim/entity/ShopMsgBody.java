package com.dfcj.videoim.entity;

public class ShopMsgBody  extends MsgBody{

    //    产品json:{
//        "goodsIcon": "商品缩略图",
//    "goodsCode":"商品货号",
//    "goodsName":"商品名称",
//    "goodsPrice":"商品价格",
//    "goodsLinkApp":"商品app端URL",
//    "goodsLinkWeb":"商品web端URL"
//    }
//



    private String goodsCode;

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsLinkApp() {
        return goodsLinkApp;
    }

    public void setGoodsLinkApp(String goodsLinkApp) {
        this.goodsLinkApp = goodsLinkApp;
    }

    public String getGoodsLinkWeb() {
        return goodsLinkWeb;
    }

    public void setGoodsLinkWeb(String goodsLinkWeb) {
        this.goodsLinkWeb = goodsLinkWeb;
    }

    private String goodsName;
    private String goodsPrice;
    private String goodsLinkApp;
    private String goodsLinkWeb;

    public String getGoodImgUrl() {
        return goodImgUrl;
    }

    public void setGoodImgUrl(String goodImgUrl) {
        this.goodImgUrl = goodImgUrl;
    }

    private String goodImgUrl;



}
