package com.gnetop.ltgame.core.impl;

/**
 * 上传设备信息接口
 */
public interface OnUploadDeviceListener {

    void onSuccess();

    void onFailed(String msg);
}
