package com.gnetop.ltgame.core.exception;

import com.gnetop.ltgame.core.util.LTGameUtil;

public class LTGameError extends RuntimeException {

    private static final String TAG = LTGameError.class.getSimpleName();


    private int code = 0;
    private String msg = "";
    private Exception error;

    /**
     * 显示错误信息
     *
     * @param msg 错误信息
     */
    public static LTGameError make(String msg) {
        LTGameError error = new LTGameError();
        error.msg = msg;
        return error;
    }

    /**
     * 显示错误码
     *
     * @param code 错误码
     */
    public static LTGameError make(int code) {
        LTGameError error = new LTGameError();
        error.code = code;
        return error;
    }

    /**
     * 显示错误信息和错误码
     *
     * @param code 错误码
     * @param msg  错误信息
     */
    public static LTGameError make(int code, String msg) {
        LTGameError error = new LTGameError();
        error.code = code;
        error.msg = msg;
        return error;
    }

    /**
     * 显示错误信息和异常
     *
     * @param code      错误码
     * @param msg       错误信息
     * @param exception 异常
     */
    public static LTGameError make(int code, String msg, Exception exception) {
        LTGameError error = new LTGameError();
        error.code = code;
        error.msg = msg;
        error.error = exception;
        return error;
    }

    private LTGameError() {
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code){
        this.code=code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void printStackTrace() {
        LTGameUtil.e(TAG, toString());
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("errCode = ").append(code)
                .append(", errMsg = ").append("\n");
        if (error != null) {
            sb.append("其他错误 : ").append(error.getMessage());
            error.printStackTrace();
        }
        return sb.toString();
    }


}
