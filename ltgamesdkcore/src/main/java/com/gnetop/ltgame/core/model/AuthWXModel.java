package com.gnetop.ltgame.core.model;

public class AuthWXModel {

    private int errcode;
    private String errmsg;

    public boolean isNoError() {
        return errcode == 0;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }


    @Override
    public String toString() {
        return "TokenValidResp{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
