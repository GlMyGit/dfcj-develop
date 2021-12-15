package com.dfcj.videoim.entity;

public class ChangeCustomerServiceEntity {
    private Boolean success;
    private DataBean data;
    private FailBean fail;
    private String code;
    private String message;

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

    public FailBean getFail() {
        return fail;
    }

    public void setFail(FailBean fail) {
        this.fail = fail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        private String staffCode;
        private Long eventId;

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
    }

    public static class FailBean {
        private Object extra;
        private String code;
        private String message;

        public Object getExtra() {
            return extra;
        }

        public void setExtra(Object extra) {
            this.extra = extra;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    // {"success":false,"data":{"staffCode":"staff1","eventId":79},"fail":{"extra":null,"code":"18800301","message":"当前无空闲客服，请您稍候，目前正在排队人数1人"}}


}
