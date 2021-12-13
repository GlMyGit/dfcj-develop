package com.dfcj.videoim.entity;

public class EventMessage<T> {


    private String code;
    private int codeInt;
    private T data;

    public EventMessage(String code) {
        this.code = code;
    }

    public EventMessage(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public EventMessage(int codeInt) {
        this.codeInt = codeInt;
    }

    public EventMessage(int codeInt, T data) {
        this.codeInt = codeInt;
        this.data = data;
    }

    public EventMessage(String code, int codeInt, T data) {
        this.code = code;
        this.codeInt = codeInt;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCodeInt() {
        return codeInt;
    }

    public void setCodeInt(int codeInt) {
        this.codeInt = codeInt;
    }

    @Override
    public String toString() {
        return "EventMessage{" +
                "code=" + code +
                ",codeInt=" + codeInt +
                ", data=" + data +
                '}';
    }
}
