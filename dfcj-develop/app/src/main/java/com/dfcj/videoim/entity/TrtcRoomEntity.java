package com.dfcj.videoim.entity;

public class TrtcRoomEntity {


    private Boolean success;
    private String data;
    private Object fail;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Object getFail() {
        return fail;
    }

    public void setFail(Object fail) {
        this.fail = fail;
    }
}
