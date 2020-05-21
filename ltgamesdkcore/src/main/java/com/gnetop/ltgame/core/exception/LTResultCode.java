package com.gnetop.ltgame.core.exception;

/**
 * 结果码
 */
public class LTResultCode {
    private static final int BASE_CODE = 0X01;
    //支付成功码
    public static final int STATE_RECHARGE_SUCCESS_CODE = BASE_CODE + 1;
    //登录成功码
    public static final int STATE_LOGIN_SUCCESS_CODE = BASE_CODE + 2;
    //登录失败码
    public static final int STATE_LOGIN_FAILED_CODE = BASE_CODE + 3;
    //取消码
    public static final int STATE_CANCEL_CODE = BASE_CODE + 4;
    //完成码
    public static final int STATE_COMPLETE_CODE = BASE_CODE + 5;
    //完成码
    public static final int STATE_RECHARGE_START = BASE_CODE + 6;
    //支付失败码
    public static final int STATE_RECHARGE_FAILED_CODE = BASE_CODE + 7;
    /***已经绑定过*/
    public static final int USER_ALREADY_BIND_CODE = BASE_CODE + 8;
    //==========================Google登录错误==============================//
    //==========================Google登录错误==============================//
    //==========================Google登录错误==============================//
    /***Google签名错误*/
    public static final int STATE_GOOGLE_SIGN_FAILED = BASE_CODE + 9;
    /***Google登录成功*/
    public static final int STATE_GOOGLE_LOGIN_SUCCESS = BASE_CODE + 10;
    /***绑定Google成功*/
    public static final int STATE_GOOGLE_BIND_SUCCESS = BASE_CODE + 11;
    /***绑定Google失败*/
    public static final int STATE_GOOGLE_BIND_FAILED = BASE_CODE + 12;
    /***Google签名错误*/
    public static final int STATE_GOOGLE_LOGIN_FAILED = BASE_CODE + 13;
    /***Google邮箱配置错误*/
    public static final int STATE_GOOGLE_EMAIL_FAILED = BASE_CODE + 14;
    /***Google本地配置错误*/
    public static final int STATE_GOOGLE_LOCAL_FAILED = BASE_CODE + 15;
    /***网络错误*/
    public static final int STATE_GOOGLE_NET_FAILED = BASE_CODE + 16;
    /***Google综合错误*/
    public static final int STATE_GOOGLE_COMMON_FAILED = BASE_CODE + 17;
    /***已经绑定*/
    public static final int STATE_GOOGLE_ALREADY_BIND = BASE_CODE + 18;

    //==========================Facebook登录错误==============================//
    //==========================Facebook登录错误==============================//
    //==========================Facebook登录错误==============================//

    /***Facebook取消登录*/
    public static final int STATE_FB_CANCEL_CODE = BASE_CODE + 19;
    /***Facebook登录成功*/
    public static final int STATE_FB_LOGIN_SUCCESS = BASE_CODE + 20;
    /***Facebook登录错误*/
    public static final int STATE_FB_LOGIN_FAILED = BASE_CODE + 21;
    /***绑定Facebook成功*/
    public static final int STATE_FB_BIND_SUCCESS = BASE_CODE + 22;
    /***已经绑定*/
    public static final int STATE_FB_ALREADY_BIND = BASE_CODE + 23;
    /***绑定Facebook失败*/
    public static final int STATE_FB_BIND_FAILED = BASE_CODE + 24;


    //==========================游客登录错误==============================//
    //==========================游客登录错误==============================//
    //==========================游客登录错误==============================//

    /***游客登录错误*/
    public static final int STATE_GUEST_LOGIN_FAILED = BASE_CODE + 25;
    /***游客登录成功*/
    public static final int STATE_GUEST_LOGIN_SUCCESS = BASE_CODE + 26;
    /***游客已经绑定*/
    public static final int STATE_GUEST_ALREADY_BIND_CODE = BASE_CODE + 27;
    /***绑定游客成功*/
    public static final int STATE_GUEST_BIND_SUCCESS = BASE_CODE + 28;
    /***绑定游客失败*/
    public static final int STATE_GUEST_BIND_FAILED = BASE_CODE + 29;
    /***已经绑定*/
    public static final int STATE_GUEST_ALREADY_BIND = BASE_CODE + 30;
    //==========================Google支付错误码==============================//
    //==========================Google支付错误码==============================//
    //==========================Google支付错误码==============================//

