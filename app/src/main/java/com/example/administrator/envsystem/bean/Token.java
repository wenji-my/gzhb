package com.example.administrator.envsystem.bean;

/**
 * Created by Administrator on 2017/4/13.
 */

public class Token {

    /**
     * token : ……//令牌号
     * isSuc : 1
     * msg : 校验成功
     */

    private String token;
    private String isSuc;
    private String msg;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsSuc() {
        return isSuc;
    }

    public void setIsSuc(String isSuc) {
        this.isSuc = isSuc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
