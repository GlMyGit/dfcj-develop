package com.dfcj.videoim.entity;


public class TextMsgBody extends MsgBody {
     private String message;
     private String extra;

    public CharSequence getCharsequence() {
        return charsequence;
    }

    public void setCharsequence(CharSequence charsequence) {
        this.charsequence = charsequence;
    }

    private CharSequence charsequence;

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
    public TextMsgBody() {
    }

    public TextMsgBody(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    @Override
    public String toString() {
        return "TextMsgBody{" +
                "message='" + message + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
