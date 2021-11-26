package com.dfcj.videoim.base;

/**
 * des:封装服务器返回数据
 *
 */
public class BaseRespose<T>{



    public int code;
    public String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public T data;

    public boolean success() {
        return "1".equals(""+code);
    }

    @Override
    public String toString() {
        return "BaseRespose{" +
                "code='" + code + '\'' +
                ", message='" +  + '\'' +
                ", data=" + data +
                '}';
    }



}
