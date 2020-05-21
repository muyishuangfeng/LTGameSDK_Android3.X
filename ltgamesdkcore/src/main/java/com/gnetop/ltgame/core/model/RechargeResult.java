package com.gnetop.ltgame.core.model;

import com.gnetop.ltgame.core.base.BaseEntry;
import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;

/**
 * 支付结果
 */
public class RechargeResult extends Result {

    private BaseEntry<ResultModel> resultModel;
    private RechargeObject rechargeObject;
    private OneStoreResult result;
    private String errorMsg;
    private OneStoreResult oneStoreResult;
    //状态
    public int state;
    //错误信息
    private LTGameError error;
    private String msg;


    private RechargeResult(int state) {
        super(state);
    }


    private RechargeResult(int state, BaseEntry<ResultModel> resultModel) {
        super(state, resultModel);
    }

    private RechargeResult(int state, OneStoreResult resultModel) {
        super(state, resultModel);
    }

    private RechargeResult(int state, String msg) {
        super(state, msg);
    }

    private RechargeResult(int state, RechargeObject shareObj, int target) {
        super(state, target);
        this.rechargeObject = shareObj;
    }

    private RechargeResult() {
    }

    /**
     * 开始支付
     */
    public static RechargeResult startOf() {
        return new RechargeResult(LTResultCode.STATE_RECHARGE_START);
    }

    public static RechargeResult stateOneStoreOf(OneStoreResult oneStoreResult) {
        RechargeResult result = new RechargeResult();
        result.result = oneStoreResult;
        return result;
    }


    public static RechargeResult successOf(BaseEntry<ResultModel> resultModel) {
        RechargeResult result = new RechargeResult(LTResultCode.STATE_RECHARGE_SUCCESS_CODE, resultModel);
        result.resultModel = resultModel;
        return result;

    }

    public static RechargeResult successOf(int code, BaseEntry<ResultModel> resultModel) {
        RechargeResult result = new RechargeResult(code, resultModel);
        result.resultModel = resultModel;
        return result;

    }


    public static RechargeResult failOf(BaseEntry<ResultModel> resultModel) {
        RechargeResult result = new RechargeResult(LTResultCode.STATE_RECHARGE_FAILED_CODE, resultModel);
        result.resultModel = resultModel;
        return result;
    }

    public static RechargeResult failOf(LTGameError error) {
        RechargeResult result = new RechargeResult();
        result.error = error;
        return result;
    }

    public static RechargeResult failOf(int target, LTGameError error) {
        RechargeResult result = new RechargeResult(STATE_RECHARGE_FAILED);
        result.error = error;
        return result;
    }

    public static RechargeResult failOf(int code, String error) {
        RechargeResult result = new RechargeResult();
        result.state = code;
        result.msg = error;
        return result;
    }

    public static RechargeResult failOf(String errorMsg) {
        RechargeResult result = new RechargeResult(LTResultCode.STATE_RECHARGE_FAILED_CODE, errorMsg);
        result.errorMsg = errorMsg;
        return result;
    }

    public static RechargeResult cancelOf() {
        return new RechargeResult(LTResultCode.STATE_CANCEL_CODE);
    }


    public static RechargeResult completeOf() {
        return new RechargeResult(LTResultCode.STATE_COMPLETE_CODE);
    }

    public static RechargeResult completeOf(int state) {
        return new RechargeResult(LTResultCode.STATE_COMPLETE_CODE);
    }

    public static RechargeResult completeOf(int target, RechargeObject obj) {
        return new RechargeResult(STATE_RECHARGE_COMPLETE, obj, target);
    }


    public static RechargeResult stateOf(int state) {
        return new RechargeResult(state);
    }

    public static RechargeResult stateOf(int stateActive, int state) {
        return new RechargeResult(state);
    }

    public static RechargeResult stateOf(int state, int target, RechargeObject obj) {
        return new RechargeResult(state, obj, target);
    }

    public BaseEntry<ResultModel> getResultModel() {
        return resultModel;
    }

    public void setResultModel(BaseEntry<ResultModel> resultModel) {
        this.resultModel = resultModel;
    }

    public OneStoreResult getResult() {
        return result;
    }

    public void setResult(OneStoreResult result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
