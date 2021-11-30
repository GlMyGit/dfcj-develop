package com.dfcj.videoim.entity;

public class LoginBean {


    private Boolean success;
    private DataBean data;
    private String fail;

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
        private Integer status;
        private String msg;
        private String staffCode;
        private String userSig;
        private String eventId;
        private int sdkAppId;


        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }


        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getStaffCode() {
            return staffCode;
        }

        public void setStaffCode(String staffCode) {
            this.staffCode = staffCode;
        }

        public String getUserSig() {
            return userSig;
        }

        public void setUserSig(String userSig) {
            this.userSig = userSig;
        }

        public int getSdkAppId() {
            return sdkAppId;
        }

        public void setSdkAppId(int sdkAppId) {
            this.sdkAppId = sdkAppId;
        }
    }
}