    /***网络异常或者Google服务异常*/
    public static final int STATE_GP_RESPONSE_RESULT_SERVICE_UNAVAILABLE = BASE_CODE + 31;
    /***不支持Google支付*/
    public static final int STATE_GP_RESPONSE_RESULT_BILLING_UNAVAILABLE = BASE_CODE + 32;
    /***商品不可购买*/
    public static final int STATE_GP_RESPONSE_RESULT_ITEM_UNAVAILABLE = BASE_CODE + 33;
    /***提供给 API 的无效参数*/
    public static final int STATE_GP_RESPONSE_RESULT_DEVELOPER_ERROR = BASE_CODE + 34;
    /***错误*/
    public static final int STATE_GP_RESPONSE_RESULT_ERROR = BASE_CODE + 35;
    /***已经拥有该商品（未消耗掉)*/
    public static final int STATE_GP_RESPONSE_RESULT_ITEM_ALREADY_OWNED = BASE_CODE + 36;
    /***没有当前商品（不可购买）*/
    public static final int STATE_GP_RESPONSE_RESULT_ITEM_NOT_OWNED = BASE_CODE + 37;
    /***用户取消支付*/
    public static final int STATE_GP_RESPONSE_RESULT_USER_CANCELED = BASE_CODE + 38;
    /***支付失败*/
    public static final int STATE_GP_RESPONSE_RESULT_FAILED = BASE_CODE + 39;


    //==========================通用错误码==============================//
    //==========================通用错误码==============================//
    //==========================通用错误码==============================//

    /***SDK初始化错误*/
    public static final int STATE_SDK_INIT_ERROR = BASE_CODE + 40;
    /***不支持*/
    public static final int STATE_CODE_NOT_SUPPORT = BASE_CODE + 41;
    /***请求错误*/
    public static final int STATE_CODE_REQUEST_ERROR = BASE_CODE + 42;
    /***请求超时*/
    public static final int STATE_CODE_REQUEST_TIMEOUT = BASE_CODE + 43;
    /***连接超时*/
    public static final int STATE_NET_CONNECT_ERROR = BASE_CODE + 44;
    /***解析错误*/
    public static final int STATE_CODE_PARSE_ERROR = BASE_CODE + 45;
    /***参数错误*/
    public static final int STATE_CODE_PARAMETERS_ERROR = BASE_CODE + 46;
    //==========================邮箱错误码==============================//
    //==========================邮箱错误码==============================//
    //==========================邮箱错误码==============================//
    /***获取邮箱验证码失败*/
    public static final int STATE_EMAIL_GET_CODE_FAILED = BASE_CODE + 47;
    /***获取邮箱验证码成功*/
    public static final int STATE_EMAIL_GET_CODE_SUCCESS = BASE_CODE + 48;
    /***邮箱登录失败*/
    public static final int STATE_EMAIL_LOGIN_FAILED = BASE_CODE + 49;
    /***邮箱登录成功*/
    public static final int STATE_EMAIL_LOGIN_SUCCESS = BASE_CODE + 50;
    /***绑定邮箱成功*/
    public static final int STATE_EMAIL_BIND_SUCCESS = BASE_CODE + 51;
    /***绑定邮箱失败*/
    public static final int STATE_EMAIL_BIND_FAILED = BASE_CODE + 52;
    /***已经绑定*/
    public static final int STATE_EMAIL_ALREADY_BIND = BASE_CODE + 53;
    //==========================QQ错误码==============================//
    //==========================QQ错误码==============================//
    //==========================QQ错误码==============================//
    /***QQ登录成功*/
    public static final int STATE_QQ_LOGIN_SUCCESS = BASE_CODE + 54;
    /***QQ登录失败*/
    public static final int STATE_QQ_LOGIN_FAILED = BASE_CODE + 55;
    /***QQ获取token失败*/
    public static final int STATE_QQ_GET_TOKEN_FAILED = BASE_CODE + 56;
    /***绑定QQ成功*/
    public static final int STATE_QQ_BIND_SUCCESS = BASE_CODE + 57;
    /***绑定QQ失败*/
    public static final int STATE_QQ_BIND_FAILED = BASE_CODE + 58;
    /***已经绑定*/
    public static final int STATE_QQ_ALREADY_BIND = BASE_CODE + 59;
    /***QQ获取用户信息失败*/
    public static final int STATE_QQ_GET_USER_INFO_FAILED = BASE_CODE + 60;

