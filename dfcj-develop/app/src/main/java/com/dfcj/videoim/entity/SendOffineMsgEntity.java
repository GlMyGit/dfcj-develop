package com.dfcj.videoim.entity;

public class SendOffineMsgEntity {


    private Boolean success;
    private DataBean data;
    private String fail;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getFail() {
        return fail;
    }

    public void setFail(String fail) {
        this.fail = fail;
    }

    public static class DataBean {
        private Integer showType;
        private String result;
        private String code;
        private String contextMenu;
        private String menu;
        private ProductInfoBean productInfo;

        public DistriButeStaffInfo getDistributeStaffInfo() {
            return distributeStaffInfo;
        }

        public void setDistributeStaffInfo(DistriButeStaffInfo distributeStaffInfo) {
            this.distributeStaffInfo = distributeStaffInfo;
        }

        private DistriButeStaffInfo distributeStaffInfo;
        private String type;
        private MsgBean msg;

        public Integer getShowType() {
            return showType;
        }

        public void setShowType(Integer showType) {
            this.showType = showType;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getContextMenu() {
            return contextMenu;
        }

        public void setContextMenu(String contextMenu) {
            this.contextMenu = contextMenu;
        }

        public String getMenu() {
            return menu;
        }

        public void setMenu(String menu) {
            this.menu = menu;
        }

        public ProductInfoBean getProductInfo() {
            return productInfo;
        }

        public void setProductInfo(ProductInfoBean productInfo) {
            this.productInfo = productInfo;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public MsgBean getMsg() {
            return msg;
        }

        public void setMsg(MsgBean msg) {
            this.msg = msg;
        }

        public static  class  DistriButeStaffInfo{
            private String staffCode;

            public String getStaffCode() {
                return staffCode;
            }

            public void setStaffCode(String staffCode) {
                this.staffCode = staffCode;
            }

            public Long getEventId() {
                return eventId;
            }

            public void setEventId(Long eventId) {
                this.eventId = eventId;
            }

            public String getRoomId() {
                return roomId;
            }

            public void setRoomId(String roomId) {
                this.roomId = roomId;
            }

            private Long eventId;
            private String roomId;


        }

        public static class ProductInfoBean {
            private String itemCode;
            private String shareImg;
            private String itemName;
            private String itemUrl;
            private String lastSalePrice;

            public String getItemCode() {
                return itemCode;
            }

            public void setItemCode(String itemCode) {
                this.itemCode = itemCode;
            }

            public String getShareImg() {
                return shareImg;
            }

            public void setShareImg(String shareImg) {
                this.shareImg = shareImg;
            }

            public String getItemName() {
                return itemName;
            }

            public void setItemName(String itemName) {
                this.itemName = itemName;
            }

            public String getItemUrl() {
                return itemUrl;
            }

            public void setItemUrl(String itemUrl) {
                this.itemUrl = itemUrl;
            }

            public String getLastSalePrice() {
                return lastSalePrice;
            }

            public void setLastSalePrice(String lastSalePrice) {
                this.lastSalePrice = lastSalePrice;
            }
        }

        public static class MsgBean {
            private String msgType;
            private String content;

            public String getMsgType() {
                return msgType;
            }

            public void setMsgType(String msgType) {
                this.msgType = msgType;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
