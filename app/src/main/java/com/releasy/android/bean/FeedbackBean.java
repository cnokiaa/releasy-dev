
package com.releasy.android.bean;

public class FeedbackBean {

    private String date;           //会话时间
    private String msg;            //会话内容
    private int source = 0;        //会话来源   0:官方 1:用户
    
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getMsgType() {
        return source;
    }

    public void setMsgType(int source) {
    	this.source = source;
    }

    public FeedbackBean() {}

    public FeedbackBean(String date, String msg, int source) {
        super();
        this.date = date;
        this.msg = msg;
        this.source = source;
    }
}