    //==========================微信错误码==============================//
    //==========================微信错误码==============================//
    //==========================微信错误码==============================//
    /***微信扫码登录失败*/
    public static final int STATE_WX_SCAN_FAILED = BASE_CODE + 61;
    /***微信扫码登录成功*/
    public static final int STATE_WX_SCAN_SUCCESS = BASE_CODE + 62;
    /***微信登录失败*/
    public static final int STATE_WX_LOGIN_FAILED = BASE_CODE + 63;
    /***已经绑定*/
    public static final int STATE_WX_ALREADY_BIND = BASE_CODE + 64;
    /***微信登录成功*/
    public static final int STATE_WX_LOGIN_SUCCESS = BASE_CODE + 65;
    /***绑定微信成功*/
    public static final int STATE_WX_BIND_SUCCESS = BASE_CODE + 66;
    /***绑定微信失败*/
    public static final int STATE_WX_BIND_FAILED = BASE_CODE + 67;
    /***微信刷新token失败*/
    public static final int STATE_WX_REFRESH_TOKEN_FAILED = BASE_CODE + 68;
    /***微信AccessToken失败*/
    public static final int STATE_WX_ACCESS_TOKEN_FAILED = BASE_CODE + 69;
    /***微信检测AccessToken有效性失败*/
    public static final int STATE_WX_CHECK_ACCESS_TOKEN_FAILED = BASE_CODE + 70;
    /***获取微信用户信息失败*/
    public static final int STATE_WX_INFO_FAILED = BASE_CODE + 71;
    //==========================发送异常信息==============================//
    //==========================发送异常信息==============================//
    //==========================发送异常信息==============================//
    /***发送异常信息失败*/
    public static final int STATE_SEND_EXCEPTION_FAILED = BASE_CODE + 72;
    /***发送异常信息成功*/
    public static final int STATE_SEND_EXCEPTION_SUCCESS = BASE_CODE + 73;
    //==========================自动登录信息==============================//
    //==========================自动登录信息==============================//
    //==========================自动登录信息==============================//
    /***自动登录失败*/
    public static final int STATE_AUTO_LOGIN_FAILED = BASE_CODE + 74;
    /***自动登录成功*/
    public static final int STATE_AUTO_LOGIN_SUCCESS = BASE_CODE + 75;
    //请求码
    public static final int SELF_LOGIN_REQUEST_CODE = BASE_CODE + 76;
    //平台错误
    public static final int SELF_PLATFORM_CODE = BASE_CODE + 77;
    public static final int STATE_COMMON_ERROR = BASE_CODE + 78;
    //==========================UI包信息==============================//
    //==========================UI包信息==============================//
    //==========================UI包信息==============================//
    //登录成功
    public static final int STATE_UI_LOGIN_SUCCESS = BASE_CODE + 79;
    //登录失败
    public static final int STATE_UI_LOGIN_FAILED = BASE_CODE + 80;
    //==========================角色信息==============================//
    //==========================角色信息==============================//
    //==========================角色信息==============================//
    //角色信息上传成功
    public static final int STATE_ROLE_UPLOAD_SUCCESS = BASE_CODE + 81;
    //角色信息上传失败
    public static final int STATE_ROLE_UPLOAD_FAILED = BASE_CODE + 82;
    //获取Facebook的信息
    public static final int STATE_FB_UI_TOKEN = BASE_CODE + 83;
    //获取Google的信息
    public static final int STATE_GOOGLE_UI_TOKEN = BASE_CODE + 84;
    //获取QQ的信息
    public static final int STATE_QQ_UI_TOKEN = BASE_CODE + 85;
    //获取微信的信息
    public static final int STATE_WX_UI_TOKEN = BASE_CODE + 86;
    //==========================请求码==============================//
    //==========================请求码==============================//
    //==========================请求码==============================//
    //Facebook请求码
    public static final int FB_SELF_REQUEST_CODE = BASE_CODE + 87;
    //Google请求码
    public static final int GOOGLE_SELF_REQUEST_CODE = BASE_CODE + 88;
    //oneStore请求码
    public static final int ONE_STORE_SELF_REQUEST_CODE = BASE_CODE + 89;
    //googlePlay请求码
    public static final int GP_SELF_REQUEST_CODE = BASE_CODE + 90;

    //google创建订单失败
    public static final int STATE_GP_CREATE_ORDER_FAILED = BASE_CODE + 91;
    //google创建订单成功
    public static final int STATE_GP_CREATE_ORDER_SUCCESS = BASE_CODE + 92;
    //oneStore支付失败
    public static final int STATE_ONE_STORE_PLAY_FAILED = BASE_CODE + 93;
    //Facebook获取Token失败
    public static final int STATE_FB_GET_TOKEN_ERROR = BASE_CODE + 94;
    //QQ获取Token失败
    public static final int STATE_QQ_GET_TOKEN_ERROR = BASE_CODE + 95;
    //获取服务器时间失败
    public static final int STATE_GET_SERVER_FAILED = BASE_CODE + 96;
    //获取服务器时间成功
    public static final int STATE_GET_SERVER_SUCCESS = BASE_CODE + 97;

    //成功信息
    public static final String MSG_STATE_SUCCESS = "SUCCESS";
    //失败信息
    public static final String MSG_STATE_FAILED = "FAILED";
    //取消信息
    public static final String MSG_CANCEL_FAILED = "CANCEL";
    //完成信息
    public static final String MSG_COMPLETE_FAILED = "COMPLETE";
    //参数错误
    public static final String STATE_CODE_PARAMETERS_FAILED = "PARAMETERS FAILED";
}
