package com.gnetop.ltgame.core.model;

import com.gnetop.ltgame.core.base.BaseEntry;
import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;

/**
 * 登录结果
 */
public class LoginResult extends Result {
    //结果
    private BaseEntry<ResultModel> resultModel;
    private BaseEntry baseEntry;
    //错误信息
    public LTGameError error;
    //错误信息
    private String msg;
    //状态
    public int state;
    // 扫码登录二维码文件路径
    public String wxCodePath;
    // 授权码，如果 onlyAuthCode 为 true, 将会返回它
    public String wxAuthCode;


    private LoginResult() {
    }

    private LoginResult(int state) {
        super(state);
        this.state = state;
    }

    private LoginResult(LTGameError error) {
        super(error);
        this.error = error;
    }


    public LoginResult(int state, BaseEntry<ResultModel> resultModel) {
        super(state, resultModel);
        this.state = state;
        this.resultModel = resultModel;
    }

    public LoginResult(BaseEntry resultModel) {
        super(resultModel);
        this.baseEntry = resultModel;
    }


    public static LoginResult successBaseEntryOf(int code, BaseEntry baseEntry) {
        LoginResult result = new LoginResult(code);
        result.baseEntry = baseEntry;
        return result;
    }


    public static LoginResult successOf(BaseEntry<ResultModel> resultModel) {
        LoginResult result = new LoginResult(LTResultCode.STATE_LOGIN_SUCCESS_CODE);
        result.resultModel = resultModel;
        return result;
    }

    public static LoginResult successOf(int code, BaseEntry<ResultModel> resultModel) {
        LoginResult result = new LoginResult(code);
        result.resultModel = resultModel;
        return result;
    }



    public static LoginResult successOf(LTGameError error) {
        LoginResult result = new LoginResult();
        result.error = error;
        return result;
    }


    public static LoginResult failOf(LTGameError error) {
        LoginResult result = new LoginResult(error);
        result.error = error;
        return result;
    }

    public static LoginResult failOf(int code, LTGameError error) {
        LoginResult result = new LoginResult(error);
        error.setCode(code);
        result.error = error;
        return result;
    }

    public static LoginResult failOf(int code, String msg) {
        LoginResult result = new LoginResult(code);
        result.state = code;
        result.msg = msg;
        return result;
    }
    public static LoginResult failOf(int code) {
        LoginResult result = new LoginResult(code);
        result.state = code;
        return result;
    }

    public static LoginResult loginOut(LTGameError error) {
        LoginResult result = new LoginResult();
        result.error = error;
        return result;
    }


    public static LoginResult cancelOf() {
        return new LoginResult(LTResultCode.STATE_CANCEL_CODE);
    }

    public static LoginResult completeOf() {
        return new LoginResult(LTResultCode.STATE_COMPLETE_CODE);
    }


    public static LoginResult stateOf(int state) {
        return new LoginResult(state);
    }


    public BaseEntry<ResultModel> getResultModel() {
        return resultModel;
    }

    public BaseEntry getBaseEntry() {
        return baseEntry;
    }


    public LTGameError getError() {
        return error;
    }

    public LoginResult(int state, int target) {
        super(state, target);
    }


    public static LoginResult stateOf(BaseEntry baseEntry) {
        LoginResult result = new LoginResult(STATE_SUCCESS);
        result.baseEntry = baseEntry;
        return result;
    }


    public static LoginResult successOf(int target) {
        return new LoginResult(STATE_SUCCESS, target);
    }


    public static LoginResult failOf(int target, BaseEntry<ResultModel> error) {
        LoginResult result = new LoginResult(STATE_FAIL, target);
        result.resultModel = error;
        return result;
    }


    public static LoginResult cancelOf(int target) {
        return new LoginResult(STATE_CANCEL, target);
    }


    public static LoginResult completeOf(int target) {
        return new LoginResult(STATE_COMPLETE, target);
    }

    public static LoginResult stateOf(int state, int target) {
        return new LoginResult(state, target);
    }


}
